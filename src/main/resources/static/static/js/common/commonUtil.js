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
 * @param is_async true-异步，false-同步，不传则默认false
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
        headers: {
            'authorization': localStorage.getItem("authorization"),
            // 'user': localStorage.getItem("user_info")
        },
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
        let buttonStr = retData.retMap.buttonStr;
        return {
            "buttonList" : buttonList,
            "buttonStr" : buttonStr
        };
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
function commonPutNormalSelectOpts(optList, objName, defaultVal, isRequired, onlyName, tips) {
    let thisOpts;
    if (commonBlank(optList)) {
        $("#" + objName).html("<option value='' selected>-无数据-</option>");
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
        if (commonBlank(tips)) {
            tips = "";
        }
        html += "<option value='' selected>" + tips + "</option>";
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

/**
 * 格式化对象，使其可以作为onclick中的参数传递
 */
function commonFormatObj(obj) {
    return JSON.stringify(obj).replace(/"/g, '&quot;');
}

/**
 * 点击切换导航时，重置表格大小
 */
function commonResizeTable(tebleId) {
    $("#sidebar-toggle").click(function () {
        setTimeout(function () {
            layui.table.resize(tebleId);
        }, 500);
    });
    // 创建一个观察者监听指定div的变化
    /*let MutationObserver = window.MutationObserver || window.WebKitMutationObserver || window.MozMutationObserver;
    console.log("MutationObserver");
    let target = document.getElementById("container-fluid");
    let observer = new MutationObserver(function (mutations) {
        console.log("sadasda");
        layui.table.resize(tebleId);
    });
    observer.observe(target, {
        attributes : true
    });*/
}

/**
 * 文件下载
 * @param fileName
 * @param filePath
 */
function commonFileDownload(fileName, filePath) {
    //隐藏的表单，用于请求下载服务
    let html = "<form style='display: none' id='commonFileDownloadFrm' target='commonIframe' hidden='hidden'  method='post' action='fileDownload.do'>" +
                    "<input type='hidden' name='fileName' value=''>" +
                    "<input type='hidden' name='filePath' value=''>" +
                "</form>";
    let $html = $(html);
    // $(document)[0].append($html);
    $(document.body).append($html);
    $html.find('[name="fileName"]').val(fileName.replace(/\s+/g, ""));
    $html.find('[name="filePath"]').val(filePath);
    $html.submit();
}

/**
 * 格式化用户号
 */
function  commonFormatUserNo(value, onlyName) {
    let tempObj = {};
    let fieldStr = localStorage.getItem("allUser");
    if (!commonBlank(fieldStr)) {
        tempObj = JSON.parse(fieldStr);
    } else {
        console.log("缓存内没有全用户数据");
    }
    let retValue = value;
    if (!commonBlank(tempObj[value])) {
        if (onlyName) {
            retValue = tempObj[value];
        } else {
            retValue = retValue + "-" + tempObj[value];
        }

    }
    return retValue;
}

/**
 * 获取两个日期相差天数
 */
function getDaysBetween(begin_date, end_date) {
    let startDate = Date.parse(begin_date);
    let endDate = Date.parse(end_date);

    return (endDate - startDate) / (24 * 60 * 60 * 1000);
}

/**
 * 获取系统用户列表[{
 *     "name": "用户号-用户名",
 *     "value": "用户号"
 * }]
 */
function getAllUserList() {
    let users = [];
    let userListData = commonAjax("common.do", JSON.stringify({
        "beanList": [{}],
        "operType": "getAllUserNoAndName",
        "paramMap": {}
    }));
    if (userListData.retCode == HANDLE_SUCCESS) {
        let allUserMap = userListData.retMap.allUser;
        for (let allUserMapKey in allUserMap) {
            users.push({
                "name" : allUserMapKey + "-" + allUserMap[allUserMapKey],
                "value" : allUserMapKey
            });
        }
    } else {
        commonError("加载系统用户列表失败");
    }
    return users;
}

/**
 * 格式化逗号分隔的项目文档列表
 */
function formatProjectDocs(str) {
    let retData = commonAjax("projectManage.do", JSON.stringify({
        "beanList": [{}],
        "operType": "formatProjectDocs",
        "paramMap": {
            "docs": str
        }
    }));
    if (retData.retCode == HANDLE_SUCCESS) {
        return retData.retMap.retStr;
    } else {
        console.error("格式化项目文档列表");
        return str;
    }
}
/**
 * Excel导出
 * @Param operType 导出类型标识
 * @Param fileName 保存文件名
 * @Param param 参数列表 xxx=xxx&xxx=xxx
 */
function commonExportExcel(operType, fileName, param) {
    //隐藏的表单，用于请求下载服务
    let html = "<form style='display: none' id='commonExportExcelFrm' target='commonIframe' hidden='hidden'  method='post' action='exportExcel.do" + param + "'>" +
                "<input type='hidden' name='fileName' value=''>" +
                "<input type='hidden' name='operType' value=''>" +
        "</form>";
    let $html = $(html);
    console.log(html)
    // $(document)[0].append($html);
    $(document.body).append($html);
    $html.find('[name="fileName"]').val(fileName.replace(/\s+/g, ""));
    $html.find('[name="operType"]').val(operType);
    $html.submit();
}

/**
 * 更新任务状态
 * @param task_no
 * @param user_no
 */
function commonUpdateTaskState(task_no, user_no) {
    let retData = commonAjax("common.do", JSON.stringify({
        "beanList": [],
        "operType": "updateTask",
        "paramMap": {
            'task_no': task_no,
            "user_no": user_no
        }
    }));
    return retData;
}

/**
 * 获取文件后缀名,带"."
 * @param String
 */
function commonGetFilePostFix(filePath) {
    return filePath.substring(filePath.lastIndexOf("."), filePath.length);
}

/**
 * 获取系统项目列表
 * name-项目名称
 * value-项目编号
 */
function commonGetProjectList() {
    let retData = commonAjax('common.do', JSON.stringify({
        "beanList": [],
        "operType": "getProjectList",
        "paramMap": {
        }
    }));
    if (retData.retCode == HANDLE_SUCCESS) {
        return retData.retMap.list;
    } else {
        console.error("加载项目列表失败");
        return [];
    }
}