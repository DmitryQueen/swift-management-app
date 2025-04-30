package remitly.swift.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static remitly.swift.utils.SwiftConstants.COUNTRY_ISO_LENGTH;
import static remitly.swift.utils.SwiftConstants.SWIFT_MIN_LENGTH;
import static remitly.swift.utils.SwiftConstants.SWIFT_MAX_LENGTH;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SwiftDto {

    private String address;

    @NotBlank(message = "Bank field should't be blank")
    private String bankName;

    @Size(min = COUNTRY_ISO_LENGTH, max = COUNTRY_ISO_LENGTH)
    private String countryISO2;

    @NotBlank(message = "Country name field shoudn't be blank")
    private String countryName;

    @JsonProperty("isHeadquarter")
    private Boolean headquarter;

    @Size(min = SWIFT_MIN_LENGTH, max = SWIFT_MAX_LENGTH)
    private String swiftCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<SwiftDto> branches;
}
