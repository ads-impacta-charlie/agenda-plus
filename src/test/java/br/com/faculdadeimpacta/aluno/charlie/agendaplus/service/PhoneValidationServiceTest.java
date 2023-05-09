package br.com.faculdadeimpacta.aluno.charlie.agendaplus.service;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.User;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity.UserPreferences;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.exception.InvalidPhoneNumberException;
import br.com.faculdadeimpacta.aluno.charlie.agendaplus.service.phonevalidator.BRCountryValidator;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static br.com.faculdadeimpacta.aluno.charlie.agendaplus.service.PhoneValidationService.USER_PREFERENCE_DEFAULT_REGION_KEY;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PhoneValidationServiceTest {
    private static final PhoneValidationService service = new PhoneValidationService(Set.of(new BRCountryValidator()));

    @Test
    public void testPhoneValidationWithPreference() {
        User user = new User();
        user.setPreferences(
                Map.of(
                        USER_PREFERENCE_DEFAULT_REGION_KEY,
                        UserPreferences.builder().key(USER_PREFERENCE_DEFAULT_REGION_KEY).value("US").build()));

        assertDoesNotThrow(() -> service.validatePhoneNumber(user, "11 33333333"));
        assertDoesNotThrow(() -> service.validatePhoneNumber(user, "11 912345678"));
        assertDoesNotThrow(() -> service.validatePhoneNumber(user, "+1 234 2345678"));
        assertDoesNotThrow(() -> service.validatePhoneNumber(user, "+55 51 43214321"));
        assertThrows(InvalidPhoneNumberException.class, () -> service.validatePhoneNumber(user, "1"));
    }

    @Test
    public void testPhoneValidationWithoutPreference() {
        User user = new User();

        assertDoesNotThrow(() -> service.validatePhoneNumber(user, "11 33333333"));
        assertDoesNotThrow(() -> service.validatePhoneNumber(user, "11 912345678"));
        assertDoesNotThrow(() -> service.validatePhoneNumber(user, "+1 234 2345678"));
        assertDoesNotThrow(() -> service.validatePhoneNumber(user, "+55 51 43214321"));
        assertThrows(InvalidPhoneNumberException.class, () -> service.validatePhoneNumber(user, "1"));
        // Additional checks made by BRCountryValidator
        assertThrows(InvalidPhoneNumberException.class, () -> service.validatePhoneNumber(user, "11 1234"));
        assertThrows(InvalidPhoneNumberException.class, () -> service.validatePhoneNumber(user, "11 91234567"));
    }
}