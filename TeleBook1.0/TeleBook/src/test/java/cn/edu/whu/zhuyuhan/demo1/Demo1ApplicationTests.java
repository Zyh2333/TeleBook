package cn.edu.whu.zhuyuhan.demo1;

import cn.edu.whu.zhuyuhan.demo1.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class Demo1ApplicationTests {
    @Autowired
    ApplicationContext ioc;
    @Autowired
    User user;
    @Test
    void contextLoads() {
        System.out.println(user);
    }

}
