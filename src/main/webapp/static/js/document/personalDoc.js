var personalDoc_curr = 1;
var personalDoc_count = 0;
var personalDoc_tableIns;
var personalDoc_fileUploadInst = null;
var personalDoc_fileUploadFlag = false;
var personalDoc_buttonStr = "";
var personalDoc_transferInst = null;
$(document).ready(function () {
    try {
        // 获取按钮权限
        let buttonMap = commonGetAuthField('D01000');
        personalDoc_buttonStr = buttonMap.buttonStr;
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
                        "last_modi_user" : sessionStorage.getItem("user_no"),
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
                    curr : personalDoc_curr,
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
                        if (data.review_state == '4') {
                            let html = "<a title='点击查看驳回原因' href='#' style='color: red' onclick='personalDoc_showFailReason(\"" + data.judge_reason + "\")'>" + commonFormatValue(FIELD_DOC_STATE, data.review_state, false) + "</a>";
                            return html;
                        } else {
                            return commonFormatValue(FIELD_DOC_STATE, data.review_state, false);
                        }
                    }
                }
                , {
                    field: 'doc_version',
                    title: '最新版本',
                    width : 110,
                    sort: true ,
                    align : 'center',
                    templet : function (data) {
                        let html = "";
                        if (personalDoc_buttonStr.indexOf("personalDoc_versionHis") != -1) {
                            html += "<a title='点击查看版本历史' class=\"layui-btn layui-btn-xs\" name='personalDoc_versionHis' onclick='personalDoc_versionHis(" + commonFormatObj(data) + ")'>" + data.doc_version + "</a>";
                        } else {
                            return data.doc_version;
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
                        let html = "<a class=\"layui-btn layui-btn-xs layui-btn-normal \" name='personalDoc_detail' onclick='personalDoc_detail(" + commonFormatObj(data) + ")'>详情</a>";
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
        if (personalDoc_buttonStr.indexOf("personalDoc_queryBtn") == -1) {
            $("#personalDoc_queryBtn").hide();
        }
        if (personalDoc_buttonStr.indexOf("personalDoc_addBtn") == -1) {
            $("#personalDoc_addBtn").hide();
        }
        if (personalDoc_buttonStr.indexOf("personalDoc_modifyBtn") == -1) {
            $("#personalDoc_modifyBtn").hide();
        }
        if (personalDoc_buttonStr.indexOf("personalDoc_deleteBtn") == -1) {
            $("#personalDoc_deleteBtn").hide();
        }
        if (personalDoc_buttonStr.indexOf("personalDoc_Submitpreview") == -1) {
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
    let doc_no = $("#personalDoc_docNo").val();
    let doc_name = $("#personalDoc_docName").val();
    let last_modi_user = sessionStorage.getItem("user_no");
    let doc_type = $("#personalDoc_docType").val();

    personalDoc_curr = curPage;
    personalDoc_tableIns.reload({
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
 * 新增
 */
function personalDoc_addOperation() {
    layui.layer.open({
        type: 1,// 页面层
        area: ['500px', '700px'],// 宽高
        title: '新增文档',// 标题
        content: $("#personalDoc_addDiv"),//内容，直接取dom对象
        // btn: ['确定'],
        // yes: function (index, layero) {
        //     //确认按钮的回调，提交表单
        // },
        success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
            personalDoc_fileUploadFlag = false;
            // 渲染弹框元素
            personalDoc_cleanForm();
            $("#personalDoc_downLoad").hide();
            commonPutNormalSelectOpts(FIELD_DOC_LANGUAGE, "personalDoc_docLanguage_addTxt", "", true);
            commonPutNormalSelectOpts(FIELD_DOC_TYPE, "personalDoc_docType_addTxt", "", true);

            personalDoc_initUploadInst({}, "add", index);
            $("#personalDoc_submitBtn").off('click');//必须先解绑事件，否则会重复绑定
            $("#personalDoc_submitBtn").click(function () {
                personalDoc_digSubmit({}, "add", index);
            });

            $("#personalDoc_submitBtn").show();
            layui.form.render();

        }
    });
}

function personalDoc_modifyOperation() {
    let checkData = layui.table.checkStatus("personalDoc_tableObj").data;
    if (checkData.length == 0) {
        commonInfo("请选择需要修改的数据");
    } else if (checkData.length < 1){
        commonInfo("只能同时修改一条数据");
    } else if (checkData[0].review_state != '0' && checkData[0].review_state != '4'){
        commonInfo("只能修改状态为未提交评审或评审驳回的文档");
    } else {
        layui.layer.open({
            type: 1,// 页面层
            area: ['500px', '700px'],// 宽高
            title: '修改文档',// 标题
            content: $("#personalDoc_addDiv"),//内容，直接取dom对象
            // btn: ['确定'],
            // yes: function (index, layero) {
            //     //确认按钮的回调，提交表单
            // },
            success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
                personalDoc_fileUploadFlag = false;
                let cusData = checkData[0];
                let postFix = checkData[0].file_path.substring(checkData[0].file_path.lastIndexOf("."), checkData[0].file_path.length);
                // 渲染弹框元素
                personalDoc_cleanForm();
                commonPutNormalSelectOpts(FIELD_DOC_LANGUAGE, "personalDoc_docLanguage_addTxt", cusData.doc_language, true);
                commonPutNormalSelectOpts(FIELD_DOC_TYPE, "personalDoc_docType_addTxt", cusData.doc_type, true);
                layui.form.val("personalDoc_addFrm", { //formTest 即 class="layui-form" 所在元素属性 lay-filter="" 对应的值
                    "personalDoc_docNo_addTxt": cusData.doc_no, // "name": "value"
                    "personalDoc_docName_addTxt": cusData.doc_name,
                    "personalDoc_fileNameTxt": cusData.doc_no + postFix,
                    "personalDoc_docLanguage_addTxt": cusData.doc_language,
                    "personalDoc_docType_addTxt": cusData.doc_type,
                    "personalDoc_docWriter_addTxt": cusData.doc_writer,
                    "personalDoc_docDesc_addTxt": cusData.doc_desc
                });

                $("#personalDoc_downLoad").show();
                $("#personalDoc_docNo_addTxt").attr('disabled', 'disabled');
                $("#personalDoc_docType_addTxt").attr('disabled', 'disabled');
                personalDoc_initUploadInst(cusData, "modify", index);
                $("#personalDoc_submitBtn").off('click');//必须先解绑事件，否则会重复绑定
                $("#personalDoc_submitBtn").click(function () {
                    personalDoc_digSubmit(cusData, "modify", index);
                });

                $("#personalDoc_submitBtn").show();
                $("#personalDoc_downLoad").off('click');//必须先解绑事件，否则会重复绑定
                $("#personalDoc_downLoad").click(function () {
                    commonFileDownload(cusData.doc_no + postFix, cusData.file_path);
                });
                layui.form.render();

            }
        });
    }
}
function personalDoc_digSubmit(cusData, operType, digIndex) {
    if ("add" == operType && !personalDoc_fileUploadFlag) {
        commonInfo("请选择需要上传的文档");
        return;
    }
    layui.form.on('submit(personalDoc_submitBtn)', function(data){// 绑定提交按钮点击回调事件，只有表单验证通过会进入
        layui.layer.confirm("是否确认提交？", function(index) {
            layui.layer.load();//loading
            if (personalDoc_fileUploadFlag) {
                personalDoc_fileUploadInst.upload();
            } else {
                let retData = commonAjax("personalDoc.do", JSON.stringify({
                    "beanList": [{
                        "doc_serial_no": cusData.doc_serial_no,
                        "doc_no": $("#personalDoc_docNo_addTxt").val(),
                        "doc_name": $("#personalDoc_docName_addTxt").val(),
                        "doc_language": $("#personalDoc_docLanguage_addTxt").val(),
                        "doc_type": $("#personalDoc_docType_addTxt").val(),
                        "doc_writer": $("#personalDoc_docWriter_addTxt").val(),
                        "doc_desc": $("#personalDoc_docDesc_addTxt").val(),
                        "file_path": cusData.file_path,
                        "doc_version": cusData.doc_version
                    }],
                    "operType": operType,
                    "paramMap": {
                        "file_root_path": cusData.file_root_path

                    }
                }));
                if (retData.retCode == HANDLE_SUCCESS) {
                    layui.layer.closeAll('loading');
                    commonOk("操作成功！");
                    layui.layer.close(digIndex);
                    personalDoc_queryOperation("1", FIELD_EACH_PAGE_NUM);
                } else {
                    commonError("操作失败！" + retData.retMsg);
                }
            }

        });
        return false;
    });
}
/**
 * 版本历史
 * @param data
 */
function personalDoc_versionHis(data) {
    layui.layer.open({
        type: 1,// 页面层
        area: ['700px', '500px'],// 宽高
        title: '文档版本历史',// 标题
        content: $("#personalDoc_docDetail_dialogDiv"),//内容，直接取dom对象
        // btn: ['确定'],
        // yes: function (index, layero) {
        //     //确认按钮的回调，提交表单
        // },
        success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
            layui.table.render({
                id: 'personalDoc_docDetail_tableObj',
                elem: '#personalDoc_docDetail_table',
                height: 430,
                url: 'personalDoc.do',
                where: {
                    message: JSON.stringify({
                        "beanList": [{

                        }],
                        "operType": "getDocHistory",
                        "paramMap": {
                            "doc_serial_no": data.doc_serial_no

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
                        "data": res.retMap.docList //解析数据列表
                    };
                },
                cols : [[
                    {
                        field: 'doc_name',
                        title: '文档名称',
                        align : 'center'
                    },
                    {
                        field: 'doc_version',
                        title: '文档版本',
                        sort: true,
                        align : 'center'
                    }
                    , { field: 'doc_writer',
                        title: '作者',
                        align : 'center'
                    }
                    , { field: 'upload_user',
                        title: '上传用户',
                        align : 'center',
                        templet : function (data) {
                            return commonFormatUserNo(data.upload_user, false);
                        }
                    }
                    , {
                        field: 'upload_time',
                        title: '上传时间',
                        sort: true,
                        align : 'center',
                        templet : function (data) {
                            return commonFormatDate(data.upload_time);
                        }
                    }
                    , {
                        field: 'update_desc',
                        title: '更新备注',
                        sort: true,
                        align : 'center'
                    }
                    , {
                        field: 'edit',
                        title: '操作',
                        width: 80,
                        sort: false,
                        fixed: 'right',
                        align : 'center',
                        templet : function (data) {
                            let html = "";
                                html += "<a class=\"layui-btn layui-btn-xs\" onclick=\"personalDoc_downloadVersionDoc(" + commonFormatObj(data) + ")\">下载</a>";
                            return html;
                        }}
                ]]
            });
        }
    });
}

/**
 * 文档详情
 * @param data
 */
function personalDoc_detail(data) {
    layui.layer.open({
        type: 1,// 页面层
        area: ['500px', '700px'],// 宽高
        title: '文档详情',// 标题
        content: $("#personalDoc_addDiv"),//内容，直接取dom对象
        // btn: ['确定'],
        // yes: function (index, layero) {
        //     //确认按钮的回调，提交表单
        // },
        success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
            let cusData = data;
            let postFix = cusData.file_path.substring(cusData.file_path.lastIndexOf("."), cusData.file_path.length);
            // 渲染弹框元素
            commonPutNormalSelectOpts(FIELD_DOC_LANGUAGE, "personalDoc_docLanguage_addTxt", cusData.doc_language, true);
            commonPutNormalSelectOpts(FIELD_DOC_TYPE, "personalDoc_docType_addTxt", cusData.doc_type, true);
            layui.form.val("personalDoc_addFrm", { //formTest 即 class="layui-form" 所在元素属性 lay-filter="" 对应的值
                "personalDoc_docNo_addTxt": cusData.doc_no, // "name": "value"
                "personalDoc_docName_addTxt": cusData.doc_name,
                "personalDoc_fileNameTxt": cusData.doc_no + postFix,
                "personalDoc_docLanguage_addTxt": cusData.doc_language,
                "personalDoc_docType_addTxt": cusData.doc_type,
                "personalDoc_docWriter_addTxt": cusData.doc_writer,
                "personalDoc_docDesc_addTxt": cusData.doc_desc
            });
            $("#personalDoc_docNo_addTxt").attr('disabled', 'disabled');
            $("#personalDoc_docName_addTxt").attr('disabled', 'disabled');
            $("#personalDoc_docLanguage_addTxt").attr('disabled', 'disabled');
            $("#personalDoc_docType_addTxt").attr('disabled', 'disabled');
            $("#personalDoc_docWriter_addTxt").attr('disabled', 'disabled');
            $("#personalDoc_docDesc_addTxt").attr('disabled', 'disabled');
            if (personalDoc_buttonStr.indexOf("personalDoc_downloadBtn") != -1) {
                $("#personalDoc_downLoad").show();
            } else {
                $("#personalDoc_downLoad").hide();
            }
            $("#personalDoc_selectFile").parent().hide();
            $("#personalDoc_submitBtn").hide();

            $("#personalDoc_downLoad").off('click');//必须先解绑事件，否则会重复绑定
            $("#personalDoc_downLoad").click(function () {
                console.log("下载文档");
                commonFileDownload(cusData.doc_no + postFix, cusData.file_path);
            });
            layui.form.render();

        }
    });
}

function personalDoc_cleanForm() {
    $("#personalDoc_selectFile").parent().show();
    $("#personalDoc_docNo_addTxt").removeAttr('disabled');
    $("#personalDoc_docName_addTxt").removeAttr('disabled');
    $("#personalDoc_docLanguage_addTxt").removeAttr('disabled');
    $("#personalDoc_docType_addTxt").removeAttr('disabled');
    $("#personalDoc_docWriter_addTxt").removeAttr('disabled');
    $("#personalDoc_docDesc_addTxt").removeAttr('disabled');
    layui.form.val("personalDoc_addFrm", { //formTest 即 class="layui-form" 所在元素属性 lay-filter="" 对应的值
        "personalDoc_docNo_addTxt": "", // "name": "value"
        "personalDoc_docName_addTxt": "",
        "personalDoc_fileNameTxt": "",
        "personalDoc_docLanguage_addTxt": '',
        "personalDoc_docType_addTxt": '',
        "personalDoc_docWriter_addTxt": '',
        "personalDoc_docDesc_addTxt": ""
    });
}

function personalDoc_initUploadInst(data, operType, digIndex) {
    if (personalDoc_fileUploadInst == null) {
        personalDoc_fileUploadInst = layui.upload.render({
            elem: '#personalDoc_selectFile',
            url: 'docUpload.do',//改成您自己的上传接口
            data: {
                "user_no": sessionStorage.getItem("user_no"),
                "doc_type": function () {
                    return $("#personalDoc_docType_addTxt").val();
                },
                "doc_no": function () {
                    return $("#personalDoc_docNo_addTxt").val();
                },
                "file_root_path": data.file_root_path,
                "doc_serial_no": data.doc_serial_no,
                "doc_version": data.doc_version
            },
            accept: 'file',//
            auto: false,// 选择文件后是否自动上传
            // bindAction: "#createProject_fileSubmit",
            multiple: false,// 是否允许多文件上传
            choose: function(obj) {
                personalDoc_fileUploadFlag = true;
                //预读本地文件,如果是多文件，则会遍历
                obj.preview(function (index, file, result) {
                    $("#personalDoc_fileNameTxt").val(file.name);
                });
            },
            done: function(res, index, upload){
                layui.layer.closeAll('loading');
                if (res.code == '0') {
                    // 提交表单
                    let retData = commonAjax("personalDoc.do", JSON.stringify({
                        "beanList": [{
                            "doc_serial_no": data.doc_serial_no,
                            "doc_no": $("#personalDoc_docNo_addTxt").val(),
                            "doc_name": $("#personalDoc_docName_addTxt").val(),
                            "doc_language": $("#personalDoc_docLanguage_addTxt").val(),
                            "doc_type": $("#personalDoc_docType_addTxt").val(),
                            "doc_writer": $("#personalDoc_docWriter_addTxt").val(),
                            "doc_desc": $("#personalDoc_docDesc_addTxt").val(),
                            "file_path": res.data.file_path,
                            "doc_version": res.data.doc_version
                        }],
                        "operType": operType,
                        "paramMap": {
                            "file_root_path": res.data.file_root_path

                        }
                    }));
                    if (retData.retCode == HANDLE_SUCCESS) {
                        commonOk("操作成功！");
                        layui.layer.close(digIndex);
                        personalDoc_queryOperation("1", FIELD_EACH_PAGE_NUM);
                    } else {
                        commonError("操作失败！" + retData.retMsg)
                    }
                } else {
                    commonError("上传文档失败，请稍后重试");
                }
            },
            error: function (index, upload) {
                layui.layer.closeAll('loading');
                commonError("上传文档失败，请稍后重试");
            }
        });
    } else {
        personalDoc_fileUploadInst.reload({
            data: {
                "user_no": sessionStorage.getItem("user_no"),
                "doc_type": function () {
                    return $("#personalDoc_docType_addTxt").val();
                },
                "doc_no": function () {
                    return $("#personalDoc_docNo_addTxt").val();
                },
                "file_root_path": data.file_root_path,
                "doc_serial_no": data.doc_serial_no,
                "doc_version": data.doc_version
            },
            done: function(res, index, upload){
                layui.layer.closeAll('loading');
                if (res.code == '0') {
                    // 提交表单
                    let retData = commonAjax("personalDoc.do", JSON.stringify({
                        "beanList": [{
                            "doc_serial_no": data.doc_serial_no,
                            "doc_no": $("#personalDoc_docNo_addTxt").val(),
                            "doc_name": $("#personalDoc_docName_addTxt").val(),
                            "doc_language": $("#personalDoc_docLanguage_addTxt").val(),
                            "doc_type": $("#personalDoc_docType_addTxt").val(),
                            "doc_writer": $("#personalDoc_docWriter_addTxt").val(),
                            "doc_desc": $("#personalDoc_docDesc_addTxt").val(),
                            "file_path": res.data.file_path,
                            "doc_version": res.data.doc_version
                        }],
                        "operType": operType,
                        "paramMap": {
                            "file_root_path": res.data.file_root_path

                        }
                    }));
                    if (retData.retCode == HANDLE_SUCCESS) {
                        commonOk("操作成功！");
                        layui.layer.close(digIndex);
                    } else {
                        commonError("操作失败！" + retData.retMsg)
                    }
                } else {
                    commonError("上传文档失败，请稍后重试");
                }
            }
        });
    }

}

/**
 * 删除
 */
function personalDoc_deleteOperation() {
    let checkData = layui.table.checkStatus("personalDoc_tableObj").data;
    if (checkData.length == 0) {
        commonInfo("请选择需要删除的文档");
        return;
    } else {
        for (let i = 0; i < checkData.length; i++) {
            if (checkData[i].review_state != '0' && checkData[i].review_state != '4') {
                commonInfo("选中文档中包含状态不是未提交评审或评审驳回的文档，只能删除处于上述状态的文档！");
                return;
            }
        }
        layui.layer.confirm("是否确认删除选中的文档，删除后文档将进入回收站并保留90天", function (index) {
            // layui.layer.load();//loading
            layui.layer.load();
            let retData = commonAjax("personalDoc.do", JSON.stringify({
                "beanList" : checkData,
                "operType": "delete",
                "paramMap": {

                }
            }));
            layui.layer.closeAll('loading');
            if (retData.retCode == HANDLE_SUCCESS) {
                commonOk("删除成功");
                personalDoc_queryOperation("1", FIELD_EACH_PAGE_NUM);
            } else {
                commonError("删除失败！" + retData.retCode);
            }
        });
    }
}


/**
 * 提交评审
 */
function personalDoc_SubmitpreviewOperation() {
    let checkData = layui.table.checkStatus("personalDoc_tableObj").data;
    if (checkData.length == 0) {
        commonInfo("请选择需要评审的文档");
        return;
    } else if (checkData.length > 1) {
        commonInfo("只能同时提交一个文档");
        return;
    } else if (checkData[0].review_state != '0' && checkData[0].review_state != '4') {
        commonInfo("只能提交状态为未提交评审或评审驳回的文档");
        return;
    }
    $("#personalDoc_reviewSubmit_reviewer").val("");
    // $("#personalDoc_reviewSubmit_adjudicator").val("");
    $("#personalDoc_reviewSubmit_remarks").val("");
    layui.layer.open({
        type: 1,// 页面层
        area: ['600px', '450px'],// 宽高
        title: '提交评审',// 标题
        content: $("#personalDoc_reviewSubmit_dialogDiv"),//内容，直接取dom对象
        btn: ['确定'],
        yes: function (index1, layero) {
            //确认按钮的回调，提交表单
            layui.layer.confirm("是否确认提交评审？", function(index) {
                layui.layer.load();
                let retData = commonAjax("personalDoc.do", JSON.stringify({
                    "beanList": [checkData[0]],
                    "operType": "reviewSubmit",
                    "paramMap": {
                        "reviewer": $("#personalDoc_reviewSubmit_reviewer").val(),
                        // "adjudicator": $("#personalDoc_reviewSubmit_adjudicator").val(), 去除裁决步骤，审核人员会签审核文档
                        "remarks": $("#personalDoc_reviewSubmit_remarks").val()
                    }
                }));
                layui.layer.closeAll('loading');
                if (retData.retCode == HANDLE_SUCCESS) {
                    commonOk("操作成功！");
                    layui.layer.close(index1);
                    personalDoc_queryOperation("1", FIELD_EACH_PAGE_NUM);
                } else {
                    commonError("提交评审失败！");
                }

            });
            return false;
        },
        success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
            // 获取系统用户列表
            let users = [];
            let userListData = commonAjax("common.do", JSON.stringify({
                "beanList": [{}],
                "operType": "getAllUserNoAndName",
                "paramMap": {}
            }));
            if (userListData.retCode == HANDLE_SUCCESS) {
                let allUserMap = userListData.retMap.allUser;
                for (let allUserMapKey in allUserMap) {
                    users.push({
                        "name" : allUserMapKey + '-' + allUserMap[allUserMapKey],
                        "value" : allUserMapKey
                    });
                }
            } else {
                commonError("加载系统用户列表失败");
            }
            // commonPutNormalSelectOpts(users, "personalDoc_reviewSubmit_adjudicator", "", true, true);
            // 穿梭框
            $("#personalDoc_reviewSubmit_selectReviewer").off('click');//必须先解绑事件，否则会重复绑定
            $("#personalDoc_reviewSubmit_selectReviewer").click(function () {
                parent.layer.open({
                    type: 1,// 页面层
                    area: ['500px', '500px'],// 宽高
                    title: '选择审阅人',// 标题
                    content: $("#personalDoc_transfer"),//内容，直接取dom对象
                    btn: ['确定'],
                    yes: function (index3, layero) {
                        let transferData = layui.transfer.getData("personalDoc_transfer");
                        let selectTransferStr = "";
                        for (let j = 0; j < transferData.length; j ++) {
                            selectTransferStr += "," + transferData[j].value;
                        }
                        if (!commonBlank(selectTransferStr)) {
                            selectTransferStr = selectTransferStr.substring(1, selectTransferStr.length);
                        }
                        $("#personalDoc_reviewSubmit_reviewer").val(selectTransferStr);
                        layui.layer.close(index3);
                    },
                    success: function (layero, index2) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
                        if (personalDoc_transferInst == null) {// 穿梭框不可以重复渲染
                            console.log("创建穿梭框");
                            personalDoc_transferInst = layui.transfer.render({
                                elem: '#personalDoc_transfer',
                                id: "personalDoc_transfer",
                                title: ["可选", "已选"]
                                ,data: users,
                                value: $("#personalDoc_reviewSubmit_reviewer").val().split(",")
                                ,parseData: function(res){
                                    return {
                                        "value": res.value //数据值
                                        ,"title": res.name //数据标题
                                        ,"disabled": false  //是否禁用
                                        ,"checked": res.checked //是否选中
                                    }
                                }
                            });
                        } else {
                            console.log("重载穿梭框");
                            personalDoc_transferInst.reload({
                                elem: '#personalDoc_transfer',
                                id: "personalDoc_transfer",
                                title: ["可选", "已选"],
                                data: users,
                                value: $("#personalDoc_reviewSubmit_reviewer").val().split(",")
                                ,parseData: function(res){
                                    return {
                                        "value": res.value //数据值
                                        ,"title": res.name //数据标题
                                        ,"disabled": false  //是否禁用
                                        ,"checked": res.checked //是否选中
                                    }
                                }
                            });
                        }

                    }
                });

            });
            layui.form.render();
        }
    });
}

/**
 * 历史版本下载
 * @param data
 */
function personalDoc_downloadVersionDoc(data) {
    let postFix = data.file_path.substring(data.file_path.lastIndexOf("."), data.file_path.length);
    commonFileDownload((data.doc_name + "_" + data.doc_version + postFix), data.file_path);
}

/**
 * 展示驳回原因
 */
function personalDoc_showFailReason(judge_reason) {
    layui.layer.open({
        title: "驳回原因",
        content: judge_reason
    });
}