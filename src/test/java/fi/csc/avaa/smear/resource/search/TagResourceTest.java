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
    public void search_shouldReturnCorrectResult() {
        given()
                .queryParam("tablevariable", "HYY_META.Pamb0")
                .queryParam("tablevariable", "HYY_AERO.cn")
                .when()
                .get(Endpoints.SEARCH_TAGS)
                .then()
                .statusCode(200)
                .body("", hasSize(3))
                .body("displayKeyword", containsInAnyOrder("", "meteorologia", "aerosolifysiikka"))
                .body("id", containsInAnyOrder(37, 77, 80))
                .body("name", containsInAnyOrder("surface_air_pressure", "p5394", "p15192"))
                .body("vocabulary", containsInAnyOrder(
                        "http://cfconventions.org/Data/cf-standard-names/29/build/cf-standard-name-table.html#",
                        "http://www.yso.fi/onto/yso/",
                        "http://www.yso.fi/onto/yso/"
                ));
    }
}
