var createProject_stageCount = 1;// 阶段数量计数器
var createProject_fileUploadInst;
var createProject_fileUploadFlag = false;
$(document).ready(function () {
    try {
        // 渲染下拉框
        commonPutNormalSelectOpts(FIELD_STAGE, "createProject_stage1", "", true);
        createProject_updateDocList($("#createProject_stage1").val(), "createProject_stageDoc1");
        layui.form.on('select(createProject_stage1)', function(data){
            createProject_updateDocList(data.value, "createProject_stageDoc1");
        });
        layui.form.on('select(createProject_stageDoc1)', function(data){
            createProject_updateUserList(data.value, "createProject_stageUser1");
        });
        commonPutNormalSelectOpts([{
            "name" : "（阶段负责人）请先选择阶段文档",
            "value" : "-"
        }], "createProject_stageUser1", "", true, true);
        layui.form.on('select(createProject_stageUser1)', function(data){
            let menbers = $("#createProject_members").val();
            if (menbers.indexOf($(data.elem).val()) == -1) {
                $("#createProject_members").val(menbers + "，" + $(data.elem).val());
            }
        });
        //渲染表单元素
        layui.form.render();

        createProject_fileUploadInst = layui.upload.render({
            elem: '#createProject_productDoc',
            url: 'projectDescFileUpload.do',//改成您自己的上传接口
            data: {
                "projectName": function () {
                    return $("#createProject_name").val();
                }
            },
            accept: 'file',//只允许上传图片
            auto: false,// 选择文件后是否自动上传
            // bindAction: "#createProject_fileSubmit",
            multiple: false,// 是否允许多文件上传
            choose: function(obj) {
                //预读本地文件,如果是多文件，则会遍历
                createProject_fileUploadFlag = false;
                obj.preview(function (index, file, result) {
                    $("#createProject_productDoc").val(file.name);
                });
            },
            done: function(res, index, upload){
                if (res.code == '0') {
                    createProject_fileUploadFlag = true;
                    // 获取阶段信息
                    let stageList = [];
                    for (let i = 1; i <= createProject_stageCount; i++) {
                        let tempStage = {
                            "stageCount": String(i),// 用数字会被转为double
                            "stage_type": $("#createProject_stage" + i).val(),
                            "stage_begin": $("#createProject_stage_beginDate" + i).val().replaceAll("-", ""),
                            "stage_end": $("#createProject_stage_endDate" + i).val().replaceAll("-", ""),
                            "stage_doc": $("#createProject_stageDoc" + i).val(),
                            "doc_writer": $("#createProject_docWriter" + i).val(),
                            "principal": $("#createProject_stageUser" + i).val()
                        }
                        stageList.push(tempStage);
                    }
                    // 构造请求报文
                    let reqMsg = {
                        "beanList": [],
                        "operType": "createProject",
                        "paramMap": {
                            "project_name": $("#createProject_name").val(),
                            "chn_name": $("#createProject_chnName").val(),
                            "product_doc_path": res.data.filePath,
                            "begin_date": $("#createProject_beginDate").val().replaceAll("-", ""),
                            "member": $("#createProject_members").val(),
                            "stageList": stageList
                        }
                    };
                    let retData = commonAjax("createProject.do", JSON.stringify(reqMsg));
                    if (retData.retCode == HANDLE_SUCCESS) {
                        commonOk("提交立项申请成功!");
                        // 关闭标签页
                        layui.element.tabDelete("main-tab", "P02000");
                    } else {
                        commonError(retData.retMsg);
                    }
                } else {
                    commonError("上传规格说明书失败:" + res.msg)
                }
            },
            error: function (index, upload) {
                commonError("上传规格说明书失败，请稍后重试");
            }
        });
        // 渲染日期组件
        layui.laydate.render({
            elem: '#createProject_stage_beginDate1',
            value : new Date()
        });
        layui.laydate.render({
            elem: '#createProject_beginDate',
            value : new Date()/*,
            done: function (value, date, endDate) {
                $("#createProject_stage_beginDate1").val(value);
            }*/
        });

        layui.laydate.render({
            elem: '#createProject_stage_endDate1'
        });

        $("#createProject_submit").click(function () {
            createProject_submit();
        });
    } catch (e) {
        console.error(e.message, e);
        commonError(e.message);
    }
});

/**
 * 添加一个阶段
 */
