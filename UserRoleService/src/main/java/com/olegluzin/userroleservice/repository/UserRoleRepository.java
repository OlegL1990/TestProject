package com.olegluzin.userroleservice.repository;

import com.olegluzin.userroleservice.model.UserRole;
import com.olegluzin.userroleservice.model.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId>{
    List<UserRole> findByUserId(Long userId);
}
