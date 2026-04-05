package com.haidong.tuanwei.auth.entity;

import lombok.Data;

@Data
public class AdminUser {

    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String phone;

    private Boolean enabled;
}
