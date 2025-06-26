package org.service.user.controller;

import org.service.user.configration.keycloak.KeyCloakProvider;
import org.service.user.dto.TokenDto;
import org.service.user.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/api/auth")
@RestController
public class AuthController {

    @Autowired
    private  KeyCloakProvider authService;
    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam String code) throws Exception {
        TokenDto tokens = authService.exchangeCode(code);
        UserModel user = authService.mapUser(tokens.getAccessToken());
        return ResponseEntity.ok(Map.of("user", user, "token", tokens));
    }
}
