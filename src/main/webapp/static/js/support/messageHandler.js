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
        showProjectCountersignDialog("项目立项会签", msgBean.msg_content, msgStr, true);
    } else if (FIELD_MSG_TYPE_COUNTERSIGN_RESULT == msgBean.msg_type) {
        showNotificationKeep("立项结果", msgBean.msg_content, msgBean.msg_no, true);
    } else if (FIELD_MSG_TYPE_BOSS_CHECK == msgBean.msg_type) {
        showProjectBossCheckDig("立项审核", msgBean.msg_content, msgStr, true);
    } else if (FIELD_MSG_TYPE_PROJECT_STAGE == msgBean.msg_type) {
        showNotificationKeep("项目负责阶段提醒", msgBean.msg_content, msgBean.msg_no, true);
    } else if (FIELD_MSG_TYPE_PROJECT_END == msgBean.msg_type) {
        showNotificationKeep("项目结项", msgBean.msg_content, msgBean.msg_no, true);
    } else if (FIELD_MSG_TYPE_DOC_REVIEW == msgBean.msg_type) {
        showNotificationKeep("文档审阅", msgBean.msg_content, msgBean.msg_no, true);
    }
}

/**
 * 用于主页已读/未读消息点击事件
 * @param msgStr
 */
function showMainPageMsg(msgStr, showButton) {
    let msgBean = JSON.parse(msgStr);
    if (FIELD_MSG_TYPE_COUNTERSIGN == msgBean.msg_type) {
        showProjectCountersignDialog("项目立项会签", msgBean.msg_content, msgStr, showButton);
    } else if (FIELD_MSG_TYPE_COUNTERSIGN_RESULT == msgBean.msg_type) {
        showNotificationKeep("立项结果", msgBean.msg_content, msgBean.msg_no, showButton);
    } else if (FIELD_MSG_TYPE_BOSS_CHECK == msgBean.msg_type) {
        showProjectBossCheckDig("立项审核", msgBean.msg_content, msgStr, showButton);
    } else if (FIELD_MSG_TYPE_PROJECT_STAGE == msgBean.msg_type) {
        showNotificationKeep("项目负责阶段提醒", msgBean.msg_content, msgBean.msg_no, showButton);
    } else if (FIELD_MSG_TYPE_PROJECT_END == msgBean.msg_type) {
        showNotificationKeep("项目结项", msgBean.msg_content, msgBean.msg_no, showButton);
    }
}
/*=========================================================================*/
/**
 * 右下角消息弹框,多条消息叠加展示
 */
