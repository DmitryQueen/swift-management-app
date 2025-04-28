package remitly.swift.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import remitly.swift.dto.SwiftDto;
import remitly.swift.entity.Swift;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-28T13:56:04+0200",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.13.jar, environment: Java 17.0.14 (Amazon.com Inc.)"
)
@Component
public class SwiftMapperImpl implements SwiftMapper {

    @Override
    public SwiftDto toDto(Swift swift) {
        if ( swift == null ) {
            return null;
        }

        SwiftDto.SwiftDtoBuilder swiftDto = SwiftDto.builder();

        swiftDto.address( swift.getAddress() );
        swiftDto.bankName( swift.getBankName() );
        swiftDto.countryISO2( swift.getCountryISO2() );
        swiftDto.countryName( swift.getCountryName() );
        swiftDto.headquarter( swift.isHeadquarter() );
        swiftDto.swiftCode( swift.getSwiftCode() );

        return swiftDto.build();
    }

    @Override
    public Swift toEntity(SwiftDto swiftDto) {
        if ( swiftDto == null ) {
            return null;
        }

        Swift.SwiftBuilder swift = Swift.builder();

        swift.address( swiftDto.getAddress() );
        swift.bankName( swiftDto.getBankName() );
        swift.countryISO2( swiftDto.getCountryISO2() );
        swift.countryName( swiftDto.getCountryName() );
        swift.headquarter( swiftDto.isHeadquarter() );
        swift.swiftCode( swiftDto.getSwiftCode() );

        return swift.build();
    }

    @Override
    public List<SwiftDto> toDtoList(List<Swift> swifts) {
        if ( swifts == null ) {
            return null;
        }

        List<SwiftDto> list = new ArrayList<SwiftDto>( swifts.size() );
        for ( Swift swift : swifts ) {
            list.add( toDto( swift ) );
        }

        return list;
    }
}
