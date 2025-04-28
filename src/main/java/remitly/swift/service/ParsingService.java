package remitly.swift.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import remitly.swift.dto.SwiftDto;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static remitly.swift.utils.CsvHeaders.ADDRESS;
import static remitly.swift.utils.CsvHeaders.BANK_NAME;
import static remitly.swift.utils.CsvHeaders.COUNTRY_ISO2;
import static remitly.swift.utils.CsvHeaders.COUNTRY_NAME;
import static remitly.swift.utils.CsvHeaders.SWIFT_CODE;

@Slf4j
@RequiredArgsConstructor
@Service
public class ParsingService {
    private final SwiftService swiftService;

    @Value("${parser.file-path}")
    private String filePath;

    @Value("${parser.filename}")
    private String fileName;

    @PostConstruct
    public void parseCSVFile() throws IOException {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        String fullPath = filePath + fileName;

        try (InputStream csvDataStream = getClass()
                .getClassLoader()
                .getResourceAsStream(fullPath)) {

            if (csvDataStream == null) {
                throw new FileNotFoundException("File " + fileName + " not found in resources.");
            }

            MappingIterator<Map<String, String>> rows = mapper
                    .readerFor(Map.class)
                    .with(schema)
                    .readValues(csvDataStream);

            rows.forEachRemaining(this::processRow);
        }

        log.info("Parsing of file is completed, file: {}", fullPath);
    }

    private void processRow(Map<String, String> row) {
        SwiftDto swift = SwiftDto.builder()
                .address(safeTrim(row.get(ADDRESS)))
                .bankName(safeTrim(row.get(BANK_NAME)))
                .countryISO2(safeTrim(row.get(COUNTRY_ISO2)))
                .countryName(safeTrim(row.get(COUNTRY_NAME)))
                .swiftCode(safeTrim(row.get(SWIFT_CODE)))
                .build();

        try {
            swiftService.saveSwiftCode(swift);
        }
        catch (Exception e) {
            log.warn("Skipping invalid row due to error: {}, Row: {}", e.getMessage(), row);
        }
    }

    private String safeTrim(String value) {
        return value == null ? null : value.trim();
    }

}