function showNotificationKeep(title, content, msg_no, button) {
    let config;
    let buttons = [];
    if (button) {
        buttons = [{
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
                initUnReadMsg();
            }
        }];
    }
    // if (typeof func == 'string') {
        config = {
            title: title,
            text: content,
            timeout: 'keep',// 一直存在
            buttons: buttons
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
function showProjectCountersignDialog(title, content, msgStr, button) {
    let msgBean = JSON.parse(msgStr);
    let html = "<div class=\"comm-dialog\" id=\"projectCountersign_dialogDiv\">\n" +
        "    <form class=\"layui-form layui-form-pane\" lay-filter=\"\" action=\"\">\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <label class=\"layui-form-label\">项目名称</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <input type=\"text\" name=\"projectCountersign_projectName\" id=\"projectCountersign_projectName\" disabled  lay-verify=\"\" autocomplete=\"off\" class=\"layui-input\">\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <label class=\"layui-form-label\">中文名称</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <input type=\"text\" name=\"projectCountersign_chnName\" id=\"projectCountersign_chnName\" disabled  lay-verify=\"\" autocomplete=\"off\" class=\"layui-input\">\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <div class=\"layui-inline\">\n" +
        "                <label class=\"layui-form-label\">产品规格书</label>\n" +
        "                <div class=\"layui-input-inline\">\n" +
        "                    <input type=\"text\" name=\"projectCountersign_produceDoc\" id=\"projectCountersign_produceDoc\" disabled lay-verify=\"\" autocomplete=\"off\" class=\"layui-input\">\n" +
        "                </div>\n" +
        "            </div>\n" +
        "            <div class=\"layui-inline\">\n" +
        "                <button type='button' id=\"projectCountersign_produceDoc_download\" class=\"layui-btn\" >下载规格书</button>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <label class=\"layui-form-label\">项目开始日期</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <input type=\"text\" name=\"projectCountersign_beginDate\" id=\"projectCountersign_beginDate\" lay-verify=\"date\" placeholder=\"yyyy-MM-dd\" autocomplete=\"off\" class=\"layui-input\">\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item layui-form-text\">\n" +
        "            <label class=\"layui-form-label\">项目描述</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <textarea name=\"projectCountersign_desc\" id=\"projectCountersign_desc\" disabled placeholder=\"项目描述\" class=\"layui-textarea\"></textarea>\n" +
        "            </div>\n" +
        "        </div>" +
        /*"<fieldset name='projectManage_stageFieldSet' class=\"layui-elem-field layui-field-title\">\n" +
        "    <legend>负责阶段</legend>" +
        "    <div class=\"layui-field-box\">\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <label class=\"layui-form-label\">阶段名称</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <input type=\"text\" name=\"projectCountersign_projectName\" id=\"projectCountersign_projectName\" disabled  lay-verify=\"\" autocomplete=\"off\" class=\"layui-input\">\n" +
        "            </div>\n" +
        "        </div>\n" +
        "    </div>" +
        "</fieldset>" +*/
        "    </form>\n" +
        "</div>";
    let buttons = [];
    if (button) {
        buttons = [{
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
                            initUnReadMsg();
                        }
                    },
                    btn2: function (index, layero) {//拒绝按钮回调
                        let failHtml = "<div class=\"comm-dialog\" id=\"projectCreate_failDiv\">\n" +
                            "    <form class=\"layui-form layui-form-pane\" lay-filter=\"\" action=\"\">\n" +
                            "        <div class=\"layui-form-item layui-form-text\">\n" +
                            "            <label class=\"layui-form-label\">拒绝原因</label>\n" +
                            "            <div class=\"layui-input-block\">\n" +
                            "                <textarea name=\"projectCountersign_failReason\" id=\"projectCountersign_failReason\" placeholder=\"拒绝原因\" class=\"layui-textarea\"></textarea>\n" +
                            "            </div>\n" +
                            "        </div>" +
                            "    </form>" +
                            "</div>";
                        layui.layer.open({
                            type : '1',
                            title: '拒绝原因',
                            area: ['300px', '300px'],// 宽高
                            content: failHtml,
                            btn: ['提交'],
                            yes: function(index, layero) {// 同意按钮回调
                                let failReason = $("#projectCountersign_failReason").val();
                                if (commonBlank(failReason)) {
                                    commonInfo("请输入拒绝原因！");
                                    return;
                                }
                                let retData = commonAjax("createProject.do", JSON.stringify({
                                    "beanList": [],
                                    "operType": "countersign",
                                    "paramMap": {
                                        "state": "2",//1-同意 2-拒绝
                                        "msg_no": msgBean.msg_no,
                                        "project_no": msgBean.msg_param.project_no,
                                        "fail_reason": failReason
                                    }
                                }));
                                if (retData.retCode == HANDLE_SUCCESS) {
                                    commonOk("操作成功");
                                    layui.layer.close(index);
                                    initUnReadMsg();
                                }
                            },
                            success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
                            }
                        });

                    },
                    success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
                        $("#projectCountersign_projectName").val(msgBean.msg_param.project_name);
                        $("#projectCountersign_chnName").val(msgBean.msg_param.chn_name);
                        $("#projectCountersign_produceDoc").val(msgBean.msg_param.project_name);
                        $("#projectCountersign_desc").val(msgBean.msg_param.desc);
                        layui.laydate.render({
                            elem: '#projectCountersign_beginDate',
                            value: commonFormatDate(msgBean.msg_param.begin_date)
                        });
                        $("#projectCountersign_produceDoc_download").click(function () {
                            let postFix = msgBean.msg_param.file_path.substring(msgBean.msg_param.file_path.lastIndexOf("."), msgBean.msg_param.file_path.length);
                            // 下载文件
                            commonFileDownload(msgBean.msg_param.project_name + "_产品规格说明书" + postFix, msgBean.msg_param.file_path);
                        });
                    }
                });
            }
        }, {
            text: '关闭',
            click: function (e) {
                e.closeNotification()
            }
        }];
    }
    naranja()['log']({
        title: title,
        text: content,
        timeout: 'keep',// 一直存在
        buttons: buttons
    })
}

