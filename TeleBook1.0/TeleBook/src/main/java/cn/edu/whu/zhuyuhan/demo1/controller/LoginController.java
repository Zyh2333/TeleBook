package cn.edu.whu.zhuyuhan.demo1.controller;

import cn.edu.whu.zhuyuhan.demo1.config.ImageConfig;
import cn.edu.whu.zhuyuhan.demo1.config.MyMvcConfigurerAdapter;
import cn.edu.whu.zhuyuhan.demo1.entity.User;
import cn.edu.whu.zhuyuhan.demo1.dao.IUserDao;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author Zhuyuhan
 * @date 2020-05-14 19:56
 */
@Controller
public class LoginController {

    @Autowired
    User user;

    @Autowired
    SqlSession userSession;

    //@RequestParam
    //从请求参数中获取相应的值
    @PostMapping(value = "/user/login")
    public String login(@RequestParam("userName") String userName,
                        @RequestParam("password")String password,
                        Map<String,Object> map, HttpSession session){
        IUserDao userDao = userSession.getMapper(IUserDao.class);
        System.out.println(userDao.logIn(userName).getPassword());
        if (password.equals(userDao.logIn(userName).getPassword())) {
            //防止表单重复提交，添加重定向功能，即不要重复直接返回资源
            //session保存上一页面的数据到跳转页面
            session.setAttribute("loginUser", userName);
            //获取数据库中的相对路径
            String imagePath = userDao.loadImage(userName);
            System.out.println(imagePath);
            session.setAttribute("image",imagePath);
            userSession.commit();
            return "redirect:/main.html";
        } else {
            map.put("msg", "用户名或密码错误");
            return "index";
        }
    }
}
