package fi.csc.avaa.smear.resource.search;

import fi.csc.avaa.smear.config.Endpoints;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;

@QuarkusTest
@Tag("integration")
public class StationResourceTest {

    @Test
    public void getStations_shouldReturnCorrectResult() {
        given()
                .get(Endpoints.SEARCH_STATIONS)
                .then()
                .statusCode(200)
                .body("id", contains(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13))
                .body("name", contains(
                        "Värriö",
                        "Hyytiälä",
                        "Kumpula",
                        "Puijo",
                        "Erottaja",
                        "Torni",
                        "Siikaneva 1",
                        "Siikaneva 2",
                        "Kuivajärvi",
                        "Dome_C",
                        "Nuorttiaapa",
                        "Viikki",
                        "Haltiala"
                ));
    }
}
