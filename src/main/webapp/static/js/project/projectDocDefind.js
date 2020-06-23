var buttonStr;
var mainTreeInst;
$(document).ready(function () {
    try {
        // 获取按钮权限列表
        let buttonMap = commonGetAuthField('P01000');
        buttonStr = buttonMap.buttonStr;

        let treeData = commonAjax("projectDocDefind.do", JSON.stringify({
            "beanList": [],
            "operType": "getDocTree",
            "paramMap": {

            }
        }));
        if (treeData.retCode != HANDLE_SUCCESS) {
            commonError("加载文档列表失败:" + treeData.retMsg);
            return;
        }
        let treeConfig = {
            check: {
                enable: false//不显示checkBox
            },
            callback: {
                beforeRightClick: projectDocDefind_treeNodeBeforeClick
            },
            edit: {
                enable: false//可编辑
            }
        };

        mainTreeInst = $.fn.zTree.init($("#projectDocDefind_mainTree"), treeConfig, treeData.retMap.dataList);
    } catch (e) {
        console.error(e.message, e);
        commonError(e.message);
    }
});


function projectDocDefind_treeNodeBeforeClick(treeId, treeNode) {
    if (treeNode == undefined || treeNode == null) {// 不是在节点上点击的
        return false;
    }
    let items = {};//菜单选项集合
    let item_add = {
        name: "新增",
        icon: "fa-plus",
        callback: function (key, options) {
            layui.layer.open({
                type: 1,// 页面层
                area: ['500px', '650px'],// 宽高
                title: '新增项目文档',// 标题
                content: $("#projectDocDefind_addDiv"),//内容，直接取dom对象
                // btn: ['确定'],
                // yes: function (index, layero) {
                //     //确认按钮的回调，提交表单
                // },
                success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
                    // 渲染弹框元素
                    projectDocDefind_cleanForm();
                    commonPutNormalSelectOpts(FIELD_DEPARTMENT, "projectDocDefind_add_department", "", true);
                    /*commonPutNormalSelectOpts([{
                        "name" : "请先选择负责部门",
                        "value" : ""
                    }], "projectDocDefind_add_dutyRole", "", true, true);*/
                    projectDocDefind_updateRoleList($("#projectDocDefind_add_department").val());
                    layui.form.on('select(projectDocDefind_add_department)', function(data){
                        projectDocDefind_updateRoleList(data.value);
                    });
                    commonPutNormalSelectOpts(FIELD_STAGE, "projectDocDefind_add_stage", "", true);

                    projectDocDefind_digSubmit(index, "add", treeNode);
                    layui.form.render();
                }
            });
        }
    };

    let item_modify = {
        name: "修改",
        icon: "fa-pencil-square-o",
        callback: function (key, options) {
            layui.layer.open({
                type: 1,// 页面层
                area: ['500px', '650px'],// 宽高
                title: '修改项目文档',// 标题
                content: $("#projectDocDefind_addDiv"),//内容，直接取dom对象
                // btn: ['确定'],
                // yes: function (index, layero) {
                //     //确认按钮的回调，提交表单
                // },
                success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
                    // 渲染弹框元素
                    projectDocDefind_cleanForm();
                    $("#projectDocDefind_add_docNo").val(treeNode.doc_no).attr("disabled", "disabled").addClass("layui-disabled");
                    $("#projectDocDefind_add_docName").val(treeNode.name);
                    $("#projectDocDefind_add_desc").val(treeNode.description);
                    $("#projectDocDefind_add_writer").val(treeNode.writer);
                    commonPutNormalSelectOpts(FIELD_DEPARTMENT, "projectDocDefind_add_department", treeNode.department, true);
                    projectDocDefind_updateRoleList(treeNode.department);
                    $("#projectDocDefind_add_dutyRole").val(treeNode.duty_role);
                    layui.form.on('select(projectDocDefind_add_department)', function(data){
                        projectDocDefind_updateRoleList(data.value);
                    });
                    commonPutNormalSelectOpts(FIELD_STAGE, "projectDocDefind_add_stage", treeNode.stage, true);
                    $("#projectDocDefind_add_stage").attr("disabled", "disabled").addClass("layui-disabled");
                    projectDocDefind_digSubmit(index, "modify", treeNode);
                    layui.form.render();
                }
            });
        }
    };

    let item_delete = {
        name: "删除",
        icon: "fa-trash-o",
        callback: function (key, options) {
            layui.layer.confirm("是否确认删除文档？", function(index) {
                let retData = commonAjax("projectDocDefind.do", JSON.stringify({
                    "beanList" : [],
                    "operType" : "delete",
                    "paramMap" : {
                        "doc_no" : treeNode.doc_no
                    }
                }));
                if (retData.retCode == HANDLE_SUCCESS) {
                    commonOk("删除成功");
                    // 删除节点
                    mainTreeInst.removeNode(treeNode, false);
                } else {
                    commonError(retData.retMsg);
                }
            });
        }
    };
    if (buttonStr.indexOf("projectDocDefind_addBtn") != -1 && treeNode.hasOwnProperty("type")) {
        items.add = item_add;
    }
    if (buttonStr.indexOf("projectDocDefind_modifyBtn") != -1 && !treeNode.hasOwnProperty("type")) {
        items.modify = item_modify;
    }
    if (buttonStr.indexOf("projectDocDefind_deleteBtn") != -1 && !treeNode.hasOwnProperty("type")) {
        items.delete = item_delete;
    }
    $.contextMenu({
        selector: '#' + treeNode.tId,
        items: items
    });
    return true;
}

