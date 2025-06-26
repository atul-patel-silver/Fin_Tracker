package org.service.user.controller;

import jakarta.validation.Valid;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.service.user.dto.ApiResponse;
import org.service.user.dto.LoginDto;
import org.service.user.dto.UserDTO;
import org.service.user.services.KeyCloakAdminClientToUserService;
import org.service.user.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.BadRequestException;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private KeyCloakAdminClientToUserService keyCloakAdminClientToUserService;
    /**
     * This method is used to register a new user
     *
     * @param dto UserDTO containing user details
     * @return ResponseEntity with ApiResponse containing created UserDTO
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> create(@Valid @RequestBody UserDTO dto) {
        UserDTO created = userService.createUser(dto);
        return ResponseEntity.status(201).body(ApiResponse.created("Successfully Registered User", created));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginDto loginDto) {
        Keycloak keycloak = this.keyCloakAdminClientToUserService.login(loginDto).build();
        AccessTokenResponse accessTokenResponse = null;
        try {
            accessTokenResponse = keycloak.tokenManager().getAccessToken();
            return ResponseEntity.status(201).body(ApiResponse.success("Successfully Login", accessTokenResponse));
        } catch (BadRequestException ex) {
            logger.warn("invalid account. User probably hasn't verified email.", ex);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.unAuthorized("Invalid username or password", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.unAuthorized("Invalid username or password", null));

        }
    }

}
