var personalDoc_curr = 1;
var personalDoc_count = 0;
var personalDoc_tableIns;
var personalDoc_fileUploadInst;
var personalDoc_fileUploadFlag = false;
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
                    width : 80,
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
                        let html = "<a id='personalDoc_detail' onclick='personalDoc_detail(" + commonFormatObj(data) + ")'>详情</a>";
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
    let doc_no = $("#personalDoc_docNo").val();
    let doc_name = $("#personalDoc_docName").val();
    let upload_user = sessionStorage.getItem("user_no");
    let doc_type = $("#personalDoc_docType").val();

    personalDoc_curr = curPage;
    personalDoc_tableIns.reload({
        where: { //设定异步数据接口的额外参数，任意设
            message : JSON.stringify({
                "beanList" : [{
                    "doc_no" : doc_no,
                    "doc_name" : doc_name,
                    "upload_user" : upload_user,
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
            personalDoc_digSubmit({}, "add");
            $("#personalDoc_submitBtn").show();
            layui.form.render();

        }
    });
}

function personalDoc_modifyOperation() {
    let checkData = layui.table.checkStatus("personalDoc_tableObj").data;
    if (checkData.data.length == 0) {
        commonInfo("请选择需要修改的数据");
    } else if (checkData.data.length < 1){
        commonInfo("只能同时修改一条数据");
    } else if (checkData.data.review_state != '0' && checkData.data.review_state != '4'){
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
                let cusData = checkData.data[0];
                let postFix = checkData.data[0].file_path.substring(checkData.data[0].file_path.lastIndexOf("."), checkData.data[0].file_path.length);
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
                personalDoc_digSubmit(cusData, "modify");
                $("#personalDoc_submitBtn").show();
                $("#personalDoc_downLoad").click(function () {
                    commonFileDownload(cusData.doc_no + postFix, cusData.file_path);
                });
                layui.form.render();

            }
        });
    }
}
function personalDoc_digSubmit(cusData, operType) {
    if ("add" == operType && !personalDoc_fileUploadFlag) {
        commonInfo("请选择需要上传的文档");
        return;
    }
    layui.form.on('submit(personalDoc_submitBtn)', function(data){// 绑定提交按钮点击回调事件，只有表单验证通过会进入
        layui.layer.confirm("是否确认提交？", function(index) {
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
                        "doc_version": cusData.data.doc_version
                    }],
                    "operType": operType,
                    "paramMap": {
                        "file_root_path": cusData.file_root_path

                    }
                }));
                if (retData.retCode == HANDLE_SUCCESS) {
                    commonOk("操作成功！");
                    layui.layer.close(digIndex);
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

}

/**
 * 文档详情
 * @param data
 */
function personalDoc_detail(data) {
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
            if (buttonStr.indexOf("personalDoc_downloadBtn") != -1) {
                $("#personalDoc_downLoad").show();
            } else {
                $("#personalDoc_downLoad").hide();
            }

            $("#personalDoc_submitBtn").hide();

            $("#personalDoc_downLoad").click(function () {
                commonFileDownload(cusData.doc_no + postFix, cusData.file_path);
            });
            layui.form.render();

        }
    });
}

function personalDoc_cleanForm() {
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
                obj.preview(function (index, file, result) {
                    $("#personalDoc_docName_addTxt").val(file.name);
                });
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
}

function personalDoc_submit2(msg) {

}