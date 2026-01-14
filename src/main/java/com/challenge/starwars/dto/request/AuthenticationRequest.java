package com.challenge.starwars.dto.request;


import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

    @NotEmpty(message = "El nombre de usuario es requerido")
    private String username;

    @NotEmpty(message = "La contraseña es requerida")
    private String password;
}
