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
        /*============初始化操作 begin===============*/
        initData();
        /*============初始化操作 end===============*/

        //获取登录用户信息
        if (!commonBlank(sessionStorage.getItem("user_info"))) {
            user_info = JSON.parse(sessionStorage.getItem("user_info"));
        } else {
            commonError("您未登录或登录已失效！",function () {
                window.location.href = 'login.html';
            });
            return;
        }

        $("#user_name_min").text(user_info.user_name);
        $("#user_name_main").text(user_info.user_name + ' - 普通员工');
        $("#user_name_sidebar").text(user_info.user_name);

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
    //初始化数据字典
    let msg = {
      "beanList" : [],
      "operType" : "initFields",
      "paramMap" : {

      }
    };
    let fieldData = commonAjax("field.do", JSON.stringify(msg));
}