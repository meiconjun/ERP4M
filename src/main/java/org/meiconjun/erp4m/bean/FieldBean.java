package org.meiconjun.erp4m.bean;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 数据字典实体类
 * @date 2020/4/22 20:38
 */
public class FieldBean {
    /** 字典编号 **/
    private String field_no;
    /** 字典值 **/
    private String field_value;
    /** 数据字典名称 **/
    private String field_name;
    /** 数据字典类型(String 等) **/
    private String field_type;
    /** 数据字典排序 **/
    private String field_order;
    /** 是否父节点 **/
    private String is_parent;
    /** 父节点字典编号 **/
    private String parent_field;
    /** 描述 **/
    private String field_desc;
    /** 最后修改时间 **/
    private String last_modi_time;

    public String getField_no() {
        return field_no;
    }

    public void setField_no(String field_no) {
        this.field_no = field_no;
    }

    public String getField_value() {
        return field_value;
    }

    public void setField_value(String field_value) {
        this.field_value = field_value;
    }

    public String getField_name() {
        return field_name;
    }

    public void setField_name(String field_name) {
        this.field_name = field_name;
    }

    public String getField_type() {
        return field_type;
    }

    public void setField_type(String field_type) {
        this.field_type = field_type;
    }

    public String getField_order() {
        return field_order;
    }

    public void setField_order(String field_order) {
        this.field_order = field_order;
    }

    public String getIs_parent() {
        return is_parent;
    }

    public void setIs_parent(String is_parent) {
        this.is_parent = is_parent;
    }

    public String getParent_field() {
        return parent_field;
    }

    public void setParent_field(String parent_field) {
        this.parent_field = parent_field;
    }

    public String getField_desc() {
        return field_desc;
    }

    public void setField_desc(String field_desc) {
        this.field_desc = field_desc;
    }

    public String getLast_modi_time() {
        return last_modi_time;
    }

    public void setLast_modi_time(String last_modi_time) {
        this.last_modi_time = last_modi_time;
    }
}
