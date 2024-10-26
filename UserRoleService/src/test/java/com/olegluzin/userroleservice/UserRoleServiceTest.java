package com.olegluzin.userroleservice;
import com.olegluzin.userroleservice.client.RoleClient;
import com.olegluzin.userroleservice.client.UserClient;
import com.olegluzin.userroleservice.dto.RoleDTO;
import com.olegluzin.userroleservice.dto.UserDTO;
import com.olegluzin.userroleservice.dto.UserRoleDTO;
import com.olegluzin.userroleservice.model.UserRole;
import com.olegluzin.userroleservice.model.UserRoleId;
import com.olegluzin.userroleservice.repository.UserRoleRepository;
import com.olegluzin.userroleservice.service.UserRoleService;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserRoleServiceTest {

    @Mock
    private UserClient userClient;

    @Mock
    private RoleClient roleClient;

    @Mock
    private UserRoleRepository userRoleRepository;

    @InjectMocks
    private UserRoleService userRoleService;

    @Test
    public void testGetUserRoles_Success() {
        Long userId = 1L;
        UserDTO user = new UserDTO(userId, "testUser");
        RoleDTO role = new RoleDTO(2L, "ADMIN");
        UserRole userRole = new UserRole(userId, role.getId());

        when(userClient.getUserById(userId)).thenReturn(user);
        when(userRoleRepository.findByUserId(userId)).thenReturn(List.of(userRole));
        when(roleClient.getRoleById(role.getId())).thenReturn(role);

        List<UserRoleDTO> result = userRoleService.getUserRoles(userId);

        assertEquals(1, result.size());
        assertEquals("testUser", result.get(0).getUsername());
        assertEquals("ADMIN", result.get(0).getRoleName());
        verify(userClient, times(1)).getUserById(userId);
        verify(roleClient, times(1)).getRoleById(role.getId());
    }

    @Test
    public void testGetUserRoles_UserNotFound() {
        Long userId = 1L;

        when(userClient.getUserById(userId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ResponseStatusException thrown = assertThrows(ResponseStatusException.class, () -> {
            userRoleService.getUserRoles(userId);
        });

        assertEquals("404 NOT_FOUND \"User not found with ID: " + userId + "\"", thrown.getMessage());
        verify(userClient, times(1)).getUserById(userId);
        verifyNoMoreInteractions(roleClient, userRoleRepository);
    }

    @Test
    public void testAssignRoleToUser_Success() {
        Long userId = 1L;
        Long roleId = 2L;
        UserDTO user = new UserDTO(userId, "testUser");
        RoleDTO role = new RoleDTO(roleId, "ADMIN");

        when(userClient.getUserById(userId)).thenReturn(user);
        when(roleClient.getRoleById(roleId)).thenReturn(role);
        UserRole userRole = new UserRole(userId, roleId);
        when(userRoleRepository.save(any(UserRole.class))).thenReturn(userRole);

        UserRole result = userRoleService.assignRoleToUser(userId, roleId);

        assertEquals(userId, result.getUserId());
        assertEquals(roleId, result.getRoleId());
        verify(userClient, times(1)).getUserById(userId);
        verify(roleClient, times(1)).getRoleById(roleId);
        verify(userRoleRepository, times(1)).save(any(UserRole.class));
    }

    @Test
    public void testAssignRoleToUser_UserNotFound() {
        Long userId = 1L;
        Long roleId = 2L;

        when(userClient.getUserById(userId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ResponseStatusException thrown = assertThrows(ResponseStatusException.class, () -> {
            userRoleService.assignRoleToUser(userId, roleId);
        });

        assertEquals("404 NOT_FOUND \"User not found with ID: " + userId + "\"", thrown.getMessage());
        verify(userClient, times(1)).getUserById(userId);
        verifyNoMoreInteractions(roleClient, userRoleRepository);
    }

    @Test
    public void testAssignRoleToUser_RoleNotFound() {
        Long userId = 1L;
        Long roleId = 2L;
        UserDTO user = new UserDTO(userId, "testUser");

        when(userClient.getUserById(userId)).thenReturn(user);
        when(roleClient.getRoleById(roleId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));

        ResponseStatusException thrown = assertThrows(ResponseStatusException.class, () -> {
            userRoleService.assignRoleToUser(userId, roleId);
        });

        assertEquals("404 NOT_FOUND \"Role not found with ID: " + roleId + "\"", thrown.getMessage());
        verify(userClient, times(1)).getUserById(userId);
        verify(roleClient, times(1)).getRoleById(roleId);
        verifyNoMoreInteractions(userRoleRepository);
    }

    @Test
    public void testRemoveRoleFromUser_Success() {
        Long userId = 1L;
        Long roleId = 2L;
        UserRoleId userRoleId = new UserRoleId(userId, roleId);
        UserRole userRole = new UserRole(userId, roleId);

        when(userRoleRepository.findById(userRoleId)).thenReturn(Optional.of(userRole));

        userRoleService.removeRoleFromUser(userId, roleId);

        verify(userRoleRepository, times(1)).findById(userRoleId);
        verify(userRoleRepository, times(1)).delete(userRole);
    }

    @Test
    public void testRemoveRoleFromUser_NotFound() {
        Long userId = 1L;
        Long roleId = 2L;
        UserRoleId userRoleId = new UserRoleId(userId, roleId);

        when(userRoleRepository.findById(userRoleId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            userRoleService.removeRoleFromUser(userId, roleId);
        });

        assertEquals("User role not found", thrown.getMessage());
        verify(userRoleRepository, times(1)).findById(userRoleId);
        verify(userRoleRepository, never()).delete(any(UserRole.class));
    }
}
