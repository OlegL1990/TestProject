package com.olegluzin.userroleservice.client;

import com.olegluzin.userroleservice.config.FeignClientConfig;
import com.olegluzin.userroleservice.dto.RoleDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "RoleService", url = "${role.service.url}", configuration = FeignClientConfig.class)
public interface RoleClient {
    @GetMapping("/api/roles/{roleId}")
    RoleDTO getRoleById(@PathVariable("roleId") Long roleId);
}
