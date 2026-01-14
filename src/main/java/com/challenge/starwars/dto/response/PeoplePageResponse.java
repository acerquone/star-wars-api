package com.challenge.starwars.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeoplePageResponse {

    private Integer totalRecords;


    private Integer totalPages;

    private List<PeopleSummaryResponse> results;
}
