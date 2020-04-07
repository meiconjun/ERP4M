var layer;
$(document).ready(function () {
    try {
        //layui相关模组初始化
        layui.use(['layer'], function () {
            layer = layui.layer;
        });
        //获取登录用户信息
        let user_info;
        if (!commonBlank(sessionStorage.getItem("user_info"))) {
            user_info = JSON.parse(sessionStorage.getItem("user_info"));
        } else {
            console.error("前台缓存中没有用户信息！");
        }

        $("#user_name_min").text(user_info.user_name);
        $("#user_name_main").text(user_info.user_name + ' - 普通员工');
        $("#user_name_sidebar").text(user_info.user_name);

        initMenuLeft(user_info.user_no);
    } catch (e) {
        alert("加载主页异常：" + e.message);
        console.error(e.message);
    }

});

/**
 * 初始化左侧菜单列表
 */
function initMenuLeft(user_no) {

}