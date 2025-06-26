package org.service.user.repository;

import org.service.user.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {

    boolean existsByNameAndIsActiveTrueAndIsDeletedFalse(String name);

    Optional<Role> findByIdAndIsActiveTrueAndIsDeletedFalse(Long id);

    Optional<Role> findByNameAndIsActiveTrueAndIsDeletedFalse(String name);

    List<Role> findAllByIsActiveTrueAndIsDeletedFalse();

    List<Role> findAllByIsActiveTrueAndIsDeletedFalseAndIsDefaultTrue();

}
