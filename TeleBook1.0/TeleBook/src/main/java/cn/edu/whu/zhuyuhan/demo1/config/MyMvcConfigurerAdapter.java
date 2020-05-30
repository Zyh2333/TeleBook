package cn.edu.whu.zhuyuhan.demo1.config;

import cn.edu.whu.zhuyuhan.demo1.component.LoginInterceptor;
import cn.edu.whu.zhuyuhan.demo1.component.MyLocaleResolver;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.io.InputStream;


/**
 * config for login page
 *
 * @author Zhuyuhan
 * @date 2020-05-13 14:52
 */
//扩展的主配置类
//添加javaBean到容器中的类
@Configuration
//WebMvcConfigurerAdapter在spring5中过时
public class MyMvcConfigurerAdapter implements WebMvcConfigurer {

    public static InputStream in;
    //映射
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //super.addViewControllers(registry);
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/index.html").setViewName("index");
        registry.addViewController("/main.html").setViewName("dashboard");

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //super.addInterceptors(registry);
        //拦截器注意排除主页及静态资源
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**")
                .excludePathPatterns("/index.html", "/", "/user/login","/asserts/**","/webjars/**");
    }
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

    @Bean
    public LocaleResolver localeResolver(){
        return new MyLocaleResolver();
    }

    @Bean
    public SqlSession session() throws IOException {
        in = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(in);
        SqlSession session = factory.openSession();
        return session;
    }
}
