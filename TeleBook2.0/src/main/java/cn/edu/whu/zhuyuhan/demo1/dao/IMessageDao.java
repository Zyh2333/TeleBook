package cn.edu.whu.zhuyuhan.demo1.dao;

import cn.edu.whu.zhuyuhan.demo1.entity.Message;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;

@Mapper
public interface IMessageDao {

    @Insert({"insert into message(id,context,date,userName) values (#{id},#{context},#{date},#{userName})"})
    void addMessage(Message m);

    @Delete({"delete from message where id=#{id}"})
    void deleteMessageById(int id);

    @Select({"select * from message where id=#{id}"})
    Message getMessageById(int id);

    @Select({"select * from message"})
    Collection<Message> getAll();
}
