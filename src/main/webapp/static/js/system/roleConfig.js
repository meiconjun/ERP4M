$(document).ready(function () {

    // 获取按钮权限列表
    let buttonList = commonGetAuthField('S10100');
    // 绑定按钮功能

    // 初始化表格
    layui.table.render({
        elem: '#roleConfig_table',
        height: 'full-380',
        url: 'static/json/tableBlankData.json',
        // method : 'post',
        even : true,
        page: true,
        skin : 'row',
        cols: [[
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

});