function createProject_addStage() {
    let createProject_stageCount_temp = createProject_stageCount + 1;
    let html =
        "        <div stageCount=\"" + createProject_stageCount_temp + "\">\n" +
        "            &nbsp;&nbsp;&nbsp;&nbsp;阶段" + createProject_stageCount_temp + "\n" +
        "            <hr>" +
        "            <div class=\"layui-form-item\" >\n" +
        "                <div class=\"layui-inline layui-inline-spc\">\n" +
        "                    <label class=\"layui-form-label layui-form-label-spc\">阶段类型</label>\n" +
        "                    <div class=\"layui-input-inline layui-input-inline-spc\">\n" +
        "                        <select lay-filter='createProject_stage" + createProject_stageCount_temp + "' name=\"createProject_stage" + createProject_stageCount_temp + "\" id=\"createProject_stage" + createProject_stageCount_temp + "\" required lay-verify=\"required\">\n" +
        "                        </select>\n" +
        "                    </div>\n" +
        "                </div>\n" +
        "                <div class=\"layui-inline layui-inline-spc\">\n" +
        "                    <label class=\"layui-form-label layui-form-label-spc\">开始日期</label>\n" +
        "                    <div class=\"layui-input-inline layui-input-inline-spc\">\n" +
        "                        <input type=\"text\" name=\"createProject_stage_beginDate" + createProject_stageCount_temp + "\" id=\"createProject_stage_beginDate" + createProject_stageCount_temp + "\" lay-verify=\"date\" placeholder=\"yyyy-MM-dd\" autocomplete=\"off\" class=\"layui-input\">\n" +
        "                    </div>\n" +
        "                </div>\n" +
        "            </div>\n" +
        "            <div class=\"layui-form-item\" >\n" +
        "                <div class=\"layui-inline layui-inline-spc\">\n" +
        "                    <label class=\"layui-form-label layui-form-label-spc\">结束日期</label>\n" +
        "                    <div class=\"layui-input-inline layui-input-inline-spc\">\n" +
        "                        <input type=\"text\" name=\"createProject_stage_endDate" + createProject_stageCount_temp + "\" id=\"createProject_stage_endDate" + createProject_stageCount_temp + "\" lay-verify=\"date\" placeholder=\"yyyy-MM-dd\" autocomplete=\"off\" class=\"layui-input\">\n" +
        "                    </div>\n" +
        "                </div>\n" +
                        "<div class=\"layui-inline layui-inline\">\n" +
        "                    <select lay-filter='createProject_stageDoc" + createProject_stageCount_temp + "' name=\"createProject_stageDoc" + createProject_stageCount_temp + "\" id=\"createProject_stageDoc" + createProject_stageCount_temp + "\" required lay-verify=\"required\">\n" +
        "                    </select>\n" +
        "                </div>\n" +
        "                <div class=\"layui-inline layui-inline\">\n" +
        "                    <input type=\"text\" placeholder=\"文档作者\" name=\"createProject_docWriter" + createProject_stageCount_temp + "\" id=\"createProject_docWriter" + createProject_stageCount_temp + "\" required  lay-verify=\"required\" autocomplete=\"off\" class=\"layui-input\">\n" +
        "                </div>" +
        "                <div class=\"layui-inline layui-inline\">\n" +
        "                    <select name=\"createProject_stageUser" + createProject_stageCount_temp + "\" id=\"createProject_stageUser" + createProject_stageCount_temp + "\" required lay-verify=\"required\">\n" +
        "                    </select>\n" +
        "                </div>\n" +
        "                <button type=\"button\" class=\"layui-btn\" onclick=\"createProject_addStage()\"><i class=\"layui-icon layui-icon-add-circle\"></i></button>" +
        "                <button type=\"button\" class=\"layui-btn\" onclick=\"createProject_minStage()\"><i class=\"layui-icon layui-icon-reduce-circle\"></i></button>\n" +
        "            </div>\n" +
        "        </div>";
    $("#roleConfig_addFrm").append(html);
    // 渲染下拉框
    commonPutNormalSelectOpts(FIELD_STAGE, "createProject_stage" + createProject_stageCount_temp, "", true);
    createProject_updateDocList($("#createProject_stage" + createProject_stageCount_temp).val(), "createProject_stageDoc" + createProject_stageCount_temp);
    layui.form.on('select(createProject_stage' + createProject_stageCount_temp + ')', function(data){
        createProject_updateDocList(data.value, "createProject_stageDoc" + createProject_stageCount_temp);
    });
    layui.form.on('select(createProject_stageDoc' + createProject_stageCount_temp + ')', function(data){
        createProject_updateUserList(data.value, "createProject_stageUser" + createProject_stageCount_temp);
    });
    commonPutNormalSelectOpts([{
        "name" : "（阶段负责人）请先选择阶段文档",
        "value" : "-"
    }], "createProject_stageUser" + createProject_stageCount_temp, "", true, true);
    layui.form.on('select(createProject_stageUser' + createProject_stageCount_temp + ')', function(data){
        let menbers = $("#createProject_members").val();
        if (menbers.indexOf($(data.elem).val()) == -1) {
            $("#createProject_members").val(menbers + "，" + $(data.elem).val());
        }
    });
    //渲染表单元素
    layui.form.render();
    // 渲染日期组件
    layui.laydate.render({
        elem: '#createProject_stage_beginDate' + createProject_stageCount_temp,
        value : $("#createProject_stage_endDate" + createProject_stageCount).val()
    });
    // 渲染日期组件
    layui.laydate.render({
        elem: '#createProject_stage_endDate' + createProject_stageCount_temp/*,
        done: function (value) {
            if ($("#createProject_stage_beginDate" + createProject_stageCount_temp + 1) != undefined) {
                $("#createProject_stage_beginDate" + createProject_stageCount_temp + 1).val(value);
            }
        }*/
    });
    createProject_stageCount = createProject_stageCount_temp;
}
/**
 * 减少一个阶段
 */
