package com.challenge.starwars.controller;


import com.challenge.starwars.dto.request.AuthenticationRequest;
import com.challenge.starwars.dto.request.RegisterRequest;
import com.challenge.starwars.dto.response.AuthenticationResponse;
import com.challenge.starwars.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Tag(name = "Autenticación")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Registro de usuario nuevo")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public void register(@Valid @RequestBody RegisterRequest registerRequest) throws Exception{

        authenticationService.register(registerRequest);
    }

    @Operation(summary = "Login y generación de JWT")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public AuthenticationResponse login(@Valid @RequestBody AuthenticationRequest authenticationRequest){

        return authenticationService.login(authenticationRequest);
    }
}
