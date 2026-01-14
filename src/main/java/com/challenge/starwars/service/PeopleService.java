package com.challenge.starwars.service;

import com.challenge.starwars.dto.response.PeopleDetailResponse;
import com.challenge.starwars.dto.response.PeoplePageResponse;

public interface PeopleService {


    PeopleDetailResponse getPersonById(String id);

    PeoplePageResponse getPeople(String name, int page, int limit);


}
