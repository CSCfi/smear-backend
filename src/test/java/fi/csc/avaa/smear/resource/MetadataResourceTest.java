package fi.csc.avaa.smear.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@Tag("integration")
public class MetadataResourceTest {

    @Test
    public void getMetadata_shouldReturnCorrectResult() {
        given()
                .when()
                .get(Endpoints.METADATA)
                .then()
                .statusCode(200)
                .body("accessRights", equalTo("https://creativecommons.org/licenses/by/4.0/"))
                .body("contact", equalTo("atm-data@helsinki.fi"))
                .body("creator", equalTo("http://orcid.org/0000-0001-8826-9108"))
                .body("discipline", equalTo("http://www.yso.fi/onto/okm-tieteenala/ta114"))
                .body("maintainingOrganisation", equalTo("University of Helsinki, Department of Physics, Division of Atmospheric Sciences"))
                .body("project", equalTo("http://www.atm.helsinki.fi/SMEAR/"))
                .body("ref", equalTo("Cite: Junninen, H; Lauri, A; Keronen, P; Aalto, P; Hiltunen, V; Hari, P; Kulmala, M. Smart-SMEAR: on-line data exploration and visualization tool for SMEAR stations.| BOREAL ENVIRONMENT RESEARCH (BER) Vol 14, Issue 4, pp.447-457"))
                .body("rightsCategory", equalTo("LICENSED"))
                .body("timestamp", equalTo("2015-05-04T11:54:57.000"))
                .body("title", equalTo("SMEAR Station for measuring ecosystem-atmosphere relations"));

    }
}
