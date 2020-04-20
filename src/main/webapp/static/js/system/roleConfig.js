$(document).ready(function () {

    // 获取按钮权限列表
    let buttonList = commonGetAuthField('S10100');
    // 绑定按钮功能

    // 初始化表格
    console.log(layui);
    layui.table.render({
        elem : '#roleConfig_table',
        height: 'full-380',
        url : '',
        page : true,
        cols : [[
            {field: 'id', title: 'ID', sort: true, fixed: 'left'}
            ,{field: 'username', title: '用户名'}
            ,{field: 'sex', title: '性别', sort: true}
            ,{field: 'city', title: '城市'}
            ,{field: 'sign', title: '签名'}
            ,{field: 'experience', title: '积分', sort: true}
            ,{field: 'score', title: '评分', sort: true}
            ,{field: 'classify', title: '职业'}
            ,{field: 'wealth', title: '财富', sort: true}
        ]]
    });
    let temp = [
              {
                     "value" : "",
                     "name" : "—— 请选择 ——"
                 },
             {
             "value" : "010",
             "name" : "北京"
             }
 ];
    commonPutNormalSelectOpts(temp, "roleConfig_Q_position", "", false);
});