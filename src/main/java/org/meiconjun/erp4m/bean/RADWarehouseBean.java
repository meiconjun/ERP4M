package org.meiconjun.erp4m.bean;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 研发仓bean
 * @date 2020/8/17 22:01
 */
public class RADWarehouseBean {
    /**流水号*/
    private String serial_no;
    /**料号*/
    private String material_no;
    /**物料名称*/
    private String material_name;
    /**本次出入库流水描述*/
    private String desc;
    /**作用项目的项目编号*/
    private String project_no;
    /**数量*/
    private String number;
    /**厂家*/
    private String supplier;
    /**厂商型号*/
    private String supplier_type;
    /**代理*/
    private String proxy;
    /**操作类型*/
    private String oper_type;
    /**最后修改用户*/
    private String last_modi_user;
    /**最后修改时间*/
    private String last_modi_time;

    public String getSerial_no() {
        return serial_no;
    }

    public void setSerial_no(String serial_no) {
        this.serial_no = serial_no;
    }

    public String getMaterial_no() {
        return material_no;
    }

    public void setMaterial_no(String material_no) {
        this.material_no = material_no;
    }

    public String getMaterial_name() {
        return material_name;
    }

    public void setMaterial_name(String material_name) {
        this.material_name = material_name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getProject_no() {
        return project_no;
    }

    public void setProject_no(String project_no) {
        this.project_no = project_no;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getSupplier_type() {
        return supplier_type;
    }

    public void setSupplier_type(String supplier_type) {
        this.supplier_type = supplier_type;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public String getOper_type() {
        return oper_type;
    }

    public void setOper_type(String oper_type) {
        this.oper_type = oper_type;
    }

    public String getLast_modi_user() {
        return last_modi_user;
    }

    public void setLast_modi_user(String last_modi_user) {
        this.last_modi_user = last_modi_user;
    }

    public String getLast_modi_time() {
        return last_modi_time;
    }

    public void setLast_modi_time(String last_modi_time) {
        this.last_modi_time = last_modi_time;
    }
}
