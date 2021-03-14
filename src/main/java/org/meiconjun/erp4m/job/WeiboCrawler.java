package org.meiconjun.erp4m.job;

import org.meiconjun.erp4m.dao.WeiboDao;
import org.meiconjun.erp4m.dto.RetObj;
import org.meiconjun.erp4m.interceptor.SpringContextHolder;
import org.meiconjun.erp4m.util.CommonUtil;
import org.meiconjun.erp4m.util.SendRequestUtil;
import org.springframework.stereotype.Component;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 无内鬼爬虫
 * @date 2021/3/2 22:23
 */
@Component
public class WeiboCrawler {
    private Document document = null;

    private static Logger logger = LoggerFactory.getLogger(WeiboCrawler.class);

    private static String basePath = File.separator + "usr" + File.separator + "local" + File.separator + "dddsx" + File.separator;
    @Resource
    private WeiboDao weiboDao;

    public void execute() throws InterruptedException {
        logger.info("==========================无内鬼爬虫启动===================");
        // 关闭httpclient的debug日志
//        System.setProperty("org.apache.commons.logging.LogFactory", "org.apache.commons.logging.impl.LogFactoryImpl");
//        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
//        LogFactory.getFactory().setAttribute("org.apache.commons.logging.simplelog.defaultlog", "error");
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (weiboDao == null) {
                            weiboDao = SpringContextHolder.getBean("weiboDao");
                        }
                        // 通用头信息
                        HashMap<String, String> baseHeader = new HashMap();
                        //		baseHeader.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.190 Safari/537.36");
                        baseHeader.put("X-Requested-With", "XMLHttpRequest");
                        baseHeader.put("Accept-Encoding", "gzip, deflate, br");
                        baseHeader.put("Accept", "application/json, text/plain, */*");
                        baseHeader.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
                        baseHeader.put("Host", "m.weibo.cn");
                        baseHeader.put("Connection", "keep-alive");
                        baseHeader.put("Sec-Fetch-Dest", "empty");
                        baseHeader.put("Sec-Fetch-Site", "same-origin");
                        baseHeader.put("Sec-Fetch-Mode", "cors");
                        baseHeader.put("MWeibo-Pwa", "1");
                        // api/config
                        HashMap<String, String> header1 = new HashMap();
                        header1.putAll(baseHeader);
                        header1.put("Referer", "https://m.weibo.cn/u/3176010690?uid=3176010690&luicode=10000011&lfid=231093_-_selffollowed");
                        header1.put("X-XSRF-TOKEN", "d6f36e");
                        header1.put("Cookie", "_T_WM=84643847835; MLOGIN=1; SCF=AmyxoxZLV1FkQo5mEb8fJvmUR61v2MffWrgrsU0sXSK4fK2pePz0_jsStai4VpOgKufxN9pNU_mO_Lm3Cja5J64.; SUB=_2A25NPk4-DeRhGeFL7VoU9ifIyTuIHXVuwVJ2rDV6PUJbktANLXfmkW1Nfd05wwizu2VI1hWUpBnOGzPvNLFvdgRz; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9W552hceaqvuqXW58NuY.hxL5NHD95QNSKqRSKq4ShzNWs4DqcjMi--NiK.Xi-2Ri--ciKnRi-zNSh2NSKqESKn4S5tt; SSOLoginState=1614429806; XSRF-TOKEN=d6f36e; WEIBOCN_FROM=1110106030; M_WEIBOCN_PARAMS=luicode%3D10000011%26lfid%3D231093_-_selffollowed%26fid%3D1076033176010690%26uicode%3D10000011");

                        String url1 = "https://m.weibo.cn/api/config";
                        RetObj reObj1 = SendRequestUtil.doGet(url1, header1, null, getRandomMillis());
                        if (!"0".equals(reObj1.getRetCode())) {
                            throw new RuntimeException("请求失败:" + url1);
                        }
                        String retStr1 = (String) reObj1.getRetMap().get("retStr");
                        HashMap<String, Object> configMap = (HashMap<String, Object>) CommonUtil.jsonToObjByJackson(retStr1, HashMap.class);
                        if (!(1 == (int)configMap.get("ok"))) {
                            throw new RuntimeException("请求Config失败");
                        }
                        // 获取cookie
                        Header[] headers1 = (Header[]) reObj1.getRetMap().get("headers");
                        String curToken = getHeaderXSRFTOKEN(headers1);
                        String curCookie = getCookie(curToken, getMWEIBOCNPARAMS(headers1));

                        header1.put("Referer", "https://m.weibo.cn/u/3176010690?uid=3176010690&luicode=10000011&lfid=231093_-_selffollowed");
                        header1.put("X-XSRF-TOKEN", curToken);
                        header1.put("Cookie", curCookie);

                        HashMap<String, String> condMap2 = new HashMap();
                        condMap2.put("uid", "3176010690");
//		condMap2.put("t", "0");
                        condMap2.put("luicode", "10000011");
                        condMap2.put("lfid", "231093_-_selffollowed");
                        condMap2.put("type", "uid");
                        condMap2.put("value", "3176010690");
                        condMap2.put("containerid", "1005053176010690");

                        String url11 = "https://m.weibo.cn/api/container/getIndex";
                        RetObj reObj = SendRequestUtil.doGet(url11, header1, condMap2, getRandomMillis());
                        if (!"0".equals(reObj.getRetCode())) {
                            throw new RuntimeException("请求失败:" + url11);
                        }
                        String retStr = (String) reObj.getRetMap().get("retStr");
                        HashMap<String, Object> retMapMainPage = (HashMap<String, Object>) CommonUtil.jsonToObjByJackson(retStr, HashMap.class);
                        if (!(1 == (int)retMapMainPage.get("ok"))) {
                            throw new RuntimeException("获取孙狗主页微博数据1失败");
                        }
                        Header[] rheaders2 = (Header[]) reObj.getRetMap().get("headers");
                        curToken = getHeaderXSRFTOKEN(rheaders2);
                        curCookie = getCookie(curToken, getMWEIBOCNPARAMS(rheaders2));
                        /**
                         * 获取孙狗主页面的微博数据
                         */
                        condMap2.put("containerid", "1076033176010690");

                        Map<String, String> headers2 = new HashMap<String, String>();
                        headers2.putAll(baseHeader);
                        headers2.put("Cookie", curCookie);
                        headers2.put("Referer", "https://m.weibo.cn/u/3176010690?uid=3176010690&luicode=10000011&lfid=231093_-_selffollowed");
                        headers2.put("X-XSRF-TOKEN", curToken);
                        headers2.put("X-Log-Uid", "7568569427");
//		System.out.println(CommonUtil.formatJson(CommonUtil.objToJson(headers2)));
                        String url2 = "https://m.weibo.cn/api/container/getIndex";

                        reObj = SendRequestUtil.doGet(url2, headers2, condMap2, getRandomMillis());
                        if (!"0".equals(reObj.getRetCode())) {
                            throw new RuntimeException("请求失败:" + url2);
                        }
                        retStr = (String) reObj.getRetMap().get("retStr");
                        retMapMainPage = (HashMap<String, Object>) CommonUtil.jsonToObjByJackson(retStr, HashMap.class);
                        if (!(1 == (int)retMapMainPage.get("ok"))) {
                            throw new RuntimeException("获取孙狗主页微博数据2失败");
                        }
                        // 获取cookie
                        rheaders2 = (Header[]) reObj.getRetMap().get("headers");

                        curToken = getHeaderXSRFTOKEN(rheaders2);
                        curCookie = getCookie(curToken, getMWEIBOCNPARAMS(rheaders2));
                        List<Map<String, Object>> mainCards = (List<Map<String, Object>>) ((Map)retMapMainPage.get("data")).get("cards");
                        Map<String, Object> newestWeibo = mainCards.get(2);
                        Map<String, Object> newestWeiboMblog = (Map<String, Object>) newestWeibo.get("mblog");
                        String newestWeiboId = (String) newestWeiboMblog.get("id");
                        String newestWeiboMid = (String) newestWeiboMblog.get("mid");
                        /**
                         * 即使是旧微博，依旧不断会有人发布新图！所以需要一直重复爬取【先控制每次请求隔1秒】
                         */
                /*if (!commentIdFilter.contains(newestWeiboId)) {
                    commentIdFilter.add(newestWeiboId);
                    commentFilter = new HashSet<String>();
                }*/
		/*if (lastWeiboId.equals(newestWeiboId)) {
			// 如果尚未发布新微博，跳过
//			continue;
		}
		lastWeiboId = newestWeiboId;*/
                        /**
                         * 点击进入最新一条微博
                         */
                        HashMap<String, String> condMap3 = new HashMap();
                        condMap3.put("mid", newestWeiboMid);
                        condMap3.put("id", newestWeiboId);
                        condMap3.put("max_id_type", "0");
                        Map<String, String> headers3 = new HashMap<String, String>();
                        headers3.putAll(baseHeader);
                        headers3.put("Cookie", curCookie);
                        //https://m.weibo.cn/detail/4608171891232564
                        headers3.put("Referer", "https://m.weibo.cn/detail/" + newestWeiboId);
                        headers3.put("X-XSRF-TOKEN", curToken);
//		headers3.put("X-Log-Uid", "7568569427");

                        String url3 = "https://m.weibo.cn/comments/hotflow";

                        /**
                         * 从中爬取含有“无内鬼”或“与玫瑰”的评论的子评论图片内容
                         * !!!!需要判断该评论是否已经爬过，决定使用布隆过滤器
                         */
                        RetObj reObj3 = SendRequestUtil.doGet(url3, headers3, condMap3, getRandomMillis());
                        if (!"0".equals(reObj3.getRetCode())) {
                            throw new RuntimeException("请求失败:" + url3);
                        }
                        String retStr3 = (String) reObj3.getRetMap().get("retStr");
                        HashMap<String, Object> retMapNewestInfo = (HashMap<String, Object>) CommonUtil.jsonToObjByJackson(retStr3, HashMap.class);
                        if (!(1 == (int)retMapNewestInfo.get("ok"))) {
                            throw new RuntimeException("获取孙狗最新一条微博数据失败");
                        }
                        headers1 = (Header[]) reObj3.getRetMap().get("headers");
                        curToken = getHeaderXSRFTOKEN(headers1);
                        curCookie = getCookie(curToken, getMWEIBOCNPARAMS(headers1));
                        List<Map<String, Object>> newestInfoData = (List<Map<String, Object>>) ((Map<String, Object>) retMapNewestInfo.get("data")).get("data");
                        for (Map<String, Object> map : newestInfoData) {
                            String text = (String) map.get("text");
                            if (text.contains("无内鬼") || text.contains("与玫瑰")) {
                                // 获取该微博评论的子评论，从中爬取所有图片
                                String curId = (String) map.get("id");
                                String curMId = (String) map.get("mid");

                                String commentUrl = "https://m.weibo.cn/comments/hotFlowChild";
                                Map<String, String> headers4 = new HashMap<String, String>();
                                headers4.putAll(baseHeader);
                                headers4.put("Cookie", curCookie);
                                headers4.put("Referer", "https://m.weibo.cn/detail/" + newestWeiboId + "?cid=" + curId);
                                headers4.put("X-XSRF-TOKEN", curToken);
//				headers4.put("X-Log-Uid", "7568569427");

                                HashMap<String, String> condMap4 = new HashMap();
                                condMap4.put("cid", curId);
                                condMap4.put("max_id", "0");
                                condMap4.put("max_id_type", "0");

                                RetObj reObj4 = SendRequestUtil.doGet(commentUrl, headers4, condMap4, getRandomMillis());
                                if (!"0".equals(reObj4.getRetCode())) {
                                    throw new RuntimeException("请求失败:" + commentUrl);
                                }
                                String retStr4 = (String) reObj4.getRetMap().get("retStr");
                                HashMap<String, Object> commentInfo = (HashMap<String, Object>) CommonUtil.jsonToObjByJackson(retStr4, HashMap.class);
                                if (!(1 == (int)commentInfo.get("ok"))) {
                                    throw new RuntimeException("获取无内鬼评论失败");
                                }
                                headers1 = (Header[]) reObj4.getRetMap().get("headers");
                                curToken = getHeaderXSRFTOKEN(headers1);
                                curCookie = getCookie(curToken, getMWEIBOCNPARAMS(headers1));
                                int total_number = (int) commentInfo.get("total_number");
                                int number_min = 1;
                                try {
                                    while (number_min <= total_number) {
                                        String max_id = String.valueOf((long)commentInfo.get("max_id"));
                                        String max_id_type = String.valueOf((int)commentInfo.get("max_id_type"));
                                        if ("0.0".equals(max_id_type)) {
                                            max_id_type = "0";
                                        }
                                        List<Map<String, Object>> commentInfoData = (List<Map<String, Object>>) commentInfo.get("data");
                                        for (Map<String, Object> comment : commentInfoData) {
                                            number_min ++;
                                            String commentId = (String) comment.get("id");
                                            HashMap condMap = new HashMap();
                                            condMap.put("weibo_id", newestWeiboId);
                                            condMap.put("comment_id", commentId);
                                            int count = weiboDao.selectCommentCount(condMap);
                                            if (count > 0) {
                                                continue;
                                            }
                                            condMap.put("user_id", "3176010690");
                                            condMap.put("pic_url", "");
                                            weiboDao.insertComment(condMap);
                                            String html = (String) comment.get("text");
                                            if (!html.contains("<img")) {
                                                continue;
                                            }
                                            document = Jsoup.parse(html);
                                            if (document == null) {
                                                continue;
                                            }
//								System.out.println("########################html:" + html);
                                            // 目前只取第一个a标签
                                            Elements els = document.getElementsByTag("a");
                                            if (els != null && els.size() > 0) {
                                                String href = els.get(0).attr("href").trim();
                                                if (!CommonUtil.isStrBlank(href)) {
                                                    String fileName = href.substring(href.lastIndexOf("/") + 1);
                                                    String filePath = basePath + CommonUtil.getCurrentDateStr() + File.separator + curId;
                                                    File file = new File(filePath);
                                                    if (!file.exists()) {
                                                        file.mkdirs();
                                                    }
                                                    logger.info("href:" + href);
                                                    if (!href.contains("http")) {
                                                        continue;
                                                    }
                                                    condMap.put("pic_url", href);
                                                    weiboDao.updateUrl(condMap);
                                                    SendRequestUtil.doGetFile(href, null, null, filePath + File.separator + fileName, getRandomMillis());
                                                }
                                            }
                                        }
                                        condMap4.put("max_id", max_id);
                                        condMap4.put("max_id_type", max_id_type);

                                        headers4.put("X-XSRF-TOKEN", curToken);
                                        headers4.put("Cookie", curCookie);
                                        reObj4 = SendRequestUtil.doGet(commentUrl, headers4, condMap4, getRandomMillis());
                                        if (!"0".equals(reObj4.getRetCode())) {
                                            throw new RuntimeException("请求失败:" + commentUrl);
                                        }
                                        retStr4 = (String) reObj4.getRetMap().get("retStr");
                                        commentInfo = (HashMap<String, Object>) CommonUtil.jsonToObjByJackson(retStr4, HashMap.class);
                                        if (!(1 == (int)commentInfo.get("ok"))) {
                                            throw new RuntimeException("获取无内鬼评论失败:" + commentUrl);
                                        }
                                        headers1 = (Header[]) reObj4.getRetMap().get("headers");
                                        curToken = getHeaderXSRFTOKEN(headers1);
                                        curCookie = getCookie(curToken, getMWEIBOCNPARAMS(headers1));
                                    }
                                } catch (Exception e) {
                                    logger.error("爬取评论[" + curId + "]出现异常，跳过本条", e);
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        logger.error("出现异常， 重新爬取");
                    }
                    try {
                        Thread.sleep(1000 * 60 * 3);// 间隔3分钟
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }
    /**
     * 从返回头中获取TSRF_TOKEN
     * @param headers
     * @return
     */
    private static String getHeaderXSRFTOKEN(Header[] headers) {
        String XSRF_TOKEN = "";
        for (Header hd : headers) {
            if (hd.getValue().contains("Max-Age=1200;")) {
                XSRF_TOKEN = hd.getValue();
                XSRF_TOKEN = XSRF_TOKEN.substring(11, XSRF_TOKEN.indexOf(";"));
                break;
            }
        }
        logger.info("新XSRF_TOKEN：" + XSRF_TOKEN);
        return XSRF_TOKEN;
    }

    /**
     * 从返回头中获取M_WEIBOCN_PARAMS
     * @param headers
     * @return
     */
    private static String getMWEIBOCNPARAMS(Header[] headers) {
        String M_WEIBOCN_PARAMS = "";
        for (Header hd : headers) {
            if (hd.getValue().contains("M_WEIBOCN_PARAMS")) {
                M_WEIBOCN_PARAMS = hd.getValue();
                M_WEIBOCN_PARAMS = M_WEIBOCN_PARAMS.substring(17, M_WEIBOCN_PARAMS.indexOf(";"));
            }
        }
        logger.info("新M_WEIBOCN_PARAMS：" + M_WEIBOCN_PARAMS);
        return M_WEIBOCN_PARAMS;
    }

    private static String getCookie(String XSRF_TOKEN, String M_WEIBOCN_PARAMS) {
        String thisCookie = "SCF=AmyxoxZLV1FkQo5mEb8fJvmUR61v2MffWrgrsU0sXSK4fK2pePz0_jsStai4VpOgKufxN9pNU_mO_Lm3Cja5J64.; SUB=_2A25NPk4-DeRhGeFL7VoU9ifIyTuIHXVuwVJ2rDV6PUJbktANLXfmkW1Nfd05wwizu2VI1hWUpBnOGzPvNLFvdgRz; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9W552hceaqvuqXW58NuY.hxL5NHD95QNSKqRSKq4ShzNWs4DqcjMi--NiK.Xi-2Ri--ciKnRi-zNSh2NSKqESKn4S5tt; SSOLoginState=1614429806; MLOGIN=1; WEIBOCN_FROM=1110106030; _T_WM=84643847835; ";
        thisCookie += "XSRF-TOKEN=" + XSRF_TOKEN + "; ";
        thisCookie += "M_WEIBOCN_PARAMS=" + M_WEIBOCN_PARAMS;
        logger.info("新cookie：" + thisCookie);
        return thisCookie;
    }

    private static int getRandomMillis() {
        return (int) (Math.random() * 1000 + 2000);
    }
}
