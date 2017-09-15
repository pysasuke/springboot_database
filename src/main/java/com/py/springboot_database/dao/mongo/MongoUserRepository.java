package com.py.springboot_database.dao.mongo;

import com.py.springboot_database.entity.mongo.MgUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoUserRepository extends MongoRepository<MgUser, Long> {

    //有默认实现类似findBy*(支持*And*)方法，其中*号为实体属性名
    MgUser findByNickname(String nickname);

    //根据id为条件搜索(默认实现的findOne方法是以mongodb中ObjectId为条件的)
    MgUser findById(Long id);
}
