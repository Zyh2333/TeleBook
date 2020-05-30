package cn.edu.whu.zhuyuhan.demo1.controller;

import cn.edu.whu.zhuyuhan.demo1.dao.IUserDao;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Zhuyuhan
 * @date 2020-05-23 19:16
 */
@Controller
public class ImageController {

    /**上传地址*/
    @Value("${file.upload.path}")
    private String filePath;

    //得到数据库工具
    @Autowired
    private SqlSession session;

    @GetMapping(value = "/main/imageAdd")
    public String toImage(){
        return "image";
    }
    // 执行上传
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
        //System.out.println(httpSession.getAttribute("loginUser"));
        //存储的是相对路径
        userDao.updateImage((String) httpSession.getAttribute("loginUser"),"/images/"+filename);
        session.commit();
        return "image";
    }
    @GetMapping(value = "/back")
    public String back(HttpSession httpSession){
        //回显图片
        IUserDao userDao = session.getMapper(IUserDao.class);
        String imagePath = userDao.loadImage((String) httpSession.getAttribute("loginUser"));
        httpSession.setAttribute("image",imagePath);
        return "redirect:/main.html";
    }
}
