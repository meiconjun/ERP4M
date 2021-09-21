var createProject_stageCount = 1;// 阶段数量计数器
var createProject_fileUploadInst;
var createProject_fileUploadFlag = false;
$(document).ready(function () {
    try {
        // 渲染下拉框
        commonPutNormalSelectOpts(FIELD_STAGE, "createProject_stage1", "", true);
        // 获取系统用户列表
        let users = getAllUserList();
        $("#createProject_members").click(function () {
            layui.layer.open({
                type: 1,// 页面层
                area: ['500px', '500px'],// 宽高
                title: '选择项目成员',// 标题
                content: $("#createProject_memberTransfer"),//内容，直接取dom对象
                btn: ['确定'],
                yes: function (index, layero) {
                    let transferData = layui.transfer.getData("createProject_memberTransfer");
                    let selectTransferStr = "";
                    for (let j = 0; j < transferData.length; j ++) {
                        selectTransferStr += "," + transferData[j].value;
                    }
                    if (!commonBlank(selectTransferStr)) {
                        selectTransferStr = selectTransferStr.substring(1, selectTransferStr.length);
                    }
                    $("#createProject_members").val(selectTransferStr);
                    layui.layer.close(index);
                },
                success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
                    layui.transfer.render({
                        elem: '#createProject_memberTransfer',
                        id: "createProject_memberTransfer",
                        title: ["可选", "已选"]
                        ,data: users,
                        value: $("#createProject_members").val().split(",")
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
            });

        });
        //渲染表单元素
        layui.form.render();

        createProject_fileUploadInst = layui.upload.render({
            elem: '#createProject_productDoc',
            url: 'projectDescFileUpload.do',//改成您自己的上传接口
            headers: {
                'authorization': localStorage.getItem("authorization"),
                // 'user': localStorage.getItem("user_info")
            },
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
                    layui.layer.load();
                    createProject_fileUploadFlag = true;
                    // 获取阶段信息
                    let stageList = [];
                    for (let i = 1; i <= createProject_stageCount; i++) {
                        let tempStage = {
                            "stageCount": String(i),// 用数字会被转为double
                            "stage_type": $("#createProject_stage" + i).val(),
                            "stage_begin": $("#createProject_stage_beginDate" + i).val().replace(/-/g, ""),
                            "stage_end": $("#createProject_stage_endDate" + i).val().replace(/-/g, ""),
                            "stage_doc": $("#createProject_stageDoc" + i).val()
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
                            "file_root_path": res.data.file_root_path,
                            "begin_date": $("#createProject_beginDate").val().replace(/-/g, ""),
                            "member": $("#createProject_members").val(),
                            "desc": $("#createProject_desc").val(),
                            "stageList": stageList
                        }
                    };
                    let retData = commonAjax("createProject.do", JSON.stringify(reqMsg));
                    layui.layer.closeAll('loading');
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
            value : new Date()
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
        "                    <button type=\"button\" class=\"layui-btn layui-btn-primary\" onclick=\"createProject_selectProjectDoc(this)\" name=\"createProject_stageDoc" + createProject_stageCount_temp + "\" id=\"createProject_stageDoc" + createProject_stageCount_temp + "\" >\n" +
        "                    选择阶段文档</button>\n" +
        "                </div>\n" +
        "                <button type=\"button\" class=\"layui-btn\" onclick=\"createProject_addStage()\"><i class=\"layui-icon layui-icon-add-circle\"></i></button>" +
        "                <button type=\"button\" class=\"layui-btn\" onclick=\"createProject_minStage()\"><i class=\"layui-icon layui-icon-reduce-circle\"></i></button>\n" +
        "            </div>\n" +
        "        </div>";
    $("#roleConfig_addFrm").append(html);
    // 渲染下拉框
    commonPutNormalSelectOpts(FIELD_STAGE, "createProject_stage" + createProject_stageCount_temp, "", true);
    //渲染表单元素
    layui.form.render();
    // 渲染日期组件
    layui.laydate.render({
        elem: '#createProject_stage_beginDate' + createProject_stageCount_temp,
        value : $("#createProject_stage_endDate" + createProject_stageCount).val()
    });
    // 渲染日期组件
    layui.laydate.render({
        elem: '#createProject_stage_endDate' + createProject_stageCount_temp
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
    $("div[stageCount='" + createProject_stageCount + "']").remove();
    createProject_stageCount--;
}

function createProject_submit() {
    layui.form.on('submit(createProject_submit)', function(data){// 绑定提交按钮点击回调事件，只有表单验证通过会进入
        layui.layer.confirm("是否确认提交？", function(index) {
            // 判断是否所有阶段都已选择阶段文档
            let docList = $("button[id^='createProject_stageDoc']");
            for (let i = 0; i < docList.length; i++) {
                if (commonBlank($(docList[i]).val())) {
                    commonInfo("请确保所有阶段已选择文档");
                    return;
                }
            }
            //先提交规格说明书
            createProject_fileUploadInst.upload();// 异步的
            // 表单提交操作放到上传成功的回调函数内
        });
        return false;
    });
}

/*function createProject_getAllStageUser() {
    let selects = $("select[name^='createProject_stageUser']");
    let retStr = "";
    for (let i = 0; i < selects.length; i++) {
        if (i == 0) {
            retStr = $(selects[i]).val();
        } else {
            retStr += "," + $(selects[i]).val();
        }
    }
    return retStr;
}*/

function createProject_selectProjectDoc(thisObj) {
    let $thisObj = $(thisObj);
    let stageCount = $thisObj.parent().parent().parent().attr("stageCount");
    let stageType = $("#createProject_stage" + stageCount).val();

    // 获取当前阶段文档列表
    let retData = commonAjax("createProject.do", JSON.stringify({
        "beanList": [],
        "operType": "getProjectDoc",
        "paramMap": {
            "stage": stageType
        }
    }));
    if (retData.retCode == HANDLE_SUCCESS) {
        layui.layer.open({
            type: 1,// 页面层
            area: ['500px', '500px'],// 宽高
            title: '选择阶段文档',// 标题
            content: $("#createProject_memberTransfer2"),//内容，直接取dom对象
            btn: ['确定'],
            yes: function (index, layero) {
                let transferData = layui.transfer.getData("createProject_memberTransfer2");
                let selectTransferStr = "";
                for (let j = 0; j < transferData.length; j ++) {
                    selectTransferStr += "," + transferData[j].value;
                }
                if (!commonBlank(selectTransferStr)) {
                    selectTransferStr = selectTransferStr.substring(1, selectTransferStr.length);
                }
                $thisObj.val(selectTransferStr);
                layui.layer.close(index);
            },
            success: function (layero, index1) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
                // 渲染穿梭框
                layui.transfer.render({
                    elem: "#createProject_memberTransfer2",
                    id: "createProject_memberTransfer2",
                    title: ["可选", "已选"],
                    data: retData.retMap.docList,
                    parseData: function (res) {
                        return {
                            "value": res.value,
                            "title": res.value + "-" + res.name
                        }
                    },
                    value: $thisObj.val().split(","),
                    onchange: function (data, index) {
                    }
                });
            }
        });

    } else {
        commonError("加载项目文档失败！");
        return;
    }
}