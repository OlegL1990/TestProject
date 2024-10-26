package com.olegluzin.userroleservice.client;

import com.olegluzin.userroleservice.config.FeignClientConfig;
import com.olegluzin.userroleservice.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "UserService", url = "${user.service.url}", configuration = FeignClientConfig.class)
public interface UserClient {
    @GetMapping("/api/users/{userId}")
    UserDTO getUserById(@PathVariable("userId") Long userId);
}
