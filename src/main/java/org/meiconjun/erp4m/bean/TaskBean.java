package org.meiconjun.erp4m.bean;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 任务Bean
 * @date 2020/6/20 19:28
 */
public class TaskBean {
    // 任务编号
    private String task_no;
    // 任务类型
    private String task_type;
    // 创建用户
    private String create_user;
    // 创建时间
    private String create_time;
    // 处理角色
    private String receive_role;
    // 处理用户
    private String receive_user;
    // 已处理用户
    private String deal_user;
    // 任务参数
    private String task_param;
    // 任务标题
    private String task_title;
    // 处理状态
    private String status;
    // 任务过期时间
    private String end_time;
    // 处理方式
    private String deal_type;

    public String getTask_no() {
        return task_no;
    }

    public void setTask_no(String task_no) {
        this.task_no = task_no;
    }

    public String getTask_type() {
        return task_type;
    }

    public void setTask_type(String task_type) {
        this.task_type = task_type;
    }

    public String getCreate_user() {
        return create_user;
    }

    public void setCreate_user(String create_user) {
        this.create_user = create_user;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getReceive_role() {
        return receive_role;
    }

    public void setReceive_role(String receive_role) {
        this.receive_role = receive_role;
    }

    public String getReceive_user() {
        return receive_user;
    }

    public void setReceive_user(String receive_user) {
        this.receive_user = receive_user;
    }

    public String getDeal_user() {
        return deal_user;
    }

    public void setDeal_user(String deal_user) {
        this.deal_user = deal_user;
    }

    public String getTask_param() {
        return task_param;
    }

    public void setTask_param(String task_param) {
        this.task_param = task_param;
    }

    public String getTask_title() {
        return task_title;
    }

    public void setTask_title(String task_title) {
        this.task_title = task_title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getDeal_type() {
        return deal_type;
    }

    public void setDeal_type(String deal_type) {
        this.deal_type = deal_type;
    }
}
