package com.py.springboot_database.entity.jpa;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity//标记这个类为数据库表映射实体
@Table(name="user")//对应数据库表名
public class JpaUser implements Serializable{
    @Id//标记为主键
    @GeneratedValue//标记主键自增
    private Long id;
    private String username;
    private String password;
    private String mobile;
    private String email;
}