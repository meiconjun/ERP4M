package org.meiconjun.erp4m.bean;

/**
 * 登录用户Bean
 */
public class User {
    /**
     * 用户号
     */
    private String user_no;
    /**
     * 用户名
     */
    private String user_name;
    /**
     * 密码
     */
    private String pass_word;
    /**
     * 头像图片路径
     */
    private String picture;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 手机
     */
    private String phone;
    /**
     * 最后修改时间
     */
    private String last_modi_time;
    /**
     * 授权人
     */
    private String auth_user;
    /**
     * 用户状态
     */
    private String status;

    public String getUser_no() {
        return user_no;
    }

    public void setUser_no(String user_no) {
        this.user_no = user_no;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPass_word() {
        return pass_word;
    }

    public void setPass_word(String pass_word) {
        this.pass_word = pass_word;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLast_modi_time() {
        return last_modi_time;
    }

    public void setLast_modi_time(String last_modi_time) {
        this.last_modi_time = last_modi_time;
    }

    public String getAuth_user() {
        return auth_user;
    }

    public void setAuth_user(String auth_user) {
        this.auth_user = auth_user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
