package org.meiconjun.erp4m.bean;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 文档实体类
 * @date 2020/6/11 20:34
 */
public class DocBean {
    // 主键，流水号
    private String doc_serial_no;
    //文档编号
    private String doc_no;
    // 文档名称
    private String doc_name;
    // 文档语言
    private String doc_language;
    // 密级
    private String secret_level;
    // 文档类型
    private String doc_type;
    // 文档作者
    private String doc_writer;
    // 文档摘要
    private String doc_desc;
    // 文档上传者
    private String upload_user;
    // 上传时间
    private String upload_time;
    // 最后修改时间
    private String last_modi_time;
    // 最新版本号
    private String doc_version;
    // 文档状态
    private String review_state;
    // 审阅用户列表
    private String review_user;
    // 审阅详情，打报文
    private String review_detail;
    // 裁决者
    private String judge_user;
    // 退回原因
    private String judge_reason;
    // 裁决时间
    private String judge_time;
    // 文档存储路径
    private String file_path;

    public String getDoc_serial_no() {
        return doc_serial_no;
    }

    public void setDoc_serial_no(String doc_serial_no) {
        this.doc_serial_no = doc_serial_no;
    }

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

    public String getDoc_language() {
        return doc_language;
    }

    public void setDoc_language(String doc_language) {
        this.doc_language = doc_language;
    }

    public String getSecret_level() {
        return secret_level;
    }

    public void setSecret_level(String secret_level) {
        this.secret_level = secret_level;
    }

    public String getDoc_type() {
        return doc_type;
    }

    public void setDoc_type(String doc_type) {
        this.doc_type = doc_type;
    }

    public String getDoc_writer() {
        return doc_writer;
    }

    public void setDoc_writer(String doc_writer) {
        this.doc_writer = doc_writer;
    }

    public String getDoc_desc() {
        return doc_desc;
    }

    public void setDoc_desc(String doc_desc) {
        this.doc_desc = doc_desc;
    }

    public String getUpload_user() {
        return upload_user;
    }

    public void setUpload_user(String upload_user) {
        this.upload_user = upload_user;
    }

    public String getUpload_time() {
        return upload_time;
    }

    public void setUpload_time(String upload_time) {
        this.upload_time = upload_time;
    }

    public String getLast_modi_time() {
        return last_modi_time;
    }

    public void setLast_modi_time(String last_modi_time) {
        this.last_modi_time = last_modi_time;
    }

    public String getDoc_version() {
        return doc_version;
    }

    public void setDoc_version(String doc_version) {
        this.doc_version = doc_version;
    }

    public String getReview_state() {
        return review_state;
    }

    public void setReview_state(String review_state) {
        this.review_state = review_state;
    }

    public String getReview_user() {
        return review_user;
    }

    public void setReview_user(String review_user) {
        this.review_user = review_user;
    }

    public String getReview_detail() {
        return review_detail;
    }

    public void setReview_detail(String review_detail) {
        this.review_detail = review_detail;
    }

    public String getJudge_user() {
        return judge_user;
    }

    public void setJudge_user(String judge_user) {
        this.judge_user = judge_user;
    }

    public String getJudge_reason() {
        return judge_reason;
    }

    public void setJudge_reason(String judge_reason) {
        this.judge_reason = judge_reason;
    }

    public String getJudge_time() {
        return judge_time;
    }

    public void setJudge_time(String judge_time) {
        this.judge_time = judge_time;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }
}
