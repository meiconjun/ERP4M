/**
 * 消息推送相关处理
 */
/*=========================================================================*/
/**
 * 判断消息类型，调用对应方法
 */
function distributionMessage(msgStr) {
    let msgBean = JSON.parse(msgStr);
    if (FIELD_MSG_TYPE_COUNTERSIGN == msgBean.msg_type) {
        showProjectCountersignDialog("项目立项会签", msgBean.msg_content, msgStr);
    } else if (FIELD_MSG_TYPE_COUNTERSIGN_RESULT == msgBean.msg_type) {
        showNotificationKeep("立项结果", msgBean.msg_content, msgBean.msg_no);
    } else if (FIELD_MSG_TYPE_BOSS_CHECK == msgBean.msg_type) {
        showNotificationKeep("立项审核", msgBean.msg_content, showProjectBossCheckDig(msgStr));
    } else if (FIELD_MSG_TYPE_PROJECT_STAGE == msgBean.msg_type) {
        showNotificationKeep("项目阶段提醒", msgBean.msg_content, msgBean.msg_no);
    }
}


/*=========================================================================*/
/**
 * 右下角消息弹框,多条消息叠加展示
 */
function showNotificationKeep(title, content, msg_no) {
    let config;
    // if (typeof func == 'string') {
        config = {
            title: title,
            text: content,
            timeout: 'keep',// 一直存在
            buttons: [{
                text: '确定',
                click: function (e) {
                    e.closeNotification();
                    //更新为已读
                    commonAjax("common.do", JSON.stringify({
                        "beanList": [],
                        "operType": "updateMessage",
                        "paramMap": {
                            'msg_no': msg_no
                        }
                    }));
                }
            }]
        };
    /*} else {
        config = {
            title: title,
            text: content,
            timeout: 'keep',// 一直存在
            buttons: [{
                text: '查看详情',
                click: function (e) {
                    // 执行函数,
                    func();
                }
            },{
                text: '关闭',
                click: function (e) {
                    e.closeNotification()
                }
            }]
        };
    }*/
    naranja()['log'](config)
}

/**
 * 项目立项会签消息
 */
function showProjectCountersignDialog(title, content, msgStr) {
    let msgBean = JSON.parse(msgStr);
    let html = "<div class=\"comm-dialog\" id=\"projectCountersign_dialogDiv\">\n" +
        "    <form class=\"layui-form layui-form-pane\" lay-filter=\"\" action=\"\">\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <label class=\"layui-form-label\">项目名称</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <input type=\"text\" name=\"projectCountersign_projectName\" id=\"projectCountersign_projectName\" disabled  lay-verify=\"\" autocomplete=\"off\" class=\"layui-input layui-disabled\">\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <label class=\"layui-form-label\">中文名称</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <input type=\"text\" name=\"projectCountersign_chnName\" id=\"projectCountersign_chnName\" disabled  lay-verify=\"\" autocomplete=\"off\" class=\"layui-input layui-disabled\">\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <div class=\"layui-inline\">\n" +
        "                <label class=\"layui-form-label\">产品规格书</label>\n" +
        "                <div class=\"layui-input-inline\">\n" +
        "                    <input type=\"text\" name=\"projectCountersign_produceDoc\" id=\"projectCountersign_produceDoc\" disabled lay-verify=\"\" autocomplete=\"off\" class=\"layui-input layui-disabled\">\n" +
        "                </div>\n" +
        "            </div>\n" +
        "            <div class=\"layui-inline\">\n" +
        "                <button id=\"projectCountersign_produceDoc_download\" class=\"layui-btn\" >下载规格书</button>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <label class=\"layui-form-label\">项目开始日期</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <input type=\"text\" name=\"projectCountersign_beginDate\" id=\"projectCountersign_beginDate\" lay-verify=\"date\" placeholder=\"yyyy-MM-dd\" autocomplete=\"off\" class=\"layui-input layui-disabled\">\n" +
        "            </div>\n" +
        "        </div>\n" +
        "    </form>\n" +
        "</div>";

    naranja()['log']({
        title: title,
        text: content,
        timeout: 'keep',// 一直存在
        buttons: [{
            text: '查看详情',
            click: function (e) {
                // 执行函数,
                e.closeNotification()
                layui.layer.open({
                    type: '1',
                    title: '项目立项会签',
                    area: ['500px', '550px'],// 宽高
                    content: html,
                    btn: ['同意', '拒绝'],
                    yes: function (index, layero) {// 同意按钮回调
                        let retData = commonAjax("createProject.do", JSON.stringify({
                            "beanList": [],
                            "operType": "countersign",
                            "paramMap": {
                                "state": "1",//1-同意 2-拒绝
                                "msg_no": msgBean.msg_no,
                                "project_no": msgBean.msg_param.project_no
                            }
                        }));
                        if (retData.retCode == HANDLE_SUCCESS) {
                            commonOk("操作成功");
                            layui.layer.close(index);
                        }
                    },
                    btn2: function (index, layero) {//拒绝按钮回调
                        let retData = commonAjax("createProject.do", JSON.stringify({
                            "beanList": [],
                            "operType": "countersign",
                            "paramMap": {
                                "state": "2",//1-同意 2-拒绝
                                "msg_no": msgBean.msg_no,
                                "project_no": msgBean.msg_param.project_no
                            }
                        }));
                        if (retData.retCode == HANDLE_SUCCESS) {
                            commonOk("操作成功");
                            layui.layer.close(index);
                        }
                    },
                    success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
                        $("#projectCountersign_projectName").val(msgBean.msg_param.project_name);
                        $("#projectCountersign_chnName").val(msgBean.msg_param.chn_name);
                        $("#projectCountersign_produceDoc").val(msgBean.msg_param.project_name);
                        layui.laydate.render({
                            elem: '#projectCountersign_beginDate',
                            value: commonFormatDate(msgBean.msg_param.begin_date)
                        });
                        $("#projectCountersign_produceDoc_download").click(function () {
                            // 下载文件
                            commonFileDownload(msgBean.msg_param.project_name + ".doc", msgBean.msg_param.file_path);
                        });
                    }
                });
            }
        }, {
            text: '关闭',
            click: function (e) {
                e.closeNotification()
            }
        }]
    })
}

