package com.olegluzin.userroleservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class UserRoleServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserRoleServiceApplication.class, args);
    }

}
