let layer;

$(document).ready(function () {
    try {
        /*$('input').iCheck({
            checkboxClass: 'icheckbox_square-blue',
            radioClass: 'iradio_square-blue',
            increaseArea: '20%' /!* optional *!/
        });*/

        layui.use(['layer'], function () {
            layer = layui.layer;
        });

        $("#login_submit").click(function () {
            login();
        });
    } catch (err) {
        alert("加载登录页异常：" + err.message);
        console.error(err.message);
    }
});

function login() {
    let user_no = $("#user_no").val();
    let pass_word = $("#pass_word").val();

    if (commonBlank(user_no) || commonBlank(pass_word)) {
        layer.msg("用户号或密码不能为空！");
        return false;
    }
    let msg = {
        'beanList' : [{
            "user_no" : user_no,
            'pass_word' : pass_word
        }],
        'operType' : 'login',
        'paramMap' : {}
    };
    let retData = commonAjax("login.do", JSON.stringify(msg));
    if (retData.retCode == HANDLE_SUCCESS) {
        sessionStorage.setItem("user_info", JSON.stringify(retData.retMap.user));//前台缓存中存储用户信息
        if (retData.retMap.changePsw) {
            sessionStorage.setItem("changePsw", "true");
        }
        window.location.href = 'index.html';
        commonOk("登录成功！");

    } else {
        commonError(retData.retMsg);
    }
}