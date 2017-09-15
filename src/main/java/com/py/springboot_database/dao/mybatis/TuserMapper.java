package com.py.springboot_database.dao.mybatis;

import com.py.springboot_database.entity.mybatis.Tuser;

//@Mapper(SpringbootDatabaseApplication类中使用了@MapperScan注解,故此处可以省略)
public interface TuserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Tuser record);

    Tuser selectByPrimaryKey(Long id);

    Tuser selectByUserName(String username);

    int updateByPrimaryKeySelective(Tuser record);

}