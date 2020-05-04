package org.meiconjun.erp4m.bean;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/5/3 16:54
 */
public class RoleRightBean {
    /** 角色编号*/
    private String role_no;
    /** 要素编号*/
    private String field_no;
    /** 要素类型*/
    private String field_type;
    /** 最后修改时间*/
    private String last_modi_time;

    public String getRole_no() {
        return role_no;
    }

    public void setRole_no(String role_no) {
        this.role_no = role_no;
    }

    public String getField_no() {
        return field_no;
    }

    public void setField_no(String field_no) {
        this.field_no = field_no;
    }

    public String getField_type() {
        return field_type;
    }

    public void setField_type(String field_type) {
        this.field_type = field_type;
    }

    public String getLast_modi_time() {
        return last_modi_time;
    }

    public void setLast_modi_time(String last_modi_time) {
        this.last_modi_time = last_modi_time;
    }
}
