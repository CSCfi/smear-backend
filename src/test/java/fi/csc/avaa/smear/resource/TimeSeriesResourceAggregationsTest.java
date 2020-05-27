package fi.csc.avaa.smear.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
@Tag("integration")
public class TimeSeriesResourceAggregationsTest {

    @Test
    public void arithmeticAggregation_shouldReturnCorrectResults() {
        assertAggregation("ARITHMETIC", contains(
                991.0763333333331f,
                991.1080000000001f,
                991.1156666666668f,
                991.1286666666668f,
                991.1150000000002f,
                991.0666666666665f
        ));
    }

    @Test
    public void geometricAggregation_shouldReturnCorrectResults() {
        assertAggregation("GEOMETRIC", contains(
                991.0763316836986f,
                991.1079980479669f,
                991.1156652215818f,
                991.1286647875775f,
                991.1149981140259f,
                991.0666638941008f
        ));
    }

    @Test
    public void sumAggregation_shouldReturnCorrectResults() {
        assertAggregation("SUM", contains(
                29732.289999999994f,
                29733.24f,
                29733.470000000005f,
                29733.860000000004f,
                29733.450000000008f,
                29731.999999999996f
        ));
    }

    @Test
    public void minAggregation_shouldReturnCorrectResults() {
        assertAggregation("MIN", contains(
                990.99f,
                990.98f,
                991.04f,
                991.02f,
                990.97f,
                990.91f
        ));
    }

    @Test
    public void maxAggregation_shouldReturnCorrectResults() {
        assertAggregation("MAX", contains(
                991.21f,
                991.24f,
                991.26f,
                991.28f,
                991.22f,
                991.19f
        ));
    }

    private void assertAggregation(String aggregation, Matcher<Iterable<? extends Float>> containsExpectedValues) {
        given()
                .when()
                .queryParam("tablevariable", "HYY_META.Pamb0")
                .queryParam("from", "2016-06-12T00:00:00.000")
                .queryParam("to", "2016-06-12T03:00:00.000")
                .queryParam("aggregation", aggregation)
                .get(Endpoints.TIMESERIES)
                .then()
                .statusCode(200)
                .body("aggregation", equalTo(aggregation))
                .body("aggregationInterval", equalTo(30))
                .body("data", hasSize(6))
                .body("data.samptime", contains(
                        "2016-06-12T00:00:00.000",
                        "2016-06-12T00:30:00.000",
                        "2016-06-12T01:00:00.000",
                        "2016-06-12T01:30:00.000",
                        "2016-06-12T02:00:00.000",
                        "2016-06-12T02:30:00.000"
                ))
                .body("data.'" + "HYY_META.Pamb0" + "'", containsExpectedValues);
    }
}
