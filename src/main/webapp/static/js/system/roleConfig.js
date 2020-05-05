var roleConfig_curr = 1;
var roleConfig_count = 0;
var roleConfig_tableIns;
$(document).ready(function () {
    try {
        // 获取按钮权限列表
        let buttonMap = commonGetAuthField('S10100');
        let buttonStr = buttonMap.buttonStr;
        // 初始化表格
        roleConfig_tableIns = layui.table.render({
            id : "roleConfig_tableObj",
            elem: '#roleConfig_table',
            height: 'full-450',
            url: 'roleConfig.do',
            where : {
                message : JSON.stringify({
                    "beanList" : [{
                        "role_no" : "",
                        "position" : "",
                        "department" : "",
                        "level" : ""
                    }],
                    "operType" : "query",
                    "paramMap" : {
                        "curPage" : '1',// 当前页码
                        "limit" : FIELD_EACH_PAGE_NUM// 每页条数
                    }
                })
            },
            method : 'post',
            even : true,
            page: false,
            loading : true,
            skin : 'row',
            done : function(res, curr, count){
                // 分页初始化
                layui.laypage.render({
                    elem : 'roleConfig_page',
                    limit : Number(FIELD_EACH_PAGE_NUM),
                    groups : 5,
                    curr : roleConfig_curr,
                    count : count,
                    prev : '上一页',
                    next : '下一页',
                    first : '首页',
                    last : '尾页',
                    layout : ['prev', 'first', 'page', 'last', 'next', 'count'],
                    jump : function(obj, first){
                        //obj包含了当前分页的所有参数，比如：
                        // console.log(obj.limit); //得到每页显示的条数
                        if (!first) {
                            roleConfig_queryOperation(obj.curr, obj.limit);// 重载页面
                        }
                    }
                });
            },
            parseData: function(res){ //res 即为原始返回的数据
                return {
                    "code": res.retCode, //解析接口状态
                    "msg": res.retMsg, //解析提示文本
                    "count": res.retMap.total, //解析数据长度
                    "data": res.retMap.list //解析数据列表
                };
            },
            cols : [[
                {//复选框
                    type : 'checkbox',
                    fixed: 'left'
                },
                {
                    field: 'role_no',
                    title: '角色编号',
                    sort: true,
                    align : 'center'
                },
                {
                    field: 'role_name',
                    title: '角色名称',
                    sort: true,
                    align : 'center'
                }
                , { field: 'position',
                    title: '职位',
                    align : 'center',
                    templet : function (data) {
                        return commonFormatValue(FIELD_POSITION, data.position, false);
                    }}
                , {
                    field: 'level',
                    title: '级别',
                    align : 'center',
                    templet : function (data) {
                        return commonFormatValue(FIELD_ROLELEVEL, data.level, false);
                    }
                }
                , {
                    field: 'department',
                    title: '部门',
                    align : 'center',
                    templet : function (data) {
                        return commonFormatValue(FIELD_DEPARTMENT, data.department, false);
                    }
                }
                , {
                    field: 'last_modi_time',
                    title: '最后修改时间',
                    sort: true ,
                    align : 'center',
                    templet : function (data) {
                        return commonFormatDate(data.last_modi_time);
                    }
                }
                , {
                    field: 'edit',
                    title: '操作',
                    width : 170,
                    sort: true,
                    fixed: 'right',
                    align : 'center',
                    templet : function (data) {
                        let html = "";
                        if (buttonStr.indexOf("roleConfig_rightBtn") != -1) {
                            html += "<a class=\"layui-btn layui-btn-xs\" name='roleConfig_rightBtn' onclick='roleConfig_rightOperation(" + commonFormatObj(data) + ")'>权限设定</a>";
                        }
                        if (buttonStr.indexOf("roleConfig_modifyBtn") != -1) {
                            html += "<a class=\"layui-btn layui-btn-xs layui-btn-normal \" name='roleConfig_modifyBtn' onclick='roleConfig_modifyOperation(" + commonFormatObj(data) + ")'>修改信息</a>";
                        }
                        return html;
                    }}
            ]]
        });

        commonPutNormalSelectOpts(FIELD_POSITION, "roleConfig_Q_position", "", false);
        commonPutNormalSelectOpts(FIELD_DEPARTMENT, "roleConfig_Q_department", "", false);
        commonPutNormalSelectOpts(FIELD_ROLELEVEL, "roleConfig_Q_level", "", false);

        layui.form.render();// 此步是必须的，否则无法渲染一些表单元素
        // 绑定按钮功能
        //查询
        $("#roleConfig_Q_query").click(function () {
            roleConfig_queryOperation('1', FIELD_EACH_PAGE_NUM);
        });
        //新增
        $("#roleConfig_addBtn").click(function () {
            roleConfig_addOperation();
        });
        //删除
        $("#roleConfig_deleteBtn").click(function () {
            roleConfig_deleteOperation();
        });

        // 权限控制
        if (buttonStr.indexOf("roleConfig_Q_query") == -1) {
            $("#roleConfig_Q_query").hide();
        }
        if (buttonStr.indexOf("roleConfig_addBtn") == -1) {
            $("#roleConfig_addBtn").hide();
        }
        if (buttonStr.indexOf("roleConfig_deleteBtn") == -1) {
            $("#roleConfig_deleteBtn").hide();
        }
        // 绑定重置表格事件
        commonResizeTable('roleConfig_tableObj');
    } catch (e) {
        console.error(e.message, e);
        commonError(e.message);
    }
});

