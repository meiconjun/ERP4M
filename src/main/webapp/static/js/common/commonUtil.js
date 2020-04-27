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
                console.log(textStatus);
                console.log(jqXHR);
                if (jqXHR.status == 999) {
                    alert("您尚未登录或登录已失效，请重新登录！");
                    window.location.href = 'login.html';
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
function commonGetAuthField(menu_id) {
    let msg = {
        "beanList" : [],
        "paramMap" : {
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

/**
 * 给普通下拉框构造下拉选项
 * @param optList 选项列表，可传入数组或数据字典ID
 * [
 *      {
 *          "value" : "",
 *          "name" : "—— 请选择 ——"
 *      },
 *      {
 *          "value" : "010",
 *          "name" : "北京"
 *      }
 * ];
 * @param objName select对象ID
 * @param defaultVal 默认值
 * @param isRequired 是否必输，false时添加一个空选项
 * @param onlyName 是否只展示name  默认为value-name
 */
function commonPutNormalSelectOpts(optList, objName, defaultVal, isRequired, onlyName) {
    let thisOpts;
    if (commonBlank(optList)) {
        return [];
    }
    if (typeof optList == 'string') {// 字符串-字典码
        let localList = localStorage.getItem(optList);
        if (commonBlank(localList)) {
            console.error(optList + "对应的数据字典没有数据");
            return [];
        }
        thisOpts = JSON.parse(localList);
    } else {// 数据对象
        thisOpts = optList;
    }

    let html = "";
    if (!isRequired) {
        html += "<option value='' selected>-</option>";
    }
    for (let i = 0; i < thisOpts.length; i++) {
        html += "<option value='" + thisOpts[i].value + "'>";
        if (onlyName) {
            html += thisOpts[i].name + "</option>";
        } else {
            html += thisOpts[i].value + "-" + thisOpts[i].name + "</option>";
        }
    }
    $("#" + objName).html(html);
    // console.log(html);
    if (!commonBlank(defaultVal)) {
        $("#" + objName).val(defaultVal);
    }
}

/**
 * 获取当前日期字符串 yyyyMMdd
 */
function commonCurrentDateStr() {
    return new Date().formatDate('yyyyMMdd');
}

/**
 * 获取当前时间字符串 yyyyMMddHHmmss
 */
function commonCurrentDateTimeStr() {
    return new Date().formatDate('yyyyMMddHHmmss');
}

/**
 * 获取当前日期指定格式字符串
 */
function commonCurrentDateFormatStr(format) {
    return new Date().formatDate(format);
}

/**
 * 根据数组或数据字典格式化值
 */
function commonFormatValue(fieldId, value, onlyName) {
    let tempObj = [];
    if (typeof fieldId == 'string') {
        let fieldStr = localStorage.getItem(fieldId);
        if (!commonBlank(fieldStr)) {
            tempObj = JSON.parse(fieldStr);
        } else {
            console.log("缓存内没有数据字典号[" + fieldId + "]对应的数据字典");
        }
    } else {
        tempObj = fieldId;
    }
    let retVlue = value;
    for (let i = 0; i < tempObj.length; i++) {
        if (tempObj[i].value == retVlue) {
            if (onlyName) {
                retVlue = tempObj[i].name;
            } else {
                retVlue = retVlue + "-" + tempObj[i].name;
            }
        }
    }
    return retVlue;
}

/**
 * 格式化日期字符串为 yyyy-MM-dd HH:mm:ss格式
 * @param value
 * @param formatStr
 */
function commonFormatDate(dateStr, formatStr) {
    if (commonBlank(dateStr)) {
        return "";
    }
    let formatStr1 = formatStr;
    let formatStr2 = formatStr;
    if(commonBlank(formatStr)) {
        formatStr1 = 'yyyy-MM-dd';
        formatStr2 = 'yyyy-MM-dd HH:mm:ss';
    }
    try {
        if (dateStr.length == 8) {
            dateStr = dateStr.parseDate("yyyyMMdd").formatDate(formatStr1);

        } else if (dateStr.length == 14) {
            dateStr = dateStr.parseDate("yyyyMMddHHmmss").formatDate(formatStr2);
        }
    } catch (e) {
        // TODO: handle exception
        console.error(e);
    }
    return dateStr;
}