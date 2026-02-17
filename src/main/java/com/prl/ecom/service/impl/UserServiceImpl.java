package com.prl.ecom.service.impl;

import com.prl.ecom.dto.UserDTO;
import com.prl.ecom.dto.AddressDTO;
import com.prl.ecom.entity.User;
import com.prl.ecom.entity.Address;
import com.prl.ecom.exception.ResourceNotFoundException;
import com.prl.ecom.repository.UserRepository;
import com.prl.ecom.repository.AddressRepository;
import com.prl.ecom.service.UserService;
import com.prl.ecom.util.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Override
    public UserDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND));
        return mapToUserDTO(user);
    }

    @Override
    public UserDTO updateUserProfile(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND));

        if (userDTO.getFirstName() != null) {
            user.setFirstName(userDTO.getFirstName());
        }
        if (userDTO.getLastName() != null) {
            user.setLastName(userDTO.getLastName());
        }
        if (userDTO.getPhone() != null) {
            user.setPhone(userDTO.getPhone());
        }

        User updatedUser = userRepository.save(user);
        log.info("User profile updated. User ID: {}", userId);

        return mapToUserDTO(updatedUser);
    }

    @Override
    public List<AddressDTO> getUserAddresses(Long userId) {
        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND));

        return addressRepository.findByUserId(userId)
                .stream()
                .map(this::mapToAddressDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AddressDTO addAddress(Long userId, AddressDTO addressDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND));

        Address address = Address.builder()
                .user(user)
                .street(addressDTO.getStreet())
                .city(addressDTO.getCity())
                .state(addressDTO.getState())
                .zipCode(addressDTO.getZipCode())
                .country(addressDTO.getCountry())
                .isDefault(addressDTO.getIsDefault() != null && addressDTO.getIsDefault())
                .build();

        Address savedAddress = addressRepository.save(address);
        log.info("Address added for user. User ID: {}", userId);

        return mapToAddressDTO(savedAddress);
    }

    @Override
    public AddressDTO updateAddress(Long userId, Long addressId, AddressDTO addressDTO) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Address not found for user");
        }

        if (addressDTO.getStreet() != null) {
            address.setStreet(addressDTO.getStreet());
        }
        if (addressDTO.getCity() != null) {
            address.setCity(addressDTO.getCity());
        }
        if (addressDTO.getState() != null) {
            address.setState(addressDTO.getState());
        }
        if (addressDTO.getZipCode() != null) {
            address.setZipCode(addressDTO.getZipCode());
        }
        if (addressDTO.getCountry() != null) {
            address.setCountry(addressDTO.getCountry());
        }
        if (addressDTO.getIsDefault() != null) {
            address.setIsDefault(addressDTO.getIsDefault());
        }

        Address updatedAddress = addressRepository.save(address);
        log.info("Address updated. Address ID: {}", addressId);

        return mapToAddressDTO(updatedAddress);
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Address not found for user");
        }

        addressRepository.delete(address);
        log.info("Address deleted. Address ID: {}", addressId);
    }

    private UserDTO mapToUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getActive(),
                user.getEmailVerified(),
                user.getCreatedAt()
        );
    }

    private AddressDTO mapToAddressDTO(Address address) {
        return new AddressDTO(
                address.getId(),
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getCountry(),
                address.getIsDefault()
        );
    }
}
