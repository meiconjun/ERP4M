package org.meiconjun.erp4m.controller;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.poi.ss.usermodel.Workbook;
import org.meiconjun.erp4m.dao.CommonDao;
import org.meiconjun.erp4m.dao.ProjectManageDao;
import org.meiconjun.erp4m.util.CommonUtil;
import org.meiconjun.erp4m.util.ExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 导出操作controller
 * @date 2020/6/26 16:59
 */
@Controller
public class ExportController {
    private Logger logger = LoggerFactory.getLogger(ExportController.class);

    @Resource
    private ProjectManageDao projectManageDao;
    @Resource
    private CommonDao commonDao;

    @RequestMapping(value = "/exportExcel.do", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String operType = request.getParameter("operType");
        String fileName = request.getParameter("fileName");
        Workbook workbook = null;
        if ("projectMember".equals(operType)) {
            logger.info("====================导出项目成员表=====================");
            workbook = getProjectMemberExcel(request);
        }
        OutputStream outputStream = null;
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/x-download;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            outputStream = response.getOutputStream();
            workbook.write(outputStream);
        } catch (IOException e) {
            logger.error("写入response输出流失败！" + e.getMessage(), e);
        } finally {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
            workbook.close();
        }

    }

    /**
     * 导出项目成员表
     * @param request
     * @return
     */
    private Workbook getProjectMemberExcel(HttpServletRequest request) {
        String project_no = request.getParameter("project_no");
        HashMap<String, Object> condMap = new HashMap<String, Object>();
        condMap.put("project_no", project_no);
        HashMap<String, String> projectMap = projectManageDao.selectProjectInfo(condMap).get(0);
        String[] project_menbers = projectMap.get("project_menbers").split(",");
        List<HashMap<String, String>> userInfo = commonDao.selectAllUserAndRoleInfo();

        List<String[]> dataList = new ArrayList<>();
        List<String> headList = new ArrayList<>();
        headList.add("用户号");
        headList.add("用户名");
        headList.add("部门");
        headList.add("职位");

        for (String member : project_menbers) {
            for (HashMap<String, String> userMap : userInfo) {
                String user_no = userMap.get("user_no");
                if (member.equals(user_no)) {
                    String[] tempStrList = new String[]{member, userMap.get("user_name"), CommonUtil.getFieldName("S010200", userMap.get("department")), CommonUtil.getFieldName("S010100", userMap.get("position"))};
                    dataList.add(tempStrList);
                    continue;
                }
            }
        }

        Workbook workbook = ExcelUtil.exportExcel(headList, dataList);
        return workbook;
    }
}
