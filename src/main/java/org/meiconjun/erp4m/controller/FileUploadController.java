package org.meiconjun.erp4m.controller;

import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.service.ProjectManageService;
import org.meiconjun.erp4m.util.CommonUtil;
import org.meiconjun.erp4m.util.PropertiesUtil;
import org.meiconjun.erp4m.util.SerialNumberGenerater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 头像上传Controller
 * @date 2020/5/7 21:15
 */
@Controller
public class FileUploadController {
    private Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Resource(name = "projectManageService")
    private ProjectManageService projectManageService;
    /**
     * 上传用户头像
     * @param img
     * @param request
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "/uploadHeaderImg.do", method = RequestMethod.POST)
    public String headerUpload(@RequestParam(value = "file")MultipartFile img, HttpServletRequest request) throws IOException {
        String user_no = request.getParameter("user_no");//用户号
        if (CommonUtil.isStrBlank(user_no)) {
            user_no = "default";
        }
        String imgPath = PropertiesUtil.getProperty("fileSavePath")  + File.separator;
        String orgName = img.getOriginalFilename();
        String reNameFile = "userHeaderFile" + File.separator + user_no + File.separator + user_no + "_" +  SerialNumberGenerater.getInstance().generaterNextNumber() + "." + orgName.substring(orgName.lastIndexOf(".") + 1);

        File file = new File(imgPath, reNameFile);
        if (!file.exists()) {
            file.mkdirs();
        }

        //写入
        img.transferTo(file);
        HashMap<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("filePath", reNameFile);
        HashMap<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("code", SystemContants.HANDLE_SUCCESS);
        retMap.put("msg", "上传成功");
        retMap.put("data", dataMap);
        return CommonUtil.objToJson(retMap);
    }

    @ResponseBody
    @RequestMapping(value = "/projectDescFileUpload.do", method = RequestMethod.POST)
    public String fileUpload(@RequestParam(value = "file")MultipartFile img, HttpServletRequest request) throws IOException {
        String imgRootPath = PropertiesUtil.getProperty("fileSavePath")  + File.separator;
        String filePath = "";
        String file_root_path = "";
        filePath += "project" + File.separator + CommonUtil.getCurrentDateStr() + File.separator;
        file_root_path = filePath + request.getParameter("projectName") + File.separator;//项目文件存放根路径 /project/yyyyMMdd/paojectName/
        filePath = filePath + request.getParameter("projectName") + File.separator + "productDescDoc";
        String orgName = img.getOriginalFilename();
        filePath += File.separator + request.getParameter("projectName") + "_" + SerialNumberGenerater.getInstance().generaterNextNumber() + "." + orgName.substring(orgName.lastIndexOf(".") + 1);

        File file = new File(imgRootPath, filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        img.transferTo(file);
        HashMap<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("file_root_path", file_root_path);
        dataMap.put("filePath", filePath);
        dataMap.put("docName", orgName.substring(0, orgName.lastIndexOf(".") + 1));
        HashMap<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("code", SystemContants.HANDLE_SUCCESS);
        retMap.put("msg", "上传成功");
        retMap.put("data", dataMap);
        return CommonUtil.objToJson(retMap);
    }

    /**
     * 项目阶段文档上传
     * @param img
     * @param request
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "/projectStageDocUpload.do", method = RequestMethod.POST)
    public String projectStageDocUpload(@RequestParam(value = "file")MultipartFile img, HttpServletRequest request) throws IOException {
        HashMap<String, Object> retMap = new HashMap<String, Object>();

        String project_no = request.getParameter("project_no");
        String stage_num = request.getParameter("stage_num");
        String doc_version = request.getParameter("doc_version");
        String file_root_path = request.getParameter("file_root_path");
        String doc_serial = request.getParameter("doc_serial");
        String orgName = img.getOriginalFilename();

        String rootPath = PropertiesUtil.getProperty("fileSavePath")  + File.separator;
        String filePath = file_root_path;
        String this_version = "1.0";
        if (!CommonUtil.isStrBlank(doc_version)) {
            double doc_version_n = Double.valueOf(doc_version);
            doc_version_n = doc_version_n + 0.01;
            this_version = String.valueOf(doc_version_n);
        }
        filePath = filePath + "stage" + File.separator + stage_num + File.separator + this_version;
        filePath = filePath + File.separator + SerialNumberGenerater.getInstance().generaterNextNumber() + "_" + this_version + "." + orgName.substring(orgName.lastIndexOf(".") + 1);
        logger.info("阶段文件存放路径：" + filePath);
        // 存储文件路径
        RequestBean bean = new RequestBean();
        bean.setBeanList(null);
        bean.setOperType("uploadStageFile");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("project_no", project_no);
        paramMap.put("stage_num", stage_num);
        paramMap.put("doc_version", this_version);
        paramMap.put("doc_serial", doc_serial);
        paramMap.put("filePath", filePath);
        bean.setParamMap(paramMap);
        try {
            ResponseBean responseBean = projectManageService.excute(bean);
            if (!SystemContants.HANDLE_SUCCESS.equals(responseBean.getRetCode())) {
                throw new RuntimeException(responseBean.getRetMsg());
            }
            // 写入文件
            File file = new File(rootPath, filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            img.transferTo(file);
            HashMap<String, String> dataMap = new HashMap<String, String>();
            dataMap.put("filePath", filePath);
            dataMap.put("docName", orgName.substring(0, orgName.lastIndexOf(".") + 1));

            retMap.put("code", SystemContants.HANDLE_SUCCESS);
            retMap.put("msg", "上传成功");
            retMap.put("data", dataMap);
            return CommonUtil.objToJson(retMap);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            retMap.put("code", SystemContants.HANDLE_FAIL);
            retMap.put("msg", "上传失败：" + e.getMessage());
            return CommonUtil.objToJson(retMap);
        }
    }
}