function projectDocDefind_cleanForm() {
    $("#projectDocDefind_add_docNo").removeAttr('disabled').removeClass('layui-disabled');
    $("#projectDocDefind_add_stage").removeAttr('disabled').removeClass('layui-disabled');
    layui.form.val("projectDocDefind_addFrm", { //formTest 即 class="layui-form" 所在元素属性 lay-filter="" 对应的值
        "projectDocDefind_add_docNo": "", // "name": "value"
        "projectDocDefind_add_docName": "",
        "projectDocDefind_add_stage": '',
        "projectDocDefind_add_department": '',
        "projectDocDefind_add_desc": '',
        "projectDocDefind_add_dutyRole": '',
        "projectDocDefind_add_writer": ''
    });
}

function projectDocDefind_digSubmit(dialogIndex, operType, parentNode) {
    layui.form.on('submit(projectDocDefind_addSubmit)', function(data){// 绑定提交按钮点击回调事件，只有表单验证通过会进入
        layui.layer.confirm("是否确认提交？", function(index) {
            let fieldObj = data.field;// 表单字段集合
            let retData = commonAjax("projectDocDefind.do", JSON.stringify({
                "beanList" : [{
                    "doc_no" : fieldObj.projectDocDefind_add_docNo,
                    "doc_name" : fieldObj.projectDocDefind_add_docName,
                    "stage" : fieldObj.projectDocDefind_add_stage,
                    "department" : fieldObj.projectDocDefind_add_department,
                    "description" : fieldObj.projectDocDefind_add_desc,
                    "duty_role": fieldObj.projectDocDefind_add_dutyRole,
                    "writer": fieldObj.projectDocDefind_add_writer
                }],
                "operType" : operType,
                "paramMap" : {

                }
            }));
            if (retData.retCode == HANDLE_SUCCESS) {
                commonOk("提交成功");
                layui.layer.close(dialogIndex);
                if ("add" == operType) {
                    //前台插入子节点
                    mainTreeInst.addNodes(parentNode, {
                        "name": fieldObj.projectDocDefind_add_docName,
                        "doc_no": fieldObj.projectDocDefind_add_docNo,
                        "secret_level": "",
                        "stage": fieldObj.projectDocDefind_add_stage,
                        "department": fieldObj.projectDocDefind_add_department,
                        "description": fieldObj.projectDocDefind_add_desc,
                        "duty_role": fieldObj.projectDocDefind_add_dutyRole,
                        "writer": fieldObj.projectDocDefind_add_writer
                    }, true);
                } else {
                    // 更新节点
                    parentNode.name = fieldObj.projectDocDefind_add_docName;
                    parentNode.department = fieldObj.projectDocDefind_add_department;
                    parentNode.description = fieldObj.projectDocDefind_add_desc;
                    parentNode.duty_role = fieldObj.projectDocDefind_add_dutyRole;
                    parentNode.writer = fieldObj.projectDocDefind_add_writer;
                    mainTreeInst.updateNode(parentNode, true);
                }

            } else {
                commonError(retData.retMsg);
            }
        });
        return false;
    });
}

function projectDocDefind_updateRoleList(department) {
    let retData = commonAjax("common.do", JSON.stringify({
        "beanList": [],
        "operType": "getRoleList",
        "paramMap": {
            "position": "",
            "level": "",
            "department": department
        }
    }));
    if (retData.retCode == HANDLE_SUCCESS) {
        commonPutNormalSelectOpts(retData.retMap.roleList, "projectDocDefind_add_dutyRole", "", true);
        layui.form.render();
    } else {
        commonError("加载角色列表失败");
    }
}