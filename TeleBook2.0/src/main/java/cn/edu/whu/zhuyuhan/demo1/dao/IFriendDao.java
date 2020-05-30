package cn.edu.whu.zhuyuhan.demo1.dao;

import cn.edu.whu.zhuyuhan.demo1.entity.Friend;
import org.apache.ibatis.annotations.*;

import java.util.Collection;

@Mapper
public interface IFriendDao {
    /**
     * 添加操作
     */
    @Insert("insert into friend(id,name,schoolName,email,gender,birthString,tel,userName)values(#{id},#{name},#{schoolName},#{email},#{gender},#{birthString},#{tel},#{userName})")
    void putFriend(Friend friend);
    @Select("select * from friend where userName=#{arg0}")
    Collection<Friend> getByName(String userName);
    @Select("select * from friend where id=#{arg0}")
    Friend getById(Integer id);
    @Delete("delete from friend where id=#{arg0}")
    void deleteById(Integer id);
    @Update("update friend set id=#{id},name=#{name},schoolName=#{schoolName},email=#{email},gender=#{gender},birthString=#{birthString},tel=#{tel} where id=#{id}")
    void editFriend(Friend friend);
    }
