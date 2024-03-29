var buglist_tableInst = null;
var buglist_curr = 1;
var iframeWindowObj;
$(document).ready(function () {
    try {
        let buttonMap = commonGetAuthField('O02000');
        let buttonStr = buttonMap.buttonStr;

        // 初始化表格
        buglist_tableInst = layui.table.render({
            id : "buglist_tableObj",
            elem : '#buglist_table',
            height : 'full-450',
            url: 'buglist.do',
            headers: {
                'authorization': localStorage.getItem("authorization"),
                // 'user': localStorage.getItem("user_info")
            },
            where : {
                message : JSON.stringify({
                    "beanList": [{
                        serial_no: "",
                        bug_name: "",
                        product: "",
                        bug_status: ""
                    }],
                    "operType": "query",
                    "paramMap": {
                        'curPage': '1',
                        'limit': FIELD_EACH_PAGE_NUM
                    }
                })
            },
            method: 'post',
            even: true,
            page: false,
            loading: true,
            skin: 'nob',// 无边框
            done: function (res, curr, count) {
                // 分页初始化
                layui.laypage.render({
                    elem : 'buglist_page',
                    limit : Number(FIELD_EACH_PAGE_NUM),
                    groups : 5,
                    curr : buglist_curr,
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
                            buglist_queryOperation(obj.curr, obj.limit);// 重载页面
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
                    fixed: 'left',
                    width: 40,
                },
                {
                    field: 'bug_name',
                    title: 'BUG名称',
                    align : 'center',
                    width: '60%',
                    templet : function (data) {
                        return "<a style='cursor:pointer;' href='#' title='" + data.bug_name + "' onclick='buglist_showDetail(" + commonFormatObj(data) + ")'>" + data.bug_name + "</a>";
                    }
                }
                , { field: 'bug_status',
                    title: '状态',
                    align : 'center',
                    templet : function (data) {
                        return commonFormatValue(FIELD_BUG_STATUS, data.bug_status, false);
                    }
                }
                , {
                    field: 'severity',
                    title: '严重级别',
                    align : 'center',
                    templet : function (data) {
                        let color = "";
                        if (data.severity == '1') {// 根据严重级别标识颜色
                            color = '#23ca30';
                        } else if (data.severity == '2') {
                            color = '#d6d133';
                        } else if (data.severity == '3') {
                            color = '#ec3027';
                        }
                        return "<span style='color: " + color + ";' title='" + commonFormatValue(FIELD_BUG_SEVERITY, data.severity, true) + "'>" + commonFormatValue(FIELD_BUG_SEVERITY, data.severity, false) + "</span>";
                    }
                }
                , {
                    field: 'priority',
                    title: '优先级',
                    align : 'center',
                    templet : function (data) {
                        return commonFormatValue(FIELD_BUG_PRIORITY, data.priority, false);
                    }
                }
                , {
                    field: 'last_modi_time',
                    title: '最后修改时间',
                    align : 'center',
                    templet : function (data) {
                        return commonFormatDate(data.last_modi_time);
                    }
                }
            ]]
        });

        commonPutNormalSelectOpts(FIELD_BUG_STATUS, "buglist_bugStatus", "", false);

        layui.form.render();// 此步是必须的，否则无法渲染一些表单元素

        if (buttonStr.indexOf("buglist_deleteBtn") == -1) {
            $("#buglist_deleteBtn").hide();
        }
        //暂不做权限控制
        $("#buglist_queryBtn").click(function () {
            buglist_queryOperation('1', FIELD_EACH_PAGE_NUM);
        });

        $("#buglist_addBtn").click(function () {
            buglist_addBtnOperation();
        });
        $("#buglist_editBtn").click(function () {
            buglist_editBtnOperation();
        });
        $("#buglist_deleteBtn").click(function () {
            buglist_deleteBtnOperation();
        });
    } catch (e) {
        console.error(e.message, e);
        commonError(e.message);
    }
});

/**
 * 查询
 */
function buglist_queryOperation(curPage, limit) {
    let serial_no = $("#buglist_serialNo").val();
    let bug_name = $("#buglist_bugName").val();
    let product = $("#buglist_product").val();
    let bug_status = $("#buglist_bugStatus").val();

    buglist_curr = curPage;
    buglist_tableInst.reload({
        where: { //设定异步数据接口的额外参数，任意设
            message : JSON.stringify({
                "beanList" : [{
                    serial_no: serial_no,
                    bug_name: bug_name,
                    product: product,
                    bug_status: bug_status
                }],
                "operType" : "query",
                "paramMap" : {
                    "curPage" : String(curPage),// 当前页码
                    "limit" : String(limit)
                }
            })
        }
    });
}

