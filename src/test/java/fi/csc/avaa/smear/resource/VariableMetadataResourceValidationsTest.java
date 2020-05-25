package fi.csc.avaa.smear.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
@Tag("integration")
public class VariableMetadataResourceValidationsTest {

    @Test
    public void searchWithNoParameters_shouldReturnParameterViolations() {
        given()
                .when()
                .get(Endpoints.VARIABLE_METADATA + "/search")
                .then()
                .statusCode(400)
                .body("parameterViolations", hasSize(1))
                .body("parameterViolations.path", contains("variableMetadata.params"));
    }

    @Test
    public void searchWithMutuallyExclusiveParameters_shouldReturnParameterViolations() {
        given()
                .when()
                .queryParam("table", "HYY_META")
                .queryParam("variable", "G_sc")
                .queryParam("source", "hukse")
                .queryParam("tablevariable", "HYY_META.Pamb0")
                .get(Endpoints.VARIABLE_METADATA + "/search")
                .then()
                .statusCode(400)
                .body("parameterViolations", hasSize(1))
                .body("parameterViolations.path", contains("variableMetadata.params"));
    }
}
