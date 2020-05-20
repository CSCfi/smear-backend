package fi.csc.avaa.smear.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class TimeSeriesResourceTest {

    @Test
    public void fetchTimeSeries_shouldReturnCorrectValues() {
        String startTime = "2016-02-12T00:00:00.000";
        String endTime = "2016-02-12T00:06:00.000";
        String tableVariable = "HYY_META.Pamb0";
        given()
                .when()
                .queryParam("tablevariable", tableVariable)
                .queryParam("from", startTime)
                .queryParam("to", endTime)
                .queryParam("quality", "ANY")
                .queryParam("aggregation", "NONE")
                .get(Endpoints.TIMESERIES)
                .then()
                .statusCode(200)
                .body("startTime", equalTo(startTime))
                .body("endTime", equalTo(endTime))
                .body("recordCount", is(6))
                .body("aggregation", equalTo("NONE"))
                .body("aggregationInterval", blankOrNullString())
                .body("columns", hasSize(1))
                .body("columns", hasItem(tableVariable))
                .body("data", hasSize(6))
                .body("data.samptime", hasItems(
                        "2016-02-12T00:00:00.000",
                        "2016-02-12T00:01:00.000",
                        "2016-02-12T00:02:00.000",
                        "2016-02-12T00:03:00.000",
                        "2016-02-12T00:04:00.000",
                        "2016-02-12T00:05:00.000"
                ))
                .body("data.'" + tableVariable + "'",
                        hasItems(973.71f, 973.82f, 973.86f, 973.92f, 973.81f, 973.82f));
    }

    @Test
    public void fetchTimeSeriesSummerTime_shouldReturnCorrectResults() {
        String startTime = "2016-06-12T00:00:00.000";
        String endTime = "2016-06-12T00:06:00.000";
        String tableVariable = "HYY_META.Pamb0";
        given()
                .when()
                .queryParam("tablevariable", tableVariable)
                .queryParam("from", startTime)
                .queryParam("to", endTime)
                .queryParam("quality", "ANY")
                .queryParam("aggregation", "NONE")
                .get(Endpoints.TIMESERIES)
                .then()
                .statusCode(200)
                .body("startTime", equalTo(startTime))
                .body("endTime", equalTo(endTime))
                .body("recordCount", is(6))
                .body("aggregation", equalTo("NONE"))
                .body("aggregationInterval", blankOrNullString())
                .body("columns", hasSize(1))
                .body("columns", hasItem(tableVariable))
                .body("data", hasSize(6))
                .body("data.samptime", hasItems(
                        "2016-06-12T00:00:00.000",
                        "2016-06-12T00:01:00.000",
                        "2016-06-12T00:02:00.000",
                        "2016-06-12T00:03:00.000",
                        "2016-06-12T00:04:00.000",
                        "2016-06-12T00:05:00.000"
                ))
                .body("data.'" + tableVariable + "'",
                        hasItems(991.04f, 990.99f, 991.12f, 991.1f, 991.05f, 991.01f));
    }
}
