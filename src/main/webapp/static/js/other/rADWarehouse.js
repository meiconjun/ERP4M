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
                        return commonFormatUserNo(data.last_modi_user);
                    }
                }
                , {
                    field: 'edit',
                    title: '操作',
                    width : 80,
                    fixed: 'right',
                    align : 'center',
                    templet : function (data) {
                        let html = "<a class=\"layui-btn layui-btn-xs layui-btn-normal \" name='rADWarehouse_detail' onclick='rADWarehouse_detail(" + commonFormatObj(data) + ")'>详情</a>";
                        return html;
                    }
                }
            ]]
        });

        commonPutNormalSelectOpts(FIELD_WAREHOSE_OPERTYOPE, "rADWarehouse_operType", "", false);
        layui.laydate.render({
            elem: '#rADWarehouse_inputDate'
            ,range: true
        });
        layui.form.render();// 此步是必须的，否则无法渲染一些表单元素

        //查询
        $("#rADWarehouse_queryBtn").click(function () {
            rADWarehouse_queryOperation('1', FIELD_EACH_PAGE_NUM);
        });
        $("#rADWarehouse_addBtn").click(function () {
            rADWarehouse_addOperation();
        });
        if (buttonStr.indexOf("rADWarehouse_queryBtn") == -1) {
            $("#rADWarehouse_queryBtn").hide();
        }
        if (buttonStr.indexOf("rADWarehouse_addBtn") == -1) {
            $("#rADWarehouse_addBtn").hide();
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

/**
 * 录入出入库流水
 */
function rADWarehouse_addOperation() {
    layui.layer.open({
        type: 1,// 页面层
        area: ['500px', '700px'],// 宽高
        title: '录入流水',// 标题
        content: $("#rADWarehouse_addDiv"),//内容，直接取dom对象
        // btn: ['确定'],
        // yes: function (index, layero) {
        //     //确认按钮的回调，提交表单
        // },
        success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
            // 渲染弹框元素
            rADWarehouse_cleanForm();
            // 加载项目列表
            let projectList = commonGetProjectList();

            commonPutNormalSelectOpts(projectList, "rADWarehouse_projectNo_addTxt", "", false);
            commonPutNormalSelectOpts(FIELD_WAREHOSE_OPERTYOPE, "rADWarehouse_operType_addTxt", "", true);
            $("#rADWarehouse_submitBtn").off('click');//必须先解绑事件，否则会重复绑定
            $("#rADWarehouse_submitBtn").click(function () {
                rADWarehouse_digSubmit({}, "add", index);
            });
            $("#rADWarehouse_submitBtn").show();
            layui.form.render();

        }
    });
}
function rADWarehouse_digSubmit(curData, operType, digIndex) {
    layui.form.on('submit(rADWarehouse_submitBtn)', function(data){// 绑定提交按钮点击回调事件，只有表单验证通过会进入
        layui.layer.confirm("是否确认提交？", function(index) {
            layui.layer.load();//loading
            let retData = commonAjax("rADWarehouse.do", JSON.stringify({
                "beanList": [{
                    "material_no": $("#rADWarehouse_materialNo_addTxt").val(),
                    "material_name": $("#rADWarehouse_materialName_addTxt").val(),
                    "number": $("#rADWarehouse_number_addTxt").val(),
                    "oper_type": $("#rADWarehouse_operType_addTxt").val(),
                    "desc": $("#rADWarehouse_desc_addTxt").val(),
                    "project_no": $("#rADWarehouse_projectNo_addTxt").val(),
                    "supplier": $("#rADWarehouse_supplier_addTxt").val(),
                    "supplier_type": $("#rADWarehouse_supplierType_addTxt").val(),
                    "proxy": $("#rADWarehouse_proxy_addTxt").val()
                }],
                "operType": operType,
                "paramMap": {

                }
            }));
            layui.layer.closeAll('loading');
            if (retData.retCode == HANDLE_SUCCESS) {
                commonOk("录入成功！");
                layui.layer.close(digIndex);
                rADWarehouse_queryOperation("1", FIELD_EACH_PAGE_NUM);
            } else {
                commonError("操作失败！" + retData.retMsg);
            }

        });
        return false;
    });
}
function rADWarehouse_cleanForm() {
    $("#rADWarehouse_materialNo_addTxt").removeAttr('disabled');
    $("#rADWarehouse_materialName_addTxt").removeAttr('disabled');
    $("#rADWarehouse_number_addTxt").removeAttr('disabled');
    $("#rADWarehouse_operType_addTxt").removeAttr('disabled');
    $("#rADWarehouse_desc_addTxt").removeAttr('disabled');
    $("#rADWarehouse_projectNo_addTxt").removeAttr('disabled');
    $("#rADWarehouse_supplier_addTxt").removeAttr('disabled');
    $("#rADWarehouse_supplierType_addTxt").removeAttr('disabled');
    $("#rADWarehouse_proxy_addTxt").removeAttr('disabled');
    layui.form.val("rADWarehouse_addFrm", { //formTest 即 class="layui-form" 所在元素属性 lay-filter="" 对应的值
        "rADWarehouse_serialNo_addTxt": "", // "name": "value"
        "rADWarehouse_materialNo_addTxt": "",
        "rADWarehouse_materialName_addTxt": "",
        "rADWarehouse_number_addTxt": '',
        "rADWarehouse_operType_addTxt": '',
        "rADWarehouse_desc_addTxt": '',
        "rADWarehouse_projectNo_addTxt": "",
        "rADWarehouse_supplier_addTxt": "",
        "rADWarehouse_supplierType_addTxt": "",
        "rADWarehouse_proxy_addTxt": ""
    });
}

