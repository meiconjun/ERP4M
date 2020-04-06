let layer;

$(document).ready(function () {
    try {
        $('input').iCheck({
            checkboxClass: 'icheckbox_square-blue',
            radioClass: 'iradio_square-blue',
            increaseArea: '20%' /* optional */
        });

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
    let retData = commonAjax("login.do", $.toJSON(msg));
    if (retData.retCode == '0') {
        window.location.href = 'index.html';
    } else {
        commonError(retData.retMsg);
    }
}