package com.prl.ecom.service;

import com.prl.ecom.dto.UserDTO;
import com.prl.ecom.dto.UserLoginRequest;
import com.prl.ecom.dto.UserRegisterRequest;
import com.prl.ecom.entity.Cart;
import com.prl.ecom.entity.User;
import com.prl.ecom.entity.Wishlist;
import com.prl.ecom.exception.BadRequestException;
import com.prl.ecom.exception.DuplicateException;
import com.prl.ecom.exception.ResourceNotFoundException;
import com.prl.ecom.exception.UnauthorizedException;
import com.prl.ecom.repository.UserRepository;
import com.prl.ecom.util.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private UserRegisterRequest registerRequest;
    private UserLoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new UserRegisterRequest(
                "test@example.com",
                "password123",
                "John",
                "Doe",
                "1234567890"
        );

        loginRequest = new UserLoginRequest(
                "test@example.com",
                "password123"
        );

        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("hashedPassword")
                .firstName("John")
                .lastName("Doe")
                .phone("1234567890")
                .active(true)
                .emailVerified(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testRegisterUser_Success() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO result = authService.registerUser(registerRequest);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("John", result.getFirstName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThrows(DuplicateException.class, () -> authService.registerUser(registerRequest));
    }

    @Test
    void testLoginUser_Success() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(user.getEmail())).thenReturn("accessToken");
        when(jwtTokenProvider.generateRefreshToken(user.getEmail())).thenReturn("refreshToken");

        var result = authService.loginUser(loginRequest);

        assertNotNull(result);
        assertEquals("accessToken", result.getAccessToken());
        assertEquals("refreshToken", result.getRefreshToken());
    }

    @Test
    void testLoginUser_UserNotFound() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> authService.loginUser(loginRequest));
    }

    @Test
    void testLoginUser_InvalidPassword() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> authService.loginUser(loginRequest));
    }

    @Test
    void testGetUserByEmail_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDTO result = authService.getUserByEmail("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testGetUserByEmail_NotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.getUserByEmail("notfound@example.com"));
    }
}
