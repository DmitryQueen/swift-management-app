package remitly.swift.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SwiftServiceTest {

    @Mock
    private SwiftRepository swiftRepository;

    @Mock
    private SwiftValidator swiftValidator;

    @Mock
    private SwiftMapper swiftMapper;

    @InjectMocks
    private SwiftService swiftService;

    private Swift swiftEntity;
    private SwiftDto swiftDto;
    private List<Swift> swifts;
    private List<SwiftDto> swiftDtos;

    private String swiftCode = "ABCDEF12XXX";
    private String countryName = "POLAND";
    private String countryISO2 = "PL";

    @BeforeEach
    void setup() {
        swiftEntity = new Swift();
        swiftEntity.setSwiftCode(swiftCode);
        swiftEntity.setCountryName(countryName);
        swiftEntity.setCountryISO2(countryISO2);
        swiftEntity.setHeadquarter(true);

        swiftDto = new SwiftDto();
        swiftDto.setSwiftCode(swiftCode);
        swiftDto.setCountryName(countryName);
        swiftDto.setCountryISO2(countryISO2);

        swifts = List.of(new Swift(), new Swift(), new Swift());
        swiftDtos = List.of(new SwiftDto(), new SwiftDto(), new SwiftDto());
    }

    @Test
    public void getSwiftDetailsTest() {
        when(swiftRepository.findBySwiftCode(swiftCode)).thenReturn(Optional.of(swiftEntity));
        when(swiftMapper.toDto(swiftEntity)).thenReturn(swiftDto);
        when(swiftRepository.findAllBranches(swiftCode)).thenReturn(swifts);
        when(swiftMapper.toDtoList(anyList())).thenReturn(swiftDtos);

        SwiftDto result = swiftService.getSwiftDetails(swiftCode);

        assertNotNull(result);
        assertEquals(swiftCode, result.getSwiftCode());
        assertEquals(result.getBranches(), swiftDtos);
        verify(swiftRepository).findBySwiftCode(swiftCode);
        verify(swiftRepository).findAllBranches(swiftCode);
    }

    @Test
    public void getSwiftDetailsTest_throwsSwiftNotFoundException() {
        when(swiftRepository.findBySwiftCode(swiftCode)).thenReturn(Optional.empty());

        assertThrows(SwiftCodeNotFoundException.class, () -> swiftService.getSwiftDetails(swiftCode));

        verify(swiftRepository).findBySwiftCode(swiftCode);
    }

    @Test
    public void getAllSwiftsByCountryTest() {
        when(swiftRepository.findAllByCountryISO2(countryISO2)).thenReturn(swifts);
        when(swiftRepository.existsByCountryISO2(countryISO2)).thenReturn(true);
        SwiftCodesByCountryDto expectedDto = SwiftCodesByCountryDto.builder()
                .countryISO2(countryISO2)
                .countryName(countryName)
                .swiftCodes(swiftDtos)
                .build();
        when(swiftMapper.toCountrySwiftCodesDto(eq(countryISO2), anyList())).thenReturn(expectedDto);

        SwiftCodesByCountryDto result = swiftService.getAllSwiftsByCountry(countryISO2);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(swiftRepository).findAllByCountryISO2(countryISO2);
        verify(swiftMapper).toCountrySwiftCodesDto(eq(countryISO2), anyList());
    }

    @Test
    public void getAllSwiftsByCountryTest_throwsCountryIsoCodeNotFoundException() {
        when(swiftRepository.existsByCountryISO2(countryISO2)).thenReturn(false);

        assertThrows(CountryIsoCodeNotFoundException.class, () -> swiftService.getAllSwiftsByCountry(countryISO2));
    }

    @Test
    public void saveSwiftCodeTest() {
        when(swiftMapper.toEntity(swiftDto)).thenReturn(swiftEntity);
        when(swiftRepository.save(any(Swift.class))).thenReturn(swiftEntity);
        when(swiftMapper.toDto(swiftEntity)).thenReturn(swiftDto);

        SwiftDto result = swiftService.saveSwiftCode(swiftDto);

        assertNotNull(result);
        assertEquals(swiftCode, result.getSwiftCode());
        verify(swiftValidator).validateFieldsSwiftDto(swiftDto);
        verify(swiftRepository).save(any(Swift.class));
        verify(swiftMapper).toEntity(swiftDto);
        verify(swiftMapper).toDto(swiftEntity);
    }

    @Test
    public void saveSwiftCodeTest_throwsDuplicateSwiftCodeException() {
        String swiftCode = swiftDto.getSwiftCode();

        when(swiftRepository.existsBySwiftCode(swiftCode)).thenReturn(true);

        assertThrows(DuplicateSwiftCodeException.class, () -> swiftService.saveSwiftCode(swiftDto));

        verify(swiftRepository).existsBySwiftCode(swiftCode);
    }

    @Test
    public void saveSwiftCodeTest_throwsDuplicateHeadquarterException() {
        String swiftCode = swiftDto.getSwiftCode();
        swiftDto.setHeadquarter(true);

        when(swiftRepository.existsBySwiftCode(swiftCode)).thenReturn(false);
        when(swiftRepository.countExistingHeadquarters(swiftCode)).thenReturn(1);

        assertThrows(DuplicateHeadquarterException.class, () -> swiftService.saveSwiftCode(swiftDto));

        verify(swiftRepository).existsBySwiftCode(swiftCode);
        verify(swiftRepository).countExistingHeadquarters(swiftCode);
    }

    @Test
    public void deleteSwiftCodeTest() {
        when(swiftRepository.findBySwiftCode(swiftCode)).thenReturn(Optional.of(swiftEntity));

        swiftService.deleteSwiftCode(swiftCode);

        verify(swiftRepository).findBySwiftCode(swiftCode);
        verify(swiftRepository).delete(swiftEntity);
    }

    @Test
    public void deleteSwiftCodeTest_throwsSwiftCodeNotFoundException() {
        when(swiftRepository.findBySwiftCode(swiftCode)).thenReturn(Optional.empty());

        assertThrows(SwiftCodeNotFoundException.class, () -> swiftService.deleteSwiftCode(swiftCode));

        verify(swiftRepository).findBySwiftCode(swiftCode);
    }
}
