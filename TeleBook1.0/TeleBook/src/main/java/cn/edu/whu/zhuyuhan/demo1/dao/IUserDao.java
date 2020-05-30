package cn.edu.whu.zhuyuhan.demo1.dao;

import cn.edu.whu.zhuyuhan.demo1.entity.User;

public interface IUserDao {
    User logIn(String userName);
    void signIn(String userName, String passWord);
    void updateImage(String userName, String path);
    String loadImage(String userName);

}
