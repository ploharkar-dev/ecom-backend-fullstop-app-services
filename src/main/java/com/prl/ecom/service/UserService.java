package com.prl.ecom.service;

import com.prl.ecom.dto.UserDTO;
import com.prl.ecom.dto.AddressDTO;
import java.util.List;

public interface UserService {
    UserDTO getUserProfile(Long userId);
    UserDTO updateUserProfile(Long userId, UserDTO userDTO);
    List<AddressDTO> getUserAddresses(Long userId);
    AddressDTO addAddress(Long userId, AddressDTO addressDTO);
    AddressDTO updateAddress(Long userId, Long addressId, AddressDTO addressDTO);
    void deleteAddress(Long userId, Long addressId);
}
