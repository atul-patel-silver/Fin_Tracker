package org.service.user.services;

import org.service.user.dto.UserDTO;

import java.util.List;

public interface UserService {

    List<UserDTO> getAllUsers();

    UserDTO getUser(Long id);

    void deleteUser(Long id);

    UserDTO createUser(UserDTO userDTO);
}
