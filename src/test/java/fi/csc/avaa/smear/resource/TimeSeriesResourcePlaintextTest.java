package fi.csc.avaa.smear.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static fi.csc.avaa.smear.util.TestUtils.expectedResponse;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@Tag("integration")
public class TimeSeriesResourcePlaintextTest {

    @Test
    public void fetchTimeSeriesAsCSV_shouldReturnCorrectResults() {
        RestAssured.registerParser("text/csv", Parser.TEXT);
        String expected = expectedResponse("TimeSeries_search.csv");
        given()
                .when()
                .queryParam("tablevariable", "HYY_META.Pamb0")
                .queryParam("tablevariable", "HYY_AERO.scat_t")
                .queryParam("from", "2020-04-12T23:55:00.000")
                .queryParam("to", "2020-04-13T00:00:00.000")
                .queryParam("quality", "ANY")
                .queryParam("aggregation", "NONE")
                .get(Endpoints.TIMESERIES + "/csv")
                .then()
                .statusCode(200)
                .body(equalTo(expected));
    }

    @Test
    public void fetchTimeSeriesAsTSV_shouldReturnCorrectResults() {
        RestAssured.registerParser("text/plain", Parser.TEXT);
        String expected = expectedResponse("TimeSeries_search.tsv");
        given()
                .when()
                .queryParam("tablevariable", "HYY_META.Pamb0")
                .queryParam("tablevariable", "HYY_AERO.scat_t")
                .queryParam("from", "2020-04-12T23:55:00.000")
                .queryParam("to", "2020-04-13T00:00:00.000")
                .queryParam("quality", "ANY")
                .queryParam("aggregation", "NONE")
                .get(Endpoints.TIMESERIES + "/tsv")
                .then()
                .statusCode(200)
                .body(equalTo(expected));
    }
}