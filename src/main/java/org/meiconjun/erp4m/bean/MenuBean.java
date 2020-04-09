package org.meiconjun.erp4m.bean;

/**
 * @author Lch
 * @Title: MenuBean
 * @Package org.meiconjun.erp4m.bean.MenuBean
 * @Description: 菜单实体类
 * @date 2020/4/9 20:45
 */
public class MenuBean {
    /**
     * 菜单编号
     */
    private String menu_id;
    /**
     * 菜单名称
     */
    private String menu_name;
    /**
     * 父级菜单（编号）
     */
    private String parent_menu;
    /**
     * 菜单描述
     */
    private String menu_desc;
    /**
     * 菜单路径
     */
    private String menu_url;
    /**
     * 菜单级别
     */
    private String menu_level;
    /**
     * 是否父级菜单
     */
    private String is_parent;
    /**
     * 最后修改时间
     */
    private String last_modi_time;

    public String getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(String menu_id) {
        this.menu_id = menu_id;
    }

    public String getMenu_name() {
        return menu_name;
    }

    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }

    public String getParent_menu() {
        return parent_menu;
    }

    public void setParent_menu(String parent_menu) {
        this.parent_menu = parent_menu;
    }

    public String getMenu_desc() {
        return menu_desc;
    }

    public void setMenu_desc(String menu_desc) {
        this.menu_desc = menu_desc;
    }

    public String getMenu_url() {
        return menu_url;
    }

    public void setMenu_url(String menu_url) {
        this.menu_url = menu_url;
    }

    public String getMenu_level() {
        return menu_level;
    }

    public void setMenu_level(String menu_level) {
        this.menu_level = menu_level;
    }

    public String getIs_parent() {
        return is_parent;
    }

    public void setIs_parent(String is_parent) {
        this.is_parent = is_parent;
    }

    public String getLast_modi_time() {
        return last_modi_time;
    }

    public void setLast_modi_time(String last_modi_time) {
        this.last_modi_time = last_modi_time;
    }
}
