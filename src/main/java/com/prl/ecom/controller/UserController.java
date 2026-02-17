package com.prl.ecom.controller;

import com.prl.ecom.dto.UserDTO;
import com.prl.ecom.dto.AddressDTO;
import com.prl.ecom.service.UserService;
import com.prl.ecom.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Profile", description = "User profile management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "Get user profile")
    public ResponseEntity<ApiResponse<UserDTO>> getProfile(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        UserDTO profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(profile, "Profile retrieved successfully"));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update user profile")
    public ResponseEntity<ApiResponse<UserDTO>> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UserDTO userDTO) {
        Long userId = getUserIdFromAuth(authentication);
        log.info("Updating user profile. User ID: {}", userId);
        UserDTO updatedProfile = userService.updateUserProfile(userId, userDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedProfile, "Profile updated successfully"));
    }

    @GetMapping("/addresses")
    @Operation(summary = "Get user addresses")
    public ResponseEntity<ApiResponse<List<AddressDTO>>> getAddresses(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<AddressDTO> addresses = userService.getUserAddresses(userId);
        return ResponseEntity.ok(ApiResponse.success(addresses, "Addresses retrieved successfully"));
    }

    @PostMapping("/addresses")
    @Operation(summary = "Add new address")
    public ResponseEntity<ApiResponse<AddressDTO>> addAddress(
            Authentication authentication,
            @Valid @RequestBody AddressDTO addressDTO) {
        Long userId = getUserIdFromAuth(authentication);
        log.info("Adding address for user. User ID: {}", userId);
        AddressDTO newAddress = userService.addAddress(userId, addressDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(newAddress, "Address added successfully", 201));
    }

    @PutMapping("/addresses/{addressId}")
    @Operation(summary = "Update address")
    public ResponseEntity<ApiResponse<AddressDTO>> updateAddress(
            Authentication authentication,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressDTO addressDTO) {
        Long userId = getUserIdFromAuth(authentication);
        AddressDTO updatedAddress = userService.updateAddress(userId, addressId, addressDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedAddress, "Address updated successfully"));
    }

    @DeleteMapping("/addresses/{addressId}")
    @Operation(summary = "Delete address")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            Authentication authentication,
            @PathVariable Long addressId) {
        Long userId = getUserIdFromAuth(authentication);
        userService.deleteAddress(userId, addressId);
        return ResponseEntity.ok(ApiResponse.success(null, "Address deleted successfully"));
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        // In a real app, fetch user ID from database using email from authentication.getPrincipal()
        return 1L;  // This should be replaced with actual user lookup
    }
}