function roleConfig_queryOperation(curPage, limit) {
    let role_no = $("#roleConfig_Q_id").val();
    let position = $("#roleConfig_Q_position").val();
    let department = $("#roleConfig_Q_department").val();
    let level = $("#roleConfig_Q_level").val();

    roleConfig_curr = curPage;
    roleConfig_tableIns.reload({
        where: { //设定异步数据接口的额外参数，任意设
            message : JSON.stringify({
                "beanList" : [{
                    "role_no" : role_no,
                    "position" : position,
                    "department" : department,
                    "level" : level
                }],
                "operType" : "query",
                "paramMap" : {
                    "curPage" : String(curPage),// 当前页码
                    "limit" : String(limit)// 每页条数
                }
            })
        }
    });
}

/**
 * 新增角色
 */
function roleConfig_addOperation() {
    layui.layer.open({
        type: 1,// 页面层
        area: ['500px', '600px'],// 宽高
        title: '新增角色',// 标题
        content: $("#roleConfig_addDiv"),//内容，直接取dom对象
        // btn: ['确定'],
        // yes: function (index, layero) {
        //     //确认按钮的回调，提交表单
        // },
        success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
            // 渲染弹框元素
            let dialogIndex = index;// 弹框层索引
            commonPutNormalSelectOpts(FIELD_POSITION, "roleConfig_position_addTxt", "", true);
            commonPutNormalSelectOpts(FIELD_ROLELEVEL, "roleConfig_level_addTxt", "", true);
            commonPutNormalSelectOpts(FIELD_DEPARTMENT, "roleConfig_department_addTxt", "", true);

            roleConfig_digSubmit(dialogIndex, "add");
            layui.form.render();
        }
    });
}

/**
 * 修改角色信息
 */
function roleConfig_modifyOperation(oldData) {
    layui.layer.open({
        type: 1,// 页面层
        area: ['500px', '600px'],// 宽高
        title: '修改角色',// 标题
        content: $("#roleConfig_addDiv"),//内容，直接取dom对象
        // btn: ['确定'],
        // yes: function (index, layero) {
        //     //确认按钮的回调，提交表单
        // },
        success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
            // 渲染弹框元素
            let dialogIndex = index;// 弹框层索引
            commonPutNormalSelectOpts(FIELD_POSITION, "roleConfig_position_addTxt", oldData.position, true);
            commonPutNormalSelectOpts(FIELD_ROLELEVEL, "roleConfig_level_addTxt", oldData.level, true);
            commonPutNormalSelectOpts(FIELD_DEPARTMENT, "roleConfig_department_addTxt", oldData.department, true);

            // 设置表单val
            $("#roleConfig_roleNo_addTxt").val(oldData.role_no).attr('disabled', 'disabled').addClass('layui-disabled');//禁用输入框并添加禁用的样式
            $("#roleConfig_roleName_addTxt").val(oldData.role_name);
            $("#roleConfig_desc_addTxt").val(oldData.desc);
            roleConfig_digSubmit(dialogIndex, "modify");
            layui.form.render();
        }
    });
}

function roleConfig_digSubmit(dialogIndex, operType) {
    layui.form.on('submit(roleConfig_addSubmit)', function(data){// 绑定提交按钮点击回调事件，只有表单验证通过会进入
        layui.layer.confirm("是否确认提交？", function(index) {
            let fieldObj = data.field;// 表单字段集合
            let retData = commonAjax("roleConfig.do", JSON.stringify({
                "beanList" : [{
                    "role_no" : fieldObj.roleConfig_roleNo_addTxt,
                    "role_name" : fieldObj.roleConfig_roleName_addTxt,
                    "position" : fieldObj.roleConfig_position_addTxt,
                    "level" : fieldObj.roleConfig_level_addTxt,
                    "department" : fieldObj.roleConfig_department_addTxt,
                    "desc" : fieldObj.roleConfig_desc_addTxt
                }],
                "operType" : operType,
                "paramMap" : {}
            }));
            if (retData.retCode == HANDLE_SUCCESS) {
                commonOk("操作成功");
                layui.layer.close(dialogIndex);
                roleConfig_queryOperation('1', FIELD_EACH_PAGE_NUM);
            } else {
                commonError(retData.retMsg);
            }
        });
        return false;
    });
}

