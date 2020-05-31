var userConfig_curr = 1;
var userConfig_tableIns;
$(document).ready(function () {
    try {
        // 获取按钮权限列表
        let buttonMap = commonGetAuthField('S02000');
        let buttonStr = buttonMap.buttonStr;
        // 获取角色列表
        let roleList = UserConfig_getRoleList();
        // 初始化表格
        userConfig_tableIns = layui.table.render({
            id : "userConfig_tableObj",
            elem: '#userConfig_table',
            height: 'full-450',
            url: 'userConfig.do',
            where : {
                message : JSON.stringify({
                    "beanList" : [{
                        "user_no" : "",
                        "user_name" : ""
                    }],
                    "operType" : "query",
                    "paramMap" : {
                        "curPage" : '1',// 当前页码
                        "limit" : FIELD_EACH_PAGE_NUM,// 每页条数
                        "role_no" : ""
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
                    elem : 'userConfig_page',
                    limit : Number(FIELD_EACH_PAGE_NUM),
                    groups : 5,
                    curr : userConfig_curr,
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
                            userConfig_queryOperation(obj.curr, obj.limit);// 重载页面
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
                    field: 'user_no',
                    title: '用户编号',
                    sort: true,
                    align : 'center'
                },
                {
                    field: 'user_name',
                    title: '用户名称',
                    align : 'center'
                }
                , { field: 'role_no',
                    title: '角色',
                    align : 'center',
                    templet : function (data) {
                        return commonFormatValue(roleList, data.role_no, false);
                    }}
                , {
                    field: 'status',
                    title: '用户状态',
                    align : 'center',
                    templet : function (data) {
                        return commonFormatValue(FIELD_USER_STATUS, data.status, false);
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
                    fixed: 'right',
                    align : 'center',
                    templet : function (data) {
                        let html = "";
                        if (buttonStr.indexOf("userConfig_rightBtn") != -1) {
                            html += "<a class=\"layui-btn layui-btn-xs\" name='userConfig_rightBtn' onclick='userConfig_rightOperation(" + commonFormatObj(data) + ")'>权限设定</a>";
                        }
                        if (buttonStr.indexOf("userConfig_modifyBtn") != -1) {
                            html += "<a class=\"layui-btn layui-btn-xs layui-btn-normal \" name='userConfig_modifyBtn' onclick='userConfig_modifyOperation(" + commonFormatObj(data) + "," +  commonFormatObj(roleList) + ")'>修改信息</a>";
                        }
                        return html;
                    }}
            ]]
        });

        commonPutNormalSelectOpts(roleList, "userConfig_Q_role", "", false);
        layui.form.render();// 此步是必须的，否则无法渲染一些表单元素

        //查询
        $("#userConfig_Q_query").click(function () {
            userConfig_queryOperation('1', FIELD_EACH_PAGE_NUM);
        });
        //新增
        $("#userConfig_addBtn").click(function () {
            userConfig_addOperation(roleList);
        });
        //删除
        $("#userConfig_deleteBtn").click(function () {
            userConfig_deleteOperation();
        });
        //停用启用用户
        $("#userConfig_deactBtn").click(function () {
            userConfig_deactOperation();
        });

        //权限控制
        if (buttonStr.indexOf("userConfig_Q_query") == -1) {
            $("#userConfig_Q_query").hide();
        }
        if (buttonStr.indexOf("userConfig_addBtn") == -1) {
            $("#userConfig_addBtn").hide();
        }
        if (buttonStr.indexOf("userConfig_deleteBtn") == -1) {
            $("#userConfig_deleteBtn").hide();
        }
        if (buttonStr.indexOf("userConfig_deactBtn") == -1) {
            $("#userConfig_deactBtn").hide();
        }
    } catch (e) {
        console.error(e.message, e);
        commonError(e.message);
    }
});

/**
 * 动态获取角色列表
 * @constructor
 */
function UserConfig_getRoleList() {
    let retData = commonAjax("userConfig.do", JSON.stringify({
        "beanList" : [],
        "operType" : "getRoleList",
        "paramMap" : {}
    }));
    if (retData.retCode == HANDLE_SUCCESS) {
        return retData.retMap.list;
    } else {
        commonError(retData.retMsg);
        return null;
    }
}

/**
 * 查询
 * @param curPage
 * @param limit
 */
function userConfig_queryOperation(curPage, limit) {
    let user_no = $("#userConfig_Q_userNo").val();
    let user_name = $("#userConfig_Q_userName").val();
    let role_no = $("#userConfig_Q_role").val();

    userConfig_curr = curPage;
    userConfig_tableIns.reload({
        where: {
            message: JSON.stringify({
                "beanList": [{
                    "user_no": user_no,
                    "user_name": user_name
                }],
                "operType": "query",
                "paramMap": {
                    "curPage" : String(curPage),// 当前页码
                    "limit" : String(limit),// 每页条数
                    "role_no": role_no
                }
            })
        }
    });
}

/**
 * 新增用户
 */
function userConfig_addOperation(roleList) {
    layui.layer.open({
        type: 1,// 页面层
        area: ['720px', '550px'],// 宽高
        title: '新增用户',// 标题
        content: $("#userConfig_addDiv"),//内容，直接取dom对象
        // btn: ['确定'],
        // yes: function (index, layero) {
        //     //确认按钮的回调，提交表单
        // },
        success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
            userConfig_cleanForm();
            // 渲染弹框元素
            commonPutNormalSelectOpts(roleList, "userConfig_add_roleNo", "", true);
            commonPutNormalSelectOpts(FIELD_USER_STATUS, "userConfig_add_status", "", true);

            // roleConfig_digSubmit(dialogIndex, "add");
            layui.form.render();
            userConfig_initFileUpload();
            userConfig_digSubmit(index, "add");

        }
    });
}

/**
 * 修改用户
 * @param oldData
 */
function userConfig_modifyOperation(oldData, roleList) {
    layui.layer.open({
        type: 1,// 页面层
        area: ['720px', '550px'],// 宽高
        title: '修改用户',// 标题
        content: $("#userConfig_addDiv"),//内容，直接取dom对象
        success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
            userConfig_cleanForm();
            // 渲染弹框元素
            commonPutNormalSelectOpts(roleList, "userConfig_add_roleNo", oldData.role_no, true);
            commonPutNormalSelectOpts(FIELD_USER_STATUS, "userConfig_add_status", oldData.status, true);

            // 设置头像回显
            let imgData = commonAjax("userConfig.do", JSON.stringify({
                "beanList": [],
                "operType": "getImgBase64",
                "paramMap": {
                    "imgUrl": oldData.picture
                }
            }));
            if (imgData.retCode == HANDLE_SUCCESS) {
                layui.$('#userConfig_uploadHeaderPrev').prev().hide();
                layui.$('#userConfig_uploadHeaderPrev').prev().prev().hide();
                layui.$('#userConfig_uploadHeaderPrev').removeClass('layui-hide').find('img').attr('src', "data:image/jpg;base64," + imgData.retMap.imgBase64);
            }


            $("#userConfig_add_userNo").val(oldData.user_no).attr('disabled', 'disabled').addClass('layui-disabled');//禁用输入框并添加禁用的样式
            $("#userConfig_add_userName").val(oldData.user_name);
            $("#userConfig_add_email").val(oldData.email);
            $("#userConfig_add_phone").val(oldData.phone);
            $("#userConfig_add_filePath").val(oldData.picture);
            layui.form.render();
            userConfig_initFileUpload();
            userConfig_digSubmit(index, "modify");

        }
    });
}
function userConfig_initFileUpload() {
    // 初始化文件上传组件
    layui.upload.render({
        elem: '#userConfig_uploadHeader',
        url: 'uploadHeaderImg.do',//改成您自己的上传接口
        data: {
            "user_no": function () {
                return $("#userConfig_add_userNo").val();
            }
        },
        accept: 'images',//只允许上传图片
        acceptMime: 'images',// 规定打开文件选择框时，筛选出的文件类型，值为用逗号隔开的 MIME 类型列表
        exts: 'jpg|png|gif|bmp|jpeg',
        auto: true,// 选择文件后是否自动上传
        multiple: false,// 是否允许多文件上传
        choose: function(obj) {
            //预读本地文件,如果是多文件，则会遍历
            obj.preview(function (index, file, result) {
                layui.$('#userConfig_uploadHeaderPrev').prev().hide();
                layui.$('#userConfig_uploadHeaderPrev').prev().prev().hide();
                layui.$('#userConfig_uploadHeaderPrev').removeClass('layui-hide').find('img').attr('src', result);
            });
        },
        done: function(res, index, upload){
            // 将头像地址赋值给表单
            $("#userConfig_add_filePath").val(res.data.filePath)
        },
        error: function (index, upload) {
            commonError("上传头像失败");
        }
    });
}

