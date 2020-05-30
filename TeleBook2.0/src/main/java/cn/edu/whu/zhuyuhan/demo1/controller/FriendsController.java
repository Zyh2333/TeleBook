package cn.edu.whu.zhuyuhan.demo1.controller;

import cn.edu.whu.zhuyuhan.demo1.entity.Friend;
import cn.edu.whu.zhuyuhan.demo1.dao.IFriendDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Collection;

/**
 * 处理通讯录的增删改查请求
 *
 * @author Zhuyuhan
 * @date 2020-05-16 15:52
 */
@Controller
public class FriendsController {

    @Autowired
    IFriendDao friendDao;
    /**
     * 查询所有
     * @param model
     * @param httpSession
     * @return
     */
    @GetMapping(value = "/friends")
    public String list(Model model, HttpSession httpSession){
        String userName = (String) httpSession.getAttribute("loginUser");
        Collection<Friend> friends = friendDao.getByName(userName);
        model.addAttribute("friends",friends);
        return "friend/list";
    }
    //跳转到添加页面
    @GetMapping(value = "/friends/add")
    public String gotoAdd(){
        return "friend/add";
    }

    /**
     * 增
     * @param friend
     * @param httpSession
     * @return
     */
    //添加数据
    //保证对象名一致SpringMVC将自动绑定Post对象数据
    @PostMapping(value = "/friends/edit")
    public String add(Friend friend,HttpSession httpSession){
        String userName = (String) httpSession.getAttribute("loginUser");
        friend.setUserName(userName);
        System.out.println("用户:"+userName+"添加"+friend);
        System.out.println("-----------------------------------------------------------------");
        friendDao.putFriend(friend);
        //重定向可以重定向到任何位置
        return "redirect:/friends";
    }

    /**
     * 根据id查询并回显
     * @param id
     * @param model
     * @return
     */
    @GetMapping(value = "/friends/{id}")
    public String gotoEdit(@PathVariable("id") int id, Model model){
        Friend friend = friendDao.getById(id);
        //System.out.println("回显" + friend);
        //System.out.println("-----------------------------------------------------------------");
        model.addAttribute("reFriend", friend);
        return "friend/add";
    }

    /**
     * 修改
     * @param friend
     * @return
     */
    @PutMapping(value = "/friends/edit")
    public String edit(Friend friend,HttpSession httpSession){
        String userName = (String) httpSession.getAttribute("loginUser");
        System.out.println("用户:"+userName+"修改"+friend);
        System.out.println("-----------------------------------------------------------------");
        friendDao.editFriend(friend);
        return "redirect:/friends";
    }

    /**
     * 根据id删除
     * @param id
     * @return
     */
    @GetMapping(value = "/friendsDelete/{id}")
    public String delete(@PathVariable("id") Integer id,HttpSession httpSession){
        String userName = (String) httpSession.getAttribute("loginUser");
        System.out.println("用户:"+userName+"删除"+friendDao.getById(id));
        System.out.println("-----------------------------------------------------------------");
        friendDao.deleteById(id);
        return "redirect:/friends";
    }
}
