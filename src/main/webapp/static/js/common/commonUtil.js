/**
 * 前台公共方法集
 */

/** ==============================================**/

/**
 * 判断对象是否为空
 * @param obj
 * @returns {boolean}
 */
function commonBlank(obj) {
    if (obj == undefined || obj == null || $.trim(obj) == '') {
        return true;
    } else {
return false;
}
}

/**
 * 普通弹框提示
 * @param msg 提示信息
 * @param f 确定按钮回调函数
 */
function commonInfo(msg, f) {
    layer.alert(msg, {'icon': 0}, function (index) {
        if (!commonBlank(f)) {
            f();
        }
        layer.close(index);
    });
}

/**
 * 成功提示，三秒消失
 * @param msg
 */
function commonOk(msg) {
    layer.msg(msg, {'icon' : 1});
}

/**
 * 错误弹框提示
 * @param msg 提示信息
 * @param f 确定按钮回调函数
 */
function commonError(msg, f) {
    layer.alert(msg, {'icon': 2}, function (index) {
        if (!commonBlank(f)) {
            f();
        }
        layer.close(index);
    });
}

/**
 * 请求后台，默认为同步请求
 * (似乎异步请求无法统一封装,因为不会立即返回结果)
 * @param url 请求action地址
 * @param data 请求的json报文
 * @param is_async true-同步，false-异步，不传则默认false
 */
function commonAjax(url, data, is_async) {
    // console.log("发送请求......");
    let result = null;
    let async = false;
    if (!commonBlank(is_async)) {
        async = is_async;
    }

    $.ajax({
        async : async,
        type : 'post',
        url : url,
        dataType : 'json',
        timeout : 6000,//请求超时时间
        contentType : "application/x-www-form-urlencoded; charset=utf-8",
        data : {
            message : data
        },
        success : function (data) {
            result = data;
        },
        error : function (jqXHR, textStatus, errorThrown) {
            if (textStatus == 'timeout') {
                result = {
                    'retCode' : '9999',
                    'retMsg' : '请求后台超时！'
                };
            } else {
                if (jqXHR.status == 999) {
                    commonError("您尚未登录或登录已失效，请重新登录！", function () {
                        window.location.href = 'login.html';
                    });
                    return null;
                }
            }
        }
    });
    if (commonBlank(result)) {
        result = {
            'retCode' : '9998',
            'retMsg' : '请求后台异常！'
        };
    }
    return result;
}

/**
 * 查询用户的菜单按钮权限
 */
function commonGetAuthField(user_no, menu_id) {
    let msg = {
        "beanList" : [],
        "paramMap" : {
            "user_no" : user_no,
            "menu_id" : menu_id
        },
        "operType" : "getUserButton"
    };

    let retData = commonAjax("button.do", JSON.stringify(msg));
    if (retData.retCode == HANDLE_SUCCESS) {
        let buttonList = retData.retMap.buttonList;
        return buttonList;
    } else {
        commonError("获取用户权限按钮失败！");
        return null;
    }
}