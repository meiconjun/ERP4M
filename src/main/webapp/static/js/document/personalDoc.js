var personalDoc_curr = 1;
var personalDoc_count = 0;
var personalDoc_tableIns;
$(document).ready(function () {
    try {
        // 获取按钮权限
        let buttonMap = commonGetAuthField('D01000');
        let buttonStr = buttonMap.buttonStr;
        //初始化表格
        personalDoc_tableIns = layui.table.render({
            id : "personalDoc_tableObj",
            elem: '#personalDoc_table',
            height: 'full-450',
            url: 'personalDoc.do',
            where : {
                message : JSON.stringify({
                    "beanList" : [{
                        "doc_no" : "",
                        "doc_name" : "",
                        "upload_user" : "",
                        "doc_type" : ""
                    }],
                    "operType" : "query",
                    "paramMap" : {
                        "curPage" : '1',// 当前页码
                        "limit" : FIELD_EACH_PAGE_NUM// 每页条数
                    }
                })
            },
            method : 'post',
            even : true,
            page: false,
            loading : true,
            skin : 'row',
            done : function(res, curr, count){
                // 分页初始化
                layui.laypage.render({
                    elem : 'personalDoc_page',
                    limit : Number(FIELD_EACH_PAGE_NUM),
                    groups : 5,
                    curr : roleConfig_curr,
                    count : count,
                    prev : '上一页',
                    next : '下一页',
                    first : '首页',
                    last : '尾页',
                    layout : ['prev', 'first', 'page', 'last', 'next', 'count'],
                    jump : function(obj, first){
                        //obj包含了当前分页的所有参数，比如：
                        // console.log(obj.limit); //得到每页显示的条数
                        if (!first) {
                            personalDoc_queryOperation(obj.curr, obj.limit);// 重载页面
                        }
                    }
                });
            },
            parseData: function(res){ //res 即为原始返回的数据
                return {
                    "code": res.retCode, //解析接口状态
                    "msg": res.retMsg, //解析提示文本
                    "count": res.retMap.total, //解析数据长度
                    "data": res.retMap.list //解析数据列表
                };
            },
            cols : [[
                {//复选框
                    type : 'checkbox',
                    fixed: 'left'
                },
                {
                    field: 'doc_no',
                    title: '文档编号',
                    sort: true,
                    align : 'center'
                },
                {
                    field: 'doc_name',
                    title: '文档名称',
                    align : 'center'
                }
                , { field: 'upload_user',
                    title: '上传用户',
                    align : 'center',
                    templet : function (data) {
                        return commonFormatUserNo(data.upload_user, false);
                    }}
                , {
                    field: 'upload_time',
                    title: '上传时间',
                    align : 'center',
                    templet : function (data) {
                        return commonFormatDate(data.upload_date);
                    }
                }
                , {
                    field: 'doc_type',
                    title: '文档类别',
                    align : 'center',
                    templet : function (data) {
                        return commonFormatValue(FIELD_DOC_TYPE, data.doc_type, false);
                    }
                }
                , {
                    field: 'doc_version',
                    title: '最新版本',
                    sort: true ,
                    align : 'center',
                    templet : function (data) {
                        let html = "";
                        if (buttonStr.indexOf("personalDoc_versionHis") != -1) {
                            html += "<a id='personalDoc_versionHis' onclick='personalDoc_versionHis(" + commonFormatObj(data) + ")'>" + data.doc_version + "</a>"
                        }
                        return html;
                    }
                }
                , {
                    field: 'edit',
                    title: '操作',
                    width : 80,
                    fixed: 'right',
                    align : 'center',
                    templet : function (data) {
                        let html = "<a id='personalDoc_detail' onclick='personalDoc_detail(" + commonFormatObj(data) + ")'>" + data.doc_version + "</a>";
                        return html;
                    }}
            ]]
        });

        commonPutNormalSelectOpts(FIELD_DOC_TYPE, "personalDoc_docType", "", false);

        layui.form.render();// 此步是必须的，否则无法渲染一些表单元素

        //查询
        $("#personalDoc_queryBtn").click(function () {
            personalDoc_queryOperation('1', FIELD_EACH_PAGE_NUM);
        });
        //新增
        $("#personalDoc_addBtn").click(function () {
            personalDoc_addOperation();
        });
        // 修改
        $("#personalDoc_modifyBtn").click(function () {
            personalDoc_modifyOperation();
        });
        //删除
        $("#personalDoc_deleteBtn").click(function () {
            personalDoc_deleteOperation();
        });
        //提交评审
        $("#personalDoc_Submitpreview").click(function () {
            personalDoc_SubmitpreviewOperation();
        });

        // 权限控制
        if (buttonStr.indexOf("personalDoc_queryBtn") == -1) {
            $("#personalDoc_queryBtn").hide();
        }
        if (buttonStr.indexOf("personalDoc_addBtn") == -1) {
            $("#personalDoc_addBtn").hide();
        }
        if (buttonStr.indexOf("personalDoc_modifyBtn") == -1) {
            $("#personalDoc_modifyBtn").hide();
        }
        if (buttonStr.indexOf("personalDoc_deleteBtn") == -1) {
            $("#personalDoc_deleteBtn").hide();
        }
        if (buttonStr.indexOf("personalDoc_Submitpreview") == -1) {
            $("#personalDoc_Submitpreview").hide();
        }
        // 绑定重置表格事件
        commonResizeTable('personalDoc_tableObj');
    } catch (e) {
        console.error(e.message, e);
        commonError(e.message);
    }
});

function personalDoc_queryOperation(curPage, limit) {

}
/**
 * 版本历史
 * @param data
 */
function personalDoc_versionHis(data) {

}

/**
 * 文档详情
 * @param data
 */
function personalDoc_detail(data) {

}