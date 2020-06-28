/**
 * 任务处理函数
 */
function taskHandler_showTask(taskBean) {
    if (taskBean.task_type == FIELD_TASK_TYPE_STAGE_DOC_UPLOAD) {
        taskHandler_stageDocUpload(taskBean);
    }
}

/** 文档审阅 */
function taskHandler_docReview(taskBean) {
    let html = "<div class=\"comm-dialog\" >\n" +
        "    <form class=\"layui-form layui-form-pane\" lay-filter=\"docReview_Frm\" id=\"docReview_Frm\" action=\"\">\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <label class=\"layui-form-label\">文档编号</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <input type=\"text\" name=\"docReview_docNo\" id=\"docReview_docNo\" required class=\"layui-input\" disabled>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <label class=\"layui-form-label\">文档名称</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <input type=\"text\" name=\"docReview_docName\" id=\"docReview_docName\" class=\"layui-input\" disabled>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <label class=\"layui-form-label\">文档语言</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <select name=\"docReview_docLanguage\" id=\"docReview_docLanguage\" disabled>\n" +
        "                </select>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <label class=\"layui-form-label\">文档类型</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <select name=\"docReview_docType\" id=\"docReview_docType\" disabled>\n" +
        "                </select>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <label class=\"layui-form-label\">文档作者</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <input type=\"text\" name=\"docReview_docWriter\" id=\"docReview_docWriter\" class=\"layui-input\" disabled>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item layui-form-text\">\n" +
        "            <label class=\"layui-form-label\">文档摘要</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <textarea name=\"docReview_docDesc\" id=\"docReview_docDesc\" class=\"layui-textarea\" disabled></textarea>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item comm-dialog-button\">\n" +
        "            <button class=\"layui-btn\" type=\"button\"  id=\"docReview_review\" >提交意见</button>\n" +
        "            <button class=\"layui-btn\" type=\"button\"  id=\"docReview_download\" >下载文档</button>\n" +
        "        </div>\n" +
        "    </form>\n" +
        "</div>";

    let html2 = "<div class=\"comm-dialog\" >\n" +
        "    <form class=\"layui-form layui-form-pane\" lay-filter=\"personalDoc_submitFrm\" id=\"personalDoc_submitFrm\" action=\"\">\n" +
        "        <div class=\"layui-form-item layui-form-text\">\n" +
        "            <label class=\"layui-form-label\">审阅意见</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <textarea name=\"docReview_opinion\" id=\"docReview_opinion\" required lay-verify=\"required\" class=\"layui-textarea\" ></textarea>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item comm-dialog-button\">\n" +
        "            <button class=\"layui-btn\" id=\"docReview_submit\" lay-submit lay-filter=\"docReview_submit\">提交</button>\n" +
        "        </div>\n" +
        "    </form>\n" +
        "</div>";

    layui.layer.open({
        type: 1,// 页面层
        area: ['500px', '600px'],// 宽高
        title: '文档审阅',// 标题
        content: html,//内容，直接取dom对象
        // btn: ['确定'],
        // yes: function (index, layero) {
        //     //确认按钮的回调，提交表单
        // },
        success: function (layero, index1) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
            let docBean = JSON.parse((taskBean.task_param));
            let postFix = docBean.file_path.substring(docBean.file_path.lastIndexOf("."), docBean.file_path.length);
            // 渲染弹框元素
            commonPutNormalSelectOpts(FIELD_DOC_LANGUAGE, "docReview_docLanguage", docBean.doc_language, true);
            commonPutNormalSelectOpts(FIELD_DOC_TYPE, "docReview_docType", docBean.doc_type, true);
            layui.form.val("docReview_Frm", {
                "docReview_docNo": docBean.doc_no, // "name": "value"
                "docReview_docName": docBean.doc_name,
                "docReview_docLanguage": docBean.doc_no + postFix,
                "docReview_docType": docBean.doc_language,
                "docReview_docWriter": docBean.doc_type,
                "docReview_docDesc": docBean.doc_writer
            });

            $("#docReview_download").click(function () {
                commonFileDownload(docBean.doc_no + postFix, docBean.file_path);
            });
            layui.form.render();
            $("#docReview_review").click(function () {
                layui.layer.open({
                    type: 1,// 页面层
                    area: ['500px', '250px'],// 宽高
                    title: '审阅意见',// 标题
                    content: html2,//内容，直接取dom对象
                    // btn: ['确定'],
                    // yes: function (index, layero) {
                    //     //确认按钮的回调，提交表单
                    // },
                    success: function (layero, index2) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
                        layui.form.render();
                        layui.form.on('submit(docReview_submit)', function(data){// 绑定提交按钮点击回调事件，只有表单验证通过会进入
                            layui.layer.confirm("是否确认提交？", function(index) {
                                let retData = commonAjax("personalDoc.do", JSON.stringify({
                                    "beanList": [{}],
                                    "operType": "userReview",
                                    "paramMap": {
                                        "doc_serial_no": docBean.doc_serial_no,
                                        "review_user": sessionStorage.getItem("user_no"),
                                        "opinion": $("#docReview_opinion").val(),
                                        "task_no": taskBean.task_no
                                    }
                                }));
                                if (retData.retCode == HANDLE_SUCCESS) {
                                    commonOk("提交成功!");
                                    layui.layer.closeAll();
                                } else {
                                    commonError(retData.retMsg);
                                }
                            });
                            return false;
                        });
                    }
                });
            });
        }
    });
}

