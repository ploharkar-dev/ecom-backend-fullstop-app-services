package com.prl.ecom.service;

import com.prl.ecom.dto.UserDTO;
import com.prl.ecom.dto.UserLoginRequest;
import com.prl.ecom.dto.UserRegisterRequest;
import com.prl.ecom.dto.LoginResponse;
import com.prl.ecom.dto.RefreshTokenRequest;

public interface AuthService {
    UserDTO register(UserRegisterRequest request);
    LoginResponse login(UserLoginRequest request);
    LoginResponse refreshToken(RefreshTokenRequest request);
}
