var ws = null;//websocket连接对象
var user_info = null;// 当前用户对象
$(document).ready(function () {
    try {
        // 建立websocket连接
        if (ws == null) {
            let ws_url = getWebSocketUrl();
            ws = new WebSocket(ws_url);
            connectToWebSocket(ws);
        }
        // 页面刷新或关闭前断开websocket
        window.onbeforeunload = function () {
            disconnectWebSocket();
        }
        //获取登录用户信息
        if (!commonBlank(sessionStorage.getItem("user_info"))) {
            user_info = JSON.parse(sessionStorage.getItem("user_info"));
            sessionStorage.setItem("user_no", user_info.user_no);
            // 头像回显
            if (!commonBlank(user_info.picture)) {
                $("#user_picture_min").attr("src", user_info.picture);
                $("#user_picture_main").attr("src", user_info.picture);
                $("#user_picture_sidebar").attr("src", user_info.picture);
            }
            if (sessionStorage.getItem("changePsw") == "true") {
                console.log("111111111");
                // 强制要求修改密码
                commonInfo("您的密码还是初始密码，请修改！", userFileChange());
                sessionStorage.removeItem("changePsw");
            } else {
                sessionStorage.removeItem("changePsw");
            }
        } else {
            commonError("您未登录或登录已失效！",function () {
                window.location.href = 'login.html';
            });
            return;
        }

        $("#user_name_min").text(user_info.user_name);
        $("#user_name_main").text(user_info.user_name);
        $("#user_name_sidebar").text(user_info.user_name);
        /*============初始化操作 begin===============*/
        layui.layer.load();//loading
        initData();
        initAllUser();
        initFilePath();
        initUnReadMessage(user_info.user_no, user_info.role_no);
        initUnReadMsg();
        initTodoTask();
        layui.layer.closeAll('loading');
        /*============初始化操作 end===============*/



        initMenuLeft(user_info.user_no);
    } catch (e) {
        commonError("加载主页异常：" + e.message);
        console.error(e.message);
    }

});

/**
 * 初始化左侧菜单列表，并获取按钮权限
 */
function initMenuLeft(user_no) {
    let msg = {
        "beanList" : [],
        "operType" : "getUserMenu",
        "paramMap" : {
            "user_no" : user_no
        }
    };
    let retData = commonAjax("menu.do", JSON.stringify(msg));
    if (retData.retCode == HANDLE_SUCCESS) {
        // 菜单列表
        let menuList = retData.retMap.menuList;
        // 菜单列表存到缓存里
        sessionStorage.setItem("menuList", JSON.stringify(menuList));
        // 构造左侧菜单
        /*================  init MenuTree left  begin ================*/
        let $sidebar_menu = $("#sidebar-menu");// 左侧菜单容器Jquery对象

        function createMenu(menuList, contObj) {
            for(let i = 0; i < menuList.length; i++) {
                // 逐级构建菜单li
                let menuli = document.createElement('li');
                if (menuList[i].is_parent == '1') {//父级,添加class
                    menuli.setAttribute("class", "treeview");
                    $(menuli).append("<a href='#'></a>");
                    $(menuli.childNodes[0]).append("<i class='fa fa-" + menuList[i].menu_icon + "'></i>");//菜单小图标
                    $(menuli.childNodes[0]).append("<span title='" + menuList[i].menu_desc + "'>" + menuList[i].menu_name + "</span>");//菜单名称
                    $(menuli.childNodes[0]).append("<span class='pull-right-container'><i class='fa fa-angle-left pull-right'></i></span>");//右侧箭头
                    // 子菜单列表容器
                    $(menuli).append('<ul class="treeview-menu"></ul>');
                    // 递归调用本方法
                    arguments.callee(menuList[i].submemus, menuli.childNodes[1]);
                } else {
                    //非父级
                    $(menuli).append("<a onclick=\"addTalMenu('" + menuList[i].menu_url + "','" + menuList[i].menu_name + "','" + menuList[i].menu_id + "')\" href='#'></a>");
                    $(menuli.childNodes[0]).append("<i class='fa fa-" + menuList[i].menu_icon + "'></i><span title='" + menuList[i].menu_desc + "'>" + menuList[i].menu_name + "</span>");
                }
                contObj.append(menuli);
            }
        };
        //注册TAB删除事件，若全部tab都删除了，隐藏TAb div，展示主页
        layui.element.on('tabDelete(main-tab)', function(data){
            let length = $("div[lay-filter=main-tab] ul:first-child li").length;
            if (length == 0) {// 关闭了最后一个标签
                $("div[lay-filter='main-tab']").attr("hidden", "hidden");
                $("#main-page").removeAttr("hidden");
            }

        });
        createMenu(menuList, $sidebar_menu);

        /*================  init MenuTree left  end ================*/
    } else {
        commonError(retData.retMsg);
    }
}

