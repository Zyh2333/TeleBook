package cn.edu.whu.zhuyuhan.demo1.controller;

import cn.edu.whu.zhuyuhan.demo1.dao.IUserDao;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
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
    private String path;
    @Autowired
    IUserDao userDao;

    @GetMapping(value = "/main/imageAdd")
    public String toImage(){
        return "image";
    }
    // 执行上传
    @PostMapping("/main/imageUpload")
    public String upload(@RequestParam("file") MultipartFile file, HttpSession httpSession) {
        // 获取上传文件名
        String filename = file.getOriginalFilename();
        //获取图片后缀名
        String suffixName = filename.substring(filename.lastIndexOf("."));
        //新建UUID
        String uuid = UUID.randomUUID().toString();
        //合并生成唯一文件名
        filename = uuid + suffixName;
        // 新建文件,注意这里是绝对路径
        File filepath = new File(path, filename);
        // 判断路径是否存在，如果不存在就创建一个
        if (!filepath.getParentFile().exists()) {
            filepath.getParentFile().mkdirs();
        }
        try {
            // 写入文件
            file.transferTo(new File(path + filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将路径存入数据库
        //System.out.println(httpSession.getAttribute("loginUser"));
        //存储的是相对路径
        userDao.updateImage((String) httpSession.getAttribute("loginUser"),"/images/"+filename);
        System.out.println("用户"+httpSession.getAttribute("loginUser")+"更换头像成功");
        System.out.println("-----------------------------------------------------------------");
        return "image";
    }

    @GetMapping(value = "/back")
    public String back(){
        //回显图片
        return "redirect:/main.html";
    }

    /**
     * 根据用户名加载图片文件到Controller
     * @param userName
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/image/{loginUser}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable("loginUser") String userName) throws Exception{
        System.out.println("开始加载"+userName+"的头像");
        byte[] imageContent ;
        //获取数据库中的相对路径
        String imagePath = userDao.loadImage(userName);
        //获取相对文件名如/**.jpg
        String suffixPath = imagePath.substring(imagePath.lastIndexOf("/"));
        String suffix = suffixPath.substring(1);
        //绝对路径
        String inputPath = this.path+suffix;
        //System.out.println(inputPath);
        imageContent = fileToByte(new File(inputPath));
        System.out.println("成功加载"+userName+"的自定义头像");
        System.out.println("-----------------------------------------------------------------");
        final HttpHeaders headers = new HttpHeaders();
        //返回为jpg格式
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(imageContent, headers, HttpStatus.OK);
    }

    public static byte[] fileToByte(File img) throws Exception {
        byte[] bytes = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            BufferedImage bi;
            bi = ImageIO.read(img);
            ImageIO.write(bi, "jpg", baos);
            bytes = baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            baos.close();
        }
        return bytes;
    }
}