function showProjectBossCheckDig(title, content, msgStr, button) {
    let msgBean = JSON.parse(msgStr);
    let html = "<div class=\"comm-dialog\" id=\"projectCreate_bossCheckDiv\">\n" +
        "    <form class=\"layui-form layui-form-pane\" id=\"projectCreate_bossCheckFrm\" lay-filter=\"\" action=\"\">\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <label class=\"layui-form-label\">项目名称</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <input type=\"text\" name=\"projectCreate_bossCheck_projectName\" id=\"projectCreate_bossCheck_projectName\" disabled lay-verify=\"\" autocomplete=\"off\" class=\"layui-input \">\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <label class=\"layui-form-label\">中文名称</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <input type=\"text\" name=\"projectCreate_bossCheck_chnName\" id=\"projectCreate_bossCheck_chnName\" disabled lay-verify=\"\" autocomplete=\"off\" class=\"layui-input \">\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <label class=\"layui-form-label\">项目开始日期</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <input type=\"text\" name=\"projectCreate_bossCheck_beginDate\" id=\"projectCreate_bossCheck_beginDate\" disabled lay-verify=\"date\" placeholder=\"yyyy-MM-dd\" class=\"layui-input \">\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <div class=\"layui-inline\">\n" +
        "                <label class=\"layui-form-label\">产品规格书</label>\n" +
        "                <div class=\"layui-input-inline\">\n" +
        "                    <input type=\"text\" name=\"projectCreate_bossCheck_produceDoc\" id=\"projectCreate_bossCheck_produceDoc\" disabled lay-verify=\"\" autocomplete=\"off\" class=\"layui-input \">\n" +
        "                </div>\n" +
        "            </div>\n" +
        "            <div class=\"layui-inline\">\n" +
        "                <button type='button' id=\"projectCreate_bossCheck_download\" class=\"layui-btn\">下载规格书</button>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <label class=\"layui-form-label\">负责人</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <input type=\"text\" name=\"projectCreate_bossCheck_principal\" id=\"projectCreate_bossCheck_principal\" disabled lay-verify=\"\" autocomplete=\"off\" class=\"layui-input \">\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <label class=\"layui-form-label\">项目成员</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <input type=\"text\" title='' name=\"projectCreate_bossCheck_members\" id=\"projectCreate_bossCheck_members\" disabled lay-verify=\"\" autocomplete=\"off\" class=\"layui-input \">\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <button type='button' id=\"projectCreate_bossCheck_memberDownload\" class=\"layui-btn\"><i class=\"layui-icon layui-icon-download-circle\"></i>项目成员表</button>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item layui-form-text\">\n" +
        "            <label class=\"layui-form-label\">项目描述</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <textarea name=\"projectCreate_bossCheck__desc\" id=\"projectCreate_bossCheck__desc\" disabled placeholder=\"项目描述\" class=\"layui-textarea\"></textarea>\n" +
        "            </div>\n" +
        "        </div>" +
        "    </form>\n" +
        "</div>";
    let buttons = [];
    if (button) {
        buttons = [{
            text: '查看详情',
            click: function (e) {
                // 执行函数,
                e.closeNotification()
                layui.layer.open({
                    type : '1',
                    title: '项目立项审批',
                    area: ['720px', '700px'],// 宽高
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
                            initUnReadMsg();
                        } else {
                            commonError("操作失败！:" + retData.retMsg);
                        }
                    },
                    btn2: function(index, layero) {//拒绝按钮回调
                        let failHtml = "<div class=\"comm-dialog\" id=\"projectCreate_failDiv\">\n" +
                            "    <form class=\"layui-form layui-form-pane\" lay-filter=\"\" action=\"\">\n" +
                            "        <div class=\"layui-form-item layui-form-text\">\n" +
                            "            <label class=\"layui-form-label\">拒绝原因</label>\n" +
                            "            <div class=\"layui-input-block\">\n" +
                            "                <textarea name=\"projectCreate_bossCheck_failReason\" id=\"projectCreate_bossCheck_failReason\" placeholder=\"拒绝原因\" class=\"layui-textarea\"></textarea>\n" +
                            "            </div>\n" +
                            "        </div>" +
                            "    </form>" +
                            "</div>";
                        layui.layer.open({
                            type : '1',
                            title: '拒绝原因',
                            area: ['300px', '300px'],// 宽高
                            content: failHtml,
                            btn: ['提交'],
                            yes: function(index, layero) {// 同意按钮回调
                                let failReason = $("#projectCreate_bossCheck_failReason").val();
                                if (commonBlank(failReason)) {
                                    commonInfo("请输入拒绝原因！");
                                    return;
                                }
                                let retData = commonAjax("createProject.do", JSON.stringify({
                                    "beanList": [],
                                    "operType": "bossCheck",
                                    "paramMap": {
                                        "state": "2",//1-同意 2-拒绝
                                        "msg_no": msgBean.msg_no,
                                        "project_no": msgBean.msg_param.project_no,
                                        "fail_reason" : failReason
                                    }
                                }));
                                if (retData.retCode == HANDLE_SUCCESS) {
                                    commonOk("操作成功");
                                    layui.layer.close(index);
                                    initUnReadMsg();
                                } else {
                                    commonError("操作失败！:" + retData.retMsg);
                                }
                            },
                            success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
                            }
                        });

                    },
                    success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
                        $("#projectCreate_bossCheck_projectName").val(msgBean.msg_param.project_name);
                        $("#projectCreate_bossCheck_chnName").val(msgBean.msg_param.chn_name);
                        $("#projectCreate_bossCheck_produceDoc").val(msgBean.msg_param.project_name);
                        $("#projectCreate_bossCheck_principal").val(msgBean.msg_param.principal);
                        let menbers = msgBean.msg_param.project_menbers.split(",");
                        let menber = "";
                        for (let t = 0; t < menbers.length; t++) {
                            menber += "," + commonFormatUserNo(menbers[t], true);
                        }
                        menber = menber.substring(1, menber.length);
                        $("#projectCreate_bossCheck_members").val(menber);
                        $("#projectCreate_bossCheck_members").attr('title', menber);
                        layui.laydate.render({
                            elem: '#projectCreate_bossCheck_beginDate',
                            value : commonFormatDate(msgBean.msg_param.begin_date)
                        });
                        $("#projectCreate_bossCheck_download").click(function () {
                            let postFix = msgBean.msg_param.specifications.substring(msgBean.msg_param.specifications.lastIndexOf("."), msgBean.msg_param.specifications.length);
                            // 下载文件
                            commonFileDownload(msgBean.msg_param.project_name + "_产品规格书" + postFix, msgBean.msg_param.specifications);
                            return false;
                        });

                        // 项目成员表下载
                        $("#projectCreate_bossCheck_memberDownload").click(function () {
                            let param = "?project_no=" + msgBean.msg_param.project_no;
                            commonExportExcel("projectMember", msgBean.msg_param.project_name + "_项目成员表.xlsx", param);
                        });
                        // 拼接阶段信息
                        let stageData = commonAjax("projectManage.do", JSON.stringify({
                            "beanList": [],
                            "operType": "getStageInfo",
                            "paramMap": {
                                "project_no": msgBean.msg_param.project_no// 项目编号
                            }
                        }));
                        if (stageData.retCode == HANDLE_SUCCESS) {
                            let stageList = stageData.retMap.stageList;
                            for (let i = 0;i < stageList.length; i++) {
                                let tempHtml = "<fieldset name='projectCreate_bossCheck_stageFieldSet' class=\"layui-elem-field layui-field-title\">\n";
                                tempHtml += "            <legend>阶段" + (i + 1) + "</legend>\n";
                                tempHtml +=    "            <div class=\"layui-field-box\">\n" +
                                    "                <div class=\"layui-form-item\">\n" +
                                    "                    <div class=\"layui-inline\">\n" +
                                    "                        <label class=\"layui-form-label\">阶段类型</label>\n" +
                                    "                        <div class=\"layui-input-inline\">\n" +
                                    "                            <input type=\"text\" value='" + commonFormatValue(FIELD_STAGE, stageList[i].stage) + "' name=\"projectCreate_bossCheck_stage_dialog" + (i + 1) + "\" id=\"projectCreate_bossCheck_stage_dialog" + (i + 1) + "\" disabled autocomplete=\"off\" class=\"layui-input\">\n" +
                                    "                        </div>\n" +
                                    "                    </div>\n" +
                                    "                    <div class=\"layui-inline\">\n" +
                                    "                        <label class=\"layui-form-label\">开始日期</label>\n" +
                                    "                        <div class=\"layui-input-inline\">\n" +
                                    "                            <input type=\"text\" value='" + commonFormatDate(stageList[i].begin_date) + "' name=\"projectCreate_bossCheck_beginDate_dialog" + (i + 1) + "\" id=\"projectCreate_bossCheck_beginDate_dialog" + (i + 1) + "\" disabled autocomplete=\"off\" class=\"layui-input\">\n" +
                                    "                        </div>\n" +
                                    "                    </div>\n" +
                                    "                </div>\n" +
                                    "                <div class=\"layui-form-item\">\n" +
                                    "                    <div class=\"layui-inline\">\n" +
                                    "                        <label class=\"layui-form-label\">结束日期</label>\n" +
                                    "                        <div class=\"layui-input-inline\">\n" +
                                    "                            <input type=\"text\" value='" + commonFormatDate(stageList[i].end_date) + "' name=\"projectCreate_bossCheck_endDate_dialog" + (i + 1) + "\" id=\"projectCreate_bossCheck_endDate_dialog" + (i + 1) + "\" disabled autocomplete=\"off\" class=\"layui-input\">\n" +
                                    "                        </div>\n" +
                                    "                    </div>\n" +
                                    "                    <div class=\"layui-inline\">\n" +
                                    "                        <label class=\"layui-form-label\">阶段文档</label>\n" +
                                    "                        <div class=\"layui-input-inline\">\n" +
                                    "                            <input type=\"text\" title='" + formatProjectDocs(stageList[i].unupload_doc) + "' value='" + formatProjectDocs(stageList[i].unupload_doc) + "' name=\"projectCreate_bossCheck_docName_dialog" + (i + 1) + "\" id=\"projectCreate_bossCheck_docName_dialog" + (i + 1) + "\" disabled autocomplete=\"off\" class=\"layui-input\">\n" +
                                    "                        </div>\n" +
                                    "                    </div>\n" +
                                    "                </div>\n" +
                                    "            </div>\n" +
                                    "        </fieldset>";
                                $("#projectCreate_bossCheckFrm").append(tempHtml);
                            }

                            layui.form.render();
                            // 绑定规格书下载事件
                            /*$("#projectManage_specificationsDownload_dialog").click(function () {
                                commonFileDownload(data.project_name + "产品规格说明书.doc", $("#projectManage_specificationsDownload_dialog").val());
                            });*/
                        } else {
                            commonError("加载项目阶段信息失败：" + stageData.retMsg);
                        }
                    }
                });
            }
        }, {
            text: '关闭',
            click: function (e) {
                e.closeNotification()
            }
        }];
    }
    naranja()['log']({
        title: title,
        text: content,
        timeout: 'keep',// 一直存在
        buttons: buttons
    })
}