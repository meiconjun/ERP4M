package org.meiconjun.erp4m.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.meiconjun.erp4m.bean.BugBean;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.dao.BugListDao;
import org.meiconjun.erp4m.service.BugListService;
import org.meiconjun.erp4m.util.CommonUtil;
import org.meiconjun.erp4m.util.PropertiesUtil;
import org.meiconjun.erp4m.util.SerialNumberGenerater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/8/22 22:24
 */
@Service("bugListServiceImpl")
@Transactional
public class BugListServiceImpl implements BugListService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private BugListDao bugListDao;


    @Override
    public ResponseBean excute(RequestBean requestBean) throws Exception {
        String operType = requestBean.getOperType();
        ResponseBean responseBean = new ResponseBean();
        if ("query".equals(operType)) {
            queryOperation(requestBean, responseBean);
        } else if ("add".equals(operType)) {
            addOperation(requestBean, responseBean);
        } else if ("getUserInfo".equals(operType)) {
            getUserInfoOperation(requestBean, responseBean);
        } else if ("queryComments".equals(operType)) {
            queryCommentsOperation(requestBean, responseBean);
        } else if ("comment".equals(operType)) {
            commentOperation(requestBean, responseBean);
        } else if ("getFloorCommentHistory".equals(operType)) {
            getFloorCommentHistoryOperation(requestBean, responseBean);
        } else if ("updateContent".equals(operType)) {
            updateContentOperation(requestBean, responseBean);
        }
        return responseBean;
    }

    /**
     * 更新bug内容
     * @param requestBean
     * @param responseBean
     */
    private void updateContentOperation(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, Object> paramMap = requestBean.getParamMap();
        String serial_no = (String) paramMap.get("serial_no");
        String content = (String) paramMap.get("content");
        String user_no = CommonUtil.getLoginUser().getUser_no();
        String last_modi_time = CommonUtil.getCurrentTimeStr();

        HashMap<String, Object> condMap = new HashMap<>();
        condMap.put("serial_no", serial_no);
        condMap.put("content", content);
        condMap.put("user_no", user_no);
        condMap.put("last_modi_time", last_modi_time);

        bugListDao.updateBugContent(condMap);
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);

    }

    /**
     * 获取指定楼层的楼中楼回复
     * @param requestBean
     * @param responseBean
     */
    private void getFloorCommentHistoryOperation(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, Object> paramMap = requestBean.getParamMap();
        String serial_no = (String) paramMap.get("serial_no");

        List<HashMap<String, Object>> list = bugListDao.selectFloorComment(serial_no);

        Map retMap = new HashMap();
        retMap.put("list", list);
        responseBean.setRetMap(retMap);
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
    }

    /**
     * 新增评论
     * @param requestBean
     * @param responseBean
     */
    private void commentOperation(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, Object> paramMap = requestBean.getParamMap();
        String bug_serial = (String) paramMap.get("bug_serial");
        boolean isCommentOtherUser = (boolean) paramMap.get("isCommentOtherUser");
        String content = (String) paramMap.get("content");
        String about_serial = (String) paramMap.get("about_serial");
        String about_user = (String) paramMap.get("about_user");
        String reply_user = CommonUtil.getLoginUser().getUser_no();
        String reply_time = CommonUtil.getCurrentTimeStr();
        String floor = isCommentOtherUser ? "0": "";
        String serial_no = SerialNumberGenerater.getInstance().generaterNextNumber();

        HashMap<String, Object> condMap = new HashMap<>();
        condMap.put("serial_no", serial_no);
        condMap.put("bug_serial", bug_serial);
        condMap.put("content", content);
        condMap.put("reply_user", reply_user);
        condMap.put("reply_time", reply_time);
        condMap.put("about_serial", about_serial);
        condMap.put("about_user", about_user);
        condMap.put("floor", floor);

        int effect = bugListDao.insertNewComment(condMap);
        if (effect > 0) {
            responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        } else {
            responseBean.setRetCode(SystemContants.HANDLE_FAIL);
            responseBean.setRetMsg("未知错误");
        }
    }

    /**
     * 查询BUG评论信息
     */
    private void queryCommentsOperation(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, Object> paramMap = requestBean.getParamMap();
        int curPage = Integer.parseInt((String)paramMap.get("curPage"));// 当前页码
        int limit = Integer.parseInt((String) paramMap.get("limit")) ;// 每页数量
        String serial_no = (String) paramMap.get("serial_no");// 每页数量

        Page page = PageHelper.startPage(curPage, limit);
        List<HashMap<String, String>> list = bugListDao.selectBugComments(serial_no, null);
        long total = page.getTotal();// 总条数
        ListIterator li = list.listIterator();
        // 将每个回复人的头像转为base64字符串
        while (li.hasNext()) {
            HashMap<String, String> map = (HashMap<String, String>)li.next();
            String picture = PropertiesUtil.getProperty("fileSavePath") + map.get("picture");
            File file = new File(picture);
            String imgBase64 = null;
            try {
                imgBase64 = CommonUtil.fileToBase64(file);
            } catch (IOException e) {
                logger.error("加载回复人[" + map.get("user_no") + "]头像异常：", e);
            }
            map.put("picture", imgBase64);
            li.set(map);
        }
        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("total", total);
        retMap.put("list", list);
        responseBean.setRetMsg("查询成功");
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        responseBean.setRetMap(retMap);
    }

    private void getUserInfoOperation(RequestBean requestBean, ResponseBean responseBean){
        Map<String, Object> paramMap = requestBean.getParamMap();
        String user_no = (String) paramMap.get("user_no");

        HashMap<String, String> userMap = bugListDao.selectUserInfoByNo(user_no);
        String url = PropertiesUtil.getProperty("fileSavePath") + (String) userMap.get("picture");
        File file = new File(url);
        String imgBase64 = null;
        try {
            imgBase64 = CommonUtil.fileToBase64(file);
        } catch (IOException e) {
            logger.error("加载发帖人头像异常：" + e.getMessage(), e);
        }
        userMap.put("picture", imgBase64);
        Map retMap = new HashMap();
        retMap.put("userInfo", userMap);
        responseBean.setRetMap(retMap);
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
    }

    private void addOperation(RequestBean requestBean, ResponseBean responseBean) {
        BugBean bugBean = (BugBean) requestBean.getBeanList().get(0);
        // 设置必要属性
        bugBean.setSerial_no(SerialNumberGenerater.getInstance().generaterNextNumber());
        bugBean.setBug_status("0");//BUG状态 0-未解决
        bugBean.setCreate_user(CommonUtil.getLoginUser().getUser_no());
        bugBean.setLast_modi_time(CommonUtil.getCurrentTimeStr());

        int effect = bugListDao.insertBugList(bugBean);
        if (effect > 0) {
            responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        } else {
            responseBean.setRetCode(SystemContants.HANDLE_FAIL);
            responseBean.setRetMsg("新增Bug信息失败");
        }
    }

    private void queryOperation(RequestBean requestBean, ResponseBean responseBean) {
        BugBean bugBean = (BugBean) requestBean.getBeanList().get(0);
        Map<String, Object> paramMap = requestBean.getParamMap();

        int curPage = Integer.parseInt((String) paramMap.get("curPage"));// 当前页码
        int limit = Integer.parseInt((String) paramMap.get("limit"));// 每页数量

        Page page = PageHelper.startPage(curPage, limit);
        List<HashMap<String, Object>> list = bugListDao.selectBugList(bugBean);

        long total = page.getTotal();// 总条数
        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("total", total);
        retMap.put("list", list);
        responseBean.setRetMsg("查询成功");
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        responseBean.setRetMap(retMap);
    }
    
    
}
