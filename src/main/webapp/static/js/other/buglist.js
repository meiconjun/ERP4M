var buglist_tableInst = null;
var buglist_curr = 1;
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
                {
                    field: 'bug_name',
                    title: 'BUG名称',
                    align : 'center',
                    width: '60%',
                    templet : function (data) {
                        let color = "";
                        if (data.severity == '1') {// 根据严重级别标识颜色
                            color = '#23ca30';
                        } else if (data.severity == '2') {
                            color = '#d6d133';
                        } else if (data.severity == '3') {
                            color = '#ec3027';
                        }
                        return "<a style='color: " + color + "' href='#' style='cursor:pointer' title='" + data.bug_name + "' onclick='buglist_showDetail(" + commonFormatObj(data) + ")'>" + data.bug_name + "</a>";
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
                        return commonFormatValue(FIELD_BUG_SEVERITY, data.severity, false);
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

        //暂不做权限控制
        $("#buglist_queryBtn").click(function () {
            buglist_queryOperation('1', FIELD_EACH_PAGE_NUM);
        });

        $("#buglist_addBtn").click(function () {
            buglist_addBtnOperation();
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

            let body = layer.getChildFrame('body', index);// 获取iframe body dom 用于操作
            $(body).find("#bugMainPage_header").attr("src", "data:image/jpg;base64," + user_herder);// 作者头像
            $(body).find("#bugMainPage_username").html(user_name);// 作者用户名
            $(body).find("#bugMainPage_lastModiTime").html("最后修改于 - " + commonFormatDate(data.last_modi_time));// 最后修改时间
            $(body).find("#bugMainPage_severity").html("严重级别 - " + commonFormatValue(FIELD_BUG_SEVERITY, data.severity, true) + " |");
            $(body).find("#bugMainPage_priority").html(" 优先级 - " + commonFormatValue(FIELD_BUG_PRIORITY, data.priority, true) + " |");
            $(body).find("#bugMainPage_status").html(" 状态 - " + commonFormatValue(FIELD_BUG_STATUS, data.bug_status, true));
            $(body).find("#bugMainPage_content").html(data.content);// content回显
            // 初始化评论编辑器操作在html内
            $(body).find("#bugMainPage_userHeader").attr("src", "data:image/jpg;base64," + user_info.picture);// content回显


            /**
             * 载入评论
             */
            function reload_comments(curr, limit) {
                let commentData = commonAjax("buglist.do", JSON.stringify({
                    "beanList": [{
                    }],
                    "operType": "queryComments",
                    "paramMap": {
                        'curPage': String(curr),
                        'limit': String(limit),
                        "serial_no": data.serial_no
                    }
                }));
                if (commentData.retCode == HANDLE_SUCCESS) {
                    let total_comment_count = commentData.retMap.total;
                    $(body).find("#bugMainPage_commentCount").html(total_comment_count + " 评论");// content回显
                    let commentList = commentData.retMap.list;
                    let allComment = "";
                    for (let i = 0; i < commentList.length; i++) {
                        allComment += "<div class=\"box-comment\" id='" + commentList[i].serial_no + "'>\n" +
                            "                <img class=\"img-circle img-sm\" src=\"" + "data:image/jpg;base64," + commentList[i].picture + "\" alt=\"用户头像\">\n" +
                            "\n" +
                            "                <div class=\"comment-text\">\n" +
                            "                      <span class=\"username\">\n" + commentList[i].user_name +
                            "                        <span class=\"text-muted pull-right\">" + commonFormatDate(commentList[i].reply_time) + "<b> #" + commentList[i].floor + "</b>" + "</span>\n" +
                            "                      </span>\n";
                            if (!commonBlank(commentList[i].about_serial)) {
                                allComment += "<span class=\"username\">回复 @" + commonFormatUserNo(commentList[i].about_user, true) + "</span>";
                            }
                        allComment += commentList[i].content + /** 评论内容*/
                            "                </div>\n" +
                            "            </div>";
                    }
                    //----
                    if (!commonBlank(allComment)) {
                        $(body).find("#bugMainPage_commentContent").html(allComment);// content回显
                    } else {
                        $(body).find("#bugMainPage_commentContent").html("暂无评论");
                    }
                    layui.laypage.render({
                        elem : $(body).find("#bugMainPage_commentPage"),
                        limit : limit,
                        groups : 5,
                        curr : curr,
                        count : total_comment_count,
                        prev : '上一页',
                        next : '下一页',
                        first : '首页',
                        last : '尾页',
                        theme: '#626465',
                        layout : ['prev', 'first', 'page', 'last', 'next', 'count'],
                        jump : function(obj, first){
                            //obj包含了当前分页的所有参数，比如：
                            if (!first) {
                                reload_comments(obj.curr, obj.limit)
                            }
                        }
                    });
                } else {
                    commonError("加载评论失败");
                }
            }
            // 初始化加载评论 每页15条
            reload_comments(1, 15);

        }
    });
}