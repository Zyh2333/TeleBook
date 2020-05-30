package cn.edu.whu.zhuyuhan.demo1.dao;

import cn.edu.whu.zhuyuhan.demo1.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface IUserDao {
    @Select("select * from users where userName=#{userName}")
    User logIn(String userName);
    @Insert("insert into users(userName,password,image) values (#{userName},#{passWord},'/images/default.jpg')")
    void signIn(String userName, String passWord);
    @Update("update users set image=#{path} where userName=#{userName}")
    void updateImage(String userName, String path);
    @Select("select image from users where userName=#{arg0}")
    String loadImage(String userName);

}