/**
 * 提交BUG
 */
function buglist_addBtnOperation() {
    layui.layer.open({
        id: 'bugListAddIframe',
        type: 2,// iframe
        area: [(window.innerWidth - 20) + 'px', (window.innerHeight - 20) + 'px'],// 宽高
        title: '提交BUG',// 标题
        content: 'static/html/other/buglistAddDiv.html',//内容，因为是iframe层，得另写一个完整的html页面
        // btn: ['提交'],
        // yes: function (index, layero) {
            //确认按钮的回调，提交表单
            // var childHtml = layero.find("iframe").contents();
            // console.log($(childHtml).find("#buglist_newBug_name").val());
        // },
        success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
            // 初始化操作早iframe的html里，此处无法获取iframe页面的DOM元素
        }
    });
}
/**
 * BUG帖子详情
 * @param data
 */
function buglist_showDetail(data) {
    // 获取用户名，头像链接
    let userData = commonAjax("buglist.do", JSON.stringify({
        "beanList": [],
        "operType": "getUserInfo",
        "paramMap": {
            "user_no": data.create_user
        }
    }));
    let user_name = userData.retMap.userInfo.user_name;
    let user_herder = userData.retMap.userInfo.picture;
    layui.layer.open({
        id: 'bugListDetailIframe',
        type: 2,// iframe
        area: [(window.innerWidth - 20) + 'px', (window.innerHeight - 20) + 'px'],// 宽高
        title: data.serial_no + '-' + data.bug_name,// 标题
        content: 'static/html/other/bugMainPage.html',//内容，因为是iframe层，得另写一个完整的html页面
        success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)

            let body = layui.layer.getChildFrame('body', index);// 获取iframe body dom 用于操作

            iframeWindowObj = $("#bugListDetailIframe").children()[0].contentWindow; //弹框Iframe层的window对象,将tinyEditor对象存到这里 方便进行引用
            $(body).find("#bugMainPage_header").attr("src", "../../../" + user_herder);// 作者头像
            $(body).find("#bugMainPage_username").html(user_name);// 作者用户名
            $(body).find("#bugMainPage_lastModiTime").html("最后修改于 - " + commonFormatDate(data.last_modi_time));// 最后修改时间
            $(body).find("#bugMainPage_severity").html("严重级别 - " + commonFormatValue(FIELD_BUG_SEVERITY, data.severity, true) + " |");
            $(body).find("#bugMainPage_priority").html(" 优先级 - " + commonFormatValue(FIELD_BUG_PRIORITY, data.priority, true) + " |");
            $(body).find("#bugMainPage_status").html(" 状态 - " + commonFormatValue(FIELD_BUG_STATUS, data.bug_status, true));
            $(body).find("#bugMainPage_editDiv").html(data.content);// content回显
            // 初始化评论编辑器操作在html内
            $(body).find("#bugMainPage_userHeader").attr("src", "../../../" + user_info.picture);// content回显
            if (data.create_user == user_info.user_no) {
                //当前用户是发帖人，支持修改帖子内容
                $(body).find("#bugMainPage_severity").after("<button class=\"layui-btn layui-btn-xs layui-btn-warm\" id='bugMainPageEdit' type=\"button\" style=\"float: right;\">编辑</button>" +
                                                            "<button class=\"layui-btn layui-btn-xs\" id='bugMainPageSave' type=\"button\" style=\"float: right;display: none;\">保存</button>");
                $(body).find("#bugMainPageEdit").click(function () {
                    // $(body).find("#bugMainPage_content").html('').hide();
                    // iframeWindowObj.editor2.show();
                    // iframeWindowObj.editor2.setContent(data.content);
                    iframeWindowObj.edit_comment(data.content);
                    $(body).find("#bugMainPageEdit").hide();
                    $(body).find("#bugMainPageSave").show();
                });
                $(body).find("#bugMainPageSave").click(function () {
                    let newContent = iframeWindowObj.editor2.getContent();
                    let editRet = commonAjax("buglist.do", JSON.stringify({
                        "beanList": [{}],
                        "operType": "updateContent",
                        "paramMap": {
                            "serial_no": data.serial_no,
                            "content": newContent
                        }
                    }));
                    if (editRet.retCode == HANDLE_SUCCESS) {
                        commonOk("更新成功");
                        // iframeWindowObj.editor2.setContent("");
                        // iframeWindowObj.editor2.hide();
                        // $(body).find("#bugMainPage_content").html(newContent).show();
                        // 销毁编辑器
                        iframeWindowObj.editor2.destroy();
                        data.content = newContent;
                        $(body).find("#bugMainPageEdit").show();
                        $(body).find("#bugMainPageSave").hide();
                        buglist_queryOperation('1', FIELD_EACH_PAGE_NUM);
                    } else {
                        commonError("更新失败:" + editRet.retMsg);
                    }

                });
            }

            // 初始化加载评论 每页15条
            iframeWindowObj.reload_comments(1, 15, data);

            //  回复提交
            $(body).find("#bugMainPage_submit").click(function () {
                if (commonBlank($(body).find("#bugMainPage_commentTextArea").val())) {
                    commonInfo("评论内容为空");
                    return;
                }
                let isCommentOtherUser = false;
                /*let $content = $(iframeWindowObj.editor1.getContent());
                if ($content != undefined && $content != null && $content.length > 0) {
                    if ($content[0].attributes["name"].value == 'aboutDiv') {
                        isCommentOtherUser = true;
                    }
                }*/
                layui.layer.confirm("是否确认提交评论？", function(index) {
                    layui.layer.load();//loading
                    let retData = commonAjax("buglist.do", JSON.stringify({
                        "beanList": [{
                        }],
                        "operType": "comment",
                        "paramMap": {
                            "bug_serial": data.serial_no,
                            "isCommentOtherUser": false,
                            "content": $(body).find("#bugMainPage_commentTextArea").val(),
                            "about_serial": isCommentOtherUser ? sessionStorage.getItem("buglist_about_serialNo") : '',
                            "about_user": isCommentOtherUser ? sessionStorage.getItem("buglist_about_replyUser") : ''
                        }
                    }));
                    layui.layer.closeAll('loading');
                    if (retData.retCode == HANDLE_SUCCESS) {
                        commonOk("评论成功！");
                        // 刷新评论
                        iframeWindowObj.reload_comments(1, 15, data);
                        // 清空评论内容
                        iframeWindowObj.editor1.setContent('');
                    } else {
                        commonError("评论失败！" + retData.retMsg);
                    }
                });
                return false;
            });

        }
    });
}