function createProject_minStage() {
    if (createProject_stageCount == 1) {
        return;
    }
    let menbers = $("#createProject_members").val();
    menbers = menbers.replaceAll($("#createProject_stageUser" + createProject_stageCount).val(), "");
    $("#createProject_members").val(menbers);
    $("div[stageCount='" + createProject_stageCount + "']").remove();
    createProject_stageCount--;
}

/**
 * 选择文档-穿梭框
 */
/*
function createProject_selectDoc(obj) {
    obj = $(obj);
    // 根据阶段类型获取阶段文档
    let retData = commonAjax("createProject.do", JSON.stringify({
        "beanList": [],
        "operType": "getProjectDoc",
        "paramMap": {
            "stage": $("#createProject_stage" + obj.attr("stageCount")).val()
        }
    }));
    if (retData.retCode == HANDLE_SUCCESS) {
        layui.layer.open({
            type: 1,// 页面层
            area: ['520px', '500px'],// 宽高
            title: '新增项目文档',// 标题
            content: $("#createProject_docTransfer"),//内容，直接取dom对象
            btn: ['确定'],
            yes: function (index, layero) {

            },
            success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
                // 渲染穿梭框
                /!*layui.transfer.render({
                    elem: "#createProject_docTransferDiv",
                    id: "createProject_docTransferDiv",
                    title: "选择项目阶段文档",
                    data: retData.retMap.docList,
                    parseData: function (res) {
                        return {
                            "value": res.doc_no,
                            "title": res.doc_name
                        }
                    },
                    value: [
                        {
                            "value": obj.attr("docValue"),
                            "title": obj.attr("docName")
                        }
                    ],
                    onchange: function (data, index) {
                        console.log(data);
                    }
                });*!/
            }
        });

    } else {
        commonError("加载项目文档失败！");
        return;
    }


}*/
function createProject_updateDocList(stage, domId) {
    let retData = commonAjax("createProject.do", JSON.stringify({
        "beanList": [],
        "operType": "getProjectDoc",
        "paramMap": {
            "stage": stage
        }
    }));
    if (retData.retCode == HANDLE_SUCCESS) {
        commonPutNormalSelectOpts(retData.retMap.docList, domId, "", false, false, "请选择阶段文档");
        layui.form.render();
    } else {
        commonError("加载项目文档列表失败");
    }
}

function createProject_updateUserList(docNo, domId) {
    let retData = commonAjax("createProject.do", JSON.stringify({
        "beanList": [],
        "operType": "getUserList",
        "paramMap": {
            "docNo": docNo
        }
    }));
    if (retData.retCode == HANDLE_SUCCESS) {
        commonPutNormalSelectOpts(retData.retMap.userList, domId, "", false, false, "请选择阶段负责人");
        layui.form.render();
    } else {
        commonError("加载用户列表失败");
    }
}

function createProject_submit() {
    layui.form.on('submit(createProject_submit)', function(data){// 绑定提交按钮点击回调事件，只有表单验证通过会进入
        layui.layer.confirm("是否确认提交？", function(index) {
            //先提交规格说明书
            createProject_fileUploadInst.upload();// 异步的
            // 表单提交操作放到上传成功的回调函数内
        });
        return false;
    });
}