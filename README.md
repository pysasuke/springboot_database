# springboot_database
## 项目介绍
springboot1.5.7集成mybatis、jpa、redis、mongodb，对不同数据库进行操作的实例
## 项目结构
### main
#### dao:数据库交互层
- jpa:jpa与mysql数据库交互类
```
public interface JpaUserRepository extends JpaRepository<JpaUser, Long> {

    //jpa有默认实现类似findBy*(支持*And*)方法，其中*号为实体属性名(名称写的不对会报错哦)
    JpaUser findByUsername(String username);

    @Query("select count(u.id) from JpaUser u")
    long count();

    @Query("select u from JpaUser u where u.id=:id")
    JpaUser findUserById(@Param("id") long id);
}
```
- mongo:mongodb数据库交互类
```
public interface MongoUserRepository extends MongoRepository<MgUser, Long> {

    //有默认实现类似findBy*(支持*And*)方法，其中*号为实体属性名
    MgUser findByNickname(String nickname);

    //根据id为条件搜索(默认实现的findOne方法是以mongodb中ObjectId为条件的)
    MgUser findById(Long id);
}
```
- mybatis:mybatis与mysql数据库交互类
```
//@Mapper(SpringbootDatabaseApplication类中使用了@MapperScan注解,故此处可以省略)
public interface TuserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Tuser record);

    Tuser selectByPrimaryKey(Long id);

    Tuser selectByUserName(String username);

    int updateByPrimaryKeySelective(Tuser record);

}
```
#### entity:实体对象层
- jpa:jpa操作mysql相关实体对象
```
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
```
- mongo:mongodb相关实体对象
```
@Data
//需要与mongo中对象(文档)名一致
public class MgUser {
    @Id
    private ObjectId _id;    //插入mongo时会自动生成_id，如果不加这个字段则会把id属性当成_id
    @Field
    private Long id;
    private String nickname;
    private String phone;
}
```
- mybatis:mybatis操作mysql相关实体对象
```
@Data
public class Tuser {
    private Long id;
    private String username;
    private String password;
    private String mobile;
    private String email;
}
```
- redis:redis相关实体对象
```
//redisTemplate操作对象时对象需要实现序列化接口
@Data
public class Person implements Serializable{
    private Long serialVersionUID=1L;
    private Long id;
    private String username;
    private String password;
}
```
#### SpringbootDatabaseApplication:springboot启动类
```
@SpringBootApplication
// mapper 接口类扫描包配置(可以省去dao层类上注解@Mapper)
@MapperScan("com.py.springboot_database.dao.mybatis")
public class SpringbootDatabaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootDatabaseApplication.class, args);
	}
}
```
### resources
- mapper:mybatis实体类映射文件存放包
- application.properties:springboot配置文件
```
## 数据源配置
spring.datasource.url=jdbc:mysql://localhost:3306/springboot_database?useUnicode=true&characterEncoding=utf8
spring.datasource.username=root
spring.datasource.password=py123456
spring.datasource.driver-class-name=com.mysql.jdbc.Driver

## Mybatis 配置
##指向实体类包路径
mybatis.typeAliasesPackage=com.py.springboot_database.entity.mybatis
##配置为 classpath 路径下 mapper 包下，* 代表会扫描所有 xml 文件
mybatis.mapperLocations=classpath:mapping/*.xml

## mongodb配置
spring.data.mongodb.uri=mongodb://localhost:27017/test

## Redis 配置
## Redis数据库索引（默认为0）
spring.redis.database=0
## Redis服务器地址
spring.redis.host=127.0.0.1
## Redis服务器连接端口
spring.redis.port=6379
## Redis服务器连接密码（默认为空）
spring.redis.password=
## 连接池最大连接数（使用负值表示没有限制）
spring.redis.pool.max-active=8
## 连接池最大阻塞等待时间（使用负值表示没有限制）
spring.redis.pool.max-wait=-1
## 连接池中的最大空闲连接
spring.redis.pool.max-idle=8
## 连接池中的最小空闲连接
spring.redis.pool.min-idle=0
## 连接超时时间（毫秒）
spring.redis.timeout=0
```
### test
- jpa:TestJpa类,jpa操作mysql对象增删改查操作测试
```
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringbootDatabaseApplication.class)
public class TestJpa {

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Test
    public void testInsert() {
        long countBefore =jpaUserRepository.count();
        JpaUser jpaUser = generateJpaUser();
        jpaUserRepository.save(jpaUser);
        long countAfter =jpaUserRepository.count();
        Assert.assertEquals(++countBefore,countAfter);
    }

    @Test
    public void testSelect() {
        JpaUser jpaUser = generateJpaUser();
        /*
        插入成功后id会有值(mysql数据库生成的)
        前提是：
        1.数据库主键设置为自动增长
        2.实体对象主键属性上有注解@Id和@GeneratedValue
         */
        jpaUserRepository.save(jpaUser);

        JpaUser selectedJpaUser = jpaUserRepository.findOne(jpaUser.getId());
        Assert.assertNotNull(selectedJpaUser);
        Assert.assertEquals(jpaUser, selectedJpaUser);
    }

    @Test
    public void testUpdate() {
        JpaUser jpaUser = generateJpaUser();
        jpaUserRepository.save(jpaUser);

        JpaUser selectedJpaUser = jpaUserRepository.findOne(jpaUser.getId());
        Assert.assertNotNull(selectedJpaUser);
        selectedJpaUser.setPassword("654321");
        //保存修改后的对象，相当于update操作
        jpaUserRepository.save(selectedJpaUser);
        //findOne(默认实现)方法与findUserById(自己实现)都是根据id查找
        JpaUser updatedJpaUser = jpaUserRepository.findUserById(selectedJpaUser.getId());
        Assert.assertNotNull(updatedJpaUser);
        Assert.assertEquals(selectedJpaUser.getPassword(), updatedJpaUser.getPassword());
    }

    @Test
    public void testDelete() {
        JpaUser jpaUser = generateJpaUser();
        jpaUserRepository.save(jpaUser);

        jpaUserRepository.delete(jpaUser.getId());
        JpaUser selectedJpaUser = jpaUserRepository.findUserById(jpaUser.getId());
        Assert.assertNull(selectedJpaUser);
    }

    private JpaUser generateJpaUser() {
        JpaUser jpaUser = new JpaUser();
        jpaUser.setUsername("zhangsan");
        jpaUser.setPassword("123456");
        jpaUser.setMobile("13666666666");
        jpaUser.setEmail("123456@qq.com");
        return jpaUser;
    }
}
```
- mongo:TestMongo类,mongodb测试
```
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
```
- mybatis:TestMybatis类,mybatis操作mysql对象增删改查测试
```
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringbootDatabaseApplication.class)
public class TestMybatis {

    @Autowired
    private TuserMapper tuserMapper;

    @Test
    public void testInsert() {
        Tuser tuser = generateTuser();
        int row = tuserMapper.insert(tuser);
        Assert.assertEquals(1, row);
    }

    @Test
    public void testSelect() {
        Tuser tuser = generateTuser();
        /*
        插入成功后id会有值(mysql数据库生成的)
        前提是：
        1.主键设置为自动增长
        2.xml中insert语句设置有:useGeneratedKeys="true" keyProperty="id"
         */
        int row = tuserMapper.insert(tuser);
        Assert.assertEquals(1, row);

        Tuser selectedTuser = tuserMapper.selectByPrimaryKey(tuser.getId());
        Assert.assertNotNull(selectedTuser);
        Assert.assertEquals(tuser, selectedTuser);
    }

    @Test
    public void testUpdate() {
        Tuser tuser = generateTuser();
        int row = tuserMapper.insert(tuser);
        Assert.assertEquals(1, row);

        Tuser selectedTuser = tuserMapper.selectByPrimaryKey(tuser.getId());
        Assert.assertNotNull(selectedTuser);
        selectedTuser.setPassword("654321");
        tuserMapper.updateByPrimaryKeySelective(selectedTuser);

        Tuser updatedTuser = tuserMapper.selectByPrimaryKey(selectedTuser.getId());
        Assert.assertNotNull(updatedTuser);
        Assert.assertEquals(selectedTuser.getPassword(), updatedTuser.getPassword());
    }

    @Test
    public void testDelete() {
        Tuser tuser = generateTuser();
        int row = tuserMapper.insert(tuser);
        Assert.assertEquals(1, row);

        tuserMapper.deleteByPrimaryKey(tuser.getId());
        Tuser selectedTuser = tuserMapper.selectByPrimaryKey(tuser.getId());
        Assert.assertNull(selectedTuser);
    }

    private Tuser generateTuser() {
        Tuser tuser = new Tuser();
        tuser.setUsername("zhangsan");
        tuser.setPassword("123456");
        tuser.setMobile("13666666666");
        tuser.setEmail("123456@qq.com");
        return tuser;
    }
}
```
- redis:TestRedis类,redis测试
```
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
```
### deploy
- update.sql:数据库sql文件
```
CREATE DATABASE springboot_database;
USE springboot_database;

DROP TABLE IF EXISTS `user` ;

CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `mobile` varchar(20) NOT NULL,
  `email` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
```
### pom.xml
```
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- lombok相关start -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <!-- lombok相关end -->

        <!-- mongodb相关start -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>
        <!-- mongodb相关end -->

        <!-- redis相关start -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-redis</artifactId>
            <version>1.4.7.RELEASE</version>
        </dependency>
        <!-- redis相关end -->

        <!-- mybatis相关start -->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>1.3.1</version>
        </dependency>
        <!-- mybatis相关end -->

        <!-- mysql相关start -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
        <!-- mysql相关end -->

        <!-- jpa相关start -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <!-- jpa相关end -->
        
    </dependencies>
```
  

  

  


