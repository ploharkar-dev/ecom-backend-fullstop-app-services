package com.prl.ecom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prl.ecom.dto.LoginResponse;
import com.prl.ecom.dto.UserDTO;
import com.prl.ecom.dto.UserLoginRequest;
import com.prl.ecom.dto.UserRegisterRequest;
import com.prl.ecom.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private UserRegisterRequest registerRequest;
    private UserLoginRequest loginRequest;
    private UserDTO userDTO;
    private LoginResponse loginResponse;

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

        userDTO = new UserDTO(
                1L,
                "test@example.com",
                "John",
                "Doe",
                "1234567890",
                true,
                false,
                LocalDateTime.now()
        );

        loginResponse = LoginResponse.builder()
                .accessToken("accessToken123")
                .refreshToken("refreshToken123")
                .expiresIn(900000L)
                .user(userDTO)
                .build();
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        when(authService.registerUser(any(UserRegisterRequest.class))).thenReturn(userDTO);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }

    @Test
    void testLoginUser_Success() throws Exception {
        when(authService.loginUser(any(UserLoginRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists());
    }

    @Test
    void testRegisterUser_InvalidEmail() throws Exception {
        UserRegisterRequest invalidRequest = new UserRegisterRequest(
                "invalidemail",
                "password123",
                "John",
                "Doe",
                "1234567890"
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLoginUser_MissingPassword() throws Exception {
        UserLoginRequest invalidRequest = new UserLoginRequest(
                "test@example.com",
                ""
        );

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
