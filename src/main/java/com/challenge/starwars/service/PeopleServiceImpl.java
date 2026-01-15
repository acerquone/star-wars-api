package com.challenge.starwars.service;

import com.challenge.starwars.dto.external.SwapiPeoplePageResponse;
import com.challenge.starwars.dto.external.SwapiPeopleSearchResponse;
import com.challenge.starwars.dto.external.SwapiPeopleSingleResponse;
import com.challenge.starwars.dto.response.PeopleDetailResponse;
import com.challenge.starwars.dto.response.PeoplePageResponse;
import com.challenge.starwars.dto.response.PeopleSummaryResponse;
import com.challenge.starwars.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Service
public class PeopleServiceImpl implements PeopleService{

    @Autowired
    private final RestClient restClient;

    public PeopleServiceImpl(@Qualifier("swapiRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public PeopleDetailResponse getPersonById(String id) {

        SwapiPeopleSingleResponse swapiResponse = restClient.get()
                .uri("/people/{id}", id)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), (request, response) -> {
                    throw new ResourceNotFoundException("No se encontró el personaje con ID: " + id);
                })
                .onStatus(status -> status.is5xxServerError(), (request, response) -> {
                    throw new RuntimeException("El servicio de Star Wars no está disponible");
                })
                .body(SwapiPeopleSingleResponse.class);

        if (swapiResponse == null || swapiResponse.getResult() == null) {
            throw new ResourceNotFoundException("La respuesta de la API externa para el ID " + id + " está vacía");
        }

        var properties = swapiResponse.getResult().getProperties();

        return PeopleDetailResponse.builder()
                .name(properties.getName())
                .gender(properties.getGender())
                .height(properties.getHeight())
                .url(properties.getUrl())
                .build();
    }

    @Override
    public PeoplePageResponse getPeople(String name, int page, int limit) {
        if (name != null && !name.isBlank()) {
            return searchPeopleByName(name);
        }
        return getPagedPeople(page, limit);
    }

    private PeoplePageResponse getPagedPeople(int page, int limit) {
        String url = buildPaginationUrl(page, limit);
        SwapiPeoplePageResponse swapiResponse = restClient.get()
                .uri(url)
                .retrieve()

                .onStatus(status -> status.isError(), (request, response) -> {
                    throw new RuntimeException("Error al recuperar lista paginada de Star Wars");
                })
                .body(SwapiPeoplePageResponse.class);

        return mapToPageDto(swapiResponse);
    }

    private PeoplePageResponse searchPeopleByName(String name) {
        String url = buildSearchUrl(name);
        SwapiPeopleSearchResponse searchResponse = restClient.get()
                .uri(url)
                .retrieve()

                .onStatus(status -> status.isError(), (request, response) -> {
                    throw new RuntimeException("Error en la búsqueda por nombre en la API externa");
                })
                .body(SwapiPeopleSearchResponse.class);

        if (searchResponse == null || searchResponse.getResult() == null || searchResponse.getResult().isEmpty()) {
            return createEmptyPageDto();
        }

        return mapSearchToPageDto(searchResponse);
    }

    private String buildPaginationUrl(int page, int limit) {
        return String.format("/people?page=%d&limit=%d", page, limit);
    }

    private String buildSearchUrl(String name) {
        return "/people/?name=" + name.trim();
    }

    private PeoplePageResponse mapToPageDto(SwapiPeoplePageResponse swapiResponse) {

        if (swapiResponse == null || swapiResponse.getResults() == null) {
            return createEmptyPageDto();
        }

        List<PeopleSummaryResponse> results = swapiResponse.getResults().stream()
                .map(item -> PeopleSummaryResponse.builder()
                        .uid(item.getUid())
                        .name(item.getName())
                        .build())
                .toList();

        return PeoplePageResponse.builder()
                .totalRecords(swapiResponse.getTotal_records())
                .totalPages(swapiResponse.getTotal_pages())
                .results(results)
                .build();
    }

    private PeoplePageResponse mapSearchToPageDto(SwapiPeopleSearchResponse searchResponse) {

        List<PeopleSummaryResponse> results = searchResponse.getResult().stream()
                .map(item -> PeopleSummaryResponse.builder()
                        .uid(item.getUid())

                        .name(item.getProperties() != null && item.getProperties().get("name") != null
                                ? item.getProperties().get("name").toString() // Convertimos Object a String
                                : "Unknown")
                        .build())
                .toList();

        return PeoplePageResponse.builder()
                .totalRecords(results.size())
                .totalPages(1)
                .results(results)
                .build();
    }

    private PeoplePageResponse createEmptyPageDto() {
        return PeoplePageResponse.builder()
                .totalRecords(0)
                .totalPages(0)
                .results(Collections.emptyList())
                .build();
    }
}
