package com.challenge.starwars.service;

import com.challenge.starwars.dto.external.SwapiPeoplePageResponse;
import com.challenge.starwars.dto.external.SwapiPeopleSearchResponse;
import com.challenge.starwars.dto.external.SwapiPeopleSingleResponse;
import com.challenge.starwars.dto.response.PeopleDetailResponse;
import com.challenge.starwars.dto.response.PeoplePageResponse;
import com.challenge.starwars.dto.response.PeopleSummaryResponse;
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

    @Override
    public PeopleDetailResponse getPersonById(String id) {

        SwapiPeopleSingleResponse peopleResponse = restClient.get()
                .uri("/people/{id}", id)
                .retrieve()
                .body(SwapiPeopleSingleResponse.class);

        if (peopleResponse == null || peopleResponse.getResult() == null) {
            return null;
        }

        SwapiPeopleSingleResponse.PersonProperties properties = peopleResponse.getResult().getProperties();

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
                .body(SwapiPeoplePageResponse.class);

        return mapToPageDto(swapiResponse);
    }

    private PeoplePageResponse searchPeopleByName(String name) {
        String url = buildSearchUrl(name);
        SwapiPeopleSearchResponse searchResponse = restClient.get()
                .uri(url)
                .retrieve()
                .body(SwapiPeopleSearchResponse.class);

        // DEFENSA: Manejo de resultados vacíos o nulos de la API externa
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
        // Si la respuesta es nula, evitamos el NullPointerException
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
        // Ya validamos la nulidad en el método searchPeopleByName,
        // pero aquí realizamos la transformación final.
        List<PeopleSummaryResponse> results = searchResponse.getResult().stream()
                .map(item -> PeopleSummaryResponse.builder()
                        .uid(item.getUid())
                        // ACCESO A LA CAPA ANIDADA: Extraemos el nombre de properties
                        .name(item.getProperties() != null && item.getProperties().get("name") != null
                                ? item.getProperties().get("name").toString() // Convertimos Object a String
                                : "Unknown")
                        .build())
                .toList();

        return PeoplePageResponse.builder()
                .totalRecords(results.size())
                .totalPages(1) // En búsquedas, swapi.tech suele devolver una sola lista
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
