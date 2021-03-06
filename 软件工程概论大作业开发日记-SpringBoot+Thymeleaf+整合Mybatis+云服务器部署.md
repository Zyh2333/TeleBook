# 软件工程概论大作业开发日记

## 一、利用spring boot框架搭建后端平台

### 学习总结DAY1

> 静态文件目录：
>
> ``` 
> "classpath:/META-INF/resources/", 
> "classpath:/resources/",
> "classpath:/static/", 
> "classpath:/public/" 
> "/"：当前项目的根路径
> ```
>
> 访问 localhost:8080/ 下默认都将从该路径下找文件，如：index.html  

#### 1.引入 thymeleaf 模板引擎协助开发

```xml
<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-thymeleaf</artifactId>
          	2.1.6
		</dependency>
切换thymeleaf版本
<properties>
		<thymeleaf.version>3.0.9.RELEASE</thymeleaf.version>
		<!-- 布局功能的支持程序  thymeleaf3主程序  layout2以上版本 -->
		<!-- thymeleaf2   layout1-->
		<thymeleaf-layout-dialect.version>2.2.2</thymeleaf-layout-dialect.version>
  </properties>
```

只要我们把HTML页面放在classpath:/templates/，thymeleaf就能自动渲染；

```xml
//导入命名空间
<html lang="en" xmlns:th="http://www.thymeleaf.org">
```

thymeleaf语法略；

#### 2.需要注意的是

```java
WebMvcConfigurerAdapter 已被弃用
继承WebMvcConfigurationSupport将会导致springboot自动配置失败
    //错误示例
    //所以这种方法好像也过时了,这种直接继承WebMvcConfigurationSupport的方法会使SpringMVC自动配置失效
    /*@Bean
    public WebMvcConfigurationSupport loginConfig(){
        return new WebMvcConfigurationSupport(){
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                //super.addViewControllers(registry);
                registry.addViewController("/").setViewName("index");
                registry.addViewController("/index.html").setViewName("index");
            }
        };
    }*/
```

应该改为实现WebMvcConfigurer接口：

```java
public class LoginMVCConfiguererAdapter implements WebMvcConfigurer {

    //映射
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //super.addViewControllers(registry);
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/index.html").setViewName("index");

    }
}
```

第二种方法无法使用。

#### 3.引入国际化

注意配置国际化文件夹

```properties
spring.messages.basename=universe.login
```

``` java
//重写国际化接口，按照http请求参数不同返回不同的Locale对象
public class myLocaleResolver implements LocaleResolver {

    @Override
    public Locale resolveLocale(HttpServletRequest httpServletRequest) {
        Locale locale = Locale.getDefault();
        String localeParameter = httpServletRequest.getParameter("l");
        if(!localeParameter.isEmpty()){
            String[] split = localeParameter.split("_");
            locale = new Locale(split[0],split[1]);
        }
        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Locale locale) {

    }
}
```

当然需要在相应的html文件中添加按钮触发的http请求参数，同时应该满足thymeleaf语法。

**最后在主配置类中添加Bean到spring容器中！！！**

#### 每日debug：没有注意方法名一致导致无法实现locale对象替换

```java
@Bean
		@ConditionalOnMissingBean
		@ConditionalOnProperty(prefix = "spring.mvc", name = "locale")
		public LocaleResolver localeResolver() {
			if (this.mvcProperties
					.getLocaleResolver() == WebMvcProperties.LocaleResolver.FIXED) {
				return new FixedLocaleResolver(this.mvcProperties.getLocale());
			}
			AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
			localeResolver.setDefaultLocale(this.mvcProperties.getLocale());
			return localeResolver;
		}
默认的就是根据请求头带来的区域信息获取Locale进行国际化
    //@ConditionalOnMissingBean
    //注意方法名一致，不然视为没有该组件，将会自动配置！！！
```

------------------------------------

### 学习总结DAY2

#### 1.对thymeleaf的理解

>模板引擎的作用除了方便开发，重点应该在于构建前后端的解析桥梁
>
>还带有一些内置方法动态控制html标签: th:if（条件判断）
>
>```html
><form class="form-signin" action="dashboard.html" th:action="@{/user/login}" method="post">
>			<img class="mb-4" src="asserts/img/bootstrap-solid.svg" alt="" width="72" height="72">
>			<h1 class="h3 mb-3 font-weight-normal" th:text="#{login.tip}">Please sign in</h1>
>			<!--判断-->
>			<p style="color: red" th:text="${msg}" th:if="${not #strings.isEmpty(msg)}"></p>
>```

#### 2.关于http协议的几种方式

GET:请求头传输相应信息

POST:有请求体，请求体主要利用JSON 、Xml格式传输动态参数

PUT:有请求体，请求体主要利用JSON 、Xml格式传输动态参数，完成更新任务

DELETE:删除请求

**对应的注解**

```java
//    @DeleteMapping
//    @PutMapping
//    @GetMapping

    //@RequestMapping(value = "/user/login",method = RequestMethod.POST)
    //@PostMapping
```

