package com.challenge.starwars.controller;

import com.challenge.starwars.dto.response.PeopleDetailResponse;
import com.challenge.starwars.dto.response.PeoplePageResponse;
import com.challenge.starwars.service.PeopleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/people")
@AllArgsConstructor
public class PeopleController {

    private final PeopleService peopleService;

    @GetMapping("{id}")
    @Operation(summary = "Obtener detalles de un personaje")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Personaje encontrado"),
            @ApiResponse(responseCode = "404", description = "Personaje no encontrado")
    })
    public ResponseEntity<PeopleDetailResponse> getPersonById(@PathVariable String id){

        return ResponseEntity.ok(peopleService.getPersonById(id));
    }

    @GetMapping
    public ResponseEntity<PeoplePageResponse> getPeople(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {

        PeoplePageResponse response = peopleService.getPeople(name, page, limit);
        return ResponseEntity.ok(response);
    }
}
