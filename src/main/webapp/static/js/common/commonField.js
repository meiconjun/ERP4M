/**
* 数据字典常量映射
* */

/** 职位*/
const FIELD_POSITION = 'S010100';
/** 部门*/
const FIELD_DEPARTMENT = 'S010200';
/** 角色级别*/
const FIELD_ROLELEVEL = 'S010300';
/** 用户状态*/
const FIELD_USER_STATUS = 'S020100';
/** 分页 每页数据条数 */
const FIELD_EACH_PAGE_NUM = '20';
/** 项目阶段*/
const FIELD_STAGE = "P010100";
/** 项目阶段-细分*/
const FIELD_STAGE_DETAIL = "P010200";
/** 项目状态*/
const FIELD_PROJECT_STATE = "P020100";
/** 消息类型数据字典*/
const FIELD_MSG_TYPE = "S030100";

/*=================== 消息相关 ============================*/
/** 消息类型-立项会签 */
const FIELD_MSG_TYPE_COUNTERSIGN = "1";
/** 消息类型-立项结果 */
const FIELD_MSG_TYPE_COUNTERSIGN_RESULT = "2";
/** 消息类型-立项-(老板)审核 */
const FIELD_MSG_TYPE_BOSS_CHECK = "3";
/** 消息类型-项目阶段提醒 */
const FIELD_MSG_TYPE_PROJECT_STAGE = "4";
/** 消息类型-项目结项提醒 */
const FIELD_MSG_TYPE_PROJECT_END = "5";
/** 消息类型-文档审阅提醒 */
const FIELD_MSG_TYPE_DOC_REVIEW = "6";
/** 消息类型-文档裁决提醒 */
const FIELD_MSG_TYPE_DOC_JUDGE = "7";
/** 消息类型-文档被驳回消息提醒 */
const FIELD_MSG_TYPE_DOC_DENIED = "8";
/** 消息类型-文档成功发行提醒 */
const FIELD_MSG_TYPE_DOC_PASS = "9";

/*===================文档管理相关==============================*/
/** 文档类型*/
const FIELD_DOC_TYPE = "D010100";
/** 密级*/
const FIELD_DOC_SECRET = "D010200";
/** 文档语言*/
const FIELD_DOC_LANGUAGE = "D010300";
/** 文档状态*/
const FIELD_DOC_STATE = "D010400";

/*=====================任务种类===============================*/
/** 审阅文档*/
const FIELD_TASK_TYPE_DOC_REVIEW = "1";
/** 裁决文档*/
const FIELD_TASK_TYPE_DOC_JUDGE = "2";
/** 阶段文档上传*/
const FIELD_TASK_TYPE_STAGE_DOC_UPLOAD = "3";

/** 通用分隔符 */
const DELIMITER = ",@,";