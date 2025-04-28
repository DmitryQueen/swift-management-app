package remitly.swift.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import remitly.swift.dto.SwiftDto;
import remitly.swift.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


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
    void validateSwiftDtoTest() {
        assertDoesNotThrow(() -> swiftValidator.validateSwiftDto(validDto));
    }

    @Test
    void validateSwiftDtoTest_blankBankName() {
        validDto.setBankName("");

        assertThrows(ValidationException.class, () -> swiftValidator.validateSwiftDto(validDto));
    }

    @Test
    void validateSwiftDtoTest_blankCountryName() {
        validDto.setCountryName("");

        assertThrows(ValidationException.class, () -> swiftValidator.validateSwiftDto(validDto));
    }

    @Test
    void validateSwiftDtoTest_blankCountryISO2() {
        validDto.setCountryISO2("");

        assertThrows(ValidationException.class, () -> swiftValidator.validateSwiftDto(validDto));
    }

    @Test
    void validateSwiftDtoTest_invalidCountryISO2() {
        validDto.setCountryISO2("POL");

        assertThrows(ValidationException.class, () -> swiftValidator.validateSwiftDto(validDto));
    }

    @Test
    void validateSwiftDtoTest_blankSwiftCode() {
        validDto.setSwiftCode("");

        assertThrows(ValidationException.class, () -> swiftValidator.validateSwiftDto(validDto));
    }

    @Test
    void validateSwiftDtoTest_invalidSwiftCode() {
        validDto.setSwiftCode("XYZXYZ");

        assertThrows(ValidationException.class, () -> swiftValidator.validateSwiftDto(validDto));
    }

}
