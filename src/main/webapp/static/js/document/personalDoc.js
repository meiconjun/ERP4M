var personalDoc_curr = 1;
var personalDoc_count = 0;
var personalDoc_tableIns;
var personalDoc_fileUploadInst;
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
                        return commonFormatDate(data.upload_date);
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
                    field: 'doc_version',
                    title: '最新版本',
                    sort: true ,
                    align : 'center',
                    templet : function (data) {
                        let html = "";
                        if (buttonStr.indexOf("personalDoc_versionHis") != -1) {
                            html += "<a id='personalDoc_versionHis' onclick='personalDoc_versionHis(" + commonFormatObj(data) + ")'>" + data.doc_version + "</a>"
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
                        let html = "<a id='personalDoc_detail' onclick='personalDoc_detail(" + commonFormatObj(data) + ")'>" + data.doc_version + "</a>";
                        return html;
                    }}
            ]]
        });

        commonPutNormalSelectOpts(FIELD_DOC_TYPE, "personalDoc_docType", "", false);

        layui.form.render();// 此步是必须的，否则无法渲染一些表单元素

        //查询
        $("#personalDoc_queryBtn").click(function () {
            personalDoc_queryOperation('1', FIELD_EACH_PAGE_NUM);
        });
        //新增
        $("#personalDoc_addBtn").click(function () {
            personalDoc_addOperation();
        });
        // 修改
        $("#personalDoc_modifyBtn").click(function () {
            personalDoc_modifyOperation();
        });
        //删除
        $("#personalDoc_deleteBtn").click(function () {
            personalDoc_deleteOperation();
        });
        //提交评审
        $("#personalDoc_Submitpreview").click(function () {
            personalDoc_SubmitpreviewOperation();
        });

        // 权限控制
        if (buttonStr.indexOf("personalDoc_queryBtn") == -1) {
            $("#personalDoc_queryBtn").hide();
        }
        if (buttonStr.indexOf("personalDoc_addBtn") == -1) {
            $("#personalDoc_addBtn").hide();
        }
        if (buttonStr.indexOf("personalDoc_modifyBtn") == -1) {
            $("#personalDoc_modifyBtn").hide();
        }
        if (buttonStr.indexOf("personalDoc_deleteBtn") == -1) {
            $("#personalDoc_deleteBtn").hide();
        }
        if (buttonStr.indexOf("personalDoc_Submitpreview") == -1) {
            $("#personalDoc_Submitpreview").hide();
        }
        // 绑定重置表格事件
        commonResizeTable('personalDoc_tableObj');
    } catch (e) {
        console.error(e.message, e);
        commonError(e.message);
    }
});

