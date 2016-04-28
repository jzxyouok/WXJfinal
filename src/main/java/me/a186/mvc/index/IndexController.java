package me.a186.mvc.index;

import me.a186.mvc.base.BaseController;
import me.a186.mvc.model.Modules;
import me.a186.mvc.model.User;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * 首页处理
 * Created by Punk on 2016/2/25.
 */
public class IndexController extends BaseController {
    private static Logger log = Logger.getLogger(IndexController.class);

    /**
     * 首页
     */
    public void index() {
        User user = getCUser();//cookie认证自动登陆处理
        if (null != user) {
            List<Modules> modulesList = IndexService.service.menu(user);
            setAttr("menuList", modulesList);//获取用户可操作菜单
            render("/room/index/index.html");
        } else {
            render("/room/login/login.html");
        }
    }

    public void content() {
        render("/room/index/content.html");
    }

}
