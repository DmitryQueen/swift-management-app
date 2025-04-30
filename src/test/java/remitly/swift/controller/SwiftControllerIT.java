package remitly.swift.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import remitly.swift.dto.SwiftDto;
import remitly.swift.entity.Swift;
import remitly.swift.repository.SwiftRepository;
import remitly.swift.service.ParsingService;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@SqlGroup({
        @Sql(scripts = "/sql/insert_test_swift_codes.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = "/sql/cleanup_test_swift_codes.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
@Testcontainers
public class SwiftControllerIT {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer(
            "postgres:16-alpine"
    );

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParsingService parsingService;

    @Autowired
    private SwiftRepository swiftRepository;

    private SwiftDto swiftDto;

    @LocalServerPort
    private Integer port;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configurePostgres(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        swiftDto = SwiftDto.builder()
                .address("123 Main St")
                .bankName("Test Bank")
                .countryISO2("US")
                .countryName("United States")
                .headquarter(true)
                .swiftCode("TESTUS33XXX")
                .build();
    }

    @Test
    void addNewSwiftCodeTest() {
        given()
                .contentType(ContentType.JSON)
                .body(swiftDto)
                .when()
                .post("v1/swift-codes")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("message", equalTo("Swift added successfully"));

        Swift result = swiftRepository.findBySwiftCode(swiftDto.getSwiftCode()).get();
        assertEquals(result.getSwiftCode(), swiftDto.getSwiftCode());
    }

    @Test
    void addNewSwiftCodeTest_throwsDuplicateHeadquarterException() {
        swiftDto.setSwiftCode("AAISALTR001");
        swiftDto.setHeadquarter(true);

        given()
                .contentType(ContentType.JSON)
                .body(swiftDto)
                .when()
                .post("v1/swift-codes")
                .then()
                .assertThat()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("message", equalTo("Duplicate headquarter code: " + swiftDto.getSwiftCode()));
    }

    @Test
    void addNewSwiftCodeTest_throwsDuplicateSwiftCodeException() {
        swiftDto.setSwiftCode("AAISALTRXXX");

        given()
                .contentType(ContentType.JSON)
                .body(swiftDto)
                .when()
                .post("v1/swift-codes")
                .then()
                .assertThat()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("message", equalTo("Swift code already exists: " + swiftDto.getSwiftCode()));
    }

    @Test
    void deleteSwiftCodeTest() {
        String swiftCode = "AAISALTRXXX";

        given()
                .when()
                .delete("v1/swift-codes/" + swiftCode)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("message", equalTo("Swift deleted successfully"));
    }

    @Test
    void deleteSwiftCodeTest_throwsSwiftCodeNotFoundException() {
        String invalidSwiftCode = "UNKNOWNCODE";

        given()
                .when()
                .delete("v1/swift-codes/" + invalidSwiftCode)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo("Swift code not found: " + invalidSwiftCode));
    }

    @Test
    void getSwiftDetailsTest_headquarter() {
        String swiftCode = "ABIEBGS1XXX";

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/v1/swift-codes/" + swiftCode)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("swiftCode", equalTo(swiftCode))
                .body("branches[0].swiftCode", equalTo("ABIEBGS1001"))
                .body("branches[0].bankName", equalTo("TestBranch1"))
                .body("branches[1].swiftCode", equalTo("ABIEBGS1002"))
                .body("branches[1].bankName", equalTo("TestBranch2"));
    }

    @Test
    void getSwiftDetailsTest_branch() {
        String swiftCode = "ABIEBGS1001";

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/v1/swift-codes/" + swiftCode)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("swiftCode", equalTo(swiftCode))
                .body("$", not(hasKey("branches")));
    }

    @Test
    void getAllSwiftsByCountryTest() {

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/v1/swift-codes/country/BG")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("swiftCodes[0].countryISO2", equalTo("BG"))
                .body("swiftCodes[1].countryISO2", equalTo("BG"))
                .body("swiftCodes[2].countryISO2", equalTo("BG"));
    }

    @Test
    void shouldGetAllSwiftsByCountryTest_throwsCountryIsoCodeNotFoundException() {
        String isoCode = "GG";

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/v1/swift-codes/country/" + isoCode)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo("There is no requested ISO2 code in our database: " + isoCode));
    }

}
