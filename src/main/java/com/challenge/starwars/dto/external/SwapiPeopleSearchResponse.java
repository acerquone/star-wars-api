package com.challenge.starwars.dto.external;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SwapiPeopleSearchResponse {

    @JsonAlias({"result", "results"})
    private List<PeopleSearchItem> result;

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PeopleSearchItem {
        private String uid;
        private Map<String, Object> properties;
    }
}