function rADWarehouse_detail(data) {
    layui.layer.open({
        type: 1,// 页面层
        area: ['500px', '700px'],// 宽高
        title: '详情',// 标题
        content: $("#rADWarehouse_addDiv"),//内容，直接取dom对象
        // btn: ['确定'],
        // yes: function (index, layero) {
        //     //确认按钮的回调，提交表单
        // },
        success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
            // 渲染弹框元素
            rADWarehouse_cleanForm();
            // 加载项目列表
            let projectList = commonGetProjectList();

            commonPutNormalSelectOpts(projectList, "rADWarehouse_projectNo_addTxt", "", false);
            commonPutNormalSelectOpts(FIELD_WAREHOSE_OPERTYOPE, "rADWarehouse_operType_addTxt", "", true);
            $("#rADWarehouse_materialNo_addTxt").attr('disabled', true);
            $("#rADWarehouse_materialName_addTxt").attr('disabled', true);
            $("#rADWarehouse_number_addTxt").attr('disabled', true);
            $("#rADWarehouse_operType_addTxt").attr('disabled', true);
            $("#rADWarehouse_desc_addTxt").attr('disabled', true);
            $("#rADWarehouse_projectNo_addTxt").attr('disabled', true);
            $("#rADWarehouse_supplier_addTxt").attr('disabled', true);
            $("#rADWarehouse_supplierType_addTxt").attr('disabled', true);
            $("#rADWarehouse_proxy_addTxt").attr('disabled', true);
            layui.form.val("rADWarehouse_addFrm", { //formTest 即 class="layui-form" 所在元素属性 lay-filter="" 对应的值
                "rADWarehouse_serialNo_addTxt": data.serial_no, // "name": "value"
                "rADWarehouse_materialNo_addTxt": data.material_no,
                "rADWarehouse_materialName_addTxt": data.material_name,
                "rADWarehouse_number_addTxt": data.number,
                "rADWarehouse_operType_addTxt": data.oper_type,
                "rADWarehouse_desc_addTxt": data.desc,
                "rADWarehouse_projectNo_addTxt": data.project_no,
                "rADWarehouse_supplier_addTxt": data.supplier,
                "rADWarehouse_supplierType_addTxt": data.supplier_type,
                "rADWarehouse_proxy_addTxt": data.proxy
            });
            $("#rADWarehouse_submitBtn").hide();
            layui.form.render();

        }
    });
}