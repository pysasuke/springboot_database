package com.py.springboot_database.entity.mybatis;

import lombok.Data;

@Data
public class Tuser {
    private Long id;
    private String username;
    private String password;
    private String mobile;
    private String email;
}