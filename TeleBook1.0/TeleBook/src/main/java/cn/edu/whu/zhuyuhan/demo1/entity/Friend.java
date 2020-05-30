package cn.edu.whu.zhuyuhan.demo1.entity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 联系人实体类
 *
 * @author Zhuyuhan
 * @date 2020-05-16 20:01
 */
public class Friend
{
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private Integer id;
    private String name;
    private String schoolName;
    private String email;
    private String gender;
    private Date birth;
    private String tel;
    private String birthString;
    private String userName;

    public Friend(String name, String schoolName, String email, String gender, Date birth,String tel,Integer id,String userName) {
        this.name = name;
        this.schoolName = schoolName;
        this.email = email;
        this.gender = gender;
        this.birth = birth;
        this.tel = tel;
        this.id = id;
        this.birthString = format.format(birth);
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", schoolName='" + schoolName + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", birth=" + birth +
                ", tel='" + tel + '\'' +
                ", birthString='" + birthString + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
