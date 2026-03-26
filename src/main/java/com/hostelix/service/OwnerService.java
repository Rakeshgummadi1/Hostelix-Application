package com.hostelix.service;

import com.hostelix.dto.LoginResponse;
import com.hostelix.dto.OwnerLoginRequest;
import com.hostelix.dto.OwnerRegistrationRequest;
import com.hostelix.dto.OwnerRegistrationResponse;

public interface OwnerService {

	OwnerRegistrationResponse registerOwner(OwnerRegistrationRequest ownerRegistrationRequest);
	LoginResponse login(OwnerLoginRequest loginRequest);
}