function roleConfig_deleteOperation() {
    let checkData = layui.table.checkStatus('roleConfig_tableObj').data;
    if (checkData.length == 0) {
        commonInfo("请选择需要删除的角色");
        return;
    } else {
        layui.layer.confirm("是否确认删除选中的角色？", function(index) {
            let retData = commonAjax("roleConfig.do", JSON.stringify({
                "beanList" : checkData,
                "operType" : "delete",
                "paramMap" : {}
            }));
            if (retData.retCode == HANDLE_SUCCESS) {
                commonOk("删除成功");
                roleConfig_queryOperation('1', FIELD_EACH_PAGE_NUM);
            } else {
                commonError(retData.retMsg);
            }
        });
    }
}

function roleConfig_rightOperation(data) {
    let treeInst;
    let beforeNodes = [];
    let afterNodes = [];
    let addRight = {};//新增权限列表
    let delRight = {};//删除权限列表
    layui.layer.open({
        type: 1,// 页面层
        area: ['450px', '600px'],// 宽高
        title: '角色权限设定',// 标题
        content: $("#roleConfig_rightTree"),//内容，直接取dom对象
        btn: ['提交'],
        yes: function (index1, layero) {
            //确认按钮的回调，提交表单
            layui.layer.confirm("是否确认提交修改？", function(index) {
                afterNodes = treeInst.getCheckedNodes(true);// 获取最终的勾选节点
                for (let i = 0; i < afterNodes.length; i++) {
                    if (!beforeNodes.some(function (curValue) {
                        return curValue.field_no == afterNodes[i].field_no;
                    })) {
                        addRight[afterNodes[i].field_no] = afterNodes[i].field_type;
                    }
                }
                for (let i = 0; i < beforeNodes.length; i++) {
                    if (!afterNodes.some(function (curValue) {
                        return curValue.field_no == beforeNodes[i].field_no;
                    })) {
                        delRight[beforeNodes[i].field_no] = beforeNodes[i].field_type;
                    }
                }
                if ($.isEmptyObject(addRight) && $.isEmptyObject(delRight)) {
                    commonInfo("没有任何修改");
                    return;
                } else {
                    let retData = commonAjax("roleRight.do", JSON.stringify({
                        "beanList": [data],
                        "operType": "updateRight",
                        "paramMap": {
                            "addRight" : addRight,
                            "delRight" : delRight
                        }
                    }));
                    if (retData.retCode == HANDLE_SUCCESS) {
                        commonOk("更新成功");
                        layui.layer.close(index1);
                    } else {
                        commonError(retData.retMsg);
                    }
                }
            });
        },
        success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
            let retData = commonAjax("roleRight.do", JSON.stringify({
                "beanList" : [data],
                "operType" : "getRoleRight",
                "paramMap" : {}
            }));
            if (retData.retCode == HANDLE_SUCCESS) {
                let treeConfig = {
                    check: {
                        chkboxType: {'Y': "ps", "N": 'ps'},
                        chkStyle: 'checkbox',
                        enable: true
                    },
                    edit: {
                        enable: false//不可编辑
                    }
                };
                // 渲染树形组件
                treeInst = $.fn.zTree.init($("#roleConfig_rightTree"), treeConfig, retData.retMap.dataList);
                beforeNodes = treeInst.getCheckedNodes(true);//获取初始的勾选节点
                /*let treeInst = layui.tree.render({
                    elem: '#roleConfig_rightTree',
                    id: 'roleConfig_rightTree',
                    showCheckbox: true,
                    edit: false,
                    oncheck: function (obj) {
                        console.log(obj);
                        let checkState = obj.checked;
                        let tempFun = function (data) {
                            if (checkState == true) {
                                if (delRight.hasOwnProperty(data.id)) {
                                    // 删除删除列表里的该要素
                                    delete delRight[data.id];
                                } else {
                                    // 添加进新增列表
                                    addRight[data.id] = data.field_type;
                                }
                            } else {
                                if (addRight.hasOwnProperty(data.id)) {
                                    // 删除新增列表里的该要素
                                    delete addRight[data.id];
                                } else {
                                    // 添加进删除列表
                                    delRight[data.id] = data.field_type;
                                }

                            }
                            if (data.hasOwnProperty('children')) {
                                for (let g = 0; g < data.children.length; g++) {
                                    tempFun(data.children[g]);
                                }
                            }
                        }
                        tempFun(obj.data);
                    },
                    data: retData.retMap.dataList
                });*/
                // 树构造完成时会触发根节点的勾选事件，所以会自动往下述两个列表里添加要素，先清空
            } else {
                commonError("加载角色权限失败！" + retData.retMsg);
            }

        }
    });
}