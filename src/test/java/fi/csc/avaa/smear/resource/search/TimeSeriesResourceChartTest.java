package fi.csc.avaa.smear.resource.search;

import fi.csc.avaa.smear.config.Endpoints;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
@Tag("integration")
public class TimeSeriesResourceChartTest {

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
                .get(Endpoints.SEARCH_TIMESERIES + "/chart")
                .then()
                .statusCode(200)
                .body(escape(tableVariable), hasSize(6))
                .body(escape(tableVariable), hasItems(
                        hasItems(1455235200, 973.71f),
                        hasItems(1455235260, 973.82f),
                        hasItems(1455235320, 973.86f),
                        hasItems(1455235380, 973.92f),
                        hasItems(1455235440, 973.81f),
                        hasItems(1455235500, 973.82f)
                ));
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
                .get(Endpoints.SEARCH_TIMESERIES + "/chart")
                .then()
                .statusCode(200)
                .body(escape(tableVariable), hasSize(6))
                .body(escape(tableVariable), hasItems(
                        hasItems(1465689600, 991.04f),
                        hasItems(1465689660, 990.99f),
                        hasItems(1465689720, 991.12f),
                        hasItems(1465689780, 991.1f),
                        hasItems(1465689840, 991.05f),
                        hasItems(1465689900, 991.01f)
                ));
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
                .get(Endpoints.SEARCH_TIMESERIES + "/chart")
                .then()
                .statusCode(200)
                .body(escape(hyyMetaPamb0), hasSize(5))
                .body(escape(hyyMetaPamb0), hasItems(
                        hasItems(1586735700, 967.99f),
                        hasItems(1586735760, 967.85f),
                        hasItems(1586735820, 967.97f),
                        hasItems(1586735880, 967.84f),
                        hasItems(1586735940, 967.86f)
                ))
                .body(escape(hyyAeroScatT), hasSize(4))
                .body(escape(hyyAeroScatT), hasItems(
                        hasItems(1586735700, 300.75f),
                        hasItems(1586735820, 300.717f),
                        hasItems(1586735880, 300.65f),
                        hasItems(1586735940, 300.65f)
                ));
    }

    @Test
    public void fetchTimeSeriesFromHyySlow_shouldReturnCorrectResults() {
        String startTime = "2016-02-10T00:00:00.000";
        String endTime = "2016-02-20T00:00:00.000";
        String tableVariable = "HYY_SLOW.SD_PIT050";
        given()
                .when()
                .queryParam("tablevariable", tableVariable)
                .queryParam("from", startTime)
                .queryParam("to", endTime)
                .get(Endpoints.SEARCH_TIMESERIES + "/chart")
                .then()
                .statusCode(200)
                .body(escape(tableVariable), hasSize(2))
                .body(escape(tableVariable), hasItems(
                        hasItems(1455235200, 25.0f),
                        hasItems(1455840000, 27.0f)
                ));
    }

    @Test
    public void fetchTimeSeriesFromHyyTree_shouldReturnCorrectResults() {
        String startTime = "2012-06-01T00:00:00.000";
        String endTime = "2012-06-01T04:00:00.000";
        String tableVariable = "HYY_TREE.PARcuv_leaf";
        given()
                .when()
                .queryParam("tablevariable", tableVariable)
                .queryParam("from", startTime)
                .queryParam("to", endTime)
                .queryParam("cuv_no", 186)
                .get(Endpoints.SEARCH_TIMESERIES + "/chart")
                .then()
                .statusCode(200)
                .body(escape(tableVariable), hasSize(6))
                .body(escape(tableVariable), hasItems(
                        hasItems(1338508800, 1.5125f),
                        hasItems(1338514860, 1.2375f),
                        hasItems(1338515940, 0.9625f),
                        hasItems(1338517020, 1.375f),
                        hasItems(1338518160, 0.6875f),
                        hasItems(1338519600, 3.1675f)
                ));
    }

    private static String escape(String tableVariable) {
        return String.format("'%s'", tableVariable);
    }
}
