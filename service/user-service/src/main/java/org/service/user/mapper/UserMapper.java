package org.service.user.mapper;

import org.mapstruct.Mapper;
import org.service.user.dto.UserDTO;
import org.service.user.model.UserModel;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserModel toEntity(UserDTO dto);
    UserDTO toDto(UserModel entity);

    // List mapping
    List<UserModel> toEntityList(List<UserDTO> dtoList);

    List<UserDTO> toDtoList(List<UserModel> entityList);
}