function showProjectBossCheckDig(msgStr) {
    let msgBean = JSON.parse(msgStr);
    let html = "<div class=\"comm-dialog\" id=\"projectCreate_bossCheckDiv\">\n" +
        "    <form class=\"layui-form layui-form-pane\" lay-filter=\"\" action=\"\">\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <label class=\"layui-form-label\">项目名称</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <input type=\"text\" name=\"projectCreate_bossCheck_projectName\" id=\"projectCreate_bossCheck_projectName\" disabled lay-verify=\"\" autocomplete=\"off\" class=\"layui-input layui-disabled\">\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <label class=\"layui-form-label\">中文名称</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <input type=\"text\" name=\"projectCreate_bossCheck_chnName\" id=\"projectCreate_bossCheck_chnName\" disabled lay-verify=\"\" autocomplete=\"off\" class=\"layui-input layui-disabled\">\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <label class=\"layui-form-label\">项目开始日期</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <input type=\"text\" name=\"projectCreate_bossCheck_beginDate\" id=\"projectCreate_bossCheck_beginDate\" disabled lay-verify=\"date\" placeholder=\"yyyy-MM-dd\" class=\"layui-input layui-disabled\">\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <div class=\"layui-inline\">\n" +
        "                <label class=\"layui-form-label\">产品规格书</label>\n" +
        "                <div class=\"layui-input-inline\">\n" +
        "                    <input type=\"text\" name=\"projectCreate_bossCheck_produceDoc\" id=\"projectCreate_bossCheck_produceDoc\" disabled lay-verify=\"\" autocomplete=\"off\" class=\"layui-input layui-disabled\">\n" +
        "                </div>\n" +
        "            </div>\n" +
        "            <div class=\"layui-inline\">\n" +
        "                <button id=\"projectCreate_bossCheck_download\" class=\"layui-btn\">下载规格书</button>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <label class=\"layui-form-label\">负责人</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <input type=\"text\" name=\"projectCreate_bossCheck_principal\" id=\"projectCreate_bossCheck_principal\" disabled lay-verify=\"\" autocomplete=\"off\" class=\"layui-input layui-disabled\">\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <label class=\"layui-form-label\">项目成员</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <input type=\"text\" name=\"projectCreate_bossCheck_members\" id=\"projectCreate_bossCheck_members\" disabled lay-verify=\"\" autocomplete=\"off\" class=\"layui-input layui-disabled\">\n" +
        "            </div>\n" +
        "        </div>\n" +
        "    </form>\n" +
        "</div>";
        layui.layer.open({
            type : '1',
            title: '项目立项审批',
            area: ['500px', '700px'],// 宽高
            content: html,
            btn: ['同意', '拒绝'],
            yes: function(index, layero) {// 同意按钮回调
                let retData = commonAjax("createProject.do", JSON.stringify({
                    "beanList": [],
                    "operType": "bossCheck",
                    "paramMap": {
                        "state": "1",//1-同意 2-拒绝
                        "msg_no": msgBean.msg_no,
                        "project_no": msgBean.msg_param.project_no
                    }
                }));
                if (retData.retCode == HANDLE_SUCCESS) {
                    commonOk("操作成功");
                    layui.layer.close(index);
                }
            },
            btn2: function(index, layero) {//拒绝按钮回调
                let retData = commonAjax("createProject.do", JSON.stringify({
                    "beanList": [],
                    "operType": "bossCheck",
                    "paramMap": {
                        "state": "2",//1-同意 2-拒绝
                        "msg_no": msgBean.msg_no,
                        "project_no": msgBean.msg_param.project_no
                    }
                }));
                if (retData.retCode == HANDLE_SUCCESS) {
                    commonOk("操作成功");
                    layui.layer.close(index);
                }
            },
            success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
                $("#projectCreate_bossCheck_projectName").val(msgBean.msg_param.project_name);
                $("#projectCreate_bossCheck_chnName").val(msgBean.msg_param.chn_name);
                $("#projectCreate_bossCheck_produceDoc").val(msgBean.msg_param.project_name);
                $("#projectCreate_bossCheck_principal").val(msgBean.msg_param.principal);
                $("#projectCreate_bossCheck_members").val(msgBean.msg_param.project_menbers);
                layui.laydate.render({
                    elem: '#projectCreate_bossCheck_beginDate',
                    value : commonFormatDate(msgBean.msg_param.begin_date)
                });
                $("#projectCreate_bossCheck_download").click(function () {
                    // 下载文件
                    commonFileDownload(msgBean.msg_param.project_name + ".doc", msgBean.msg_param.specifications);
                    return false;
                });
            }
        });
}