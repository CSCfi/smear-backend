package fi.csc.avaa.smear.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@Tag("integration")
public class TimeSeriesResourcePlaintextTest {

    @Test
    public void fetchTimeSeriesAsCSV_shouldReturnCorrectResults() {
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
                .queryParam("quality", "ANY")
                .queryParam("aggregation", "NONE")
                .get(Endpoints.TIMESERIES + "/csv")
                .then()
                .statusCode(200)
                .body(equalTo(
                        "Year,Month,Day,Hour,Minute,Second,HYY_AERO.scat_t,HYY_META.Pamb0\n" +
                                "2020,04,12,23,55,00,300.75,967.99\n" +
                                "2020,04,12,23,56,00,null,967.85\n" +
                                "2020,04,12,23,57,00,300.717,967.97\n" +
                                "2020,04,12,23,58,00,300.65,967.84\n" +
                                "2020,04,12,23,59,00,300.65,967.86\n"
                ));
    }

    @Test
    public void fetchTimeSeriesAsTSV_shouldReturnCorrectResults() {
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
                .queryParam("quality", "ANY")
                .queryParam("aggregation", "NONE")
                .get(Endpoints.TIMESERIES + "/tsv")
                .then()
                .statusCode(200)
                .body(equalTo(
                        "Year\tMonth\tDay\tHour\tMinute\tSecond\tHYY_AERO.scat_t\tHYY_META.Pamb0\n" +
                                "2020\t04\t12\t23\t55\t00\t300.75\t967.99\n" +
                                "2020\t04\t12\t23\t56\t00\tnull\t967.85\n" +
                                "2020\t04\t12\t23\t57\t00\t300.717\t967.97\n" +
                                "2020\t04\t12\t23\t58\t00\t300.65\t967.84\n" +
                                "2020\t04\t12\t23\t59\t00\t300.65\t967.86\n"
                ));
    }
}