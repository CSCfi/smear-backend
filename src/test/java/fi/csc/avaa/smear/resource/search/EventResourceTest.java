package fi.csc.avaa.smear.resource.search;

import fi.csc.avaa.smear.config.Endpoints;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
@Tag("integration")
public class EventResourceTest {

    @Test
    public void findByVariable_shouldReturnCorrectResult() {
        given()
                .queryParam("variable", "VOC_M81_1250")
                .when()
                .get(Endpoints.SEARCH_EVENTS)
                .then()
                .statusCode(200)
                .body("", hasSize(4))
                .body("description", contains(
                        "Processing and quality check of 2016-2017 ambient VOC data",
                        "Processing and quality check of 2018 ambient VOC data",
                        "Processing and quality check of 2019 ambient VOC data",
                        "Processing of 2019 ambient VOC data with improved quality filtering"
                ))
                .body("eventType",
                        contains("NewVersionRelease", "NewVersionRelease", "NewVersionRelease", "NewVersionRelease"))
                .body("id", contains(214, 215, 229, 258))
                .body("periodStart", contains("2016-08-18", "2018-01-02", "2019-01-01", "2019-01-01"))
                .body("periodEnd", contains("2017-12-22", "2018-12-11", "2019-09-05", "2019-10-25"))
                .body("timestamp", contains(
                        "2018-01-31T10:00:00.000",
                        "2019-02-11T09:00:00.000",
                        "2019-09-12T14:41:54.000",
                        "2020-04-21T18:36:37.000"
                ));
    }
}
