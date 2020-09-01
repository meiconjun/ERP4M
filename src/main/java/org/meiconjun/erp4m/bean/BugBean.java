package org.meiconjun.erp4m.bean;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: bugBean
 * @date 2020/8/22 21:01
 */
public class BugBean {
    /**
     * bug编号
     */
    private String serial_no;
    /**
     * bug标题
     */
    private String bug_name;
    /**
     * 所属产品
     */
    private String product;
    /**
     * bug帖子正文
     */
    private String content;
    /**
     * 优先级
     */
    private String priority;
    /**
     * 严重级别
     */
    private String severity;
    /**
     * bug状态
     */
    private String bug_status;
    /**
     * 解决用户
     */
    private String solve_user;
    /**
     * bug解决的备注
     */
    private String solve_desc;
    /**
     * 创建用户
     */
    private String create_user;
    /**
     * 最后修改时间
     */
    private String last_modi_time;

    public String getSerial_no() {
        return serial_no;
    }

    public void setSerial_no(String serial_no) {
        this.serial_no = serial_no;
    }

    public String getBug_name() {
        return bug_name;
    }

    public void setBug_name(String bug_name) {
        this.bug_name = bug_name;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getBug_status() {
        return bug_status;
    }

    public void setBug_status(String bug_status) {
        this.bug_status = bug_status;
    }

    public String getSolve_user() {
        return solve_user;
    }

    public void setSolve_user(String solve_user) {
        this.solve_user = solve_user;
    }

    public String getSolve_desc() {
        return solve_desc;
    }

    public void setSolve_desc(String solve_desc) {
        this.solve_desc = solve_desc;
    }

    public String getCreate_user() {
        return create_user;
    }

    public void setCreate_user(String create_user) {
        this.create_user = create_user;
    }

    public String getLast_modi_time() {
        return last_modi_time;
    }

    public void setLast_modi_time(String last_modi_time) {
        this.last_modi_time = last_modi_time;
    }
}
