package cn.edu.whu.zhuyuhan.demo1.controller;

import cn.edu.whu.zhuyuhan.demo1.entity.Friend;
import cn.edu.whu.zhuyuhan.demo1.dao.IFriendDao;
import org.apache.ibatis.session.SqlSession;
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

    /**
    @Autowired
    FriendList friendList;
     */
    @Autowired
    SqlSession session;

    /**
     * 查询所有
     * @param model
     * @param httpSession
     * @return
     */
    @GetMapping(value = "/friends")
    public String list(Model model, HttpSession httpSession){
        /*InputStream in = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(in);
        SqlSession session = factory.openSession();*/
        IFriendDao friendDao = session.getMapper(IFriendDao.class);
        String userName = (String) httpSession.getAttribute("loginUser");
        Collection<Friend> friends = friendDao.getAll(userName);
        session.commit();
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
        IFriendDao friendDao = session.getMapper(IFriendDao.class);
        System.out.println("添加"+friend);
        friendDao.putFriend(friend);
        session.commit();
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
        IFriendDao friendDao = session.getMapper(IFriendDao.class);
        Friend friend = friendDao.getById(id);
        System.out.println("回显" + friend);
        model.addAttribute("reFriend", friend);
        session.commit();
        return "friend/add";
    }

    /**
     * 修改
     * @param friend
     * @return
     */
    @PutMapping(value = "/friends/edit")
    public String edit(Friend friend){
        IFriendDao friendDao = session.getMapper(IFriendDao.class);
        System.out.println("修改"+friend);
        friendDao.editFriend(friend);
        session.commit();
        return "redirect:/friends";
    }

    /**
     * 根据id删除
     * @param id
     * @return
     */
    @PostMapping(value = "/friends/{id}")
    public String delete(@PathVariable("id") Integer id){
        System.out.println(id);
        IFriendDao friendDao = session.getMapper(IFriendDao.class);
        System.out.println("删除"+friendDao.getById(id));
        friendDao.deleteById(id);
        session.commit();
        return "redirect:/friends";
    }
}
