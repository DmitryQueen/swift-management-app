package remitly.swift.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import remitly.swift.dto.SwiftDto;
import remitly.swift.exception.DuplicateHeadquarterException;
import remitly.swift.exception.DuplicateSwiftCodeException;
import remitly.swift.exception.ValidationException;
import remitly.swift.repository.SwiftRepository;
import remitly.swift.service.SwiftService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class SwiftValidatorTest {

    @InjectMocks
    private SwiftValidator swiftValidator;

    private SwiftDto validDto;

    @BeforeEach
    void setUp() {
        validDto = SwiftDto.builder().bankName("PKO")
                .address("Grunwaldzka 43")
                .swiftCode("JFKOLSJFD21")
                .countryName("POLAND")
                .countryISO2("PL")
                .build();
    }

    @Test
    void validateFieldsSwiftDtoTest() {
        assertDoesNotThrow(() -> swiftValidator.validateFieldsSwiftDto(validDto));
    }

    @Test
    void validateFieldsSwiftDtoTest_blankBankName() {
        validDto.setBankName("");

        assertThrows(ValidationException.class, () -> swiftValidator.validateFieldsSwiftDto(validDto));
    }

    @Test
    void validateFieldsSwiftDtoTest_blankCountryName() {
        validDto.setCountryName("");

        assertThrows(ValidationException.class, () -> swiftValidator.validateFieldsSwiftDto(validDto));
    }

    @Test
    void validateFieldsSwiftDtoTest_blankCountryISO2() {
        validDto.setCountryISO2("");

        assertThrows(ValidationException.class, () -> swiftValidator.validateFieldsSwiftDto(validDto));
    }

    @Test
    void validateFieldsSwiftDtoTest_invalidCountryISO2() {
        validDto.setCountryISO2("POL");

        assertThrows(ValidationException.class, () -> swiftValidator.validateFieldsSwiftDto(validDto));
    }

    @Test
    void validateSwiftDtoTest_blankFieldsSwiftCode() {
        validDto.setSwiftCode("");

        assertThrows(ValidationException.class, () -> swiftValidator.validateFieldsSwiftDto(validDto));
    }

    @Test
    void validateSwiftDtoTest_invalidFieldsSwiftCode() {
        validDto.setSwiftCode("XYZXYZ");

        assertThrows(ValidationException.class, () -> swiftValidator.validateFieldsSwiftDto(validDto));
    }

}
