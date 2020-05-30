本身是后端开发，负责开发数据库的持久层，故使用Mybatis框架来学习开发。开发工具IDEA（不得不说，真的好用）。


# 初见

零开始学习Mybits的开发，首先就是学习如何去使用，学习“框架的框架”，首先导入依赖

```


<dependencies>
    <dependency>
        <groupId>org.mybatis</groupId>
        <artifactId>mybatis</artifactId>
        <version>3.5.4</version>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.20</version>
    </dependency>
    <dependency>
        <groupId>log4j</groupId>
        <artifactId>log4j</artifactId>
        <version>1.2.17</version>
    </dependency>
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>3.8.1</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

当然MySQL和mybatis包是很重要的，但是日志和单元测试需不需要就不好说了，为保险起见还是都导入进去。
但是首先就遇到了一个大问题，**依赖下载速度过慢**。解决方法，更改setting文件

```java
<?xml version="1.0" encoding="UTF-8"?>
  <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
      <mirrors> <!-- mirror
           | Specifies a repository mirror site to use instead of a given repository. The repository that
           | this mirror serves has an ID that matches the mirrorOf element of this mirror. IDs are used
           | for inheritance and direct lookup purposes, and must be unique across the set of mirrors.
           |
         <mirror>
           <id>mirrorId</id>
           <mirrorOf>repositoryId</mirrorOf>
           <name>Human Readable Name for this Mirror.</name>
           <url>http://my.repository.com/repo/path</url>
         </mirror>
          -->
         <mirror>
             <id>alimaven</id>
             <name>aliyun maven</name>
             <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
             <mirrorOf>central</mirrorOf>
         </mirror>
         <mirror>
             <id>uk</id>
             <mirrorOf>central</mirrorOf>
             <name>Human Readable Name for this Mirror.</name>
             <url>http://uk.maven.org/maven2/</url>
         </mirror>
         <mirror>
             <id>CN</id>
             <name>OSChina Central</name>
             <url>http://maven.oschina.net/content/groups/public/</url>
             <mirrorOf>central</mirrorOf>
         </mirror>
         <mirror>
             <id>nexus</id>
             <name>internal nexus repository
             </name> <!-- <url>http://192.168.1.100:8081/nexus/content/groups/public/</url>-->
             <url>http://repo.maven.apache.org/maven2</url>
             <mirrorOf>central</mirrorOf>
         </mirror>
     </mirrors>
 </settings>
```
大概就是下载阿里的镜像，速度果然正常了。


**

## 大致架构与配置文件

**
![大概架构](https://img-blog.csdnimg.cn/20200530224152407.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjc2Mzgw,size_16,color_FFFFFF,t_70)
一般来说就是这样的，做后端的持久层开发要注意几个文件：log4j.properties, SqlMapConfig,xml. 这是后端配置的最基本的注册文件。
log4j.properties:
`log4j.rootCategory=debug, CONSOLE, LOGFILE
log4j.logger.org.apache.axis.enterprise=FATAL, CONSOLE
log4j.appender.CONSOLE=org.apache.log4j.ConsoleAppender
log4j.appender.CONSOLE.layout=org.apache.log4j.PatternLayout
log4j.appender.CONSOLE.layout.ConversionPattern=%d{ISO8601} %-6r {%15, 15t} %-5p %30.30c %x - %m
log4j.appender.LOGFILE=org.apache.log4j.FileAppender
log4j.appender.LOGFILE.File=d:/axis.log
log4j.appender.LOGFILE.Append=true
log4j.appender.LOGFILE.layout=org.apache.log4j.PatternLayout
log4j.appender.LOGFILE.layout.ConversionPattern=%d{ISO8601} %-6r {%15, 15t} %-5p %30.30c %x - %m`
日志的配置（虽然我也不知道有什么用），测试后果然报错

```
log4j:ERROR Unexpected char [,] at position 22 in conversion patterrn.
log4j:ERROR Unexpected char [,] at position 22 in conversion patterrn.
2020-05-30 22:39:39,123 0      {%15, 15t} DEBUG ache.ibatis.logging.LogFactory  - Logging initialized using 'class org.apache.ibatis.logging.log4j.Log4jImpl' adapter.2020-05-30 22:39:39,206 83     {%15, 15t} DEBUG source.pooled.PooledDataSource  - PooledDataSource forcefully closed/removed all connections.2020-05-30 22:39:39,207 84     {%15, 15t} DEBUG source.pooled.PooledDataSource  - PooledDataSource forcefully closed/removed all connections.2020-05-30 22:39:39,207 84     {%15, 15t} DEBUG source.pooled.PooledDataSource  - PooledDataSource forcefully closed/removed all connections.2020-05-30 22:39:39,207 84     {%15, 15t} DEBUG source.pooled.PooledDataSource  - PooledDataSource forcefully closed/removed all connections.2020-05-30 22:39:39,365 242    {%15, 15t} DEBUG ansaction.jdbc.JdbcTransaction  - Opening JDBC ConnectionException in thread "main" org.apache.ibatis.exceptions.PersistenceException:
 ```
所以就不要这两行了，果然没有问题了。
然后就是SqlMapConfig.xml
这个文件用于数据库的连接配置，当然后面的资源映射也需要用到它。
具体配置信息如下：

```java
<dataSource type="POOLED">
    <!--配置连接数据库的基本信息-->
    <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
    <property name="url" value="jdbc:mysql://localhost:3306/mydatisdb?serverTimezone=UTC"/>
    <property name="username" value="root"/>
    <property name="password" value="123456"/>
