package com.challenge.starwars.dto.external;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SwapiPeoplePageResponse {

    private String message;
    private Integer total_records;
    private Integer total_pages;
    private String next;
    private String previous;

    private List<SwapiPeopleItem> results;
}
