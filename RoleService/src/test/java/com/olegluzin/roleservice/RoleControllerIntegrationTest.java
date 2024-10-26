package com.olegluzin.roleservice;
import com.olegluzin.roleservice.config.TestSecurityConfig;
import com.olegluzin.roleservice.model.Role;
import com.olegluzin.roleservice.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RoleServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@Transactional
public class RoleControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        roleRepository.deleteAll();
    }

    @Test
    public void testCreateRole_Success() throws Exception {
        Role roleDTO = new Role(null, "ADMIN", "Administrator role");

        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ADMIN"))
                .andExpect(jsonPath("$.description").value("Administrator role"));
    }

    @Test
    public void testGetRoleById_Success() throws Exception {
        Role role = new Role();
        role.setName("USER");
        role.setDescription("User role");
        Role savedRole = roleRepository.save(role);

        mockMvc.perform(get("/api/roles/{id}", savedRole.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("USER"))
                .andExpect(jsonPath("$.description").value("User role"));
    }

    @Test
    public void testGetRoleById_NotFound() throws Exception {
        mockMvc.perform(get("/api/roles/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllRoles_Success() throws Exception {
        Role role1 = new Role(null, "ADMIN", "Admin role");
        Role role2 = new Role(null, "USER", "User role");
        roleRepository.save(role1);
        roleRepository.save(role2);

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("ADMIN"))
                .andExpect(jsonPath("$[1].name").value("USER"));
    }

  @Test
    public void testUpdateRole_Success() throws Exception {
        Role role = new Role(null, "MODERATOR", "Moderator role");
        Role savedRole = roleRepository.save(role);

        Role updatedRole = new Role(null, "MODERATOR", "Updated Moderator role");

        mockMvc.perform(put("/api/roles/{id}", savedRole.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedRole)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("MODERATOR"))
                .andExpect(jsonPath("$.description").value("Updated Moderator role"));

        Optional<Role> updatedRoleResult = roleRepository.findById(savedRole.getId());
        assertTrue(updatedRoleResult.isPresent());
        assertEquals("Updated Moderator role", updatedRoleResult.get().getDescription());
    }

    @Test
    public void testDeleteRole_Success() throws Exception {
        Role role = new Role(null, "TEMP", "Temporary role");
        Role savedRole = roleRepository.save(role);

        mockMvc.perform(delete("/api/roles/{id}", savedRole.getId()))
                .andExpect(status().isNoContent());

        Optional<Role> deletedRole = roleRepository.findById(savedRole.getId());
        assertFalse(deletedRole.isPresent());
    }

    @Test
    public void testCreateRole_DuplicateName() throws Exception {
        Role role = new Role(null, "DUPLICATE", "Original role");
        roleRepository.save(role);

        Role duplicateRoleDTO = new Role(null, "DUPLICATE", "Attempted duplicate");

        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRoleDTO)))
                .andExpect(status().isConflict());
    }
}