/**
 * 进行数据字典等数据的缓存
 */
function initData() {
    let curDate = commonCurrentDateStr();
    let fieldDate = localStorage.getItem("fieldDate");
    if (curDate >= fieldDate) {
        //初始化数据字典
        let msg = {
            "beanList" : [],
            "operType" : "initFields",
            "paramMap" : {

            }
        };
        let fieldData = commonAjax("field.do", JSON.stringify(msg));
        if (fieldData.retCode == HANDLE_SUCCESS) {
            for (let key in fieldData.retMap.fieldMap) {
                if (fieldData.retMap.fieldMap.hasOwnProperty(key)) { //否则会把原型链的属性都循环进去
                    localStorage.setItem(key, JSON.stringify(fieldData.retMap.fieldMap[key]));
                }
            }
            localStorage.setItem("fieldDate", commonCurrentDateStr());
            console.log("初始化数据字典成功");
        } else {
            commonError("加载数据字典失败");
        }
    }
}

/**
 * 获取文件存储根路径
 */
function initFilePath() {
    let retData = commonAjax("init.do", JSON.stringify({
        "beanList": [],
        "operType": "initFilePath",
        "paramMap": {}
    }));
    if (retData.retCode == HANDLE_SUCCESS) {
        localStorage.setItem("filePath", retData.retMap.filePath);
        console.log("初始化文件路径成功");
    } else {
        commonError("初始化文件存储路径失败！" + retData.retMsg);
    }
}

/**
 * 查询未读消息并弹出
 * @param user_no
 */
function initUnReadMessage(user_no, role_no) {
    let retData = commonAjax("common.do", JSON.stringify({
        "beanList": [],
        "operType": "initUnReadMessage",
        "paramMap": {
            "user_no": user_no,
            "role_no": role_no
        }
    }));
    if (retData.retCode == HANDLE_SUCCESS) {
        let beanList = retData.retMap.msgBeanList;
        for (let i = 0; i < beanList.length; i++) {
            distributionMessage(JSON.stringify(beanList[i]));
        }
    } else {
        commonError("初始化离线消息失败！")
    }
}

/**
 * 将全用户的用户号和用户名存储在缓存中，便于格式化
 */
function initAllUser() {
    let retData = commonAjax("common.do", JSON.stringify({
        "beanList": [],
        "operType": 'getAllUserNoAndName',
        "paramMap": {}
    }));
    if (retData.retCode == HANDLE_SUCCESS) {
        let allUser = retData.retMap.allUser;
        localStorage.setItem("allUser", JSON.stringify(allUser));
        console.log("初始化用户数据成功");
    } else {
        commonError("初始化用户数据失败");
    }
}

