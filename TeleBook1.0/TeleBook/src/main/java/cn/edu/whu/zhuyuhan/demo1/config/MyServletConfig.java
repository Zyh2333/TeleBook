package cn.edu.whu.zhuyuhan.demo1.config;

import cn.edu.whu.zhuyuhan.demo1.listener.Mylistener;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Zhuyuhan
 * @date 2020-05-23 18:52
 */
@Configuration
public class MyServletConfig {
    @Bean
    public ServletListenerRegistrationBean listener(){
        ServletListenerRegistrationBean<Mylistener> mylistener = new ServletListenerRegistrationBean<>(new Mylistener());
        return mylistener;
    }
}
