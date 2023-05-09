package br.com.faculdadeimpacta.aluno.charlie.agendaplus.service;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.User;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.UserPreferences;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.exception.InvalidPhoneNumberException;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.service.phonevalidator.Country;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.service.phonevalidator.CountryValidator;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PhoneValidationService {

    public static final String DEFAULT_REGION = "BR";
    public static final String USER_PREFERENCE_DEFAULT_REGION_KEY = "default_region";
    public static final String INVALID_MOBILE_PHONE_ERROR_CODE = "invalid_mobile_phone";

    private final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
    private final Map<Integer, CountryValidator> validators;

    public PhoneValidationService(Set<CountryValidator> validators) {
        this.validators = validators.stream()
                .collect(Collectors.toMap(v -> v.getClass().getAnnotation(Country.class).code(), Function.identity()));
    }

    /**
     * Validates the given phone number
     *
     * @param phoneNumber The number to be validated
     * @throws InvalidPhoneNumberException if the phone number is invalid
     */
    public void validatePhoneNumber(User user, String phoneNumber) {
        try {
            var phone = phoneNumberUtil.parse(phoneNumber, getDefaultRegionForUser(user));
            var parsedNumber = Long.toString(phone.getNationalNumber());

            var validator = validators.get(phone.getCountryCode());
            if (validator != null) {
                validator.validate(parsedNumber);
            }
        } catch (NumberParseException e) {
            throw new InvalidPhoneNumberException(phoneNumber, e.getErrorType().name().toLowerCase(Locale.ROOT));
        }
    }

    private String getDefaultRegionForUser(User user) {
        return Optional.ofNullable(user.getPreferences())
                .map(p -> p.get(USER_PREFERENCE_DEFAULT_REGION_KEY))
                .filter(p -> p.getDeletedAt() == null)
                .map(UserPreferences::getValue)
                .orElse(DEFAULT_REGION);
    }

}
