let roleConfig_tableIns;// 表格对象实例
$(document).ready(function () {

    // 获取按钮权限列表
    let buttonList = commonGetAuthField('S10100');

    // 初始化表格
    roleConfig_tableIns = layui.table.render({
        id : "roleConfig_tableObj",
        elem: '#roleConfig_table',
        height: 'full-380',
        url: 'static/json/tableBlankData.json',
        // method : 'post',
        even : true,
        page: true,
        loading : true,
        limit : FIELD_EACH_PAGE_NUM,
        jump : function(obj, first){
            //obj包含了当前分页的所有参数，比如：
            console.log(obj.curr); //得到当前页，以便向服务端请求对应页的数据。
            console.log(obj.limit); //得到每页显示的条数
            roleConfig_queryOperation(obj.curr, obj.limit);// 重载页面
        },
        skin : 'row',
        done : function(res, curr, count){

        },
        cols : [[
            {
                field: 'role_no',
                title: '角色编号',
                sort: true, fixed: 'left'
            }
            , { field: 'position',
                title: '职位',
                templet : function (data) {
                    return commonFormatValue(FIELD_POSITION, data.position, false);
                }}
            , {
                field: 'level',
                title: '级别',
                templet : function (data) {
                    return commonFormatValue(FIELD_ROLELEVEL, data.level, false);
                }
            }
            , {
                field: 'department',
                title: '部门',
                templet : function (data) {
                    return commonFormatValue(FIELD_DEPARTMENT, data.department, false);
                }
            }
            , {
                field: 'last_modi_time',
                title: '最后修改时间',
                sort: true ,
                templet : function (data) {
                    commonFormatDate(data.last_modi_time);
                }
            }
            , {
                field: 'edit',
                title: '操作',
                sort: true,
                fixed: 'right',
                templet : function (data) {
                    return "操作";
                }}
        ]]
    });
    commonPutNormalSelectOpts(FIELD_POSITION, "roleConfig_Q_position", "", false);
    commonPutNormalSelectOpts(FIELD_DEPARTMENT, "roleConfig_Q_department", "", false);
    commonPutNormalSelectOpts(FIELD_ROLELEVEL, "roleConfig_Q_level", "", false);

    layui.form.render();// 此步是必须的，否则无法渲染一些表单元素

    // 绑定按钮功能
    $("#roleConfig_Q_query").click(function () {
        roleConfig_queryOperation(1, limit);
    });
});

function roleConfig_queryOperation(curPage, limit) {
    let role_no = $("#roleConfig_Q_id").val();
    let position = $("#roleConfig_Q_position").val();
    let department = $("#roleConfig_Q_department").val();
    let level = $("#roleConfig_Q_level").val();

    let msg = {
        "beanList" : [{
            "role_no" : role_no,
            "position" : position,
            "department" : department,
            "level" : level
        }],
        "operType" : "query",
        "paramMap" : {
            "curPage" : curPage,// 当前页码
            "limit" : limit// 每页条数
        }
    };
}