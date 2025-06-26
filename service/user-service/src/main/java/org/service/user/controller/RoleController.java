package org.service.user.controller;

import jakarta.validation.Valid;
import org.service.user.dto.ApiResponse;
import org.service.user.dto.RoleDto;
import org.service.user.dto.UserDTO;
import org.service.user.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleController {


    @Autowired
    private RoleService roleService;

    @PostMapping
    public ResponseEntity<ApiResponse<RoleDto>> create(@Valid @RequestBody RoleDto dto) {
        RoleDto created = roleService.create(dto);
        return ResponseEntity.status(201).body(ApiResponse.created("Successfully Created Role", created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleDto>>> getAll() {
        List<RoleDto> roleDtos = roleService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Successfully Fetched Roles", roleDtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleDto>> get(@PathVariable Long id) {
        RoleDto role = roleService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Successfully Fetched Role", role));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<RoleDto>> getByName(@PathVariable String name) {
        RoleDto role = roleService.findByName(name);
        return ResponseEntity.ok(ApiResponse.success("Successfully Fetched Role", role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        roleService.deleteRoleById(id);
        return ResponseEntity.ok(ApiResponse.noContent("Successfully Deleted Role"));
    }

    @DeleteMapping("/name/{name}")
    public ResponseEntity<ApiResponse<Void>> deleteByName(@PathVariable String name) {
        roleService.deleteRole(name);
        return ResponseEntity.ok(ApiResponse.noContent("Successfully Deleted Role"));
    }

}
