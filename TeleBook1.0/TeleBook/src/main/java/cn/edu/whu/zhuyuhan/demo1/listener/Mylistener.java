package cn.edu.whu.zhuyuhan.demo1.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletOutputStream;

/**
 * 自定义监听器
 *
 * @author Zhuyuhan
 * @date 2020-05-23 16:50
 */
public class Mylistener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Initializing...web应用成功启动");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Destroying...web应用成功关闭");
    }
}
