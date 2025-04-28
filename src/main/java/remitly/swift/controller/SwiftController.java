package remitly.swift.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import remitly.swift.dto.SwiftCodesByCountryDto;
import remitly.swift.dto.MessageResponseDto;
import remitly.swift.dto.SwiftDto;
import remitly.swift.service.SwiftService;

@RequiredArgsConstructor
@Validated
@RequestMapping("v1/swift-codes")
@RestController
public class SwiftController {
    private final SwiftService swiftService;

    @GetMapping("/{swiftCode}")
    public ResponseEntity<SwiftDto> getSwiftDetails(@PathVariable @Size(min = 11, max = 11, message = "Swift code size must have 11 characters") String swiftCode) {
        SwiftDto retrieved = swiftService.getSwiftDetails(swiftCode);
        return ResponseEntity.ok().body(retrieved);
    }

    @GetMapping("/country/{countryISO2}")
    public ResponseEntity<SwiftCodesByCountryDto> getAllSwiftsByCountry(@PathVariable @Size(min = 2, max = 2, message = "Country ISO2 code must contain 2 characters") String countryISO2) {
        SwiftCodesByCountryDto swiftDtos = swiftService.getAllSwiftsByCountry(countryISO2);
        return ResponseEntity.ok(swiftDtos);
    }

    @PostMapping
    public ResponseEntity<MessageResponseDto> addNewSwiftCode(@RequestBody @Valid SwiftDto swiftDto) {
        swiftService.saveSwiftCode(swiftDto);
        return ResponseEntity.ok()
                .body(new MessageResponseDto("Swift added successfully"));
    }

    @DeleteMapping("/{swiftCode}")
    public ResponseEntity<MessageResponseDto> deleteSwiftCode(@PathVariable @Size(min = 11, max = 11, message = "Swift code size must have 11 characters") String swiftCode) {
        swiftService.deleteSwiftCode(swiftCode);
        return ResponseEntity.ok()
                .body(new MessageResponseDto("Swift deleted successfully"));
    }
}
