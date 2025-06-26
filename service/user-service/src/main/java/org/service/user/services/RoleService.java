package org.service.user.services;


import org.service.user.dto.RoleDto;

import java.util.List;
public interface RoleService {

    RoleDto create(RoleDto role);

    RoleDto findByName(String name);
    RoleDto findById(Long id);

    List<RoleDto> findAll();

    RoleDto updateRole(RoleDto role);

    void deleteRole(String name);

    void deleteRoleById(Long id);
}
