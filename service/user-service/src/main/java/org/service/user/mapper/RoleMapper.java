package org.service.user.mapper;

import org.mapstruct.Mapper;
import org.service.user.dto.RoleDto;
import org.service.user.dto.UserDTO;
import org.service.user.model.Role;
import org.service.user.model.UserModel;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    Role toEntity(RoleDto dto);
    RoleDto toDto(Role entity);

    // List mapping
    List<Role> toEntityList(List<RoleDto> dtoList);

    List<RoleDto> toDtoList(List<Role> entityList);
}
