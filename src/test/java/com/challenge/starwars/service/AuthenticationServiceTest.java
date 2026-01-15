package com.challenge.starwars.service;

import com.challenge.starwars.dto.request.AuthenticationRequest;
import com.challenge.starwars.dto.request.RegisterRequest;
import com.challenge.starwars.dto.response.AuthenticationResponse;
import com.challenge.starwars.entity.User;
import com.challenge.starwars.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationServiceImpl authService;

    @Test
    @DisplayName("Registro exitoso de un nuevo usuario")
    void shouldRegisterUserSuccessfully() {

        RegisterRequest request = new RegisterRequest("user@test.com", "password123");
        when(userRepository.findByUsername("user@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        authService.register(request);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Login exitoso devuelve un token JWT")
    void shouldLoginSuccessfully() {

        AuthenticationRequest request = new AuthenticationRequest("user@test.com", "password123");
        User user = User.builder().username("user@test.com").role("USER").build();

        when(userRepository.findByUsername("user@test.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(anyMap(), any(User.class))).thenReturn("fake-jwt-token");

        AuthenticationResponse response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("fake-jwt-token");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Registro fallido por nombre de usuario ya existente")
    void shouldThrowExceptionWhenUserAlreadyExists() {

        RegisterRequest request = new RegisterRequest("user@test.com", "password123");
        when(userRepository.findByUsername("user@test.com")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Login fallido porque el usuario no existe en BD")
    void shouldThrowExceptionWhenUserNotFoundDuringLogin() {

        AuthenticationRequest request = new AuthenticationRequest("user@test.com", "password123");
        when(userRepository.findByUsername("user@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
