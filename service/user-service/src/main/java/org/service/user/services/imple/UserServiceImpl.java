package org.service.user.services.imple;


import org.service.user.dto.UserDTO;
import org.service.user.exception.ResourceNotFoundException;
import org.service.user.exception.UserAlreadyExistsException;
import org.service.user.mapper.UserMapper;
import org.service.user.model.Role;
import org.service.user.model.UserModel;
import org.service.user.repository.RoleRepository;
import org.service.user.repository.UserRepository;
import org.service.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserMapper mapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;


    @Override
    public List<UserDTO> getAllUsers() {
        return this.mapper.toDtoList(userRepo.findByIsActiveTrueAndIsDeletedFalse());
    }

    @Override
    public UserDTO getUser(Long id) {
        UserModel user = userRepo.findByIdAndIsActiveTrueAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", id.toString()));
        return this.mapper.toDto(user);
    }




    @Override
    public void deleteUser(Long id) {
        UserModel user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", id.toString()));
        user.setDeleted(true);
        user.setActive(false);
        this.userRepo.save(user);

    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepo.existsByEmailId(userDTO.getEmailId())) {
            throw new UserAlreadyExistsException("User with email " + userDTO.getEmailId() + " already exists.");
        }
        if (userRepo.existsByUserName(userDTO.getUserName())) {
            throw new UserAlreadyExistsException("User with username " + userDTO.getUserName() + " already exists.");
        }
        if (userRepo.existsByMobileNumber(userDTO.getMobileNumber())) {
            throw new UserAlreadyExistsException("User with mobile number " + userDTO.getMobileNumber() + " already exists.");
        }
        UserModel userModel = this.mapper.toEntity(userDTO);

        List<Role> list = roleRepository.findAllByIsActiveTrueAndIsDeletedFalseAndIsDefaultTrue();
        if (!list.isEmpty()) {
            Set<Role> roles = new HashSet<>(list);
            userModel.setRoles(roles);
        }

        userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));
        UserModel savedUser = userRepo.save(userModel);
        return this.mapper.toDto(savedUser);
    }

}

