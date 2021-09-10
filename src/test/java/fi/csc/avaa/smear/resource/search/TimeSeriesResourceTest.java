package fi.csc.avaa.smear.resource.search;

import fi.csc.avaa.smear.config.Endpoints;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@Tag("integration")
public class TimeSeriesResourceTest {

    @Test
    public void fetchTimeSeries_shouldReturnCorrectResults() {
        String startTime = "2016-02-12T00:00:00.000";
        String endTime = "2016-02-12T00:06:00.000";
        String tableVariable = "HYY_META.Pamb0";
        given()
                .when()
                .queryParam("tablevariable", tableVariable)
                .queryParam("from", startTime)
                .queryParam("to", endTime)
                .get(Endpoints.SEARCH_TIMESERIES)
                .then()
                .statusCode(200)
                .body("startTime", equalTo(startTime))
                .body("endTime", equalTo(endTime))
                .body("recordCount", is(6))
                .body("aggregation", equalTo("NONE"))
                .body("aggregationInterval", blankOrNullString())
                .body("columns", contains(tableVariable))
                .body("data.samptime", contains(
                        "2016-02-12T00:00:00.000",
                        "2016-02-12T00:01:00.000",
                        "2016-02-12T00:02:00.000",
                        "2016-02-12T00:03:00.000",
                        "2016-02-12T00:04:00.000",
                        "2016-02-12T00:05:00.000"
                ))
                .body("data.'" + tableVariable + "'",
                        contains(973.71f, 973.82f, 973.86f, 973.92f, 973.81f, 973.82f));
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
                .get(Endpoints.SEARCH_TIMESERIES)
                .then()
                .statusCode(200)
                .body("startTime", equalTo(startTime))
                .body("endTime", equalTo(endTime))
                .body("recordCount", is(6))
                .body("aggregation", equalTo("NONE"))
                .body("aggregationInterval", blankOrNullString())
                .body("columns", contains(tableVariable))
                .body("data.samptime", contains(
                        "2016-06-12T00:00:00.000",
                        "2016-06-12T00:01:00.000",
                        "2016-06-12T00:02:00.000",
                        "2016-06-12T00:03:00.000",
                        "2016-06-12T00:04:00.000",
                        "2016-06-12T00:05:00.000"
                ))
                .body("data.'" + tableVariable + "'",
                        contains(991.04f, 990.99f, 991.12f, 991.1f, 991.05f, 991.01f));
    }

    @Test
    public void fetchTimeSeriesFromMultipleTables_shouldReturnCorrectResults() {
        String startTime = "2020-04-12T23:55:00.000";
        String endTime = "2020-04-13T00:00:00.000";
        String hyyMetaPamb0 = "HYY_META.Pamb0";
        String hyyAeroScatT = "HYY_AERO.scat_t";
        given()
                .when()
                .queryParam("tablevariable", hyyMetaPamb0)
                .queryParam("tablevariable", hyyAeroScatT)
                .queryParam("from", startTime)
                .queryParam("to", endTime)
                .get(Endpoints.SEARCH_TIMESERIES)
                .then()
                .statusCode(200)
                .body("startTime", equalTo(startTime))
                .body("endTime", equalTo(endTime))
                .body("recordCount", is(5))
                .body("aggregation", equalTo("NONE"))
                .body("aggregationInterval", blankOrNullString())
                .body("columns", contains(hyyAeroScatT, hyyMetaPamb0))
                .body("data.samptime", contains(
                        "2020-04-12T23:55:00.000",
                        "2020-04-12T23:56:00.000",
                        "2020-04-12T23:57:00.000",
                        "2020-04-12T23:58:00.000",
                        "2020-04-12T23:59:00.000"
                ))
                .body("data.'" + hyyMetaPamb0 + "'",
                        contains(967.99f, 967.85f, 967.97f, 967.84f, 967.86f))
                .body("data.'" + hyyAeroScatT + "'",
                        contains(300.75f, null, 300.717f, 300.65f, 300.65f));
    }

    @Test
    public void fetchTimeSeriesFromHyySlow_shouldReturnCorrectResults() {
        String startTime = "2012-12-29T00:00:00.000";
        String endTime = "2012-12-31T00:00:00.000";
        String tablevariable = "HYY_SLOW.dbh_tree1";
        given()
                .when()
                .queryParam("tablevariable", tablevariable)
                .queryParam("from", startTime)
                .queryParam("to", endTime)
                .get(Endpoints.SEARCH_TIMESERIES)
                .then()
                .statusCode(200)
                .body("startTime", equalTo(startTime))
                .body("endTime", equalTo(endTime))
                .body("recordCount", is(2))
                .body("aggregation", equalTo("NONE"))
                .body("aggregationInterval", blankOrNullString())
                .body("columns", contains(tablevariable))
                .body("data.samptime", contains(
                        "2012-12-29T00:00:00.000",
                        "2012-12-30T00:00:00.000"
                ))
                .body("data.'" + tablevariable + "'",
                        contains(12.812322f, 12.814073f));
    }

    @Test
    public void fetchTimeSeriesFromHyyTree_shouldReturnCorrectResults() {
        String startTime = "2012-06-01T00:00:00.000";
        String endTime = "2012-06-01T04:00:00.000";
        String tablevariable = "HYY_TREE.PARcuv_leaf";
        String cuvNoColumn = "HYY_TREE.cuv_no";
        given()
                .when()
                .queryParam("tablevariable", tablevariable)
                .queryParam("from", startTime)
                .queryParam("to", endTime)
                .queryParam("cuv_no", 186)
                .get(Endpoints.SEARCH_TIMESERIES)
                .then()
                .statusCode(200)
                .body("startTime", equalTo(startTime))
                .body("endTime", equalTo(endTime))
                .body("recordCount", is(6))
                .body("aggregation", equalTo("NONE"))
                .body("aggregationInterval", blankOrNullString())
                .body("columns", contains(cuvNoColumn, tablevariable))
                .body("data.samptime", contains(
                        "2012-06-01T00:00:00.000",
                        "2012-06-01T01:41:00.000",
                        "2012-06-01T01:59:00.000",
                        "2012-06-01T02:17:00.000",
                        "2012-06-01T02:36:00.000",
                        "2012-06-01T03:00:00.000"
                ))
                .body("data.'" + cuvNoColumn + "'",
                        contains(186, 186, 186, 186, 186, 186))
                .body("data.'" + tablevariable + "'",
                        contains(1.5125f, 1.2375f, 0.9625f, 1.375f, 0.6875f, 3.1675f));
    }
}
