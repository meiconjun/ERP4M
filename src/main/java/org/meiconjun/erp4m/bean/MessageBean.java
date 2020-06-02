package org.meiconjun.erp4m.bean;

import java.util.Map;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: MessageBean
 * @date 2020/5/19 22:50
 */
public class MessageBean {
    /**
     * 消息编号
     */
    String msg_no;
    /**
     * 消息类型
     */
    String msg_type;
    /**
     * 创建用户
     */
    String create_user;
    /**
     * 创建时间
     */
    String create_time;
    /**
     * 消息文字
     */
    String msg_content;
    /**
     * 是否为任务
     */
    String is_task;
    /**
     * 消息参数
     */
    Map<String, Object> msg_param;

    public String getMsg_no() {
        return msg_no;
    }

    public void setMsg_no(String msg_no) {
        this.msg_no = msg_no;
    }

    public String getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(String msg_type) {
        this.msg_type = msg_type;
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

    public String getMsg_content() {
        return msg_content;
    }

    public void setMsg_content(String msg_content) {
        this.msg_content = msg_content;
    }

    public Map<String, Object> getMsg_param() {
        return msg_param;
    }

    public void setMsg_param(Map<String, Object> msg_param) {
        this.msg_param = msg_param;
    }

    public String getIs_task() {
        return is_task;
    }

    public void setIs_task(String is_task) {
        this.is_task = is_task;
    }
}
