package cn.edu.whu.zhuyuhan.demo1.controller;

import cn.edu.whu.zhuyuhan.demo1.dao.IMessageDao;
import cn.edu.whu.zhuyuhan.demo1.entity.Message;
import org.apache.ibatis.annotations.Insert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Date;

/**
 * @author Zhuyuhan
 * @date 2020-05-26 20:42
 */
@Controller
public class MessageController {

    @Autowired
    IMessageDao messageDao;

    /**
     * 查询所有
     * @return
     */
    @GetMapping(value = "/message")
    public String toMessage(Model model){
        Collection<Message> messages = messageDao.getAll();
        model.addAttribute("messages",messages);
        return "message";
    }

    @PostMapping(value = "/message/submit")
    public String submitMessage(Message message, HttpSession httpSession){
        String userName = (String)httpSession.getAttribute("loginUser");
        message.setUserName(userName);
        message.setDate(new Date());
        System.out.println("用户"+userName+"留言了："+message);
        messageDao.addMessage(message);
        return "redirect:/message";
    }

    @GetMapping(value = "/messagesDelete/{id}")
    public String deleteMessage(@PathVariable("id") Integer id){
        Message message = messageDao.getMessageById(id);
        messageDao.deleteMessageById(id);
        String userName = message.getUserName();
        System.out.println("用户"+userName+"删去了留言："+message);
        return "redirect:/message";
    }
}