function personalDoc_queryOperation(curPage, limit) {
    let doc_no = $("#personalDoc_docNo").val();
    let doc_name = $("#personalDoc_docName").val();
    let upload_user = $("#personalDoc_uploadUser").val();
    let doc_type = $("#personalDoc_docType").val();

    personalDoc_curr = curPage;
    personalDoc_tableIns.reload({
        where: { //设定异步数据接口的额外参数，任意设
            message : JSON.stringify({
                "beanList" : [{
                    "doc_no" : doc_no,
                    "doc_name" : doc_name,
                    "upload_user" : upload_user,
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
 * 新增
 */
function personalDoc_addOperation() {
    layui.layer.open({
        type: 1,// 页面层
        area: ['500px', '700px'],// 宽高
        title: '新增文档',// 标题
        content: $("#personalDoc_addDiv"),//内容，直接取dom对象
        // btn: ['确定'],
        // yes: function (index, layero) {
        //     //确认按钮的回调，提交表单
        // },
        success: function (layero, index) {//层弹出后的成功回调方法(当前层DOM,当前层索引)
            // 渲染弹框元素
            personalDoc_cleanForm();
            let dialogIndex = index;// 弹框层索引
            commonPutNormalSelectOpts(FIELD_DOC_LANGUAGE, "personalDoc_docLanguage_addTxt", "", true);
            commonPutNormalSelectOpts(FIELD_DOC_TYPE, "personalDoc_docType_addTxt", "", true);

            personalDoc_digSubmit(dialogIndex, "add");

            layui.form.render();

        }
    });
}

function personalDoc_digSubmit(dialogIndex, operType) {
    layui.form.on('submit(personalDoc_submitBtn)', function(data){// 绑定提交按钮点击回调事件，只有表单验证通过会进入
        layui.layer.confirm("是否确认提交？", function(index) {
            let fieldObj = data.field;// 表单字段集合
            let retData = commonAjax("personalDoc.do", JSON.stringify({
                "beanList" : [{
                    "doc_no" : fieldObj.personalDoc_docNo_addTxt,
                    "doc_name" : fieldObj.personalDoc_docName_addTxt,
                    "doc_language" : fieldObj.personalDoc_docLanguage_addTxt,
                    "doc_type" : fieldObj.personalDoc_docType_addTxt,
                    "doc_writer" : fieldObj.personalDoc_docWriter_addTxt,
                    "doc_desc" : fieldObj.personalDoc_docDesc_addTxt
                }],
                "operType" : operType,
                "paramMap" : {}
            }));
            if (retData.retCode == HANDLE_SUCCESS) {
                commonOk("操作成功");
                layui.layer.close(dialogIndex);
                personalDoc_queryOperation('1', FIELD_EACH_PAGE_NUM);
            } else {
                commonError(retData.retMsg);
            }
        });
        return false;
    });
}
/**
 * 版本历史
 * @param data
 */
function personalDoc_versionHis(data) {

}

/**
 * 文档详情
 * @param data
 */
function personalDoc_detail(data) {

}

function personalDoc_cleanForm() {
    $("#personalDoc_docNo_addTxt").removeAttr('disabled');
    $("#personalDoc_docName_addTxt").removeAttr('disabled');
    $("#personalDoc_docLanguage_addTxt").removeAttr('disabled');
    $("#personalDoc_docType_addTxt").removeAttr('disabled');
    $("#personalDoc_docWriter_addTxt").removeAttr('disabled');
    $("#personalDoc_docDesc_addTxt").removeAttr('disabled');
    layui.form.val("personalDoc_addFrm", { //formTest 即 class="layui-form" 所在元素属性 lay-filter="" 对应的值
        "personalDoc_docNo_addTxt": "", // "name": "value"
        "personalDoc_docName_addTxt": "",
        "personalDoc_docLanguage_addTxt": '',
        "personalDoc_docType_addTxt": '',
        "personalDoc_docWriter_addTxt": '',
        "personalDoc_docDesc_addTxt": ""
    });
}

function personalDoc_initUploadInst(data) {
    personalDoc_fileUploadInst = layui.upload.render({
        elem: '#personalDoc_selectFile',
        url: 'docUpload.do',//改成您自己的上传接口
        data: {
            "user_no": sessionStorage.getItem("user_no"),
            "doc_type": function () {
                return $("#personalDoc_docType_addTxt").val();
            },
            "doc_no": function () {
                return $("#personalDoc_docNo_addTxt").val();
            },
            "file_root_path": data.file_root_path,
            "doc_serial": stageList[i].doc_serial
        },
        accept: 'file',//只允许上传图片
        auto: false,// 选择文件后是否自动上传
        // bindAction: "#createProject_fileSubmit",
        multiple: false,// 是否允许多文件上传
        choose: function(obj) {
            //预读本地文件,如果是多文件，则会遍历
            obj.preview(function (index, file, result) {
                let confirmStr = commonBlank(doc_version) ? "是否确认提交文件[" + file.name + "]？当前阶段文档未定版，上传后将覆盖原先文件" : "是否确认提交文件[" + file.name + "]？当前阶段文档最新版本为[" + doc_version + "]";
                layui.layer.confirm(confirmStr, function(index) {
                    layui.layer.load();//loading
                    fileUploadInst.upload();
                });
            });
        },
        done: function(res, index, upload){
            if (res.code == '0') {
                layui.layer.closeAll('loading');
                commonOk("上传阶段文档成功");
            } else {
                layui.layer.closeAll('loading');
                commonError("上传阶段文档失败:" + res.msg)
            }
        },
        error: function (index, upload) {
            layui.layer.closeAll('loading');
            commonError("上传阶段文档失败，请稍后重试");
        }
    });
}