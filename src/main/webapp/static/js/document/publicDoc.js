var publicDoc_curr = 1;
var publicDoc_tableIns;
$(document).ready(function () {
    try {
        // 获取按钮权限
        let buttonMap = commonGetAuthField('D03000');
        let buttonStr = buttonMap.buttonStr;

        publicDoc_tableIns = layui.table.render({
            id : "publicDoc_docDetail_tableObj",
            elem: '#publicDoc_docDetail_table',
            height: 'full-450',
            url: 'publicDoc.do',
            where : {
                message : JSON.stringify({
                    "beanList" : [{
                        "doc_no" : "",
                        "doc_name" : "",
                        "create_user" : "",
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
                    elem : 'publicDoc_page',
                    limit : Number(FIELD_EACH_PAGE_NUM),
                    groups : 5,
                    curr : publicDoc_curr,
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
                            publicDoc_queryOperation(obj.curr, obj.limit);// 重载页面
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
                    field: 'doc_no',
                    title: '文档编号',
                    sort: true,
                    align : 'center'
                },
                {
                    field: 'doc_name',
                    title: '文档名称',
                    align : 'center'
                }
                , { field: 'upload_user',
                    title: '上传用户',
                    align : 'center',
                    templet : function (data) {
                        return commonFormatUserNo(data.upload_user, false);
                    }}
                , {
                    field: 'upload_time',
                    title: '上传时间',
                    align : 'center',
                    templet : function (data) {
                        return commonFormatDate(data.upload_time);
                    }
                }
                , {
                    field: 'doc_type',
                    title: '文档类别',
                    align : 'center',
                    templet : function (data) {
                        return commonFormatValue(FIELD_DOC_TYPE, data.doc_type, false);
                    }
                }
                , {
                    field: 'review_state',
                    title: '文档状态',
                    align : 'center',
                    templet : function (data) {
                        return commonFormatValue(FIELD_DOC_STATE, data.review_state, false);
                    }
                }
                , {
                    field: 'doc_version',
                    title: '最新版本',
                    width : 110,
                    sort: true ,
                    align : 'center',
                    templet : function (data) {
                        let html = "";
                        if (buttonStr.indexOf("personalDoc_versionHis") != -1) {
                            html += "<a title='点击查看版本历史' class=\"layui-btn layui-btn-xs\" name='personalDoc_versionHis' onclick='personalDoc_versionHis(" + commonFormatObj(data) + ")'>" + data.doc_version + "</a>";
                        } else {
                            return data.doc_version;
                        }
                        return html;
                    }
                }
                , {
                    field: 'edit',
                    title: '操作',
                    width : 80,
                    fixed: 'right',
                    align : 'center',
                    templet : function (data) {
                        let html = "<a class=\"layui-btn layui-btn-xs layui-btn-normal \" name='personalDoc_detail' onclick='personalDoc_detail(" + commonFormatObj(data) + ")'>详情</a>";
                        return html;
                    }}
            ]]
        });
    } catch (e) {
        console.error(e.message, e);
        commonError(e.message);
    }
});