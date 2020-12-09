package fi.csc.avaa.smear.resource.search;

import fi.csc.avaa.smear.config.Endpoints;
import fi.csc.avaa.smear.parameter.Aggregation;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
@Tag("integration")
public class TimeSeriesResourceValidationsTest {

    @Test
    public void noParametersGiven_shouldReturnParameterViolationsForDateTimeRangeAndTableVariable() {
        given()
                .when()
                .get(Endpoints.SEARCH_TIMESERIES)
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
    public void nonExistentTableGiven_shouldReturnParameterViolation() {
        given()
                .when()
                .queryParam("tablevariable", "bat.man")
                .queryParam("from", "2016-02-12T00:00:00.000")
                .queryParam("to", "2016-02-12T00:06:00.000")
                .get(Endpoints.SEARCH_TIMESERIES)
                .then()
                .statusCode(400)
                .body("parameterViolations", hasSize(1))
                .body("parameterViolations.path", contains("timeSeries.params.tablevariable"));
    }

    @Test
    public void invalidAggregationGiven_shouldReturnParameterViolation() {
        given()
                .when()
                .queryParam("tablevariable", "HYY_META.Pamb0")
                .queryParam("from", "2016-02-12T00:00:00.000")
                .queryParam("to", "2016-02-12T00:06:00.000")
                .queryParam("aggregation", "dsafasdfasd")
                .get(Endpoints.SEARCH_TIMESERIES)
                .then()
                .statusCode(400)
                .body("parameterViolations", hasSize(1))
                .body("parameterViolations.path", contains("timeSeries.params.aggregation"));
    }

    @Test
    public void invalidQualityGiven_shouldReturnParameterViolation() {
        given()
                .when()
                .queryParam("tablevariable", "HYY_META.Pamb0")
                .queryParam("from", "2016-02-12T00:00:00.000")
                .queryParam("to", "2016-02-12T00:06:00.000")
                .queryParam("quality", "dsafasdfasd")
                .get(Endpoints.SEARCH_TIMESERIES)
                .then()
                .statusCode(400)
                .body("parameterViolations", hasSize(1))
                .body("parameterViolations.path", contains("timeSeries.params.quality"));
    }

    @Test
    public void invalidAggregationIntervalGiven_shouldReturnParameterViolation() {
        given()
                .when()
                .queryParam("tablevariable", "HYY_META.Pamb0")
                .queryParam("from", "2016-02-12T00:00:00.000")
                .queryParam("to", "2016-02-12T00:06:00.000")
                .queryParam("aggregation", "GEOMETRIC")
                .queryParam("interval", 0)
                .get(Endpoints.SEARCH_TIMESERIES)
                .then()
                .statusCode(400)
                .body("parameterViolations", hasSize(1))
                .body("parameterViolations.path", contains("timeSeries.params.interval"));

        given()
                .when()
                .queryParam("tablevariable", "HYY_META.Pamb0")
                .queryParam("from", "2016-02-12T00:00:00.000")
                .queryParam("to", "2016-02-12T00:06:00.000")
                .queryParam("aggregation", "GEOMETRIC")
                .queryParam("interval", 61)
                .get(Endpoints.SEARCH_TIMESERIES)
                .then()
                .statusCode(400)
                .body("parameterViolations", hasSize(1))
                .body("parameterViolations.path", contains("timeSeries.params.interval"));
    }

    @Test
    public void fetchFromHyyTreeTableWithoutCuvNo_shouldNotReturnParameterViolation() {
        given()
                .when()
                .queryParam("tablevariable", "HYY_TREE.PARcuv_leaf")
                .queryParam("from", "2016-02-12T00:00:00.000")
                .queryParam("to", "2016-02-12T00:06:00.000")
                .get(Endpoints.SEARCH_TIMESERIES)
                .then()
                .statusCode(200);
    }

    @Test
    public void fetchFromHyyTablesWithManualAggregation_shouldReturnParameterViolation() {
        Arrays.asList(Aggregation.MEDIAN, Aggregation.CIRCULAR)
                .forEach(aggregation -> {
                    given()
                            .when()
                            .queryParam("tablevariable", "HYY_TREE.PARcuv_leaf")
                            .queryParam("from", "2016-02-12T00:00:00.000")
                            .queryParam("to", "2016-02-12T00:06:00.000")
                            .queryParam("cuv_no", 186)
                            .queryParam("aggregation", aggregation)
                            .get(Endpoints.SEARCH_TIMESERIES)
                            .then()
                            .statusCode(400)
                            .body("parameterViolations", hasSize(1))
                            .body("parameterViolations.path", contains("timeSeries.params.aggregation"));

                    given()
                            .when()
                            .queryParam("tablevariable", "HYY_SLOW.dbh_tree1")
                            .queryParam("from", "2016-02-12T00:00:00.000")
                            .queryParam("to", "2016-02-12T00:06:00.000")
                            .queryParam("aggregation", aggregation)
                            .get(Endpoints.SEARCH_TIMESERIES)
                            .then()
                            .statusCode(400)
                            .body("parameterViolations", hasSize(1))
                            .body("parameterViolations.path", contains("timeSeries.params.aggregation"));
                });
    }
}
