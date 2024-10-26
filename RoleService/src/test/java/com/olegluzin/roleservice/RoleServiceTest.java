package com.olegluzin.roleservice;

import com.olegluzin.roleservice.model.Role;
import com.olegluzin.roleservice.repository.RoleRepository;
import com.olegluzin.roleservice.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RoleServiceTest {
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateRole_Success() {
        Role role = new Role(null, "USER", "Standard user role");

        when(roleRepository.save(role)).thenReturn(role);

        Role createdRole = roleService.createRole(role);
        assertEquals("USER", createdRole.getName());
        assertEquals("Standard user role", createdRole.getDescription());

        verify(roleRepository, times(1)).save(role);
    }

    @Test
    public void testGetRoleById_RoleExists() {
        Role role = new Role(1L, "ADMIN", "Admin role");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        Role foundRole = roleService.getRoleById(1L);
        assertNotNull(foundRole);
        assertEquals("ADMIN", foundRole.getName());

        verify(roleRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetRoleById_RoleNotFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> roleService.getRoleById(1L));
        verify(roleRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateRole() {
        Role role = new Role(1L, "USER", "Standard user role");
        Role updatedRole = new Role(1L, "USER", "Updated user role");

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.save(updatedRole)).thenReturn(updatedRole);

        Role result = roleService.updateRole(1L, updatedRole);
        assertEquals("Updated user role", result.getDescription());

        verify(roleRepository, times(1)).findById(1L);
        verify(roleRepository, times(1)).save(updatedRole);
    }


    @Test
    public void testDeleteRole_Success() {
        Long roleId = 1L;
        Role role = new Role(roleId, "ADMIN", "Admin role");

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        doNothing().when(roleRepository).deleteById(roleId);

        roleService.deleteRole(roleId);

        verify(roleRepository, times(1)).findById(roleId);
        verify(roleRepository, times(1)).deleteById(roleId);
    }

    @Test
    public void testDeleteRole_NotFound() {
        Long roleId = 1L;

        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            roleService.deleteRole(roleId);
        });

        assertEquals("Role not found with id: 1", thrown.getMessage());
        verify(roleRepository, times(1)).findById(roleId);
        verify(roleRepository, never()).deleteById(roleId);
    }
}
