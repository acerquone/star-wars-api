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
        // GIVEN
        RegisterRequest request = new RegisterRequest("nuevoUsuario", "password123");
        when(userRepository.findByUsername("nuevoUsuario")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        // WHEN
        authService.register(request);

        // THEN
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Login exitoso devuelve un token JWT")
    void shouldLoginSuccessfully() {
        // GIVEN
        AuthenticationRequest request = new AuthenticationRequest("luke@skywalker", "force123");
        User user = User.builder().username("luke@skywalker").role("USER").build();

        when(userRepository.findByUsername("luke@skywalker")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(anyMap(), any(User.class))).thenReturn("fake-jwt-token");

        // WHEN
        AuthenticationResponse response = authService.login(request);

        // THEN
        assertThat(response.getToken()).isEqualTo("fake-jwt-token");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Registro fallido por nombre de usuario ya existente")
    void shouldThrowExceptionWhenUserAlreadyExists() {
        // GIVEN
        RegisterRequest request = new RegisterRequest("luke@skywalker", "password");
        when(userRepository.findByUsername("luke@skywalker")).thenReturn(Optional.of(new User()));

        // WHEN & THEN
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El nombre de usuario ya está en uso.");
    }

    @Test
    @DisplayName("Login fallido porque el usuario no existe en BD")
    void shouldThrowExceptionWhenUserNotFoundDuringLogin() {
        // GIVEN
        AuthenticationRequest request = new AuthenticationRequest("fantasma", "123");
        when(userRepository.findByUsername("fantasma")).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
