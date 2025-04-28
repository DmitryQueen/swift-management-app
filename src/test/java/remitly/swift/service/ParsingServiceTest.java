package remitly.swift.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import remitly.swift.dto.SwiftDto;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ParsingServiceTest {

    @Mock
    private SwiftService swiftService;

    @InjectMocks
    private ParsingService parsingService;

    @Captor
    private ArgumentCaptor<SwiftDto> swiftDtoCaptor;

    private String filePath = "test-data/";
    private String fileName = "test_swift_codes.csv";
    private String invalidFilePath = "invalid-path/";
    private String invalidFileName = "non_existing_file.csv";

    @BeforeEach
    void setup() throws Exception {
        setField(parsingService, "filePath", filePath);
        setField(parsingService, "fileName", fileName);
    }

    @Test
    public void parseCSVFileTest() throws IOException {
        List<String> expectedCountriesIso2 = List.of("PL", "DE");

        parsingService.parseCSVFile();

        verify(swiftService, times(2)).saveSwiftCode(swiftDtoCaptor.capture());

        List<SwiftDto> capturedSwifts = swiftDtoCaptor.getAllValues();
        List<String> actualCountriesIso2 = capturedSwifts
                .stream()
                .map(SwiftDto::getCountryISO2)
                .toList();

        assertEquals(expectedCountriesIso2, actualCountriesIso2);
    }

    @Test
    public void parseCSVFile_throwsFileNotFoundException() throws Exception {
        setField(parsingService, "filePath", invalidFilePath);
        setField(parsingService, "fileName", invalidFileName);

        assertThrows(FileNotFoundException.class, () -> parsingService.parseCSVFile());
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
