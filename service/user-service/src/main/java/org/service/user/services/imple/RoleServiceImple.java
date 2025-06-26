package org.service.user.services.imple;


import org.service.user.dto.RoleDto;
import org.service.user.exception.ResourceNotFoundException;
import org.service.user.exception.UserAlreadyExistsException;
import org.service.user.mapper.RoleMapper;
import org.service.user.model.Role;
import org.service.user.repository.RoleRepository;
import org.service.user.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImple implements RoleService {


    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleMapper mapper;

    @Override
    public RoleDto create(RoleDto role) {
        if (roleRepository.existsByNameAndIsActiveTrueAndIsDeletedFalse(role.getName())) {
            throw new UserAlreadyExistsException("Role with Name " + role.getName() + " already exists.");
        }
        Role role1 = this.mapper.toEntity(role);

        if (role.getName().toUpperCase().equals("USER")) {
           role1.setDefault(true);
        }
        role1.setCode("ROLE_"+role.getName().toUpperCase());
        Role savedRole = roleRepository.save(role1);
        return this.mapper.toDto(savedRole);
    }

    @Override
    public RoleDto findByName(String name) {
        Role role = roleRepository.findByNameAndIsActiveTrueAndIsDeletedFalse(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "Name", name));
        return this.mapper.toDto(role);
    }

    @Override
    public RoleDto findById(Long id) {
        Role role = roleRepository.findByIdAndIsActiveTrueAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "Id", id.toString()));
        return this.mapper.toDto(role);
    }

    @Override
    public List<RoleDto> findAll() {
        return this.mapper.toDtoList(roleRepository.findAllByIsActiveTrueAndIsDeletedFalse());
    }

    @Override
    public RoleDto updateRole(RoleDto role) {
        return null;
    }

    @Override
    public void deleteRole(String name) {
        Role role = roleRepository.findByNameAndIsActiveTrueAndIsDeletedFalse(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "Name", name));
        role.setDeleted(true);
        role.setActive(false);
        this.roleRepository.save(role);
    }

    @Override
    public void deleteRoleById(Long id) {
        Role role = roleRepository.findByIdAndIsActiveTrueAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "Id", id.toString()));
        role.setDeleted(true);
        role.setActive(false);
        this.roleRepository.save(role);
    }
}
