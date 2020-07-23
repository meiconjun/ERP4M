var recycleStation_tableIns;
var recycleStation_curr = 1;
$(document).ready(function () {
    try {
        // 获取按钮权限
        let buttonMap = commonGetAuthField('D02000');
        let buttonStr = buttonMap.buttonStr;

        recycleStation_tableIns = layui.table.render({
            id : "recycleStation_tableObj",
            elem: '#recycleStation_table',
            height: 'full-450',
            url: 'recycleStation.do',
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
                    elem : 'recycleStation_page',
                    limit : Number(FIELD_EACH_PAGE_NUM),
                    groups : 5,
                    curr : recycleStation_curr,
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
                            recycleStation_queryOperation(obj.curr, obj.limit);// 重载页面
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
                        return commonFormatDate(data.upload_time);
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
                    field: 'review_state',
                    title: '文档状态',
                    align : 'center',
                    templet : function (data) {
                        return commonFormatValue(FIELD_DOC_STATE, data.review_state, false);
                    }
                }
                , {
                    field: 'doc_version',
                    title: '最新版本',
                    sort: true ,
                    align : 'center'/*,
                    templet : function (data) {
                        let html = "";
                        if (personalDoc_buttonStr.indexOf("personalDoc_versionHis") != -1) {
                            html += "<a title='点击查看版本历史' class=\"layui-btn layui-btn-xs\" name='personalDoc_versionHis' onclick='personalDoc_versionHis(" + commonFormatObj(data) + ")'>" + data.doc_version + "</a>";
                        } else {
                            return data.doc_version;
                        // }
                        // return html;
                    }*/
                }
                , {
                    field: 'delete_time',
                    title: '删除时间',
                    fixed: 'right',
                    align : 'center',
                    templet : function (data) {
                        return commonFormatDate(data.delete_time);
                    }
                },
                {
                    field: 'expire_time',
                    title: '过期时间',
                    fixed: 'right',
                    align : 'center',
                    templet : function (data) {
                        return commonFormatDate(data.expire_time);
                    }
                }
            ]]
        });
        commonPutNormalSelectOpts(FIELD_DOC_TYPE, "recycleStation_docType", "", false);

        layui.form.render();// 此步是必须的，否则无法渲染一些表单元素
        //查询
        $("#recycleStation_queryBtn").click(function () {
            recycleStation_queryOperation('1', FIELD_EACH_PAGE_NUM);
        });
        //还原
        $("#recycleStation_revert").click(function () {
            recycleStation_revertOperation();
        });

        // 权限控制
        if (buttonStr.indexOf("recycleStation_queryBtn") == -1) {
            $("#recycleStation_queryBtn").hide();
        }
        if (buttonStr.indexOf("recycleStation_revert") == -1) {
            $("#recycleStation_revert").hide();
        }
        // 绑定重置表格事件
        commonResizeTable('recycleStation_tableObj');
    } catch (e) {
        console.error(e.message, e);
        commonError(e.message);
    }
});

/**
 * 查询操作
 */
function recycleStation_queryOperation(curPage, limit) {
    let doc_no = $("#recycleStation_docNo").val();
    let doc_name = $("#recycleStation_docName").val();
    let last_modi_user = sessionStorage.getItem("user_no");
    let doc_type = $("#recycleStation_docType").val();

    recycleStation_curr = curPage;
    recycleStation_tableIns.reload({
        where: { //设定异步数据接口的额外参数，任意设
            message : JSON.stringify({
                "beanList" : [{
                    "doc_no" : doc_no,
                    "doc_name" : doc_name,
                    "last_modi_user" : last_modi_user,
                    "doc_type" : doc_type
                }],
                "operType" : "query",
                "paramMap" : {
                    "curPage" : String(curPage),// 当前页码
                    "limit" : String(limit)// 每页条数
                }
            })
        }
    });
}

/**
 * 文档还原到个人库
 */
function recycleStation_revertOperation() {
    let checkData = layui.table.checkStatus("recycleStation_tableObj").data;
    if (checkData.length == 0) {
        commonInfo("请选择需要还原的文档");
        return;
    } else {
        layui.layer.confirm("是否确认还原选择的文档？", function(index) {
            let retData = commonAjax("recycleStation.do", JSON.stringify({
                "beanList": [],
                "operType": "revertDoc",
                "paramMap": {
                    "doc_list" : checkData
                }
            }));
            if (retData.retCode == HANDLE_SUCCESS) {
                commonOk("还原成功！");
                recycleStation_queryOperation('1', FIELD_EACH_PAGE_NUM);
            } else {
                commonError("还原失败!" + retData.retMsg);
            }
        })
    }
}