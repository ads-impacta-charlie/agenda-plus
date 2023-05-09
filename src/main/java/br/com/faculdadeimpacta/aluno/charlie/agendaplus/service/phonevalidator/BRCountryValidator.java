package br.com.faculdadeimpacta.aluno.charlie.agendaplus.service.phonevalidator;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.exception.InvalidPhoneNumberException;
import com.google.i18n.phonenumbers.NumberParseException;

import java.util.Locale;

import static br.com.faculdadeimpacta.aluno.charlie.agendaplus.service.PhoneValidationService.INVALID_MOBILE_PHONE_ERROR_CODE;

@Country(code = 55)
public class BRCountryValidator implements CountryValidator {
    @Override
    public void validate(String phoneNumber) {
        if (phoneNumber.length() < 8 || phoneNumber.length() > 11) {
            throw new InvalidPhoneNumberException(phoneNumber, NumberParseException.ErrorType.TOO_SHORT_NSN.name().toLowerCase(Locale.ROOT));
        }
        var position = 0;
        if (phoneNumber.length() >= 10) {
            position = 2;
        }
        if (phoneNumber.charAt(position) == '9' && phoneNumber.length() % 2 != 1) {
            throw new InvalidPhoneNumberException(phoneNumber, INVALID_MOBILE_PHONE_ERROR_CODE);
        }
    }
}
