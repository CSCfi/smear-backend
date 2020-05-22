package fi.csc.avaa.smear.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@Tag("integration")
public class TimeSeriesResourceValidationsTest {

    @Test
    public void noParametersGiven_shouldReturnErrorMessagesForDateTimeRangeAndTableVariable() {
        given()
                .when()
                .get(Endpoints.TIMESERIES)
                .then()
                .statusCode(400)
                .body("parameterViolations", hasSize(3))
                .body("parameterViolations.path", containsInAnyOrder(
                        "timeSeries.params.from",
                        "timeSeries.params.to",
                        "timeSeries.params.tablevariable"
                ));
    }

    @Test
    public void bothTableAndVariableAndTablevariableParametersGiven_shouldReturnErrorMessage() {
        given()
                .when()
                .queryParam("tablevariable", "HYY_META.Pamb0")
                .queryParam("table", "HYY_AERO")
                .queryParam("variable", "HYY_AERO.scat_t")
                .queryParam("from", "2016-02-12T00:00:00.000")
                .queryParam("to", "2016-02-12T00:06:00.000")
                .get(Endpoints.TIMESERIES)
                .then()
                .statusCode(400)
                .body("parameterViolations", hasSize(1))
                .body("parameterViolations.path", contains("timeSeries.params.tablevariable"));
    }

    @Test
    public void invalidAggregationGiven_shouldReturnErrorMessage() {
        given()
                .when()
                .queryParam("tablevariable", "HYY_META.Pamb0")
                .queryParam("from", "2016-02-12T00:00:00.000")
                .queryParam("to", "2016-02-12T00:06:00.000")
                .queryParam("aggregation", "dsafasdfasd")
                .get(Endpoints.TIMESERIES)
                .then()
                .statusCode(400)
                .body("parameterViolations", hasSize(1))
                .body("parameterViolations.path", contains("timeSeries.params.aggregation"));
    }

    @Test
    public void invalidQualityGiven_shouldReturnErrorMessage() {
        given()
                .when()
                .queryParam("tablevariable", "HYY_META.Pamb0")
                .queryParam("from", "2016-02-12T00:00:00.000")
                .queryParam("to", "2016-02-12T00:06:00.000")
                .queryParam("quality", "dsafasdfasd")
                .get(Endpoints.TIMESERIES)
                .then()
                .statusCode(400)
                .body("parameterViolations", hasSize(1))
                .body("parameterViolations.path", contains("timeSeries.params.quality"));
    }

    @Test
    public void invalidAggregationIntervalGiven_shouldReturnErrorMessage() {
        given()
                .when()
                .queryParam("tablevariable", "HYY_META.Pamb0")
                .queryParam("from", "2016-02-12T00:00:00.000")
                .queryParam("to", "2016-02-12T00:06:00.000")
                .queryParam("aggregation", "GEOMETRIC")
                .queryParam("interval", 10000)
                .get(Endpoints.TIMESERIES)
                .then()
                .statusCode(400)
                .body("parameterViolations", hasSize(1))
                .body("parameterViolations.path", contains("timeSeries.params.interval"));
    }
}
