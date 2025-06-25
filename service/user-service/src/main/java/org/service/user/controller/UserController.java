package org.service.user.controller;

import jakarta.validation.Valid;
import org.service.user.dto.ApiResponse;
import org.service.user.dto.UserDTO;
import org.service.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private  UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAll() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Successfully Fetched Users", users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> get(@PathVariable Long id) {
        UserDTO user = userService.getUser(id);
        return ResponseEntity.ok(ApiResponse.success("Successfully Fetched user", user));
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.noContent("Successfully Deleted User"));
    }
}
