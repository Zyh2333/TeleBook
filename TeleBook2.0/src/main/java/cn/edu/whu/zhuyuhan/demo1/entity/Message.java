package cn.edu.whu.zhuyuhan.demo1.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    private int id;
    private String context;
    private Date date;
    private String userName;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public Message(){}
    public Message(int id, String context, String date, String userName)throws ParseException {
        this.id = id;
        this.context = context;
        this.date = dateFormat.parse(date);
        this.userName = userName;
    }

    public Message(String context, Date date, String userName) {
        this.context = context;
        this.date = date;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", context='" + context + '\'' +
                ", date=" + date +
                '}';
    }
}
