package remitly.swift.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SwiftCodesByCountryDto {
    private String countryISO2;
    private String countryName;
    private List<SwiftDto> swiftCodes;
}
