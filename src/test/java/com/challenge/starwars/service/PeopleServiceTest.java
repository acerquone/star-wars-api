package com.challenge.starwars.service;

import com.challenge.starwars.config.RestClientConfig;
import com.challenge.starwars.dto.response.PeoplePageResponse;
import com.challenge.starwars.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(PeopleService.class)
@Import(RestClientConfig.class)
class PeopleServiceTest {

    @Autowired
    private PeopleService peopleService;

    @Autowired
    private MockRestServiceServer server;

    @Test
    @DisplayName("Debe mapear correctamente cuando el personaje existe")
    void shouldReturnPersonWhenIdExists() {
        // GIVEN
        String id = "1";
        String jsonSimulado = """
        {
          "result": {
            "properties": { "name": "Luke Skywalker", "height": "172" }
          }
        }
        """;

        this.server.expect(requestTo(containsString("/people/" + id)))
                .andRespond(withSuccess(jsonSimulado, MediaType.APPLICATION_JSON));

        // WHEN
        var result = peopleService.getPersonById(id);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Luke Skywalker");
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

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando la API devuelve 404")
    void shouldThrowExceptionWhenApiReturns404() {
        // GIVEN
        String id = "999";
        this.server.expect(requestTo(containsString("/people/" + id)))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)); // Simulamos el 404

        // WHEN & THEN
        // Verificamos que se lance la excepción correcta y tenga el mensaje esperado
        assertThatThrownBy(() -> peopleService.getPersonById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró el personaje");
    }

    @Test
    @DisplayName("Debe lanzar RuntimeException cuando la paginación de SWAPI falla")
    void shouldThrowExceptionWhenPaginationFails() {
        // GIVEN
        this.server.expect(requestTo(containsString("/people?page=1")))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        // WHEN & THEN
        assertThatThrownBy(() -> peopleService.getPeople(null, 1, 10))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error al recuperar lista paginada");
    }

    @Test
    @DisplayName("Debe devolver una página vacía cuando la búsqueda no encuentra nada")
    void shouldReturnEmptyPageWhenSearchHasNoResults() {
        // GIVEN
        String name = "PersonajeInexistente";
        String jsonVacio = "{\"result\": []}"; // Estructura que devuelve SWAPI cuando no hay match

        this.server.expect(requestTo(containsString("/people/?name=" + name)))
                .andRespond(withSuccess(jsonVacio, MediaType.APPLICATION_JSON));

        // WHEN
        PeoplePageResponse result = peopleService.getPeople(name, 1, 10);

        // THEN
        assertThat(result.getTotalRecords()).isZero();
        assertThat(result.getResults()).isEmpty();
    }
}
