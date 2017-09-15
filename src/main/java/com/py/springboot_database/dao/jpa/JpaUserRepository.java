package com.py.springboot_database.dao.jpa;

import com.py.springboot_database.entity.jpa.JpaUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface JpaUserRepository extends JpaRepository<JpaUser, Long> {

    //jpa有默认实现类似findBy*(支持*And*)方法，其中*号为实体属性名(名称写的不对会报错哦)
    JpaUser findByUsername(String username);

    @Query("select count(u.id) from JpaUser u")
    long count();

    @Query("select u from JpaUser u where u.id=:id")
    JpaUser findUserById(@Param("id") long id);
}
