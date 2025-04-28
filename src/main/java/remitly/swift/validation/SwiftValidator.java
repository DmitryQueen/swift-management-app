package remitly.swift.validation;

import org.springframework.stereotype.Component;
import remitly.swift.dto.SwiftDto;
import remitly.swift.exception.ValidationException;

import java.util.List;
import java.util.function.Predicate;

@Component
public class SwiftValidator {

    public void validateSwiftDto(SwiftDto swiftDto) {
        if (swiftDto == null) {
            throw new ValidationException("SwiftDto must not be null");
        }

        validateFieldsNotEmpty(List.of(
                swiftDto.getBankName(),
                swiftDto.getCountryName(),
                swiftDto.getCountryISO2(),
                swiftDto.getSwiftCode(),
                swiftDto.getAddress()
        ));

        validateField(dto -> isBlank(dto.getBankName()), swiftDto, "Bank name must not be blank");
        validateField(dto -> isBlank(dto.getCountryName()), swiftDto, "Country name must not be blank");
        validateField(dto -> isBlank(dto.getCountryISO2()), swiftDto, "Country ISO2 must not be blank");
        validateField(dto -> swiftDto.getCountryISO2().length() != 2, swiftDto, "Country ISO2 must be exactly 2 characters");
        validateField(dto -> isBlank(dto.getSwiftCode()), swiftDto, "SWIFT code must not be blank");
        validateField(dto -> swiftDto.getSwiftCode().length() != 11, swiftDto, "SWIFT code must be 11 characters long");
    }

    private void validateField(Predicate<SwiftDto> validator, SwiftDto swiftDto, String message) {
        if (validator.test(swiftDto)) {
            throw new ValidationException(message);
        }
    }

    private void validateFieldsNotEmpty(List<String> values) {
        boolean allBlank = values.stream()
                .allMatch(this::isBlank);

        if (allBlank) {
            throw new ValidationException("Row is completely empty and cannot be processed: " + values);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
