package org.meiconjun.erp4m.dao;

import java.util.HashMap;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 微博爬虫相关数据库操作
 * @date 2021/2/28 22:10
 */
public interface WeiboDao {

    int selectCommentCount(HashMap<String, Object> condMap);

    int insertComment(HashMap<String, Object> condMap);

    int updateUrl(HashMap<String, Object> condMap);
}
