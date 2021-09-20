package org.meiconjun.erp4m.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 登录用户实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = -7157721651022408945L;
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
     * 角色号
     */
    private String role_no;
    /**
     * 用户状态
     */
    private String status;

}
