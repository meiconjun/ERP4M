var personalDoc_curr = 1;
var personalDoc_count = 0;
var personalDoc_tableIns;
$(document).ready(function () {
    try {
        // 获取按钮权限
        let buttonMap = commonGetAuthField('D01000');
        let buttonStr = buttonMap.buttonStr;
        //初始化表格
        personalDoc_tableIns = layui.table.render({
            id : "personalDoc_tableObj",
            elem: '#personalDoc_table',
            height: 'full-450',
            url: 'personalDoc.do',
            where : {
                message : JSON.stringify({
                    "beanList" : [{
                        "doc_no" : "",
                        "doc_name" : "",
                        "upload_user" : "",
                        "doc_type" : ""
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
                    elem : 'personalDoc_page',
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
                            personalDoc_queryOperation(obj.curr, obj.limit);// 重载页面
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
    } catch (e) {
        console.error(e.message, e);
        commonError(e.message);
    }
});