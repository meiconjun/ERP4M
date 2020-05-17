package org.meiconjun.erp4m.controller;

import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.util.CommonUtil;
import org.meiconjun.erp4m.util.PropertiesUtil;
import org.meiconjun.erp4m.util.SerialNumberGenerater;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 头像上传Controller
 * @date 2020/5/7 21:15
 */
@Controller
public class FileUploadController {
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
        filePath += "project" + File.separator + CommonUtil.getCurrentDateStr() + File.separator;
        filePath = filePath + request.getParameter("projectName") + File.separator + "productDescDoc";
        String orgName = img.getOriginalFilename();
        filePath += File.separator + request.getParameter("projectName") + "_" + SerialNumberGenerater.getInstance().generaterNextNumber() + "." + orgName.substring(orgName.lastIndexOf(".") + 1);

        File file = new File(imgRootPath, filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        img.transferTo(file);
        HashMap<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("filePath", filePath);
        dataMap.put("docName", orgName.substring(0, orgName.lastIndexOf(".") + 1));
        HashMap<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("code", SystemContants.HANDLE_SUCCESS);
        retMap.put("msg", "上传成功");
        retMap.put("data", dataMap);
        return CommonUtil.objToJson(retMap);
    }
}
