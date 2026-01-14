package com.challenge.starwars.service;

import com.challenge.starwars.dto.request.AuthenticationRequest;
import com.challenge.starwars.dto.request.RegisterRequest;
import com.challenge.starwars.dto.response.AuthenticationResponse;

public interface AuthenticationService {

    void register(RegisterRequest input) throws Exception;

    AuthenticationResponse login(AuthenticationRequest input);
}
