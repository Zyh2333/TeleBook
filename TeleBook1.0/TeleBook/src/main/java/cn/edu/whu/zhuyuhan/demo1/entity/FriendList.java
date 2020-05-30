package cn.edu.whu.zhuyuhan.demo1.entity;

import com.sun.javafx.collections.MappingChange;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhuyuhan
 * @date 2020-05-16 20:04
 */
@Repository
public class FriendList {
    static Integer count = 4;
    static private Map<Integer,Friend> friendMap;
    static {
        friendMap = new HashMap<Integer, Friend>();
        friendMap.put(1,new Friend("zhangsan","WHU","123@qq.com","男",new Date(),"110",1));
        friendMap.put(2,new Friend("lisi","WHU","321@qq.com","男",new Date(),"119",2));
        friendMap.put(3,new Friend("wangwu","WHU","231@qq.com","女",new Date(),"120",3));
    }
    public void putFriend(Friend friend){
        if(friend.getId()==null) {
            friend.setId(count++);
        }
        friendMap.put(friend.getId(),friend);
    }
    public Collection<Friend> getAll(){
        return friendMap.values();
    }

    public Friend getById(Integer id){
        Friend friend = friendMap.get(id);
        return friend;
    }
    public void deleteById(Integer id){
        friendMap.remove(id);
    }
}
