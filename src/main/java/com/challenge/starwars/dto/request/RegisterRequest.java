package com.challenge.starwars.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotEmpty(message = "El nombre de usuario es requerido")
    @Email(message = "Formato de email invalido")
    private String username;

    @NotEmpty(message = "La contraseña es requerida")
    @Size(min = 5, max = 30, message = "La contraseña debe contener entre 5 y 30 caracteres")
    private String password;


}