</dataSource>
```


## 实例测试
构建基本的类

```java
public class User implements Serializable{
    private Integer id;
    private String username;
    private Date birthday;
    private String sex;
    private String address;
    }
```
**注意：这里要继承Serializable，搭建数据库的时候，数据库的结构要和创建的数据类保持一致**

![数据库结构需和数据类保持一致](https://img-blog.csdnimg.cn/20200530230000772.jpg)
构建接口

```java
public interface IUserDao {
     List<User> findAll();
     }
```
写映射及映射文件
IUserDao.xml

```
<mapper namespace="com.dao.IUserDao">
    <select id="findAll" resultType="com.domain.User">
        select * from user;
    </select>
</mapper>
```
这里可以直接使用sql语言。而且它会自动实现反序列化，直接得到User类。

以及SqlMapConfig.xml的映射

```
<mappers>
    <mapper resource="com.dao.IUserDao.xml"/>
</mappers>

```
测试，这里使用sql提供的工厂模式进行创建

```java
//读取配置文件
InputStream in = Resources.getResourceAsStream("SqlMapConfig.xml");
//创建SqlSessionFactory工厂
SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
SqlSessionFactory factory = builder.build(in);
//使用工程生产对象
SqlSession session = factory.openSession();
//使用SqlSession创建Dao接口的代理对象
IUserDao userDao = session.getMapper(IUserDao.class);
```
然而，第一次测试果然有问题。

```java
Exception in thread "main" org.apache.ibatis.exceptions.PersistenceException: 
### Error building SqlSession.
### The error may exist in com.dao.IUserDao.xml
### Cause: org.apache.ibatis.builder.BuilderException: Error parsing SQL Mapper Configuration. Cause: java.io.IOException: Could not find resource com.dao.IUserDao.xml
```
提示找不到文件IUserDao.xml。经过不断的实验发现，资源是目录存储，而项目是包存储。故只需改资源映射为

```
<mappers>
    <mapper resource="com/dao/IUserDao.xml"/>
</mappers>
```
即可。当时参考的教程所使用的是旧版本，故有所差异。

## 后续的一些小问题
**一**、再测试的时候后来发现时间数据会有所差异，比如在java代码里面插入的时间为：2017-08-21 17:29:56

但是在数据库里面显示的时间却为：2017-08-21 09:29:56
甚至有时再数据库中会看不到修改的数据。
原因：时间设置问题，需要再数据库连接配置时加上时区限制

```
<property name="url" value="jdbc:mysql://localhost:3306/mydatisdb?serverTimezone=UTC"/>
```
**二**、考虑到可能的sql注入攻击，如有参数传递的时候，其语法为

```java
<insert id="insert" parameterType="com.domain.User">
    insert into user value (#{arg0},#{arg1},#{arg2},#{arg3},#{arg4});
</insert>
```
会直接进行参数的传递，由于不确定Mybatis框架所使用的时字符串拼接的方式还是参数传递来匹配的方式。
事实上这和所使用的标识符有关，使用#是参数传递来进行匹配的方式，而使用$则是直接进行拼接，会导致可能的sql注入攻击。

————————————————————————


 [参考mybatis实例链接](https://blog.csdn.net/hellozpc/article/details/80878563).
 [视频资料](https://www.bilibili.com/video/BV1Db411s7F5?from=search&seid=9050283690417691514).


