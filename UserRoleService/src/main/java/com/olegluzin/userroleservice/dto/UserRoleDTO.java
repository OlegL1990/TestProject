package com.olegluzin.userroleservice.dto;


import lombok.Value;

@Value
public class UserRoleDTO {
    private Long userId;
    private Long roleId;
    private String username;
    private String roleName;
}
