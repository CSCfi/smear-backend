package fi.csc.avaa.smear.resource.search;

import fi.csc.avaa.smear.config.Endpoints;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@Tag("integration")
public class TableMetadataResourceTest {

    @Test
    public void findByName_shouldReturnCorrectResult() {
        given()
                .get(Endpoints.SEARCH_TABLES + "/HYY_META")
                .then()
                .statusCode(200)
                .body("id", equalTo(4))
                .body("identifier", equalTo("URN:NBN:fi-fe201207066177"))
                .body("name", equalTo("HYY_META"))
                .body("period", equalTo(1))
                .body("spatialCoverage",
                        equalTo("DCMI-point: name=Hyytiälä; east=24.294795; north=61.847463; elevation=179"))
                .body("stationId", equalTo(2))
                .body("timestamp", equalTo("2020-12-07T19:32:34.000"))
                .body("title", equalTo("Hyytiälä SMEAR II meteorology, gases and soil"));
    }
}
