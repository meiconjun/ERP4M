var projectManage_tableIns;
var projectManage_curr = 1;
$(document).ready(function () {
    try {
        // 获取按钮权限列表
        let buttonMap = commonGetAuthField('P03000');
        let buttonStr = buttonMap.buttonStr;
        //初始化表格

        projectManage_tableIns = layui.table.render({
            id: 'projectManage_tableObj',
            elem: '#projectManage_table',
            height: 'full-450',
            url: 'projectManage.do',
            where: {
                message: JSON.stringify({
                    "beanList": [{

                    }],
                    "operType": "query",
                    "paramMap": {
                        "curPage": "1",
                        "limit": FIELD_EACH_PAGE_NUM

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
                    elem : 'projectManage_page',
                    limit : Number(FIELD_EACH_PAGE_NUM),
                    groups : 5,
                    curr : projectManage_curr,
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
                            projectManage_queryOperation(obj.curr, obj.limit);// 重载页面
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
                    field: 'project_name',
                    title: '项目名称',
                    sort: true,
                    align : 'center'
                },
                {
                    field: 'chn_name',
                    title: '中文名称',
                    sort: true,
                    align : 'center'
                }
                , { field: 'principal',
                    title: '负责人',
                    align : 'center',
                    templet : function (data) {
                        return commonFormatUserNo(data.principal, false);
                    }}
                , {
                    field: 'begin_date',
                    title: '开始日期',
                    align : 'center',
                    templet : function (data) {
                        return commonFormatDate(data.begin_date);
                    }
                }
                , {
                    field: 'end_date',
                    title: '结束日期',
                    align : 'center',
                    templet : function (data) {
                        return "";
                    }
                }
                , {
                    field: 'project_state',
                    title: '项目状态',
                    sort: true ,
                    align : 'center',
                    templet : function (data) {
                        return commonFormatValue(FIELD_PROJECT_STATE, data.project_state, false)
                    }
                }
                , {
                    field: 'stage_num',
                    title: '当前阶段',
                    sort: true ,
                    align : 'center'
                }
                , {
                    field: 'fail_reason',
                    title: '失败原因',
                    sort: true ,
                    align : 'center',
                    templet : function (data) {
                        return "<span style='color: red' title='" + data.fail_reason + "'>" + data.fail_reason + "</span>";
                    }
                }
                , {
                    field: 'edit',
                    title: '操作',
                    width: 80,
                    sort: true,
                    fixed: 'right',
                    align : 'center',
                    templet : function (data) {
                        let html = "";
                        if (buttonStr.indexOf("projectManage_detailBtn") != -1) {
                            html += "<a class=\"layui-btn layui-btn-xs\" name='projectManage_detailBtn' onclick='projectManage_detailOperation(" + commonFormatObj(data) + ")'>详情</a>";
                        }
                        return html;
                    }}
            ]]
        });
        layui.form.render();
        //查询
        $("#projectManage_query").click(function () {
            projectManage_queryOperation('1', FIELD_EACH_PAGE_NUM);
        });
        //进入下阶段
        $("#projectManage_nextStage").click(function () {
            projectManage_nextStageOperation();
        });
        // 权限控制
        if (buttonStr.indexOf("projectManage_query") == -1) {
            $("#projectManage_query").hide();
        }
        if (buttonStr.indexOf("projectManage_nextStage") == -1) {
            $("#projectManage_nextStage").hide();
        }
        // 绑定重置表格事件
        commonResizeTable('projectManage_tableObj');
    } catch (e) {
        console.error(e.message, e);
        commonError(e.message);
    }
});

function projectManage_queryOperation(curPage, limit) {
    let project_name = $("#projectManage_projectName_Q").val();
    let chn_name = $("#projectManage_chnName_Q").val();
    let principal = $("#projectManage_principal_Q").val();

    projectManage_curr = curPage;
    projectManage_tableIns.reload({
        where: { //设定异步数据接口的额外参数，任意设
            message : JSON.stringify({
                "beanList" : [{
                    "project_name" : project_name,
                    "chn_name" : chn_name,
                    "principal" : principal
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
// 阶段详情
function projectManage_detailOperation(data) {
    layui.layer.open({
        type: 1,// 页面层
        area: ['720px', '700px'],// 宽高
        title: '项目详情',// 标题
        content: $("#projectManage_detailDiv"),//内容，直接取dom对象
        // btn: ['确定'],
        // yes: function (index, layero) {
        //     //确认按钮的回调，提交表单
        // },
        success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
            // 渲染弹框元素
            projectManage_cleanForm();
            layui.form.val("projectManage_detailFrm", { //formTest 即 class="layui-form" 所在元素属性 lay-filter="" 对应的值
                "projectManage_projectName_dialog": data.project_name, // "name": "value"
                "projectManage_chnName_dialog": data.chn_name,
                "projectManage_principal_dialog": data.principal,
                "projectManage_beginDate_dialog": data.begin_date,
                "projectManage_endDate_dialog": data.end_date,
                "projectManage_menbers_dialog": data.project_menbers,
                "projectManage_specificationsDownload_dialog": data.specifications
            });

            // 拼接阶段信息
            let stageData = commonAjax("projectManage.do", JSON.stringify({
                "beanList": [],
                "operType": "getStageInfo",
                "paramMap": {
                    "project_no": data.project_no// 项目编号
                }
            }));
            if (stageData.retCode == HANDLE_SUCCESS) {
                let stageList = stageData.retMap.stageList
                for (let i = 0;i < stageList.length; i++) {
                    let tempHtml = "<fieldset name='projectManage_stageFieldSet' class=\"layui-elem-field layui-field-title\">\n" +
                        "            <legend>阶段" + (i + 1) + ",负责人：" + commonFormatUserNo(stageList[i].principal, true) + "</legend>\n" +
                        "            <div class=\"layui-field-box\">\n" +
                        "                <div class=\"layui-form-item\">\n" +
                        "                    <div class=\"layui-inline\">\n" +
                        "                        <label class=\"layui-form-label\">阶段类型</label>\n" +
                        "                        <div class=\"layui-input-inline\">\n" +
                        "                            <input type=\"text\" value='" + commonFormatValue(FIELD_STAGE, stageList[i].stage) + "' name=\"projectManage_stage_dialog" + (i + 1) + "\" id=\"projectManage_stage_dialog" + (i + 1) + "\" disabled autocomplete=\"off\" class=\"layui-input\">\n" +
                        "                        </div>\n" +
                        "                    </div>\n" +
                        "                    <div class=\"layui-inline\">\n" +
                        "                        <label class=\"layui-form-label\">开始日期</label>\n" +
                        "                        <div class=\"layui-input-inline\">\n" +
                        "                            <input type=\"text\" value='" + commonFormatDate(stageList[i].begin_date) + "' name=\"projectManage_beginDate_dialog" + (i + 1) + "\" id=\"projectManage_beginDate_dialog" + (i + 1) + "\" disabled autocomplete=\"off\" class=\"layui-input\">\n" +
                        "                        </div>\n" +
                        "                    </div>\n" +
                        "                </div>\n" +
                        "                <div class=\"layui-form-item\">\n" +
                        "                    <div class=\"layui-inline\">\n" +
                        "                        <label class=\"layui-form-label\">结束日期</label>\n" +
                        "                        <div class=\"layui-input-inline\">\n" +
                        "                            <input type=\"text\" value='" + commonFormatDate(stageList[i].end_date) + "' name=\"projectManage_endDate_dialog" + (i + 1) + "\" id=\"projectManage_endDate_dialog" + (i + 1) + "\" disabled autocomplete=\"off\" class=\"layui-input\">\n" +
                        "                        </div>\n" +
                        "                    </div>\n" +
                        "                    <div class=\"layui-inline\">\n" +
                        "                            <button type=\"button\" id=\"projectManage_uploadFile_dialog" + (i + 1) + "\"  class=\"layui-btn layui-btn-primary\" >上传文档</button>\n" +//此处传参要加''，否则会被类型转换/*onclick=\"projectManage_uploadFile('" + data.project_no + "','" + (i+1) +  "')\"*/
                        "                            <button type=\"button\" id=\"projectManage_docDetail_dialog" + (i + 1) + "\"  class=\"layui-btn layui-btn-primary\" onclick=\"projectManage_docDetail('" + data.project_no + "','" + data.stage_num +  "')\">文档详情</button>\n" +
                        "                    </div>\n" +
                        "                </div>\n" +
                        "            </div>\n" +
                        "        </fieldset>";
                    $("#projectManage_detailFrm").append(tempHtml);
                    // 查询阶段文档信息
                    let stageDocData = commonAjax("projectManage.do", JSON.stringify({
                        "beanList": [],
                        "operType": "getStageDocVersion",
                        "paramMap": {
                            "project_no": data.project_no,
                            "stage_num": data.stage_num
                        }
                    }));
                    if (stageDocData.retCode == HANDLE_SUCCESS) {
                        let doc_version = stageDocData.retMap.doc_version;
                        //绑定上传组件到按钮
                        let fileUploadInst = layui.upload.render({
                            elem: '#projectManage_uploadFile_dialog' + (i + 1),
                            url: 'projectStageDocUpload.do',//改成您自己的上传接口
                            data: {
                                "project_no": data.project_no,
                                "stage_num": data.stage_num,
                                "doc_version": commonBlank(doc_version) ? "" : doc_version,
                                "file_root_path": data.file_root_path,
                                "doc_serial": stageList[i].doc_serial
                            },
                            accept: 'file',//只允许上传图片
                            auto: false,// 选择文件后是否自动上传
                            // bindAction: "#createProject_fileSubmit",
                            multiple: false,// 是否允许多文件上传
                            choose: function(obj) {
                                //预读本地文件,如果是多文件，则会遍历
                                obj.preview(function (index, file, result) {
                                    let confirmStr = commonBlank(doc_version) ? "是否确认提交文件[" + file.name + "]？当前阶段文档未定版，上传后将覆盖原先文件" : "是否确认提交文件[" + file.name + "]？当前阶段文档最新版本为[" + doc_version + "]";
                                    layui.layer.confirm(confirmStr, function(index) {
                                        layui.layer.load();//loading
                                        fileUploadInst.upload();
                                    });
                                });
                            },
                            done: function(res, index, upload){
                                if (res.code == '0') {
                                    layui.layer.closeAll('loading');
                                    commonOk("上传阶段文档成功");
                                } else {
                                    layui.layer.closeAll('loading');
                                    commonError("上传阶段文档失败:" + res.msg)
                                }
                            },
                            error: function (index, upload) {
                                layui.layer.closeAll('loading');
                                commonError("上传阶段文档失败，请稍后重试");
                            }
                        });
                    } else {
                        commonError("获取阶段文档信息失败：" + stageDocData.retMsg);
                        return;
                    }

                }

                layui.form.render();
                // 绑定规格书下载事件
                $("#projectManage_specificationsDownload_dialog").click(function () {
                    commonFileDownload(data.project_name + "产品规格说明书.doc", $("#projectManage_specificationsDownload_dialog").val());
                });
            } else {
                commonError("加载项目阶段信息失败：" + stageData.retMsg);
            }

        }
    });

}

/**
 * 阶段文档详情
 */
function projectManage_docDetail(project_no, stage_num) {
    layui.layer.open({
        type: 1,// 页面层
        area: ['700px', '500px'],// 宽高
        title: '阶段文档详情',// 标题
        content: $("#projectManage_docDetail_dialogDiv"),//内容，直接取dom对象
        // btn: ['确定'],
        // yes: function (index, layero) {
        //     //确认按钮的回调，提交表单
        // },
        success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
            //TODO 判断当前用户是否有下载权限
            let rightFlag = true;
            layui.table.render({
                id: 'projectManage_docDetail_tableObj',
                elem: '#projectManage_docDetail_table',
                height: 430,
                url: 'projectManage.do',
                where: {
                    message: JSON.stringify({
                        "beanList": [{

                        }],
                        "operType": "getStageDocInfo",
                        "paramMap": {
                            "project_no": project_no,
                            "stage_num": stage_num

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
                    {
                        field: 'doc_name',
                        title: '文档名称',
                        sort: true,
                        align : 'center'
                    },
                    {
                        field: 'doc_version',
                        title: '版本',
                        sort: true,
                        align : 'center'
                    }
                    , { field: 'doc_writer',
                        title: '作者',
                        align : 'center'
                     }
                    , { field: 'department',
                        title: '负责部门',
                        align : 'center',
                        templet : function (data) {
                            return commonFormatValue(FIELD_DEPARTMENT, data.department);
                        }
                    }
                    , {
                        field: 'upload_date',
                        title: '上传日期',
                        align : 'center',
                        templet : function (data) {
                            return commonFormatDate(data.upload_date);
                        }
                    }
                    , {
                        field: 'edit',
                        title: '操作',
                        width: 80,
                        sort: true,
                        fixed: 'right',
                        align : 'center',
                        templet : function (data) {
                            let html = "";
                            if (rightFlag) {
                                html += "<a class=\"layui-btn layui-btn-xs\" onclick=\"projectManage_downloadStageDoc(" + commonFormatObj(data) + ")\">下载</a>";
                            }
                            return html;
                        }}
                ]]
            });
        }
    });
}

function projectManage_cleanForm() {
    layui.form.val("projectManage_detailFrm", { //formTest 即 class="layui-form" 所在元素属性 lay-filter="" 对应的值
        "projectManage_projectName_dialog": "", // "name": "value"
        "projectManage_chnName_dialog": "",
        "projectManage_principal_dialog": '',
        "projectManage_beginDate_dialog": '',
        "projectManage_endDate_dialog": '',
        "projectManage_menbers_dialog": "",
        "projectManage_specificationsDownload_dialog": ""
    });
    // 清除阶段信息
    $("fieldset[name='projectManage_stageFieldSet']").remove();
}

function projectManage_downloadStageDoc(data) {
    commonFileDownload((data.doc_name + "_" + data.doc_version + ".doc"), data.file_path);
}