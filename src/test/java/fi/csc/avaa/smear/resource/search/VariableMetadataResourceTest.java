package fi.csc.avaa.smear.resource.search;

import fi.csc.avaa.smear.config.Endpoints;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static fi.csc.avaa.smear.util.TestUtils.expectedResponse;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;

@QuarkusTest
@Tag("integration")
public class VariableMetadataResourceTest {

    @Test
    public void search_shouldReturnCorrectResult() {
        validateSearchResponse(Map.of(
                "table", "HYY_META",
                "variable", "G_sc",
                "source", "hukse"
        ));
    }

    @Test
    public void searchWithTablevariable_shouldReturnCorrectResult() {
        validateSearchResponse(Map.of("tablevariable", "HYY_META.G_sc"));
    }

    private void validateSearchResponse(Map<String, String> params) {
        given()
                .when()
                .params(params)
                .get(Endpoints.SEARCH_VARIABLES + "/search")
                .then()
                .statusCode(200)
                .body("", hasSize(1))
                .body("category", contains("Soil"))
                .body("coverage", contains(0))
                .body("derivative", contains(true))
                .body("description", contains("Soil heat flux (mean of four locations) plus heat storage change in the topsoil above the heat flux plates (until 8/2017 other locations than the heat flux measurement)"))
                .body("mandatory", contains(false))
                .body("name", contains("G_sc"))
                .body("periodEnd", contains(nullValue()))
                .body("periodStart", contains("2005-01-01T00:00:00.0"))
                .body("rights", contains("public"))
                .body("source", contains("Hukseflux HFP01 heat flux sensors, tsoil_humus, tsoil_A, wsoil_humus, wsoil_A"))
                .body("tableName", contains("HYY_META"))
                .body("timestamp", contains("2020-01-13T17:09:15.000"))
                .body("title", contains("Soil heat flux and storage"))
                .body("type", contains("double"))
                .body("uiAvgType", contains(nullValue()))
                .body("uiSortOrder", contains(nullValue()))
                .body("unit", contains("W m⁻²"));
    }

    @Test
    public void searchCsv_shouldReturnCorrectResult() {
        RestAssured.registerParser("text/csv", Parser.TEXT);
        String expected = expectedResponse("VariableMetadata_search.csv");
        given()
                .when()
                .queryParam("table", "HYY_META")
                .queryParam("variable", "G_sc")
                .queryParam("source", "hukse")
                .get(Endpoints.SEARCH_VARIABLES + "/search/csv")
                .then()
                .statusCode(200)
                .body(equalTo(expected));
    }

    @Test
    public void searchTsv_shouldReturnCorrectResult() {
        RestAssured.registerParser("text/plain", Parser.TEXT);
        String expected = expectedResponse("VariableMetadata_search.tsv");
        given()
                .when()
                .queryParam("table", "HYY_META")
                .queryParam("variable", "G_sc")
                .queryParam("source", "hukse")
                .get(Endpoints.SEARCH_VARIABLES + "/search/tsv")
                .then()
                .statusCode(200)
                .body(equalTo(expected));
    }
}