function userConfig_digSubmit(dialogIndex, operType) {
    layui.form.on('submit(userConfig_addSubmit)', function(data){// 绑定提交按钮点击回调事件，只有表单验证通过会进入
        layui.layer.confirm("是否确认提交？", function(index) {
            let fieldObj = data.field;// 表单字段集合
            let retData = commonAjax("userConfig.do", JSON.stringify({
                "beanList" : [{
                    "user_no" : fieldObj.userConfig_add_userNo,
                    "user_name" : fieldObj.userConfig_add_userName,
                    "pass_word" : "",
                    "picture" : fieldObj.userConfig_add_filePath,
                    "email" : fieldObj.userConfig_add_email,
                    "phone" : fieldObj.userConfig_add_phone,
                    "status" : fieldObj.userConfig_add_status
                }],
                "operType" : operType,
                "paramMap" : {
                    "role_no" : fieldObj.userConfig_add_roleNo
                }
            }));
            if (retData.retCode == HANDLE_SUCCESS) {
                if (operType == "add") {
                    commonInfo("新增成功，初始密码为" + retData.retMap.defaultPsw);
                } else {
                    commonOk("修改成功");
                }

                layui.layer.close(dialogIndex);
                userConfig_queryOperation('1', FIELD_EACH_PAGE_NUM);
            } else {
                commonError(retData.retMsg);
            }
        });
        return false;
    });
}