function userFileChange() {
    let html = "<div class=\"comm-dialog\" id=\"userFile_modifyDiv\">\n" +
        "    <form class=\"layui-form layui-form-pane\" lay-filter=\"userFile_modifyFrm\" id=\"userFile_modifyFrm\" action=\"\">\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <div class=\"layui-upload-drag\" id=\"userFile_modify_uploadHeader\">\n" +
        "                <div id=\"userFile_modify_uploadHeaderPrev\">\n" +
        "                    <img src=\"\" alt=\"上传成功后渲染\" style=\"max-width: 196px\">\n" +
        "                </div>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <div class=\"layui-inline\">\n" +
        "                <label class=\"layui-form-label\">用户号</label>\n" +
        "                <div class=\"layui-input-inline\">\n" +
        "                    <input type=\"text\" name=\"userFile_modify_userNo\" disabled maxlength=\"50\" id=\"userFile_modify_userNo\" lay-verify=\"required\" autocomplete=\"off\" class=\"layui-input layui-disabled\">\n" +
        "                </div>\n" +
        "            </div>\n" +
        "            <div class=\"layui-inline\">\n" +
        "                <label class=\"layui-form-label\">用户名</label>\n" +
        "                <div class=\"layui-input-inline\">\n" +
        "                    <input type=\"text\" name=\"userFile_modify_userName\" id=\"userFile_modify_userName\" lay-verify=\"required\" autocomplete=\"off\" class=\"layui-input\">\n" +
        "                </div>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <div class=\"layui-inline\">\n" +
        "                <label class=\"layui-form-label\">邮箱</label>\n" +
        "                <div class=\"layui-input-inline\">\n" +
        "                    <input type=\"text\" name=\"userFile_modify_email\" id=\"userFile_modify_email\" lay-verify=\"email\" autocomplete=\"off\" class=\"layui-input\">\n" +
        "                </div>\n" +
        "            </div>\n" +
        "            <div class=\"layui-inline\">\n" +
        "                <label class=\"layui-form-label\">手机</label>\n" +
        "                <div class=\"layui-input-inline\">\n" +
        "                    <input type=\"tel\" name=\"userFile_modify_phone\" id=\"userFile_modify_phone\" lay-verify=\"phone\" autocomplete=\"off\" class=\"layui-input\">\n" +
        "                </div>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <div class=\"layui-inline\">\n" +
        "                <label class=\"layui-form-label\">密码</label>\n" +
        "                <div class=\"layui-input-inline\">\n" +
        "                    <input type=\"password\" name=\"userFile_modify_password\" id=\"userFile_modify_password\" lay-verify=\"required\" autocomplete=\"off\" class=\"layui-input\">\n" +
        "                </div>\n" +
        "            </div>\n" +
        "            <div class=\"layui-inline\">\n" +
        "                <label class=\"layui-form-label\">密码确认</label>\n" +
        "                <div class=\"layui-input-inline\">\n" +
        "                    <input type=\"password\" name=\"userFile_modify_passwordConfirm\" id=\"userFile_modify_passwordConfirm\" lay-verify=\"required|confirmPassword\" autocomplete=\"off\" class=\"layui-input\">\n" +
        "                </div>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class=\"layui-form-item\" hidden=\"hidden\">\n" +
        "            <label class=\"layui-form-label\">头像路径</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <input type=\"text\" name=\"userFile_modify_filePath\" id=\"userFile_modify_filePath\"  autocomplete=\"off\" class=\"layui-input\">\n" +
        "            </div>\n" +
        "        </div>\n" +
/*        "        <div class=\"layui-form-item comm-dialog-button\">\n" +
        "            <button id=\"userFile_modifySubmit\" class=\"layui-btn\" lay-submit lay-filter=\"userFile_modifySubmit\">确定</button>\n" +
        "        </div>\n" +*/
        "    </form>\n" +
        "</div>";
    let pswChange = false;
    layui.layer.open({
        type: 1,
        area: ['720px', '550px'],// 宽高
        title: '用户资料修改',
        content: html,
        btn: ['提交修改'],
        yes: function (index1, layero) {
            layui.layer.confirm("是否确认提交？", function(index) {
                if ($("#userFile_modify_password").val() != $("#userFile_modify_passwordConfirm").val()) {
                    commonInfo("密码和确认密码不一致！");
                    return;
                }
                if (commonBlank($("#userFile_modify_password").val()) || commonBlank($("#userFile_modify_userName").val())) {
                    commonInfo("密码和用户名不能为空！");
                    return;
                }
                let retData = commonAjax("userConfig.do", JSON.stringify({
                    "beanList" : [{
                        "user_no" : $("#userFile_modify_userNo").val(),
                        "user_name" : $("#userFile_modify_userName").val(),
                        "pass_word" : pswChange ? $("#userFile_modify_password").val() : "",
                        "picture" : $("#userFile_modify_filePath").val(),
                        "email" : $("#userFile_modify_email").val(),
                        "phone" : $("#userFile_modify_phone").val()
                    }],
                    "operType" : "modify",
                    "paramMap" : {
                    }
                }));
                if (retData.retCode == HANDLE_SUCCESS) {
                    if (!commonBlank(retData.retMap)) {
                        // 更新回显头像
                        let user_info = JSON.parse(sessionStorage.getItem("user_info"));
                        user_info.picture = retData.retMap.base64;
                        $("#user_picture_min").attr("src", user_info.picture);
                        $("#user_picture_main").attr("src", user_info.picture);
                        $("#user_picture_sidebar").attr("src", user_info.picture);
                        sessionStorage.setItem("user_info", JSON.stringify(user_info));
                    }
                    commonOk("修改成功");
                    layui.layer.close(index1);
                } else {
                    commonError(retData.retMsg);
                }
            });
            return false;
        },
        success: function (layero, index) {
            let user_info = JSON.parse(sessionStorage.getItem("user_info"));
            $("#userFile_modify_uploadHeaderPrev").find('img').attr('src', $("#user_picture_min").attr("src"));
            $("#userFile_modify_userNo").val(user_info.user_no);
            $("#userFile_modify_userName").val(user_info.user_name);
            $("#userFile_modify_email").val(user_info.email);
            $("#userFile_modify_phone").val(user_info.phone);
            $("#userFile_modify_password").val("123456");
            $("#userFile_modify_passwordConfirm").val("123456");
            $("#userFile_modify_filePath").val("");
            $("#userFile_modify_password").change(function () {
                pswChange = true;
            });
            layui.upload.render({
                elem: '#userFile_modify_uploadHeader',
                url: 'uploadHeaderImg.do',//改成您自己的上传接口
                data: {
                    "user_no": function () {
                        return user_info.user_no;
                    }
                },
                accept: 'images',//只允许上传图片
                acceptMime: 'images',// 规定打开文件选择框时，筛选出的文件类型，值为用逗号隔开的 MIME 类型列表
                exts: 'jpg|png|gif|bmp|jpeg',
                auto: true,// 选择文件后是否自动上传
                multiple: false,// 是否允许多文件上传
                choose: function(obj) {
                    //预读本地文件,如果是多文件，则会遍历
                    obj.preview(function (index, file, result) {
                        // layui.$('#userConfig_uploadHeaderPrev').prev().hide();
                        // layui.$('#userConfig_uploadHeaderPrev').prev().prev().hide();
                        layui.$('#userFile_modify_uploadHeaderPrev').find('img').attr('src', result);
                    });
                },
                done: function(res, index, upload){
                    // 将头像地址赋值给表单
                    $("#userFile_modify_filePath").val(res.data.filePath)
                },
                error: function (index, upload) {
                    commonError("上传头像失败");
                }
            });
        }
    });
}
/** 登出 */
function logout() {
    //断开websocket
    disconnectWebSocket(ws);
    // 移除后台session
    commonAjax("logout.do", JSON.stringify({
        "beanList": [],
        "operType": "",
        "paramMap": {
            "user_no": sessionStorage.getItem("user_no")
        }
    }));
    // 移除前台session
    sessionStorage.removeItem("user_info");
    // 调到登录页
    window.location.href = 'login.html';
}