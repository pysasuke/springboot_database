package com.py.springboot_database.redis;

import com.py.springboot_database.SpringbootDatabaseApplication;
import com.py.springboot_database.entity.redis.Person;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;

/**
 * redis测试
 *
 * @author pysasuke
 * @create 2017-09-15
 */
/*
当redis数据库里面本来存的是字符串数据或者要存取的数据就是字符串类型数据的时候，
那么使用StringRedisTemplate即可，
但是如果数据是复杂的对象类型，而取出的时候又不想做任何的数据转换，直接从Redis里面取出一个对象，
那么使用RedisTemplate是更好的选择。
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringbootDatabaseApplication.class)
public class TestRedis {
    //操作String
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    //操作对象
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testPutString() {
        stringRedisTemplate.opsForValue().set("testPutString", "123456");
    }

    @Test
    public void tesGetString() {
        stringRedisTemplate.opsForValue().set("tesGetString", "123456");
        String str = stringRedisTemplate.opsForValue().get("tesGetString");
        Assert.assertEquals("123456", str);
        //设置缓存时间
        stringRedisTemplate.opsForValue().set("tesGetString", "123456", 1000, TimeUnit.MILLISECONDS);//有效时间1s
        try {
            Thread.sleep(2000);//等待2s(等待设置为1s的时候后面可能取到值，redis的过期时间有延迟)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        str = stringRedisTemplate.opsForValue().get("tesGetString");
        Assert.assertNull(str);
    }

    //注意：redisTemplate操作对象时对象需要实现序列化接口
    @Test
    public void testObject() {
        Person person = generatePerson();
        redisTemplate.opsForValue().set("testObject:" + person.getUsername(), person, 600);
    }

    @Test
    public void tesGetObject() {
        Person person = generatePerson();
        redisTemplate.opsForValue().set("tesGetObject:" + person.getUsername(), person);
        Person cachePerson = (Person) redisTemplate.opsForValue().get("tesGetObject:" + person.getUsername());
        Assert.assertEquals(person, cachePerson);
    }

    @Test
    public void tesDeleteCache() {
        stringRedisTemplate.opsForValue().set("tesDeleteCache", "123456");
        String cacheStr = stringRedisTemplate.opsForValue().get("tesDeleteCache");
        Assert.assertEquals("123456", cacheStr);

        stringRedisTemplate.delete("tesDeleteCache");
        String DeleteStr = stringRedisTemplate.opsForValue().get("tesDeleteCache");
        Assert.assertNull(DeleteStr);
    }

    //两者操作互不影响
    @Test
    public void testStrRedisTempAndRedisTemp(){
        Person person = generatePerson();
        //生成key为：\xAC\xED\x00\x05t\x00\x04test
        redisTemplate.opsForValue().set("testDiff", person);
        //生称key为：test
        stringRedisTemplate.opsForValue().set("testDiff", "123456");

        String cacheStr = stringRedisTemplate.opsForValue().get("testDiff");
        Assert.assertEquals("123456", cacheStr);

        Person cachePerson = (Person) redisTemplate.opsForValue().get("testDiff");
        Assert.assertEquals(person, cachePerson);
    }

    private Person generatePerson() {
        Person person = new Person();
        person.setId(1L);
        person.setUsername("wangwu");
        person.setPassword("123456");
        return person;
    }
}