/**
 * 清空表单
 */
function userConfig_cleanForm() {
    layui.$('#userConfig_uploadHeaderPrev').prev().show();
    layui.$('#userConfig_uploadHeaderPrev').prev().prev().show();
    layui.$('#userConfig_uploadHeaderPrev').addClass('layui-hide').find('img').attr('src', "");
    $("#userConfig_add_userNo").removeAttr('disabled').removeClass('layui-disabled');
    layui.form.val("userConfig_addFrm", { //formTest 即 class="layui-form" 所在元素属性 lay-filter="" 对应的值
        "userConfig_add_userNo": "", // "name": "value"
        "userConfig_add_userName": "",
        "userConfig_add_email": '',
        "userConfig_add_phone": '',
        "userConfig_add_roleNo": '',
        "userConfig_add_status": "",
        "userConfig_add_filePath": ""
    });
}

/**
 * 删除用户
 */
function userConfig_deleteOperation() {
    let checkData = layui.table.checkStatus("userConfig_tableObj").data;
    if (checkData.length == 0) {
        commonInfo("请选择需要删除的用户");
        return;
    } else {
        layui.layer.confirm("是否确认删除选中的用户？", function(index) {
            let retData = commonAjax("userConfig.do", JSON.stringify({
                "beanList" : checkData,
                "operType" : "delete",
                "paramMap" : {}
            }));
            if (retData.retCode == HANDLE_SUCCESS) {
                commonOk("删除成功");
                userConfig_queryOperation('1', FIELD_EACH_PAGE_NUM);
            } else {
                commonError(retData.retMsg);
            }
        });
    }
}

function userConfig_deactOperation() {
    let checkData = layui.table.checkStatus("userConfig_tableObj").data;
    if (checkData.length == 0) {
        commonInfo("请选择需要启用/停用的用户");
        return;
    } else if (checkData.length > 1) {
        commonInfo("只能同时启用/停用一个用户");
        return;
    } else {
        let checked = checkData[0];
        layui.layer.confirm(checked.status == 1 ? "是否确认停用选中的用户？" : "是否确认启用选中的用户？", function(index) {
            let retData = commonAjax("userConfig.do", JSON.stringify({
                "beanList" : checkData,
                "operType" : "deact",
                "paramMap" : {}
            }));
            if (retData.retCode == HANDLE_SUCCESS) {
                commonOk("启用/停用用户成功");
                userConfig_queryOperation('1', FIELD_EACH_PAGE_NUM);
            } else {
                commonError(retData.retMsg);
            }
        });
    }

}

function userConfig_rightOperation(data) {
    let treeInst;
    let beforeNodes = [];
    let afterNodes = [];
    let addRight = {};//新增权限列表
    let delRight = {};//删除权限列表
    layui.layer.open({
        type: 1,// 页面层
        area: ['450px', '600px'],// 宽高
        title: '用户权限设定',// 标题
        content: $("#userConfig_rightTree"),//内容，直接取dom对象
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
                    let retData = commonAjax("userRight.do", JSON.stringify({
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
            let retData = commonAjax("userRight.do", JSON.stringify({
                "beanList" : [data],
                "operType" : "getUserRight",
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
                treeInst = $.fn.zTree.init($("#userConfig_rightTree"), treeConfig, retData.retMap.dataList);
                beforeNodes = treeInst.getCheckedNodes(true);//获取初始的勾选节点
            } else {
                commonError("加载用户权限失败！" + retData.retMsg);
            }

        }
    });
}