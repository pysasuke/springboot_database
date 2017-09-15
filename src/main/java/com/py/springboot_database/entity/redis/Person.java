package com.py.springboot_database.entity.redis;

import lombok.Data;

import java.io.Serializable;

//redisTemplate操作对象时对象需要实现序列化接口
@Data
public class Person implements Serializable{
    private Long serialVersionUID=1L;
    private Long id;
    private String username;
    private String password;
}
