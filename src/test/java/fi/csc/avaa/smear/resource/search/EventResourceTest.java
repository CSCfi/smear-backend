package fi.csc.avaa.smear.resource.search;

import fi.csc.avaa.smear.config.Endpoints;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;

@QuarkusTest
@Tag("integration")
public class EventResourceTest {

    @Test
    public void findByVariable_shouldReturnCorrectResult() {
        given()
                .queryParam("tablevariable", "HYY_META.VOC_M87_84")
                .when()
                .get(Endpoints.SEARCH_EVENTS)
                .then()
                .statusCode(200);
    }
}