function addCommentUserAndMark(bug_serial, serial_no, reply_user) {
    /*sessionStorage.setItem("buglist_about_serialNo", serial_no);
    sessionStorage.setItem("buglist_about_replyUser", reply_user);
    iframeWindowObj.editor1.setContent('<div name="aboutDiv" serialNo="' + serial_no + '" replyUser="' + reply_user + '"><strong>' + '@' + commonFormatUserNo(reply_user, true) + '</strong></div><br>' +
        iframeWindowObj.editor1.getContent()
    );*/
    // 打开新弹框
    layui.layer.open({
        type: 1,// 页面层
        area: ['500px', '400px'],// 宽高
        title: '评论用户',// 标题
        content: "<div class=\"comm-dialog\" >\n" +
            "    <form class=\"layui-form layui-form-pane\" lay-filter=\"buglist_commentUserFrm\" id=\"buglist_commentUserFrm\" action=\"\">\n" +
            "        <div class=\"layui-form-item layui-form-text\">\n" +
            "            <label class=\"layui-form-label\">评论内容</label>\n" +
            "            <div class=\"layui-input-block\">\n" +
            "                <textarea name=\"buglist_commentUser_content\" rows='10' id=\"buglist_commentUser_content\" required lay-verify=\"required\" class=\"layui-textarea\" ></textarea>\n" +
            "            </div>\n" +
            "        </div>\n" +
            "        <div class=\"layui-form-item comm-dialog-button\">\n" +
            "            <button class=\"layui-btn\" id=\"buglist_commentUser_submit\" lay-submit lay-filter=\"buglist_commentUser_submit\">提交</button>\n" +
            "        </div>\n" +
            "    </form>\n" +
            "</div>",
        success: function (layero, index1) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
            layui.form.render();
            layui.form.on('submit(buglist_commentUser_submit)', function(data){// 绑定提交按钮点击回调事件，只有表单验证通过会进入
                if (commonBlank($("#buglist_commentUser_content").val())) {
                    commonInfo("请输入评论内容！");
                    return;
                }
                layui.layer.confirm("是否确认提交？", function(index) {
                    let retData = commonAjax("buglist.do", JSON.stringify({
                        "beanList": [{}],
                        "operType": "comment",
                        "paramMap": {
                            "bug_serial": bug_serial,
                            "isCommentOtherUser": true,
                            "content": $("#buglist_commentUser_content").val(),
                            "about_serial": serial_no,
                            "about_user": reply_user
                        }
                    }));
                    if (retData.retCode == HANDLE_SUCCESS) {
                        layui.layer.close(index1);
                        commonOk("回复成功!");
                    } else {
                        commonError(retData.retMsg);
                    }
                });
                return false;
            });
        }
    });
}

