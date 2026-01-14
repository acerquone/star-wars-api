package com.challenge.starwars.service;

import com.challenge.starwars.config.RestClientConfig;
import com.challenge.starwars.dto.response.PeoplePageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(PeopleService.class)
@Import(RestClientConfig.class)
class PeopleServiceTest {

    @Autowired
    private PeopleService peopleService;

    @Autowired
    private MockRestServiceServer server;

    @Test
    @DisplayName("Debe mapear correctamente el JSON de 3 capas a mi DTO de salida")
    void shouldMapThreeLayerJsonToPeopleDto() {
        // GIVEN
        String id = "1";
        String jsonSimulado = """
            {
              "message": "ok",
              "result": {
                "properties": {
                  "name": "Luke Skywalker",
                  "height": "172",
                  "url": "https://www.swapi.tech/api/people/1"
                }
              }
            }
            """;

        this.server.expect(requestTo("https://www.swapi.tech/api/people/" + id))
                .andRespond(withSuccess(jsonSimulado, MediaType.APPLICATION_JSON));

        // WHEN
        var result = peopleService.getPersonById(id);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Luke Skywalker");
        assertThat(result.getHeight()).isEqualTo("172");
        assertThat(result.getUrl()).isEqualTo("https://www.swapi.tech/api/people/1");
    }

    @Test
    @DisplayName("Debe retornar lista paginada cuando NO se envía nombre")
    void shouldReturnPagedPeopleWhenNoNameProvided() throws Exception {
        // GIVEN: Estructura de SwapiPeoplePageResponse (results en plural)
        String jsonResponse = """
                {
                    "total_records": 10,
                    "total_pages": 1,
                    "results": [
                        {"uid": "1", "name": "Luke Skywalker"}
                    ]
                }
                """;

        this.server.expect(requestTo(containsString("/people?page=1&limit=10")))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        // WHEN
        PeoplePageResponse result = peopleService.getPeople(null, 1, 10);

        // THEN
        assertNotNull(result);
        assertEquals(1, result.getResults().size());
        assertEquals("Luke Skywalker", result.getResults().get(0).getName());
        assertEquals(10, result.getTotalRecords());
    }

    @Test
    @DisplayName("Debe retornar lista de búsqueda cuando SE envía nombre")
    void shouldReturnSearchPeopleWhenNameProvided() throws Exception {
        // GIVEN: Estructura de SwapiPeopleSearchResponse (result singular + properties)
        String jsonResponse = """
                {
                    "result": [
                        {
                            "uid": "1",
                            "properties": {
                                "name": "Luke Skywalker"
                            }
                        }
                    ]
                }
                """;

        this.server.expect(requestTo(containsString("/people/?name=Luke")))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        // WHEN
        PeoplePageResponse result = peopleService.getPeople("Luke", 1, 10);

        // THEN
        assertNotNull(result);
        assertEquals(1, result.getResults().size());
        assertEquals("Luke Skywalker", result.getResults().get(0).getName());
        // En búsqueda, nuestro mapeador pone totalRecords = size de la lista
        assertEquals(1, result.getTotalRecords());
    }

    @Test
    @DisplayName("Debe retornar DTO vacío cuando la API no encuentra resultados")
    void shouldReturnEmptyDtoWhenNoResultsFound() {
        // GIVEN: La API responde OK pero con lista vacía
        String jsonResponse = "{\"result\": []}";

        this.server.expect(requestTo(containsString("/people/?name=NonExistent")))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        // WHEN
        PeoplePageResponse result = peopleService.getPeople("NonExistent", 1, 10);

        // THEN
        assertNotNull(result);
        assertTrue(result.getResults().isEmpty());
        assertEquals(0, result.getTotalRecords());
    }
}
