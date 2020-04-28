var roleConfig_curr = 1;
var roleConfig_count = 0;
$(document).ready(function () {
    let roleConfig_tableIns;// 表格对象实例
    // 获取按钮权限列表
    let buttonList = commonGetAuthField('S10100');

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
            /*{
            limit : Number(FIELD_EACH_PAGE_NUM),
            jump : function(obj, first){
                //obj包含了当前分页的所有参数，比如：
                console.log(obj.limit); //得到每页显示的条数
                if (!first) {
                    console.log("分页查询！");
                    roleConfig_queryOperation(obj.curr, obj.limit, roleConfig_tableIns);// 重载页面
                }
            },
        },*/
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
                        roleConfig_queryOperation(obj.curr, obj.limit, roleConfig_tableIns);// 重载页面
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
                    return "<a class=\"layui-btn layui-btn-xs\" id='roleConfig_rightBtn'>权限设定</a>" +
                        "<a class=\"layui-btn layui-btn-xs layui-btn-normal \" id='roleConfig_modifyBtn'>修改信息</a>";
                }}
        ]]
    });

    commonPutNormalSelectOpts(FIELD_POSITION, "roleConfig_Q_position", "", false);
    commonPutNormalSelectOpts(FIELD_DEPARTMENT, "roleConfig_Q_department", "", false);
    commonPutNormalSelectOpts(FIELD_ROLELEVEL, "roleConfig_Q_level", "", false);

    layui.form.render();// 此步是必须的，否则无法渲染一些表单元素
    // 绑定按钮功能
    $("#roleConfig_Q_query").click(function () {
        roleConfig_queryOperation('1', FIELD_EACH_PAGE_NUM, roleConfig_tableIns);
    });
});

function roleConfig_queryOperation(curPage, limit, inst) {
    let role_no = $("#roleConfig_Q_id").val();
    let position = $("#roleConfig_Q_position").val();
    let department = $("#roleConfig_Q_department").val();
    let level = $("#roleConfig_Q_level").val();

    roleConfig_curr = curPage;
    inst.reload({
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