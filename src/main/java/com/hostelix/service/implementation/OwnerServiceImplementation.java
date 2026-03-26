package com.hostelix.service.implementation;


import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.hostelix.client.NotificationClient;
import com.hostelix.dto.EmailRequestDTO;
import com.hostelix.dto.LoginResponse;
import com.hostelix.dto.OwnerLoginRequest;
import com.hostelix.dto.OwnerRegistrationRequest;
import com.hostelix.dto.OwnerRegistrationResponse;
import com.hostelix.dto.OwnerResponseDTO;
import com.hostelix.exceptions.EmailAlreadyExistsException;
import com.hostelix.exceptions.InvalidCredentialsException;
import com.hostelix.exceptions.PasswordsDoNotMatchException;
import com.hostelix.exceptions.UserNotFoundException;
import com.hostelix.model.Owner;
import com.hostelix.repository.OwnerRepository;
import com.hostelix.service.OwnerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OwnerServiceImplementation implements OwnerService {

	private ModelMapper modelMapper=new ModelMapper();
	private final NotificationClient notificationClient;
	
	private final OwnerRepository ownerRepository;

	@Override
	public LoginResponse login(OwnerLoginRequest loginRequest) {
		Owner owner = ownerRepository.findByEmail(loginRequest.getEmail()).orElseThrow(
				() -> new UserNotFoundException("No Active Owner Found with this email : " + loginRequest.getEmail()));
		if(!owner.getPassword().equals(loginRequest.getPassword())) {
			throw new InvalidCredentialsException("Invalid email or password");
		}
		OwnerResponseDTO dto=modelMapper.map(owner, OwnerResponseDTO.class);
		return new LoginResponse( "OWNER", dto);
	}

	@Override
	public OwnerRegistrationResponse registerOwner(OwnerRegistrationRequest ownerRegistrationRequest) {
		if(!ownerRegistrationRequest.getPassword().equals(ownerRegistrationRequest.getConfirmPassword())) {
			throw new PasswordsDoNotMatchException("Password and Confirm password doesn't matched");
		}
		if(ownerRepository.existsByEmail(ownerRegistrationRequest.getEmail())) {
			throw new EmailAlreadyExistsException("Email is already registered");
		}
		String rawPassword=ownerRegistrationRequest.getPassword();
		Owner owner = modelMapper.map(ownerRegistrationRequest, Owner.class);
		Owner save = ownerRepository.save(owner);
		
		   // Send welcome email via Feign (non-blocking failure)
        try {
            notificationClient.sendWelcomeEmail(
                EmailRequestDTO.builder()
                    .toEmail(save.getEmail())
                    .ownerName(save.getOwnerName())
                    .password(rawPassword)
                    .build()
            );
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", save.getEmail(), e.getMessage());
            // Registration still succeeds even if email fails
        }
		return modelMapper.map(save, OwnerRegistrationResponse.class);
	}

}
