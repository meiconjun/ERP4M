package org.meiconjun.erp4m.common;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 静态常量
 * @date 2020/4/5 20:30
 */
public class SystemContants {
    /**
     * 操作成功
     */
    public static final String HANDLE_SUCCESS = "0";
    /**
     * 操作失败
     */
    public static final String HANDLE_FAIL = "1";


    /** 消息类型-立项会签 */
    public static final String FIELD_MSG_TYPE_COUNTERSIGN = "1";
    /** 消息类型-立项结果 */
    public static final String FIELD_MSG_TYPE_COUNTERSIGN_RESULT = "2";
    /** 消息类型-立项-老板审批 */
    public static final String FIELD_MSG_TYPE_BOSS_CHECK = "3";
    /** 消息类型-项目阶段提醒 */
    public static final String FIELD_MSG_TYPE_PROJECT_STAGE = "4";
    /** 消息类型-项目结项提醒 */
    public static final String FIELD_MSG_TYPE_PROJECT_END = "5";
    /** 消息类型-审阅文档消息提醒 */
    public static final String FIELD_MSG_TYPE_DOC_REVIEW = "6";
    /** 消息类型-裁决文档消息提醒 */
    public static final String FIELD_MSG_TYPE_DOC_JUDGE = "7";
    /** 消息类型-文档被驳回消息提醒 */
    public static final String FIELD_MSG_TYPE_DOC_DENIED = "8";
    /** 消息类型-文档成功发行提醒 */
    public static final String FIELD_MSG_TYPE_DOC_PASS = "9";

    /** 任务类型-审阅文档 */
    public static final String FIELD_TASK_TYPE_DOC_REVIEW = "1";
    /** 任务类型-裁决文档 */
    public static final String FIELD_TASK_TYPE_DOC_JUDGE = "2";
    /** 任务类型-阶段文档上传 */
    public static final String FIELD_TASK_TYPE_STAGE_DOC_UPLOAD = "3";
    /** 项目阶段*/
    public static final String FIELD_STAGE = "P010100";


    /** 通用分隔符 */
    public static final String DELIMITER = ",@,";
}
