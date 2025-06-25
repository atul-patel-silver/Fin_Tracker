package org.service.user.controller;

import jakarta.validation.Valid;
import org.service.user.dto.ApiResponse;
import org.service.user.dto.UserDTO;
import org.service.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    @Autowired
    private UserService userService;

    /**
     * This method is used to register a new user
     *
     * @param dto UserDTO containing user details
     * @return ResponseEntity with ApiResponse containing created UserDTO
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserDTO>> create(@Valid @RequestBody UserDTO dto) {
        UserDTO created = userService.createUser(dto);
        return ResponseEntity.status(201).body(ApiResponse.created("Successfully Registered User", created));
    }
}