#### 3.设置拦截器

```java
/**
 * 登陆检查，
 */
public class LoginHandlerInterceptor implements HandlerInterceptor {
    //目标方法执行之前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object user = request.getSession().getAttribute("loginUser");
        if(user == null){
            //未登陆，返回登陆页面
            request.setAttribute("msg","没有权限请先登陆");
            request.getRequestDispatcher("/index.html").forward(request,response);
            return false;
        }else{
            //已登陆，放行请求
            return true;
        }

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
```

注册拦截器到springboot中，即在MyMvcConfigurer类中重写下述方法：

```java
 //注册拦截器
         @Override
         public void addInterceptors(InterceptorRegistry registry) {
                //super.addInterceptors(registry);
                //静态资源；  *.css , *.js
                //SpringBoot已经做好了静态资源映射
                registry.addInterceptor(new LoginHandlerInterceptor()).addPathPatterns("/**")
                        .excludePathPatterns("/index.html","/","/user/login");
         }
```

#### 每日debug:关于拦截器的拦截范围

虽然参照教程说是springboot自动设置了静态资源映射，不会受到拦截器干扰，但是实践中发现还是会拦截CSS样式，所以修改排除路径如下：

```java
@Override
    public void addInterceptors(InterceptorRegistry registry) {
        //super.addInterceptors(registry);
        //拦截器注意排除主页及静态资源
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**")
                .excludePathPatterns("/index.html", "/", "/user/login","/asserts/**","/webjars/**");
    }
```

### 学习总结DAY3

#### 1.学习如何将sprinboot打包成jar包发布

