package com.py.springboot_database;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// mapper 接口类扫描包配置(可以省去dao层类上注解@Mapper)
@MapperScan("com.py.springboot_database.dao.mybatis")
public class SpringbootDatabaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootDatabaseApplication.class, args);
	}
}
