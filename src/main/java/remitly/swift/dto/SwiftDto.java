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

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SwiftDto {

    private String address;

    @NotBlank(message = "Bank field should't be blank")
    private String bankName;

    @Size(min = 2, max = 2)
    private String countryISO2;

    @NotBlank(message = "Country name field shoudn't be blank")
    private String countryName;

    @JsonProperty("isHeadquarter")
    private boolean headquarter;

    @Size(min = 11, max = 11)
    private String swiftCode;

    private List<SwiftDto> branches;
}
