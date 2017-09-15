package com.py.springboot_database.mongo;

import com.py.springboot_database.SpringbootDatabaseApplication;
import com.py.springboot_database.entity.mongo.MgUser;
import com.py.springboot_database.dao.mongo.MongoUserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * mongodb测试
 *
 * @author pysasuke
 * @create 2017-09-15
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringbootDatabaseApplication.class)
public class TestMongo {
    @Autowired
    private MongoUserRepository userRepository;

    @Test
    public void test() {
        MgUser mgUser = new MgUser();
        mgUser.setId(1L);
        mgUser.setNickname("pysasuke");
        mgUser.setPhone("18650140605");
        //插入成功后_id属性有值(mongo数据库生成的)
        userRepository.insert(mgUser);
        //该方法使用的是mongodb中ObjectId为条件的
//        MgUser selectMgUser = userRepository.findOne(1L);
//        MgUser selectMgUser = userRepository.findByNickname("pysasuke");
        MgUser selectMgUser = userRepository.findById(1L);
        Assert.assertEquals(mgUser.getId(), selectMgUser.getId());
    }
}
