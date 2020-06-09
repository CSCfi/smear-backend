package fi.csc.avaa.smear.resource.search;

import fi.csc.avaa.smear.config.Endpoints;
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
    public void searchWithMutuallyExclusiveParameters_shouldReturnParameterViolations() {
        given()
                .when()
                .queryParam("table", "HYY_META")
                .queryParam("variable", "G_sc")
                .queryParam("tablevariable", "HYY_META.Pamb0")
                .get(Endpoints.SEARCH_VARIABLES)
                .then()
                .statusCode(400)
                .body("parameterViolations", hasSize(1))
                .body("parameterViolations.path", contains("variableMetadata.params"));
    }
}
