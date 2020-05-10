$(document).ready(function () {
    try {
        // 获取按钮权限列表
        let buttonMap = commonGetAuthField('S01000');
        let buttonStr = buttonMap.buttonStr;

        let treeConfig = {};

        let mainTreeInst = $.fn.zTree.init($("#projectDocDefind_mainTree"), treeConfig, retData.retMap.dataList);
    } catch (e) {
        console.error(e.message, e);
        commonError(e.message);
    }
});