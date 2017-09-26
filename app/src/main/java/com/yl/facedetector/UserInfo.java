package com.yl.facedetector;

/**
 * 用户信息类
 * Created by Luke on 2017/8/21.
 */

public class UserInfo {
    private String name;
    private String sex;
    private int age;
    private String path;

    public UserInfo () {
    }

    public UserInfo(String name, String sex, int age, String path) {
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", age=" + age +
                ", path='" + path + '\'' +
                '}';
    }
}
