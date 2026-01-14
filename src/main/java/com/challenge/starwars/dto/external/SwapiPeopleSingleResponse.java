package com.challenge.starwars.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SwapiPeopleSingleResponse {

    private PersonResult result;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PersonResult {
        private PersonProperties properties;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PersonProperties {
        private String name;
        private String height;
        private String gender;
        private String url;
    }
}