/**
 * 阶段文档上传
 */
function taskHandler_stageDocUpload(taskBean) {
    let html = "<div class=\"comm-dialog\" >\n" +
        "    <form class=\"layui-form layui-form-pane\" lay-filter=\"stageDocUpload_Frm\" id=\"stageDocUpload_Frm\" action=\"\">\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <label class=\"layui-form-label\">项目名称</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <input type=\"text\" name=\"stageDocUpload_projectName\" id=\"stageDocUpload_projectName\" required class=\"layui-input\" disabled>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <label class=\"layui-form-label\">项目阶段</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <input type=\"text\" name=\"stageDocUpload_stageName\" id=\"stageDocUpload_stageName\" class=\"layui-input\" disabled>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <label class=\"layui-form-label\">阶段结束时间</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <input type=\"text\" name=\"stageDocUpload_stageEndTime\" id=\"stageDocUpload_stageEndTime\" class=\"layui-input\" disabled>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <label class=\"layui-form-label\">文档名称</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <input type=\"text\" name=\"stageDocUpload_docName\" id=\"stageDocUpload_docName\" class=\"layui-input\" disabled>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <label class=\"layui-form-label\">文档作者</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <input type=\"text\" name=\"stageDocUpload_writer\" id=\"stageDocUpload_writer\" class=\"layui-input\" disabled>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item layui-form-text\">\n" +
        "            <label class=\"layui-form-label\">选择文件</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <input type=\"text\" name=\"stageDocUpload_selectFile\" id=\"stageDocUpload_selectFile\" class=\"layui-input\" disabled>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "    </form>\n" +
        "</div>";
    let curUploadInst;
    layui.layer.open({
        type: 1,// 页面层
        area: ['500px', '600px'],// 宽高
        title: '文档审阅',// 标题
        content: html,//内容，直接取dom对象
        btn: ['提交'],
        yes: function (index, layero) {
            //确认按钮的回调，提交表单
            curUploadInst.upload();
        },
        success: function (layero, index1) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
            let taskParam = JSON.parse((taskBean.task_param));
            // 渲染弹框元素
            layui.form.val("stageDocUpload_Frm", {
                "stageDocUpload_projectName": taskParam.project_name, // "name": "value"
                "stageDocUpload_stageName": taskParam.stage_name,
                "stageDocUpload_stageEndTime": commonFormatDate(taskParam.stage_end_date),
                "stageDocUpload_docName": taskParam.doc_name,
                "stageDocUpload_writer": taskParam.writer
            });

            layui.form.render();

            // 绑定上传插件
            curUploadInst = layui.upload.render({
                elem: '#stageDocUpload_selectFile',
                url: 'projectStageDocUpload.do',//改成您自己的上传接口
                data: {
                    "project_no": taskParam.project_no,
                    "doc_serial": taskParam.doc_serial,
                    "file_root_path": taskParam.file_root_path,
                    "doc_no": taskParam.doc_no,
                    "stage_num": taskParam.stage_num,
                    "doc_name": taskParam.doc_name,
                    "doc_version": ""
                },
                accept: 'file',//
                auto: false,// 选择文件后是否自动上传
                // bindAction: "#createProject_fileSubmit",
                multiple: false,// 是否允许多文件上传
                choose: function(obj) {
                    //预读本地文件,如果是多文件，则会遍历
                    obj.preview(function (index, file, result) {
                        $("#stageDocUpload_selectFile").val(file.name);
                    });
                },
                done: function(res, index, upload){
                    if (res.code == '0') {
                        // 更新任务信息，关闭弹框，刷新任务列表
                        let updateState = commonUpdateTaskState(taskBean.task_no, sessionStorage.getItem("user_no"));
                        if (updateState.retCode != HANDLE_SUCCESS) {
                            commonError(updateState.retMsg);
                            return;
                        }
                        layui.layer.closeAll();
                        // 刷新任务列表
                        initTodoTask();
                    } else {
                        commonError("上传阶段文档失败:" + res.msg)
                    }
                },
                error: function (index, upload) {
                    commonError("上传阶段文档失败，请稍后重试");
                }
            });
        }
    });
}