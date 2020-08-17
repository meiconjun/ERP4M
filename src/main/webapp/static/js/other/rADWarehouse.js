var rADWarehouse_tableInst = null;
var rADWarehouse_curr = 1;
$(document).ready(function () {
    try {
        let buttonMap = commonGetAuthField('O01000');
        let buttonStr = buttonMap.buttonStr;

        // 初始化表格
        rADWarehouse_tableInst = layui.table.render({
            id : "rADWarehouse_tableObj",
            elem : '#rADWarehouse_table',
            height : 'full-450',
            url: 'rADWarehouse.do',
            where : {
                message : JSON.stringify({
                    "beanList": [{
                        serial_no: "",
                        material_no: "",
                        oper_type: ""
                    }],
                    "operType": "query",
                    "paramMap": {
                        'curPage': '1',
                        'limit': FIELD_EACH_PAGE_NUM,
                        "date_begin": '',
                        "date_end": ""
                    }
                })
            },
            method: 'post',
            even: true,
            page: false,
            loading: true,
            skin: 'row',
            done: function (res, curr, count) {
                // 分页初始化
                layui.laypage.render({
                    elem : 'rADWarehouse_page',
                    limit : Number(FIELD_EACH_PAGE_NUM),
                    groups : 5,
                    curr : rADWarehouse_curr,
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
                            rADWarehouse_queryOperation(obj.curr, obj.limit);// 重载页面
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
                    field: 'serial_no',
                    title: '流水号',
                    sort: true,
                    align : 'center'
                },
                {
                    field: 'material_no',
                    title: '料号',
                    align : 'center'
                }
                , { field: 'material_name',
                    title: '物料名称',
                    align : 'center'
                }
                , {
                    field: 'oper_type',
                    title: '操作类型',
                    align : 'center',
                    templet : function (data) {
                        return commonFormatValue(FIELD_WAREHOSE_OPERTYOPE, data.oper_type, false);
                    }
                }
                , {
                    field: 'project_no',
                    title: '项目',
                    align : 'center'
                }
                , {
                    field: 'number',
                    title: '数量',
                    align : 'center'
                }
                , {
                    field: 'last_modi_time',
                    title: '录入时间',
                    align : 'center',
                    templet : function (data) {
                        return commonFormatDate(data.last_modi_time);
                    }
                }
                , {
                    field: 'last_modi_user',
                    title: '录入用户',
                    align : 'center',
                    templet : function (data) {
                        return commonFormatUserNo(last_modi_user);
                    }
                }
            ]]
        });

        commonPutNormalSelectOpts(FIELD_WAREHOSE_OPERTYOPE, "rADWarehouse_operType", "", false);

        layui.form.render();// 此步是必须的，否则无法渲染一些表单元素

        //查询
        $("#rADWarehouse_queryBtn").click(function () {
            rADWarehouse_queryOperation('1', FIELD_EACH_PAGE_NUM);
        });

        if (buttonStr.indexOf("rADWarehouse_queryBtn") == -1) {
            $("#rADWarehouse_queryBtn").hide();
        }

        // 绑定重置表格事件
        commonResizeTable('rADWarehouse_tableObj');
    } catch (e) {
        console.error(e.message, e);
        commonError(e.message);
    }
});

/**
 * 查询方法
 */
function rADWarehouse_queryOperation(curPage, limit) {
    let serial_no = $("#rADWarehouse_serialNo").val();
    let material_no = $("#rADWarehouse_materialNo").val();
    let oper_type = $("#rADWarehouse_operType").val();
    let input_date = $("#rADWarehouse_inputDate").val();
    let date_begin = "";
    let date_end = "";
    if (!commonBlank(input_date)) {
        date_begin = input_date.split(" - ")[0].replace(/-/g, "") + '000000';
        date_end = input_date.split(" - ")[1].replace(/-/g, "") + '000000';
    }

    rADWarehouse_curr = curPage;
    rADWarehouse_tableInst.reload({
        where: { //设定异步数据接口的额外参数，任意设
            message : JSON.stringify({
                "beanList" : [{
                    serial_no: serial_no,
                    material_no: material_no,
                    oper_type: oper_type
                }],
                "operType" : "query",
                "paramMap" : {
                    "curPage" : String(curPage),// 当前页码
                    "limit" : String(limit),// 每页条数
                    'date_begin': date_begin,
                    'date_end': date_end
                }
            })
        }
    });
}