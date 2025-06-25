package org.service.user.repository;

import org.service.user.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, Long> {
    boolean existsByEmailId(String email);
    boolean existsByUserName(String userName);
    boolean existsByMobileNumber(String mobileNumber);
    List<UserModel> findByIsActiveTrueAndIsDeletedFalse();
    Optional<UserModel> findByIdAndIsActiveTrueAndIsDeletedFalse(Long id);
}
