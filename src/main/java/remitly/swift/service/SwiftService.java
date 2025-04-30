package remitly.swift.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import remitly.swift.dto.SwiftCodesByCountryDto;
import remitly.swift.dto.SwiftDto;
import remitly.swift.entity.Swift;
import remitly.swift.exception.CountryIsoCodeNotFoundException;
import remitly.swift.exception.DuplicateHeadquarterException;
import remitly.swift.exception.DuplicateSwiftCodeException;
import remitly.swift.exception.SwiftCodeNotFoundException;
import remitly.swift.mapper.SwiftMapper;
import remitly.swift.repository.SwiftRepository;
import remitly.swift.validation.SwiftValidator;

import java.util.List;

import static remitly.swift.utils.SwiftConstants.SWIFT_MAX_LENGTH;
import static remitly.swift.utils.SwiftConstants.SWIFT_MIN_LENGTH;

@Slf4j
@RequiredArgsConstructor
@Service
public class SwiftService {

    private final SwiftRepository swiftRepository;
    private final SwiftMapper swiftMapper;
    private final SwiftValidator swiftValidator;

    @Transactional(readOnly = true)
    public SwiftDto getSwiftDetails(String swiftCode) {
        Swift swift = swiftRepository.findBySwiftCode(swiftCode)
                .orElseThrow(() -> new SwiftCodeNotFoundException(swiftCode));

        SwiftDto dto = swiftMapper.toDto(swift);

        if (swift.getHeadquarter()) {
            List<Swift> branches = swiftRepository.findAllBranches(swiftCode);
            List<SwiftDto> branchDtos = swiftMapper.toDtoList(branches);
            dto.setBranches(branchDtos);
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public SwiftCodesByCountryDto getAllSwiftsByCountry(String countryIso2) {
        String upperCaseIso2 = countryIso2.toUpperCase();
        if (!swiftRepository.existsByCountryISO2(upperCaseIso2)) {
            throw new CountryIsoCodeNotFoundException(upperCaseIso2);
        }

        List<Swift> swifts = swiftRepository.findAllByCountryISO2(upperCaseIso2);
        return swiftMapper.toCountrySwiftCodesDto(upperCaseIso2, swifts);
    }

    @Transactional
    public SwiftDto saveSwiftCode(SwiftDto dto) {
        String swiftCode = dto.getSwiftCode();

        checkForDuplicates(dto);
        swiftValidator.validateFieldsSwiftDto(dto);

        Swift codeDetails = swiftMapper.toEntity(dto);
        codeDetails.setCountryName(codeDetails.getCountryName().toUpperCase());
        codeDetails.setCountryISO2(codeDetails.getCountryISO2().toUpperCase());
        if (codeDetails.getHeadquarter() == null) {
            codeDetails.setHeadquarter(isHeadquarter(swiftCode));
        }

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
        int swiftLength = swiftCode.length();
        if ((swiftLength == SWIFT_MAX_LENGTH && swiftCode.endsWith("XXX")) || swiftLength == SWIFT_MIN_LENGTH) {
            return true;
        }
        return swiftRepository.countExistingHeadquarters(swiftCode) == 0;
    }

    private void checkForDuplicates(SwiftDto dto) {
        String swiftCode = dto.getSwiftCode();
        if (swiftRepository.existsBySwiftCode(swiftCode)) {
            throw new DuplicateSwiftCodeException(swiftCode);
        }
        if (dto.getHeadquarter() != null && dto.getHeadquarter() && swiftRepository.countExistingHeadquarters(swiftCode) > 0) {
            throw new DuplicateHeadquarterException(swiftCode);
        }
    }
}
