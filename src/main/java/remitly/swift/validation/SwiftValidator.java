package remitly.swift.validation;

import org.springframework.stereotype.Component;
import remitly.swift.dto.SwiftDto;
import remitly.swift.exception.ValidationException;

import java.util.List;
import java.util.function.Predicate;

import static remitly.swift.utils.SwiftConstants.COUNTRY_ISO_LENGTH;
import static remitly.swift.utils.SwiftConstants.SWIFT_MAX_LENGTH;
import static remitly.swift.utils.SwiftConstants.SWIFT_MIN_LENGTH;

@Component
public class SwiftValidator {

    public void validateFieldsSwiftDto(SwiftDto swiftDto) {
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

        int swiftLength = swiftDto.getSwiftCode().length();
        int isoLength = swiftDto.getCountryISO2().length();
        validateField(dto -> isBlank(dto.getBankName()), swiftDto, "Bank name must not be blank");
        validateField(dto -> isBlank(dto.getCountryName()), swiftDto, "Country name must not be blank");
        validateField(dto -> isBlank(dto.getCountryISO2()), swiftDto, "Country ISO2 must not be blank");
        validateField(dto -> isoLength != COUNTRY_ISO_LENGTH, swiftDto, "Country ISO2 must be exactly 2 characters");
        validateField(dto -> isBlank(dto.getSwiftCode()), swiftDto, "SWIFT code must not be blank");
        validateField(dto -> swiftLength > SWIFT_MAX_LENGTH || swiftLength < SWIFT_MIN_LENGTH, swiftDto, "SWIFT code should be from 8 to 11 characters long");
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
