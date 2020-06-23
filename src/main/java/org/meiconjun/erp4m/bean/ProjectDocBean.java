package org.meiconjun.erp4m.bean;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 项目文档Bean
 * @date 2020/5/11 22:10
 */
public class ProjectDocBean {
    /** 文档编号 */
    private String doc_no;
    /** 文档名称 */
    private String doc_name;
    /** 文档密级 */
    private String secret_level;
    /** 所属阶段 */
    private String stage;
    /** 负责部门 */
    private String department;
    /** 备注 */
    private String description;
    /** 负责角色 */

    private String duty_role;
    /** 作者 */
    private String writer;

    public String getDoc_no() {
        return doc_no;
    }

    public void setDoc_no(String doc_no) {
        this.doc_no = doc_no;
    }

    public String getDoc_name() {
        return doc_name;
    }

    public void setDoc_name(String doc_name) {
        this.doc_name = doc_name;
    }

    public String getSecret_level() {
        return secret_level;
    }

    public void setSecret_level(String secret_level) {
        this.secret_level = secret_level;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuty_role() {
        return duty_role;
    }

    public void setDuty_role(String duty_role) {
        this.duty_role = duty_role;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }
}
