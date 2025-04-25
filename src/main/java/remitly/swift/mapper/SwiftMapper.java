package remitly.swift.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import remitly.swift.dto.CountrySwiftCodesDto;
import remitly.swift.dto.SwiftDto;
import remitly.swift.entity.Swift;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SwiftMapper {

    SwiftDto toDto(Swift swift);

    Swift toEntity(SwiftDto swiftDto);

    List<SwiftDto> toDtoList(List<Swift> swifts);

    default CountrySwiftCodesDto toCountrySwiftCodesDto(String countryIso2, List<Swift> swifts) {
        if (swifts == null || swifts.isEmpty()) {
            return CountrySwiftCodesDto.builder()
                    .countryISO2(countryIso2)
                    .countryName("")
                    .swiftCodes(Collections.emptyList())
                    .build();
        }

        Swift first = swifts.get(0);

        return CountrySwiftCodesDto.builder()
                .countryISO2(first.getCountryISO2())
                .countryName(first.getCountryName())
                .swiftCodes(toDtoList(swifts))
                .build();
    }
}
