package org.meiconjun.erp4m.bean;

import java.util.StringTokenizer;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/4/26 22:14
 */
public class RoleBean {
    /** 角色编号*/
    private String role_no;
    /** 职位*/
    private String position;
    /** 级别*/
    private String level;
    /** 部门*/
    private String department;
    /** 描述*/
    private String desc;
    /** 最后修改时间*/
    private String last_modi_time;
    /** 角色名称*/
    private String role_name;

    public String getRole_no() {
        return role_no;
    }

    public void setRole_no(String role_no) {
        this.role_no = role_no;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLast_modi_time() {
        return last_modi_time;
    }

    public void setLast_modi_time(String last_modi_time) {
        this.last_modi_time = last_modi_time;
    }

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }
}
