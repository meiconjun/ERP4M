$(document).ready(function () {
    try {
        /*============初始化操作 begin===============*/

        /*============初始化操作 end===============*/

        //获取登录用户信息
        let user_info;
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
        console.log(menuList);
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
                    $(menuli).append("<a href='" + menuList[i].menu_url + "'></a>");
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