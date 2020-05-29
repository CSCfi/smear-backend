package fi.csc.avaa.smear.resource.search;

import fi.csc.avaa.smear.config.Endpoints;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
@Tag("integration")
public class TagResourceTest {

    @Test
    public void findByVariable_shouldReturnCorrectResult() {
        given()
                .queryParam("variable", "Pamb0")
                .queryParam("variable", "dp")
                .when()
                .get(Endpoints.SEARCH_TAGS)
                .then()
                .statusCode(200)
                .body("", hasSize(5))
                .body("displayKeyword",
                        containsInAnyOrder("ilmankosteus", "ilmankosteus", "", "meteorologia", "meteorologia"))
                .body("id", containsInAnyOrder(2, 2, 37, 77, 77))
                .body("name",
                        containsInAnyOrder("relative_humidity", "p6452", "surface_air_pressure", "p5394", "p5394"))
                .body("vocabulary", containsInAnyOrder(
                        "http://cfconventions.org/Data/cf-standard-names/29/build/cf-standard-name-table.html#",
                        "http://www.yso.fi/onto/yso/",
                        "http://cfconventions.org/Data/cf-standard-names/29/build/cf-standard-name-table.html#",
                        "http://www.yso.fi/onto/yso/",
                        "http://www.yso.fi/onto/yso/"
                ));
    }
}
