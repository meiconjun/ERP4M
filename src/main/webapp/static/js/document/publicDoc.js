var publicDoc_curr = 1;
var publicDoc_tableIns;
var publicDoc_buttonStr;
$(document).ready(function () {
    try {
        // 获取按钮权限
        let buttonMap = commonGetAuthField('D03000');
        publicDoc_buttonStr = buttonMap.buttonStr;

        publicDoc_tableIns = layui.table.render({
            id : "publicDoc_tableObj",
            elem: '#publicDoc_table',
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
                        if (publicDoc_buttonStr.indexOf("publicDoc_versionHis") != -1) {
                            html += "<a title='点击查看版本历史' class=\"layui-btn layui-btn-xs\" name='publicDoc_versionHis' onclick='publicDoc_versionHis(" + commonFormatObj(data) + ")'>" + data.doc_version + "</a>";
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
                        let html ="";
                        if (publicDoc_buttonStr.indexOf("publicDoc_detail") != -1) {
                            html = "<a class=\"layui-btn layui-btn-xs layui-btn-normal \" name='personalDoc_detail' onclick='publicDoc_detail(" + commonFormatObj(data) + ")'>详情</a>";
                        }
                        return html;
                    }}
            ]]
        });
        commonPutNormalSelectOpts(FIELD_DOC_TYPE, "publicDoc_docType", "", false);
        layui.form.render();// 此步是必须的，否则无法渲染一些表单元素

        //查询
        $("#publicDoc_queryBtn").click(function () {
            publicDoc_queryOperation('1', FIELD_EACH_PAGE_NUM);
        });
        // 权限控制
        if (publicDoc_buttonStr.indexOf("publicDoc_queryBtn") == -1) {
            $("#publicDoc_queryBtn").hide();
        }

        // 绑定重置表格事件
        commonResizeTable('publicDoc_tableObj');
    } catch (e) {
        console.error(e.message, e);
        commonError(e.message);
    }
});

function publicDoc_queryOperation(curPage, limit) {
    let doc_no = $("#publicDoc_docNo").val();
    let doc_name = $("#publicDoc_docName").val();
    let create_user = $("#publicDoc_createUser").val();
    let doc_type = $("#publicDoc_docType").val();

    publicDoc_curr = curPage;
    publicDoc_tableIns.reload({
        where: { //设定异步数据接口的额外参数，任意设
            message : JSON.stringify({
                "beanList" : [{
                    "doc_no" : doc_no,
                    "doc_name" : doc_name,
                    "create_user" : create_user,
                    "doc_type" : doc_type
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

/**
 * 文档详情
 * @param data
 */
function publicDoc_detail(data) {
    layui.layer.open({
        type: 1,// 页面层
        area: ['500px', '700px'],// 宽高
        title: '文档详情',// 标题
        content: $("#publicDoc_addDiv"),//内容，直接取dom对象
        // btn: ['确定'],
        // yes: function (index, layero) {
        //     //确认按钮的回调，提交表单
        // },
        success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
            let cusData = data;
            let postFix = cusData.file_path.substring(cusData.file_path.lastIndexOf("."), cusData.file_path.length);
            // 渲染弹框元素
            commonPutNormalSelectOpts(FIELD_DOC_LANGUAGE, "publicDoc_docLanguage_addTxt", cusData.doc_language, true);
            commonPutNormalSelectOpts(FIELD_DOC_TYPE, "publicDoc_docType_addTxt", cusData.doc_type, true);
            layui.form.val("publicDoc_addFrm", { //formTest 即 class="layui-form" 所在元素属性 lay-filter="" 对应的值
                "publicDoc_docNo_addTxt": cusData.doc_no, // "name": "value"
                "publicDoc_docName_addTxt": cusData.doc_name,
                "publicDoc_fileNameTxt": cusData.doc_no + postFix,
                "publicDoc_docLanguage_addTxt": cusData.doc_language,
                "publicDoc_docType_addTxt": cusData.doc_type,
                "publicDoc_docWriter_addTxt": cusData.doc_writer,
                "publicDoc_docDesc_addTxt": cusData.doc_desc
            });
            if (publicDoc_buttonStr.indexOf("publicDoc_downLoad") != -1) {
                $("#publicDoc_downLoad").show();
            } else {
                $("#publicDoc_downLoad").hide();
            }
            $("#publicDoc_downLoad").click(function () {
                commonFileDownload(cusData.doc_no + postFix, cusData.file_path);
            });
            layui.form.render();
        }
    });
}

/**
 * 文档版本历史
 */
function publicDoc_versionHis(data) {
    layui.layer.open({
        type: 1,// 页面层
        area: ['700px', '500px'],// 宽高
        title: '文档版本历史',// 标题
        content: $("#publicDoc_docDetail_dialogDiv"),//内容，直接取dom对象
        // btn: ['确定'],
        // yes: function (index, layero) {
        //     //确认按钮的回调，提交表单
        // },
        success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
            layui.table.render({
                id: 'publicDoc_docDetail_tableObj',
                elem: '#publicDoc_docDetail_table',
                height: 430,
                url: 'publicDoc.do',
                where: {
                    message: JSON.stringify({
                        "beanList": [{

                        }],
                        "operType": "getDocHistory",
                        "paramMap": {
                            "doc_serial_no": data.doc_serial_no

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

                },
                parseData: function(res){ //res 即为原始返回的数据
                    return {
                        "code": res.retCode, //解析接口状态
                        "msg": res.retMsg, //解析提示文本
                        "count": res.retMap.total, //解析数据长度
                        "data": res.retMap.docList //解析数据列表
                    };
                },
                cols : [[
                    {
                        field: 'doc_name',
                        title: '文档名称',
                        align : 'center'
                    },
                    {
                        field: 'doc_version',
                        title: '文档版本',
                        sort: true,
                        align : 'center'
                    }
                    , { field: 'doc_writer',
                        title: '作者',
                        align : 'center'
                    }
                    , { field: 'upload_user',
                        title: '上传用户',
                        align : 'center',
                        templet : function (data) {
                            return commonFormatUserNo(data.upload_user, false);
                        }
                    }
                    , {
                        field: 'upload_time',
                        title: '上传时间',
                        sort: true,
                        align : 'center',
                        templet : function (data) {
                            return commonFormatDate(data.upload_time);
                        }
                    }
                    , {
                        field: 'update_desc',
                        title: '更新备注',
                        sort: true,
                        align : 'center'
                    }
                    , {
                        field: 'edit',
                        title: '操作',
                        width: 80,
                        sort: false,
                        fixed: 'right',
                        align : 'center',
                        templet : function (data) {
                            let html = "";
                            html += "<a class=\"layui-btn layui-btn-xs\" onclick=\"publicDoc_downloadVersionDoc(" + commonFormatObj(data) + ")\">下载</a>";
                            return html;
                        }}
                ]]
            });
        }
    });
}

/**
 * 下载历史文档
 * @param data
 */
function publicDoc_downloadVersionDoc(data) {
    let postFix = data.file_path.substring(data.file_path.lastIndexOf("."), data.file_path.length);
    commonFileDownload((data.doc_name + "_" + data.doc_version + postFix), data.file_path);
}