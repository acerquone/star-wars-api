package com.challenge.starwars.controller;


import com.challenge.starwars.dto.request.AuthenticationRequest;
import com.challenge.starwars.dto.request.RegisterRequest;
import com.challenge.starwars.dto.response.AuthenticationResponse;
import com.challenge.starwars.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public void register(@Valid @RequestBody RegisterRequest registerRequest) throws Exception{

        authenticationService.register(registerRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public AuthenticationResponse login(@Valid @RequestBody AuthenticationRequest authenticationRequest){

        return authenticationService.login(authenticationRequest);
    }
}
