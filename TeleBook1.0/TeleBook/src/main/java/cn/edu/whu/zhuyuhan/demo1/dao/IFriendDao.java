package cn.edu.whu.zhuyuhan.demo1.dao;

import cn.edu.whu.zhuyuhan.demo1.entity.Friend;

import java.util.Collection;

public interface IFriendDao {
    /**
     * 添加操作
     */
    void putFriend(Friend friend);
    Collection<Friend> getAll(String userName);
    Friend getById(Integer id);
    public void deleteById(Integer id);
    void editFriend(Friend friend);

    }
