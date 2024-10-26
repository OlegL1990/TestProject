package com.olegluzin.userroleservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olegluzin.userroleservice.client.RoleClient;
import com.olegluzin.userroleservice.client.UserClient;
import com.olegluzin.userroleservice.config.TestSecurityConfig;
import com.olegluzin.userroleservice.dto.RoleDTO;
import com.olegluzin.userroleservice.dto.UserDTO;
import com.olegluzin.userroleservice.model.UserRole;
import com.olegluzin.userroleservice.model.UserRoleId;
import com.olegluzin.userroleservice.repository.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Transactional
@Import(TestSecurityConfig.class)
public class UserRoleControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @MockBean
    private UserClient userClient;

    @MockBean
    private RoleClient roleClient;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        userRoleRepository.deleteAll();
    }

    @Test
    public void testGetUserRoles_Success() throws Exception {

        UserDTO mockUser = new UserDTO(1L, "testUser");
        RoleDTO mockRole = new RoleDTO(1L, "ADMIN");

        Mockito.when(userClient.getUserById(1L)).thenReturn(mockUser);
        Mockito.when(roleClient.getRoleById(1L)).thenReturn(mockRole);


        UserRole userRole = new UserRole(1L, 1L);
        userRoleRepository.save(userRole);


        mockMvc.perform(get("/api/users/1/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1L))
                .andExpect(jsonPath("$[0].roleId").value(1L))
                .andExpect(jsonPath("$[0].username").value("testUser"))
                .andExpect(jsonPath("$[0].roleName").value("ADMIN"));
    }

    @Test
    public void testAssignRoleToUser_Success() throws Exception {

        Mockito.when(userClient.getUserById(1L)).thenReturn(new UserDTO(1L, "testUser"));
        Mockito.when(roleClient.getRoleById(2L)).thenReturn(new RoleDTO(2L, "MODERATOR"));


        mockMvc.perform(post("/api/users/1/roles/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.roleId").value(2L));


        UserRoleId userRoleId = new UserRoleId();
        userRoleId.setUserId(1L);
        userRoleId.setRoleId(2L);
        Optional<UserRole> userRole = userRoleRepository.findById(userRoleId);
        assertTrue(userRole.isPresent());
    }

    @Test
    public void testAssignRoleToUser_UserNotFound() throws Exception {

        Mockito.when(userClient.getUserById(1L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));


        mockMvc.perform(post("/api/users/1/roles/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAssignRoleToUser_RoleNotFound() throws Exception {

        Mockito.when(userClient.getUserById(1L)).thenReturn(new UserDTO(1L, "testUser"));
        Mockito.when(roleClient.getRoleById(2L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));


        mockMvc.perform(post("/api/users/1/roles/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testRemoveRoleFromUser_Success() throws Exception {

        Mockito.when(userClient.getUserById(1L)).thenReturn(new UserDTO(1L, "testUser"));
        Mockito.when(roleClient.getRoleById(2L)).thenReturn(new RoleDTO(2L, "MODERATOR"));


        UserRole userRole = new UserRole(1L, 2L);
        userRoleRepository.save(userRole);


        mockMvc.perform(delete("/api/users/1/roles/2"))
                .andExpect(status().isNoContent());


        UserRoleId userRoleId = new UserRoleId();
        userRoleId.setUserId(1L);
        userRoleId.setRoleId(2L);
        Optional<UserRole> deletedUserRole = userRoleRepository.findById(userRoleId);
        assertFalse(deletedUserRole.isPresent());
    }

    @Test
    public void testRemoveRoleFromUser_NotFound() throws Exception {
        mockMvc.perform(delete("/api/users/1/roles/2"))
                .andExpect(status().isNotFound());
    }
}
