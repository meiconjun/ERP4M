function showMainPage() {
    //隐藏TAB
    $("div[lay-filter='main-tab']").attr("hidden", "hidden");
    $("#main-page").removeAttr("hidden");
}


/** 初始化未读消息列表*/
function initUnReadMsg() {
    let unReadMsgData = commonAjax("common.do", JSON.stringify({
        "beanList": [],
        "operType": "initUnReadMessage",
        "paramMap": {
            "user_no": localStorage.getItem("user_no"),
            "role_no": JSON.parse(localStorage.getItem("user_info")).role_no
        }
    }));
    if (unReadMsgData.retCode == HANDLE_SUCCESS) {
        let unReadMsgList = unReadMsgData.retMap.msgBeanList;
        let html = "";
        for (let i = 0; i < unReadMsgList.length; i++) {
            let days = getDaysBetween(unReadMsgList[i].create_time, commonCurrentDateTimeStr());
            let label = "";
            if (days <= 1) {
                label = "label label-success";
            } else if (days > 1 && days <= 3) {
                label = "label label-warning";
            } else {
                label = "label label-danger";
            }
            html += "<li>\n" +
                "         <span class=\"handle\">\n" +
                "             <i class=\"fa fa-ellipsis-v\"></i>\n" +
                "             <i class=\"fa fa-ellipsis-v\"></i>\n" +
                "         </span>\n" +
                "         <input type=\"checkbox\" value=\"\">\n" +
                "         <a class=\"text\" href='#' onclick=\"showMainPageMsgFunction1(" +  commonFormatObj(unReadMsgList[i]) + ")\">" + commonFormatValue(FIELD_MSG_TYPE, unReadMsgList[i].msg_type, true) + "</a>\n" +
                "         <small class='" + label + "'><i class=\"fa fa-clock-o\"></i>" + commonFormatDate(unReadMsgList[i].create_time) + "创建" +"</small>\n" +
                "    </li>";
        }
        $("#mainPage-todo-list").empty();
        $("#mainPage-todo-list").append(html);
    } else {
        commonError("初始化未读消息失败");
    }
}

/** 初始化已读消息列表*/
function initReadMsg() {
    let readMsgData = commonAjax("common.do", JSON.stringify({
        "beanList": [],
        "operType": "initReadMessage",
        "paramMap": {
            "user_no": localStorage.getItem("user_no"),
            "role_no": JSON.parse(localStorage.getItem("user_info")).role_no
        }
    }));
    if (readMsgData.retCode == HANDLE_SUCCESS) {
        let readMsgList = readMsgData.retMap.msgBeanList;
        let html = "";
        for (let i = 0; i < readMsgList.length; i++) {
            // let days = getDaysBetween(readMsgList[i].create_date, commonCurrentDateTimeStr());
            let label = "label label-default";
            html += "<li>\n" +
                "         <span class=\"handle\">\n" +
                "             <i class=\"fa fa-ellipsis-v\"></i>\n" +
                "             <i class=\"fa fa-ellipsis-v\"></i>\n" +
                "         </span>\n" +
                "         <input type=\"checkbox\" value=\"\">\n" +
                "         <a class=\"text\" href='#' onclick=\"showMainPageMsgFunction2(" +  commonFormatObj(readMsgList[i]) + ")\">" + commonFormatValue(FIELD_MSG_TYPE, readMsgList[i].msg_type, true) + "</a>\n" +
                "         <small class='" + label + "'><i class=\"fa fa-clock-o\"></i>" + commonFormatDate(readMsgList[i].create_time) + "创建" +"</small>\n" +
                "    </li>";
        }
        $("#mainPage-todo-list").empty();
        $("#mainPage-todo-list").append(html);
    } else {
        commonError("初始化未读消息失败");
    }
}

function showMainPageMsgFunction1(data) {
    showMainPageMsg(JSON.stringify(data), true);
}
function showMainPageMsgFunction2(data) {
    showMainPageMsg(JSON.stringify(data), false);
}

/**
 * 初始化代办任务
 */
function initTodoTask() {
    let todoTaskData = commonAjax("common.do", JSON.stringify({
        "beanList": [],
        "operType": "initTodoTask",
        "paramMap": {
            "user_no": localStorage.getItem("user_no"),
            "role_no": JSON.parse(localStorage.getItem("user_info")).role_no
        }
    }));
    if (todoTaskData.retCode == HANDLE_SUCCESS) {
        let taskList = todoTaskData.retMap.taskList;
        let html = "";
        for (let i = 0; i < taskList.length; i++) {
            let days = getDaysBetween(taskList[i].create_time, commonCurrentDateTimeStr());
            let label = "";
            if (days <= 1) {
                label = "label label-success";
            } else if (days > 1 && days <= 3) {
                label = "label label-warning";
            } else {
                label = "label label-danger";
            }
            html += "<li>\n" +
                "         <span class=\"handle\">\n" +
                "             <i class=\"fa fa-ellipsis-v\"></i>\n" +
                "             <i class=\"fa fa-ellipsis-v\"></i>\n" +
                "         </span>\n" +
                "         <input type=\"checkbox\" value=\"\">\n" +
                "         <a class=\"text\" href='#' onclick=\"taskHandler_showTask(" +  commonFormatObj(taskList[i]) + ")\">" + taskList[i].task_title + "</a>\n" +
                "         <small class='" + label + "'><i class=\"fa fa-clock-o\"></i>" + commonFormatDate(taskList[i].create_time) + "创建" +"</small>\n" +
                "    </li>";
        }
        $("#mainPage-todo-task-list").empty();
        $("#mainPage-todo-task-list").append(html);
    } else {
        commonError("初始化代办任务失败");
    }
}

/**
 * 初始化已办任务
 */
function initDoneTask() {
    let doneTaskData = commonAjax("common.do", JSON.stringify({
        "beanList": [],
        "operType": "initDoneTask",
        "paramMap": {
            "user_no": localStorage.getItem("user_no"),
            "role_no": JSON.parse(localStorage.getItem("user_info")).role_no
        }
    }));
    if (doneTaskData.retCode == HANDLE_SUCCESS) {
        let taskList = doneTaskData.retMap.taskList;
        let html = "";
        for (let i = 0; i < taskList.length; i++) {
            let label = "label label-default";
            html += "<li>\n" +
                "         <span class=\"handle\">\n" +
                "             <i class=\"fa fa-ellipsis-v\"></i>\n" +
                "             <i class=\"fa fa-ellipsis-v\"></i>\n" +
                "         </span>\n" +
                "         <input type=\"checkbox\" value=\"\">\n" +
                "         <a class=\"text\" href='#' >" + taskList[i].task_title + "</a>\n" +
                "         <small class='" + label + "'><i class=\"fa fa-clock-o\"></i>" + commonFormatDate(taskList[i].create_time) + "创建" +"</small>\n" +
                "    </li>";
        }
        $("#mainPage-todo-task-list").empty();
        $("#mainPage-todo-task-list").append(html);
    } else {
        commonError("初始化代办任务失败");
    }
}