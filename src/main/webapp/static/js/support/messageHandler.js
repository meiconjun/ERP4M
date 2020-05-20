/**
 * 消息推送相关处理
 */

/**
 * 右下角消息弹框,多条消息叠加展示
 */
function showNotificationKeep(title, content, func) {
    naranja()['log']({
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
    })
}

/**
 * 项目立项会签消息
 */
function showProjectCountersignDialog(msgStr) {
    let msgBean = JSON.parse(msgStr);
    let html = "<div class=\"comm-dialog\" id=\"projectCountersign_dialogDiv\">\n" +
        "    <form class=\"layui-form layui-form-pane\" lay-filter=\"\" action=\"\">\n" +
        "        <div class=\"layui-form-item\">\n" +
        "            <label class=\"layui-form-label\">项目名称</label>\n" +
        "            <div class=\"layui-input-block\">\n" +
        "                <input type=\"text\" name=\"projectCountersign_roleNo\" id=\"projectCountersign_roleNo\" disabled  lay-verify=\"\" autocomplete=\"off\" class=\"layui-input layui-disabled\">\n" +
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
        "                <input type=\"text\" name=\"projectCountersign_beginDate\" id=\"projectCountersign_beginDate\" lay-verify=\"date\" placeholder=\"yyyy-MM-dd\" autocomplete=\"off\" class=\"layui-input\">\n" +
        "            </div>\n" +
        "        </div>\n" +
        "    </form>\n" +
        "</div>";
    layui.layer.open({
        type : '1',
        title: '项目立项会签',
        area: ['50px', '550px'],// 宽高
        content: html,
        btn: ['同意', '拒绝'],
        yes: function(index, layero) {// 同意按钮回调

        },
        btn2: function(index, layero) {//拒绝按钮回调

        },
        success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)

        }
    });
}