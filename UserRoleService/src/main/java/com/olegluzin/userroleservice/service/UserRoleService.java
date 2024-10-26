package com.olegluzin.userroleservice.service;

import com.olegluzin.userroleservice.client.RoleClient;
import com.olegluzin.userroleservice.client.UserClient;
import com.olegluzin.userroleservice.dto.RoleDTO;
import com.olegluzin.userroleservice.dto.UserDTO;
import com.olegluzin.userroleservice.dto.UserRoleDTO;
import com.olegluzin.userroleservice.model.UserRole;
import com.olegluzin.userroleservice.model.UserRoleId;
import com.olegluzin.userroleservice.repository.UserRoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserClient userClient;
    private final RoleClient roleClient;
    private final UserRoleRepository userRoleRepository;
    public List<UserRoleDTO> getUserRoles(Long userId) {
        UserDTO user;

        try {
            user = userClient.getUserById(userId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + userId);
        }
        List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
        return userRoles.stream().map(userRole -> {
            RoleDTO role;
            try {
                role = roleClient.getRoleById(userRole.getRoleId());
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found with ID: " + userRole.getRoleId());
            }
            return new UserRoleDTO(user.getId(),role.getId(), user.getUsername(),role.getName());
        }).collect(Collectors.toList());
    }
    public UserRole assignRoleToUser(Long userId, Long roleId) {

        try {
            userClient.getUserById(userId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + userId);
        }

        try {
            roleClient.getRoleById(roleId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found with ID: " + roleId);
        }

        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        return userRoleRepository.save(userRole);
    }

    public void removeRoleFromUser(Long userId, Long roleId) {
        UserRoleId userRoleId = new UserRoleId();
        userRoleId.setUserId(userId);
        userRoleId.setRoleId(roleId);
        UserRole userRole = userRoleRepository.findById(userRoleId)
                .orElseThrow(() -> new EntityNotFoundException("User role not found"));
        userRoleRepository.delete(userRole);
    }
}