/**
 * 弹框展示楼层回复
 * @param serial_no
 */
function getCommentUserHistory(serial_no) {
    let retData = commonAjax("buglist.do", JSON.stringify({
        "beanList": [],
        "operType": "getFloorCommentHistory",
        "paramMap": {
            "serial_no": serial_no
        }
    }))
    if (retData.retCode == HANDLE_SUCCESS) {
        let retList = retData.retMap.list;
        if (retList.length == 0) {
            commonInfo("暂无回复");
        } else {
            layui.layer.open({
                type: 1,// 页面层
                area: ['500px', '500px'],// 宽高
                title: '对话',// 标题
                content: "<div class=\"box-footer box-comments\" name='box-comment-div'></div>",
                success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
                    let allComment = "";
                    for (let i = 0; i < retList.length; i++) {
                        allComment += "<div class=\"box-comment\" id='" + retList[i].serial_no + "'>\n" +
                            "                <img class=\"img-circle img-sm\" src=\"" + "../../../" + retList[i].picture + "\" alt=\"用户头像\">\n" +
                            "\n" +
                            "                <div class=\"comment-text\">\n" +
                            "                      <span class=\"username\">\n" + retList[i].user_name +
                            "                        <span class=\"text-muted pull-right\">" + commonFormatDate(retList[i].reply_time) + "</span>\n" +
                            "                      </span>\n";
                        allComment += "<div>" + retList[i].content + "</div>" + /** 评论内容*/
                            "<div class=\"pull-right text-muted\" >" +
                            "   <a href='#' onclick='addCommentUserAndMark(\"" + retList[i].bug_serial + "\", \"" + retList[i].serial_no + "\", \"" + retList[i].reply_user + "\")'>回复</a>" +
                            "   <a href='#' onclick='getCommentUserHistory(\"" + retList[i].serial_no + "\")'>查看对话</a>" +
                            "</div>" +
                            "                </div>\n" +
                            "            </div>";
                    }
                    $(layero).find("div[name='box-comment-div']").html(allComment);
                }
            });
        }
    } else {
        commonError("获取回复失败！");
    }
}

/**
 * 删除
 */
function buglist_deleteBtnOperation() {
    let checkData = layui.table.checkStatus("buglist_tableObj").data;// 获取选中数据
    if (checkData.length == 0) {
        commonInfo("请选择需要删除的BUG信息");
        return;
    } else {
        layui.layer.confirm("是否确认删除选中的BUG信息？", function(index) {
            let retData = commonAjax("buglist.do", JSON.stringify({
                "beanList" : checkData,
                "operType" : "delete",
                "paramMap" : {}
            }));
            if (retData.retCode == HANDLE_SUCCESS) {
                commonOk("删除成功");
                buglist_queryOperation('1', FIELD_EACH_PAGE_NUM);
            } else {
                commonError(retData.retMsg);
            }
        });
    }
}

/**
 * bug状态更新
 */
function buglist_editBtnOperation() {
    let checkData = layui.table.checkStatus("buglist_tableObj").data;// 获取选中数据
    if (checkData.length == 0) {
        commonInfo("请选择需要更新状态的BUG");
        return;
    }
    if (checkData.length > 1) {
        commonInfo("只能操作一条数据");
        return;
    }
    if (user_info.user_no != checkData[0].create_user) {
        commonInfo("只能修改自己提交的BUG");
        return;
    }
    layui.layer.open({
        type: 1,// 页面层
        area: ['475px', '330px'],// 宽高
        title: '状态更新',// 标题
        content: $("#bugStatus_div"),//内容，直接取dom对象
        btn: ['提交'],
        yes: function (index, layero) {
            //确认按钮的回调，提交表单
            layui.layer.confirm("是否确认提交？", function(index2) {
                let retData = commonAjax("buglist.do", JSON.stringify({
                    "beanList" : [{
                        "serial_no" : checkData[0].serial_no,
                        "bug_status" : $("#bugStatus_status").val(),
                    }],
                    "operType" : "updateStatus",
                    "paramMap" : {}
                }));
                if (retData.retCode == HANDLE_SUCCESS) {
                    commonOk("更新成功");
                    layui.layer.close(index);
                    buglist_queryOperation('1', FIELD_EACH_PAGE_NUM);
                } else {
                    commonError(retData.retMsg);
                }
            });
        },
        success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
            // 渲染弹框元素
            commonPutNormalSelectOpts(FIELD_BUG_STATUS, "bugStatus_status", checkData[0].bug_status, true);
            layui.form.render();
        }
    });
}