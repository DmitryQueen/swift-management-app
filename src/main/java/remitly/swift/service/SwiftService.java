package remitly.swift.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import remitly.swift.dto.CountrySwiftCodesDto;
import remitly.swift.dto.SwiftDto;
import remitly.swift.entity.Swift;
import remitly.swift.exception.DuplicateSwiftCodeException;
import remitly.swift.exception.SwiftCodeNotFoundException;
import remitly.swift.mapper.SwiftMapper;
import remitly.swift.repository.SwiftRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SwiftService {

    private final SwiftRepository swiftRepository;
    private final SwiftMapper swiftMapper;

    @Transactional(readOnly = true)
    public SwiftDto getSwiftDetails(String swiftCode) {
        Swift swift = swiftRepository.findBySwiftCode(swiftCode)
                .orElseThrow(() -> new SwiftCodeNotFoundException(swiftCode));

        SwiftDto dto = swiftMapper.toDto(swift);

        if (swift.isHeadquarter()) {
            List<Swift> branches = swiftRepository.findAllBranches(swiftCode);
            List<SwiftDto> branchDtos = swiftMapper.toDtoList(branches);
            dto.setBranches(branchDtos);
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public CountrySwiftCodesDto getAllSwiftsByCountry(String countryIso2) {
        List<Swift> swifts = swiftRepository.findAllByCountryISO2(countryIso2);
        return swiftMapper.toCountrySwiftCodesDto(countryIso2, swifts);
    }

    @Transactional
    public SwiftDto saveSwiftCode(SwiftDto dto) {
        String swiftCode = dto.getSwiftCode();
        if (swiftRepository.existsBySwiftCode(swiftCode)) {
            throw new DuplicateSwiftCodeException(swiftCode);
        }

        Swift codeDetails = swiftMapper.toEntity(dto);

        codeDetails.setCountryName(codeDetails.getCountryName().toUpperCase());
        codeDetails.setCountryISO2(codeDetails.getCountryISO2().toUpperCase());
        codeDetails.setHeadquarter(isHeadquarter(swiftCode));

        Swift savedSwift = swiftRepository.save(codeDetails);
        log.info("Saved new Swift code: " + savedSwift);

        return swiftMapper.toDto(savedSwift);
    }

    @Transactional
    public void deleteSwiftCode(String swiftCode) {
        Swift swift = swiftRepository.findBySwiftCode(swiftCode)
                .orElseThrow(() -> new SwiftCodeNotFoundException(swiftCode));

        swiftRepository.delete(swift);
        log.info("Deleted Swift code: " + swiftCode);
    }

    private boolean isHeadquarter(String swiftCode) {
        return swiftCode != null && swiftCode.length() == 11 && swiftCode.endsWith("XXX");
    }
}