+ **腾讯云服务器的配置**

  参照[腾讯云官方网站教程](https://cloud.tencent.com/document/product/213/2936)，购买学生机CentOS7.6.0_x6（linux发行版）

+ **学习linux基础知识及shell基本使用**

  参考关于如何部署springboot应用到服务器端，参考[腾讯云社区文章](https://cloud.tencent.com/developer/article/1530145)得知需要在服务器上安装jdk，以及如何部署项目，由于有内置tomcat倒是省去了配置tomcat 

  输入vim /etc/profile 命令后进入环境变量配置文件夹，在末尾利用vim的输入模式添加如下：

  ![image-20200515121412836](https://picturebed-1301866798.cos.ap-chengdu.myqcloud.com/image-20200515121412836.png)

  同时参考[菜鸟教程](https://www.runoob.com/linux/linux-vim.html)学习vim编辑器修改环境变量，这里比较艰辛，开始不会用vim的三种模式。   

+ **使用FileZllia传输文件到服务器上去**

  利用ftp协议传输文件

+ **使用linux命令行运行**

   ![image-20200515121156968](https://picturebed-1301866798.cos.ap-chengdu.myqcloud.com/image-20200515121156968.png)

​        注意想要后台运行（关闭终端后仍可运行）需要在项目后添加 > log.file 2>&1 &  如上图所示

#### 每日debug：关于服务器如何保持后台运行

刚才被骗了，实际上上述命令并不能解决后台运行，查找[资料](https://blog.csdn.net/qq_36487585/article/details/95031164)得知解决方案：

简而言之就是安装screen帮助我们后台单独开启一个端口号保持程序一直运行，在该端口下运行程序即可：

![image-20200515123225493](https://picturebed-1301866798.cos.ap-chengdu.myqcloud.com/image-20200515123225493.png)

screen -r 可以回到当前运行进程

screen -ls 可以查看screen下运行情况

screen -S 创建会话

screen -X -S num quit 停止运行

C-a d : detached

[Screen的基本指令](https://www.cnblogs.com/ywl925/p/3604530.html)

### 学习总结DAY4

#### 1.深入理解thymeleaf

模板引擎的作用在今天更加明晰了，它不仅能帮助前后端传输进行数据绑定（选择器），同时将静态资源转化成相对的路径，例如：

![image-20200516162853604](https://picturebed-1301866798.cos.ap-chengdu.myqcloud.com/image-20200516162853604.png)

这样替换原有访问链接，可以实现相对路径访问或是webjars资源访问更为灵活，不会因为改变文件夹结构而影响资源访问

##### 公共元素抽取

```html
1、抽取公共片段
<div th:fragment="copy">
&copy; 2011 The Good Thymes Virtual Grocery
</div>

2、引入公共片段
<div th:insert="~{footer :: copy}"></div>
~{templatename::selector}：模板名::选择器
~{templatename::fragmentname}:模板名::片段名

3、默认效果：
insert的公共片段在div标签中
如果使用th:insert等属性进行引入，可以不用写~{}：
行内写法可以加上：[[~{}]];[(~{})]；
```

便于抽取页面间公共元素，减少工作量

三种引入公共片段的th属性：

**th:insert**：将公共片段整个插入到声明引入的元素中

**th:replace**：将声明引入的元素替换为公共片段

**th:include**：将被引入的片段的内容包含进这个标签中

```html
<footer th:fragment="copy">
&copy; 2011 The Good Thymes Virtual Grocery
</footer>

引入方式
<div th:insert="footer :: copy"></div>
<div th:replace="footer :: copy"></div>
<div th:include="footer :: copy"></div>

效果
<div>
    <footer>
    &copy; 2011 The Good Thymes Virtual Grocery
    </footer>
</div>

<footer>
&copy; 2011 The Good Thymes Virtual Grocery
</footer>

<div>
&copy; 2011 The Good Thymes Virtual Grocery
</div>
```

引入片段的时候传入参数： 

```html
<nav class="col-md-2 d-none d-md-block bg-light sidebar" id="sidebar">
    <div class="sidebar-sticky">
        <ul class="nav flex-column">
            <li class="nav-item">
                <a class="nav-link active"
                   th:class="${activeUri=='main.html'?'nav-link active':'nav-link'}"
                   href="#" th:href="@{/main.html}">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-home">
                        <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
                        <polyline points="9 22 9 12 15 12 15 22"></polyline>
                    </svg>
                    Dashboard <span class="sr-only">(current)</span>
                </a>
            </li>

<!--引入侧边栏;传入参数-->
<div th:replace="commons/bar::#sidebar(activeUri='emps')"></div>
```

可实现动态CSS样式

#### 2.完成联系人列表页

暂时用HashMap模拟一些联系人及他们的数据，展示在list.html上

注意Model对象的注入和th:each标签的使用

效果如下图：

![image-20200516212405311](https://picturebed-1301866798.cos.ap-chengdu.myqcloud.com/FriendList.jpg)

 #### 3.利用Post方式完成联系人添加功能

```java
//添加数据
    //保证对象名一致SpringMVC将自动绑定Post对象数据
    @PostMapping(value = "/friends/add")
    public String add(Friend friend){
        friendList.putFriend(friend);
        //重定向可以重定向到任何位置
        return "redirect:/friends";
    }
```

添加后自动返回list页面并且利用重定向重载联系人

效果如图：

![image-20200516213753515](https://picturebed-1301866798.cos.ap-chengdu.myqcloud.com/image-20200516213753515.png)

#### 4.对用springboot开发Web应用的一点理解

逐渐领悟到一个叫做公共域的概念，这是通过http协议联系前后端的纽带，封装交给springboot框架，包括数据绑定与公共域对象的使用，例如：Map<>,Model,session等

而thymeleaf引擎所提供的是通过它的解析简化前端开发同时更具动态感

#### 每日debug:关于thymeleaf公共元素抽取与替换的语法

应当注意模板路径的查找应使用 / 而不是 . 

元素名前应用 :: 隔开，如果使用id查找，前面还需添加 # 

### 学习总结DAY5

#### 1.利用Get方式回显联系人

主要是通过路径参数将联系人id传回Controller，再通过id返回Friend对象

#### 2.利用Put方式修改联系人

使用put方式时应当注意设置"_method"属性为post（用隐藏input设置）

设置方法如下:

```html
<!--设置put方式几个步骤：
                        1、SpringMVC中配置HiddenHttpMethodFilter;（SpringBoot自动配置好的）
						2、页面创建一个post表单
						3、创建一个input项，name="_method";值就是我们指定的请求方式
                -->
<input type="hidden" name="_method" th:value="put" th:if="${reFriend!=null}">
```

同时put方式还应当将id传回，所以再设置一个隐藏input框

```html
<input type="hidden" name="id" th:value="${reFriend.getId()}" th:if="${reFriend!=null}">
```

#### 3.修改与添加复用add.html应当注意的是

由于是否有回显对象的存在：

如果是添加的话，reFriend==null，此时如果不加判断，则会出现空指针错误

如果是修改的话，reFriend!=null，这时候才需要进行回显

所以应当用th:if或者三元运算符进行判断，复用时一定要考虑空指针的现象

#### 4.利用delete方式删除联系人

需将删除button放在新建的form表单中进行删除，但是由于每个按钮一个表单过于冗余且不美观，抽取成一个表单，利用javascript代码进行表单提交

注意自定义属性th:attr的使用，这样可以根据不同联系人提交不同地址的表单实现删除功能

#### 5.定制错误页面

具体方式：

由于使用了thymeleaf引擎，错误页面应放在templates/error文件夹下，命名为status状态码.html

否则放在静态资源文件夹static/error文件夹下，命名相同

具体原理参照ErrorMvcAutoConfiguration类的自动配置原理

如果出错，主要自动产生以下属性：

timestamp：时间戳

status：状态码

error：错误提示

exception：异常对象

message：异常消息

errors：JSR303数据校验的错误都在这里

#### 6.在服务器上部署mysql数据库

[参考资料](https://blog.csdn.net/runner1920/article/details/79495368?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-2.nonecase&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-2.nonecase)

安装好后，启动mysql服务：

systemctl start mysqld

设置开机启动：

systemctl enable mysqld 

systemctl daemon-reload

查找安装时初始化的默认密码：

grep 'temporary password' /var/log/mysqld.log

![](https://picturebed-1301866798.cos.ap-chengdu.myqcloud.com/mysql%E5%88%9D%E5%A7%8B%E5%AF%86%E7%A0%81.png)

修改密码：

![](https://picturebed-1301866798.cos.ap-chengdu.myqcloud.com/%E4%BF%AE%E6%94%B9mysql%E5%AF%86%E7%A0%81.png)

尝试建表：

![](https://picturebed-1301866798.cos.ap-chengdu.myqcloud.com/%E5%B0%9D%E8%AF%95%E5%BB%BA%E8%A1%A8.png)

修改host以便远程访问：

![](https://picturebed-1301866798.cos.ap-chengdu.myqcloud.com/%E4%BF%AE%E6%94%B9host.png)     

成功在本机远程访问服务器mysql并查询结果：

![](https://picturebed-1301866798.cos.ap-chengdu.myqcloud.com/%E8%BF%9C%E7%A8%8B%E8%AE%BF%E9%97%AE.png)

默认安全组中开启了所有端口协议，若没有设置，则需开启3306端口供远程访问

#### 每日debug：解决服务器mysql不支持中文的编码问题

[参考资料](http://blog.sina.com.cn/s/blog_96b8a1540101j9r9.html)

![](https://picturebed-1301866798.cos.ap-chengdu.myqcloud.com/%E4%BF%AE%E6%94%B9%E7%BC%96%E7%A0%81%E9%85%8D%E7%BD%AE.png)

开始修改配置的时候不小心用#注释掉了，对vim的使用还不熟悉

修改后编码都改为了utf8但是依然无法支持中文待解决：

![](https://picturebed-1301866798.cos.ap-chengdu.myqcloud.com/%E4%BE%9D%E7%84%B6%E6%97%A0%E6%B3%95%E6%94%AF%E6%8C%81%E4%B8%AD%E6%96%87%E5%BE%85%E8%A7%A3%E5%86%B3.png)

**今日尝试debug失败，明日再战**    

--------------------------------

### 学习总结DAY6

#### 1.整合Mybatis框架对数据库的操作

后端主要是我和刘维帆同学搭档，今天他把写好的Mybatis对数据库的增删改查发给我了，包括用户表users和联系人表friend，于是今天着手进行整合，把之前模拟的HashMap替换为对数据库的CRUD操作

修改了以下内容：

##### 1.@Bean SqlSession对象

由于多处地方需要使用到该对象创建代理对象，所以使用该方式放入Spring容器，使用时注入即可

于是修改配置类，添加如下代码：

```java
@Bean
    public SqlSession session() throws IOException {
        InputStream in = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(in);
        SqlSession session = factory.openSession();
        return session;
    }
```

以免过多重复代码

##### 2.修改生日字段，添加birthString字段

如果直接将Date对象调用toString()方法存入数据库，在取出的时候就会报错：

![](https://picturebed-1301866798.cos.ap-chengdu.myqcloud.com/Day6-bug1.jpg)

也就是Mybatis框架不支持该种类型的字符串转化为Date对象，于是进行格式化后再存入数据库

```java
public Friend(String name, String schoolName, String email, String gender, Date birth,String tel,Integer id) {
        this.name = name;
        this.schoolName = schoolName;
        this.email = email;
        this.gender = gender;
        this.birth = birth;
        this.tel = tel;
        this.id = id;
        this.birthString = format.format(birth);
    }
```

成功解决问题

#### 每日debug：删除按钮失效

不知道为什么删除按钮点击没反应，后台Contorller根本没接收到请求，推测应该是整合的时候把前端的一些代码改错了，但是目前还没有发现错误所在



至此，增差改都已顺利完成

### 学习总结DAY7

#### 1.整合前端修改好的界面

前端的同学历经艰辛总算把界面修改的任务完成了，新添加了一些**细节方面**的功能：

**设置退出登录按钮**，点击后将退回登录界面以便切换账号或者单纯想退出登录

**修改bar.html**，凸显该项目的三个主要功能：

![](https://picturebed-1301866798.cos.ap-chengdu.myqcloud.com/%E4%B8%BB%E8%A6%81%E5%8A%9F%E8%83%BD.jpg)

登录后的首页修改如下：

![](https://picturebed-1301866798.cos.ap-chengdu.myqcloud.com/%E9%A6%96%E9%A1%B51.1.jpg)



**修改index.html**

英文界面：

![](https://picturebed-1301866798.cos.ap-chengdu.myqcloud.com/login.en.jpg)





中文界面：

![](https://picturebed-1301866798.cos.ap-chengdu.myqcloud.com/login.zh.jpg)



点击按钮即可切换

#### 每日debug:今日无bug✌

### 学习总结DAY8

#### 1.完成用户自定义上传头像并显示

前端代码做以下修改：

```html
 <div class="btn-group change-avatar col-sm-1">
                        <form th:action="@{/main/imageUpload}" method="post" enctype="multipart/form-data">
                            <input type="file" name="file">
                            <br>
                            <input type="submit" value="上传" class="btn btn-success">
                        </form>
 </div>
```

当用户需要上传文件时，应该使用 type="file" 以供用户选择文件

后端代码如下：

```java
@PostMapping("/main/imageUpload")
    public String upload(@RequestParam("file") MultipartFile file, Model model, HttpSession httpSession) {
        // 获取上传文件名
        String filename = file.getOriginalFilename();
        // 定义上传文件保存路径,即绝对路径
        String path = filePath;
        //获取图片后缀名
        String suffixName = filename.substring(filename.lastIndexOf("."));
        //新建UUID
        String uuid = UUID.randomUUID().toString();
        //合并生成唯一文件名
        filename = uuid + suffixName;
        // 新建文件
        File filepath = new File(path, filename);
        // 判断路径是否存在，如果不存在就创建一个
        if (!filepath.getParentFile().exists()) {
            filepath.getParentFile().mkdirs();
        }
        try {
            // 写入文件
            //path:D://images/
            file.transferTo(new File(path + filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 将src路径发送至html页面
        model.addAttribute("filename", "/images/"+filename);
        //将路径存入数据库
        IUserDao userDao = session.getMapper(IUserDao.class);
        System.out.println(httpSession.getAttribute("loginUser"));
        //存储的是相对路径
        userDao.updateImage((String) httpSession.getAttribute("loginUser"),"/images/"+filename);
        session.commit();
        return "image";
    }
```

MultipartFile类提供了获取前端文件及保存到本地的方法，所以该处应为绝对路径：

file.transferTo(new File(path + filename));

然后再将图片的相对路径存放到数据库中，通过资源映射找到绝对路径，并且在资源文件中配置相对路径和绝对路径，这样当需要更改时更改资源文件即可。

映射资源方法如下：

```java
@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(fileRelativePath).
                addResourceLocations("file:/" + filePath);
    }
```

不仅图片文件，其他文件同样适用

#### 2.完成自定义头像持久化

将相对路径存入数据库

```java
 //将路径存入数据库
        IUserDao userDao = session.getMapper(IUserDao.class);
        System.out.println(httpSession.getAttribute("loginUser"));
        //存储的是相对路径
        userDao.updateImage((String) httpSession.getAttribute("loginUser"),"/images/"+filename);
        session.commit();
```

在首次登录和返回首页时重载头像

```java
 //获取数据库中的相对路径
            String imagePath = userDao.loadImage(userName);
            System.out.println(imagePath);
            session.setAttribute("image",imagePath);
```

```java
@GetMapping(value = "/back")
public String back(HttpSession httpSession){
    //回显图片
    IUserDao userDao = session.getMapper(IUserDao.class);
    String imagePath = userDao.loadImage((String) httpSession.getAttribute("loginUser"));
    httpSession.setAttribute("image",imagePath);
    return "redirect:/main.html";
}
```

#### 每日debug：关于HttpSession对象的thymeleaf提取语法

今天犯了很傻的错误，HttpSession对象的thymeleaf提取语法应该是这样的：

```html
  <div id="head" class="circle"
                             style="width: 100px;height: 100px;overflow: hidden;border-radius: 50px;border: 1px solid #0062CC;">
                            <!--从后端获取到model传递的相对路径通过资源映射到绝对路径 -->
                            <img id="myuserhead" src=".." th:src="@{${session.image}}" th:if="${session.image!=null}"
                                 style="width: 100px;height: 100px;border-radius: 50px;">
                        </div>
```

但是我误以为和Model对象一样直接使用${}进行提取，这样后端不会报错，很难找到问题

**以后应该这样排查此类问题：**

查看网页源代码观察前端代码是否被thymeleaf引擎成功解析，而不是傻傻地猜来猜去

### 学习总结DAY9

#### 1.完成用户首次登录自动注册的功能

对LoginController做以下改动

```java
@PostMapping(value = "/user/login")
    public String login(@RequestParam("userName") String userName,
                        @RequestParam("password")String password,
                        Map<String,Object> map, HttpSession session){
        IUserDao userDao = userSession.getMapper(IUserDao.class);
        //登录
        if(userDao.logIn(userName)!=null) {
            if (password.equals(userDao.logIn(userName).getPassword())) {
                //防止表单重复提交，添加重定向功能，即不要重复直接返回资源
                //session保存上一页面的数据到跳转页面
                session.setAttribute("loginUser", userName);
                //获取数据库中的相对路径
                String imagePath = userDao.loadImage(userName);
                //System.out.println(imagePath);
                session.setAttribute("image", imagePath);
                userSession.commit();
                return "redirect:/main.html";
            } else {
                map.put("msg", "用户名或密码错误");
                return "index";
            }
        }
        //注册
        else {
            userDao.signIn(userName,password);
            //session保存上一页面的数据到跳转页面
            session.setAttribute("loginUser", userName);
            //获取数据库中的相对路径
            String imagePath = userDao.loadImage(userName);
            session.setAttribute("image", imagePath);
            userSession.commit();
            return "redirect:/main.html";
        }
    }
```

#### 2.friend表添加userName列实现多用户分离

修改Mybatis连接friend表的配置文件，修改增、查的部分：IFriendDao.xml , IFriendDao.java（这部分由刘维帆完成）

再修改Controller部分添加userName字段即可

也就是给user表添加了一个外键指向friend以实现多用户分离

#### 3.添加留言板页面

暂时只完成了获取用户输入的留言到后端，之后再存储到数据库和回显即可

```java
@GetMapping(value = "/message")
    public String toMessage(){
        return "message";
    }

    @PostMapping(value = "/message/submit")
    public String submitMessage(@RequestParam("Message") String message){
        System.out.println(message);
        return "message";
    }
```

完成跳转和post方式获取留言到后端

#### 每日debug

考虑到美观因素，我们将上传图片以弹窗的形式进行。但是由于关闭窗口若在前端使用JS代码关闭则会出现form无法提交的问题，所以改为在form标签外添加一个按钮“返回”来关闭窗口，确保图片信息已保存到数据库中

### 学习总结DAY10

#### 1.整合SpringBoot和Mybatis

本来分开在本地用确实是没问题的，以为在服务器上也是没问题的。但是送到服务器上发现jar包它无法找到关于Mybatis配置的xml文件，到网上一查才知道之前那种根本不叫整合，只是拼凑到了一起，于是开始整合两个框架

由于之前部署到服务器上存在xml文件找不到的问题，干脆整合的时候改成了注解版，这样也更简介

修改如下：

```java
@Mapper
public interface IFriendDao {
    /**
     * 添加操作
     */
    @Insert("insert into friend(id,name,schoolName,email,gender,birthString,tel,userName)values(#{id},#{name},#{schoolName},#{email},#{gender},#{birthString},#{tel},#{userName})")
    void putFriend(Friend friend);
    @Select("select * from friend where userName=#{arg0}")
    Collection<Friend> getByName(String userName);
    @Select("select * from friend where id=#{arg0}")
    Friend getById(Integer id);
    @Delete("delete from friend where id=#{arg0}")
    void deleteById(Integer id);
    @Update("update friend set id=#{id},name=#{name},schoolName=#{schoolName},email=#{email},gender=#{gender},birthString=#{birthString},tel=#{tel} where id=#{id}")
    void editFriend(Friend friend);
    }

```

```java
@Mapper
public interface IUserDao {
    @Select("select * from users where userName=#{userName}")
    User logIn(String userName);
    @Insert("insert into users(userName,password,image) values (#{userName},#{passWord},'/images/default.jpg')")
    void signIn(String userName, String passWord);
    @Update("update users set image=#{path} where userName=#{userName}")
    void updateImage(String userName, String path);
    @Select("select image from users where userName=#{arg0}")
    String loadImage(String userName);

}
```

#### 2.添加第一次登录自动注册并设置默认头像功能

其实就是用户名在数据库中查找结果为null时，选择insert方式将用户名和密码插入users表并更新头像路径为默认路径（对应默认头像）

代码如下：

```java
@PostMapping(value = "/user/login/")
    public String login(@RequestParam("userName") String userName,
                        @RequestParam("password")String password,
                        Map<String,Object> map, HttpSession session){
        //登录
        if(userDao.logIn(userName)!=null) {
            if (password.equals(userDao.logIn(userName).getPassword())) {
                System.out.println("用户:"+userName+"成功登录");
                System.out.println("-----------------------------------------------------------------");
                //防止表单重复提交，添加重定向功能，即不要重复直接返回资源
                //session保存上一页面的数据到跳转页面
                session.setAttribute("loginUser", userName);
                return "redirect:/main.html";
            } else {
                map.put("msg", "用户名或密码错误");
                return "index";
            }
        }
        //注册
        else {
            userDao.signIn(userName,password);
            //session保存上一页面的数据到跳转页面
            userDao.updateImage(userName,"/default.jpg");
            session.setAttribute("loginUser", userName);
            System.out.println("用户:"+userName+"成功注册"+" 已设置默认头像");
            System.out.println("-----------------------------------------------------------------");
            session.setAttribute("image", userName);
            return "redirect:/main.html";
        }
    }
```

当然在修改了头像读取方式后相应代码也做了变动，反而比原来更简单

现在只需要返回用户名到前端，加载图片时就会再调用头像Controller进行获取了

#### 3.修改头像读取方式（含每日debug）

好不容易整合好了，于是部署到服务器上又发生了以下一连串的问题：

![](https://picturebed-1301866798.cos.ap-chengdu.myqcloud.com/1.png)

上网查了说是无法识别主机名，也就是usr这个名字没有和我的服务器绑定，于是修改/etc/hosts文件如下：

![](https://picturebed-1301866798.cos.ap-chengdu.myqcloud.com/5.png)

也就是加上了127.0.0.1 usr

本以为就完事了，结果又发生了下面的异常：

![](https://picturebed-1301866798.cos.ap-chengdu.myqcloud.com/3.png)

然后上网查要么就是端口没开要么就是数据库服务没启动，但是我的程序正常启动，数据库插入也正常

后来问了同学才知道我的图片存取有问题，客户端也就是浏览器是不能直接访问服务器的静态资源的，所以要先通过Controller加载到servlet中，于是修改如下：

```java
@GetMapping(value = "/image/{loginUser}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable("loginUser") String userName) throws Exception{
        System.out.println("开始加载"+userName+"的头像");
        byte[] imageContent ;
        //获取数据库中的相对路径
        String imagePath = userDao.loadImage(userName);
        //获取相对文件名如/**.jpg
        String suffixPath = imagePath.substring(imagePath.lastIndexOf("/"));
        String suffix = suffixPath.substring(1);
        //绝对路径
        String inputPath = this.path+suffix;
        //System.out.println(inputPath);
        imageContent = fileToByte(new File(inputPath));
        System.out.println("成功加载"+userName+"的自定义头像");
        System.out.println("-----------------------------------------------------------------");
        final HttpHeaders headers = new HttpHeaders();
        //返回为jpg格式
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(imageContent, headers, HttpStatus.OK);
    }

    public static byte[] fileToByte(File img) throws Exception {
        byte[] bytes = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            BufferedImage bi;
            bi = ImageIO.read(img);
            ImageIO.write(bi, "jpg", baos);
            bytes = baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            baos.close();
        }
        return bytes;
    }
```

也就是读取成byte[]并封装后返回

前端代码则修改成这样：

```html
<img id="myuserhead" src=".." th:src="@{'/image/'+${session.loginUser}}"
                                 style="width: 100px;height: 100px;border-radius: 50px;">
```

说明图片应该单独用Controller来读取而不是通过thymeleaf引擎来自己去资源映射，否则服务器就会拒绝客户端的请求而报错

当然本以为byte[] thymeleaf引擎是不支持解析的，好在网上给出了[解决方案](https://www.jianshu.com/p/477b11a0edfb)

总的来说，现在是通过用户名查询数据库得到相对路径，在Controller中转化为绝对路径并读取服务器的数据转化成byte[]后返回浏览器

这样即是Controller也就是服务器端运行的程序访问图片文件而不是浏览器直接访问，不会被拒绝访问了

### 学习总结DAY11

#### 1.整合前端CSS

前端同学完成了背景图添加以及一些美化效果，在整合时开始找不到资源，因为thymeleaf对于静态资源有特别的规定

现记录如下：

```html
<style type="text/css">

					.myd1{
						margin: 0px;
						padding: 0px;
						z-index: -1;
						width: 53rem;
						height:35rem;
						background-image: url('[[@{/asserts/img/71.jpg}]]');
						background-repeat: no-repeat;
						background-size: cover;
					}
				</style>
```

大概就是css中的url需要用内联表达式也就是[[]]，并且由于是链接还应该加上@{}

[参考链接](https://my.oschina.net/u/3807066/blog/2208239)

#### 2.添加验证码功能

添加Controller:

```java
@Controller
public class SecurityController {
    @RequestMapping("/getVerifyCode")
    public void getVerificationCode(HttpServletResponse response, HttpServletRequest request) {
        try {
            int width = 200;
            int height = 50;

            BufferedImage verifyImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);//生成对应宽高的初始图片
            String randomText = VerifyCodeUtil.drawRandomText(width, height, verifyImg);//单独的一个类方法，出于代码复用考虑，进行了封装。功能是生成验证码字符并加上噪点，干扰线，返回值为验证码字符

            request.getSession().setAttribute("verifyCode", randomText);
            response.setContentType("image/png");//必须设置响应内容类型为图片，否则前台不识别

            OutputStream os = response.getOutputStream(); //获取文件输出流
            ImageIO.write(verifyImg, "png", os);//输出图片流
            os.flush();
            os.close();//关闭流
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

创造验证码及验证码图片的工具类：

```java
public class VerifyCodeUtil {
    public static String drawRandomText(int width, int height, BufferedImage verifyImg) {

        Graphics2D graphics = (Graphics2D) verifyImg.getGraphics();
        graphics.setColor(Color.WHITE);//设置画笔颜色-验证码背景色
        graphics.fillRect(0, 0, width, height);//填充背景
        graphics.setFont(new Font("微软雅黑", Font.BOLD, 30));

        //数字和字母的组合
        String baseNumLetter = "123456789abcdefghijklmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";

        StringBuilder builder = new StringBuilder();
        int x = 10;  //旋转原点的 x 坐标
        String ch;
        Random random = new Random();

        for (int i = 0; i < 4; i++) {
            graphics.setColor(getRandomColor());

            //设置字体旋转角度
            int degree = random.nextInt() % 30;  //角度小于30度
            int dot = random.nextInt(baseNumLetter.length());

            ch = baseNumLetter.charAt(dot) + "";
            builder.append(ch);

            //正向旋转
            graphics.rotate(degree * Math.PI / 180, x, 45);
            graphics.drawString(ch, x, 45);

            //反向旋转
            graphics.rotate(-degree * Math.PI / 180, x, 45);
            x += 48;
        }

        //画干扰线
        for (int i = 0; i < 6; i++) {
            // 设置随机颜色
            graphics.setColor(getRandomColor());

            // 随机画线
            graphics.drawLine(random.nextInt(width), random.nextInt(height),
                    random.nextInt(width), random.nextInt(height));

        }

        //添加噪点
        for (int i = 0; i < 30; i++) {
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);

            graphics.setColor(getRandomColor());
            graphics.fillRect(x1, y1, 2, 2);
        }
        return builder.toString();
    }

    /**
     * 随机取色
     */
    private static Color getRandomColor() {
        Random ran = new Random();
        return new Color(ran.nextInt(256),
                ran.nextInt(256), ran.nextInt(256));
    }
```

前端代码修改：

```html
<div>
					<a href="javascript:void(0);" title="点击更换验证码">
						<img th:src="@{/getVerifyCode}" onclick="changeCode()" class="verifyCode"/>
					</a>
				</div>
				<br>
				<input type="text" name="verifyCode" class="form-control" th:placeholder="验证码" required="">
<!--对应的JS -->
<script src="../static/js/jquery.min.js" th:src="@{/webjars/jquery/3.0.0/jquery.min.js}"></script>
	<script>
		function changeCode() {
			const src = "/getVerifyCode?" + new Date().getTime(); //加时间戳，防止浏览器利用缓存
			$('.verifyCode').attr("src", src);
		}
	</script>
```

JS代码用来重新生成验证码，加上时间戳防止重复提交表单

登录Controller加上验证码验证部分：

```java
@PostMapping(value = "/user/login/")
    public String login(@RequestParam("userName") String userName,
                        @RequestParam("password")String password,
                        @RequestParam("verifyCode") String verifyCode,
                        Map<String,Object> map, HttpSession session){
        String code = (String)session.getAttribute("verifyCode");
        //System.out.println(code);
        if (verifyCode.equalsIgnoreCase(code)) {
            //登录
            if(userDao.logIn(userName)!=null) {
                if (password.equals(userDao.logIn(userName).getPassword())) {
                    System.out.println("用户:"+userName+"成功登录");
                    System.out.println("-----------------------------------------------------------------");
                    //防止表单重复提交，添加重定向功能，即不要重复直接返回资源
                    //session保存上一页面的数据到跳转页面
                    session.setAttribute("loginUser", userName);
                    return "redirect:/main.html";
                } else {
                    map.put("msg", "用户名或密码错误");
                    return "index";
                }
            }
            //注册
            else {
                userDao.signIn(userName,password);
                //session保存上一页面的数据到跳转页面
                userDao.updateImage(userName,"/default.jpg");
                session.setAttribute("loginUser", userName);
                System.out.println("用户:"+userName+"成功注册"+" 已设置默认头像");
                System.out.println("-----------------------------------------------------------------");
                session.setAttribute("image", userName);
                return "redirect:/main.html";
            }
        }
        else {
            map.put("msg", "验证码错误");
            return "index";
        }
```

[参考资料](https://blog.csdn.net/qq_40548741/article/details/105311766)

然后我发现thymeleaf引擎虽说不能前后端分离，还是有好处的，类似于这里的用response对象开流直接写出图片，前端就能解析

（所以之前的头像应该做复杂了，不过算是两种方式返回图片，一种是流方式，一种是byte[]）

#### 每日debug

我真的是醉了，老是被自己的拦截器拦住图片资源，一开始还发现不了以为是thymeleaf的问题，以后应该先关掉拦截器再对Controller进行修改防止被拦截，当然还有更简便的方法就是注意url的命名规则，**无非就是因为乱命名导致拦截器工作异常**，其实不是拦截器的锅，是自己的习惯不好，以后多加注意

### 学习总结 DAY12

#### 1.完成留言板前后端衔接

主要是css样式优化，将留言设置为书签形式，调整背景什么的，在前端寇笑宇优化的基础之上与后台进行衔接

#### 每日debug

最后一次debug了，发现留言谁都可以删，本打算的是只能操作自己的，于是修改如下：

```html
<div th:each="message:${messages}"  style="float: left;width: 300px;height: 60px;border-radius: 280px;border: 1px solid #0062CC;text-align: center;line-height: 30px;background-image: url('/asserts/img/back1.jpg');">
					<span th:text="${#dates.format(message.getDate(),'yyyy-MM-dd')}")></span> <span th:text="${message.getUserName()}"></span> <span th:text="${message.getContext()}"></span>
					<br/>
					<a th:if="${session.loginUser==message.getUserName()}" th:href="@{/messagesDelete/}+${message.getId()}"style="color: red;">删除</a>
				</div>
```

也就是加了个th:if标签，登录名与便签名相等时删除按钮方才生效



## 完工