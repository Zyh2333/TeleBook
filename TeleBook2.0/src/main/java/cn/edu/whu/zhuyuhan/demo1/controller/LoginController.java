package cn.edu.whu.zhuyuhan.demo1.controller;

import cn.edu.whu.zhuyuhan.demo1.dao.IUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author Zhuyuhan
 * @date 2020-05-14 19:56
 */
@Controller
public class LoginController {

    @Autowired
    IUserDao userDao;

    //@RequestParam
    //从请求参数中获取相应的值
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
    }
    /*@PostMapping(value = "/user/login")
    public ModelAndView dashboardLoadImage(@RequestParam("userName") String userName){
        System.out.println("头像Controller响应");
        ModelAndView view = new ModelAndView("dashboard");
        System.out.println(userName);
        view.addObject("imageHost", userName);
        return view;
    }*/
}
