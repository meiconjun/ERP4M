function showMainPage() {
    //隐藏TAB
    $("div[lay-filter='main-tab']").attr("hidden", "hidden");
    $("#main-page").removeAttr("hidden");
}
/** 初始化未读消息列表*/
function initUnReadMsg() {
    let html = "<li>\n" +
        "                                <!-- drag handle -->\n" +
        "                                <span class=\"handle\">\n" +
        "                        <i class=\"fa fa-ellipsis-v\"></i>\n" +
        "                        <i class=\"fa fa-ellipsis-v\"></i>\n" +
        "                      </span>\n" +
        "                                <!-- checkbox -->\n" +
        "                                <input type=\"checkbox\" value=\"\">\n" +
        "                                <!-- todo text -->\n" +
        "                                <span class=\"text\">设计一个不错的主题</span>\n" +
        "                                <!-- Emphasis label -->\n" +
        "                                <small class=\"label label-danger\"><i class=\"fa fa-clock-o\"></i> 2 分钟前</small>\n" +
        "                                <!-- General tools such as edit or delete-->\n" +
        "                                <div class=\"tools\">\n" +
        "                                    <i class=\"fa fa-edit\"></i>\n" +
        "                                    <i class=\"fa fa-trash-o\"></i>\n" +
        "                                </div>\n" +
        "                            </li>";
}