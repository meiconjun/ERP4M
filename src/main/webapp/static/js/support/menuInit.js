/**
 * 在主页的tab中添加一个菜单并激活
 */
function addTalMenu(url, menuName, menuId) {
    // 判断是否已打开这个tab
    if ($("div[lay-filter='main-tab'] ul:first li[lay-id='" + menuId + "']").length == 0) {
        // 获取html内容
        let content = "";
        let rootObj = window.location
        $.ajax({
            async : false,
            url : rootObj.origin + "/" + rootObj.pathname.split("/")[1] + "/" + url,
            success : function (data) {
                content = data;
            },
            error : function (jqXHR, textStatus, errorThrown) {
                console.log(jqXHR)
            }
        });
        if (commonBlank(content)) {
            commonError("加载页面异常！");
            return;
        }
        // layui-添加tab
        layui.element.tabAdd('main-tab', {
            title : menuName,
            content : content,
            id : menuId
        });
    }

    // 切换到tab
    layui.element.tabChange('main-tab', menuId);
}