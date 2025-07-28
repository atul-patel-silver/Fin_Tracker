package org.service.user.services;


import org.keycloak.admin.client.KeycloakBuilder;
import org.service.user.dto.LoginDto;


public interface KeyCloakAdminClientToUserService {


    public KeycloakBuilder login(LoginDto loginDto);

}
