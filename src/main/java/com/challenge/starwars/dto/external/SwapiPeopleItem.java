package com.challenge.starwars.dto.external;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SwapiPeopleItem {

    private String uid;

    private String name;

    private String url;

}
