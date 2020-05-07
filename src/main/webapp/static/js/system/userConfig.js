var userConfig_curr = 1;
var userConfig_tableIns;
$(document).ready(function () {
    try {
        // 获取按钮权限列表
        let buttonMap = commonGetAuthField('S10100');
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
                    sort: true,
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
                    sort: true,
                    fixed: 'right',
                    align : 'center',
                    templet : function (data) {
                        let html = "";
                        if (buttonStr.indexOf("userConfig_rightBtn") != -1) {
                            html += "<a class=\"layui-btn layui-btn-xs\" name='userConfig_rightBtn' onclick='userConfig_rightOperation(" + commonFormatObj(data) + ")'>权限设定</a>";
                        }
                        if (buttonStr.indexOf("userConfig_modifyBtn") != -1) {
                            html += "<a class=\"layui-btn layui-btn-xs layui-btn-normal \" name='userConfig_modifyBtn' onclick='userConfig_modifyOperation(" + commonFormatObj(data) + ")'>修改信息</a>";
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
            // 渲染弹框元素
            let dialogIndex = index;// 弹框层索引
            commonPutNormalSelectOpts(roleList, "userConfig_add_roleNo", "", true);
            commonPutNormalSelectOpts(FIELD_USER_STATUS, "userConfig_add_status", "", true);

            // roleConfig_digSubmit(dialogIndex, "add");
            layui.form.render();

            // 文件上传组件
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
                    //触发表单提交
                    $("#userConfig_add_filePath").val(res.data.filePath)
                },
                error: function (index, upload) {
                    commonError("上传头像失败");
                }
            });
        }
    });
}