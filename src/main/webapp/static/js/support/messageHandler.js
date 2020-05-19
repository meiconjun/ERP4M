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
    let html = "";
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