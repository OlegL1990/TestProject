package com.olegluzin.userroleservice.controller;

import com.olegluzin.userroleservice.dto.UserRoleDTO;
import com.olegluzin.userroleservice.model.UserRole;
import com.olegluzin.userroleservice.service.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User Role Controller", description = "Управление ролями пользователя")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserRoleController {

    private final UserRoleService userRoleService;

    @Operation(summary = "Получение списка ролей пользователя по ID")
    @GetMapping("/{userId}/roles")
    public ResponseEntity<List<UserRoleDTO>> getUserRoles(@PathVariable Long userId) {
        List<UserRoleDTO> userRoles = userRoleService.getUserRoles(userId);
        return ResponseEntity.ok(userRoles);
    }

    @Operation(summary = "Назначение роли пользователю")
    @PostMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<UserRole> assignRoleToUser(@PathVariable Long userId, @PathVariable Long roleId) {
        UserRole userRole = userRoleService.assignRoleToUser(userId, roleId);
        return ResponseEntity.ok(userRole);
    }

    @Operation(summary = "Удаление роли у пользователя")
    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<Void> removeRoleFromUser(@PathVariable Long userId, @PathVariable Long roleId) {
        userRoleService.removeRoleFromUser(userId, roleId);
        return ResponseEntity.noContent().build();
    }
}
