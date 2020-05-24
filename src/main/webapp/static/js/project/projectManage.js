var projectManage_tableIns;
var projectManage_curr = 1;
$(document).ready(function () {
    try {
        // 获取按钮权限列表
        let buttonMap = commonGetAuthField('P03000');
        let buttonStr = buttonMap.buttonStr;
        //初始化表格

        projectManage_tableIns = layui.table.render({
            id: 'projectManage_tableObj',
            elem: '#projectManage_table',
            height: 'full-450',
            url: 'projectManage.do',
            where: {
                message: JSON.stringify({
                    "beanList": [{

                    }],
                    "operType": "query",
                    "paramMap": {
                        "curPage": "1",
                        "limit": FIELD_EACH_PAGE_NUM

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
                    elem : 'projectManage_page',
                    limit : Number(FIELD_EACH_PAGE_NUM),
                    groups : 5,
                    curr : projectManage_curr,
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
                            projectManage_queryOperation(obj.curr, obj.limit);// 重载页面
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
                    field: 'project_name',
                    title: '项目名称',
                    sort: true,
                    align : 'center'
                },
                {
                    field: 'chn_name',
                    title: '中文名称',
                    sort: true,
                    align : 'center'
                }
                , { field: 'principal',
                    title: '负责人',
                    align : 'center',
                    templet : function (data) {
                        return commonFormatUserNo(data.principal, false);
                    }}
                , {
                    field: 'begin_date',
                    title: '开始日期',
                    align : 'center',
                    templet : function (data) {
                        return commonFormatDate(data.begin_date);
                    }
                }
                , {
                    field: 'end_date',
                    title: '结束日期',
                    align : 'center',
                    templet : function (data) {
                        return "";
                    }
                }
                , {
                    field: 'project_state',
                    title: '项目状态',
                    sort: true ,
                    align : 'center',
                    templet : function (data) {
                        return commonFormatValue(FIELD_PROJECT_STATE, data.project_state, false)
                    }
                }
                , {
                    field: 'current_stage',
                    title: '当前阶段',
                    sort: true ,
                    align : 'center',
                    templet : function (data) {
                        //TODO 返回当前阶段
                    }
                }
                , {
                    field: 'edit',
                    title: '操作',
                    width : 10,
                    sort: true,
                    fixed: 'right',
                    align : 'center',
                    templet : function (data) {
                        //TODO 详情展示
                        let html = "";
                        if (buttonStr.indexOf("projectManage_detailBtn") != -1) {
                            html += "<a class=\"layui-btn layui-btn-xs\" name='projectManage_detailBtn' onclick='projectManage_detailOperation(" + commonFormatObj(data) + ")'>详情</a>";
                        }
                        return html;
                    }}
            ]]
        });
        layui.form.render();
        //查询
        $("#projectManage_query").click(function () {
            projectManage_queryOperation('1', FIELD_EACH_PAGE_NUM);
        });
        //进入下阶段
        $("#projectManage_nextStage").click(function () {
            projectManage_nextStageOperation();
        });
        // 权限控制
        if (buttonStr.indexOf("projectManage_query") == -1) {
            $("#projectManage_query").hide();
        }
        if (buttonStr.indexOf("projectManage_nextStage") == -1) {
            $("#projectManage_nextStage").hide();
        }
        // 绑定重置表格事件
        commonResizeTable('projectManage_tableObj');
    } catch (e) {
        console.error(e.message, e);
        commonError(e.message);
    }
});