package me.a186.mvc.login;

import com.jfinal.aop.Before;
import me.a186.constant.ConstantLogin;
import me.a186.constant.ConstantWebContext;
import me.a186.mvc.base.BaseController;
import me.a186.mvc.model.User;
import me.a186.tools.ToolWeb;
import org.apache.log4j.Logger;

/**
 * 后台登录
 * Created by Punk on 2016/2/28.
 */
public class LoginController extends BaseController {

    private static Logger log = Logger.getLogger(LoginController.class);


    /**
     * 准备登陆
     */
    public void index() {
        User user = getCUser();//cookie认证自动登陆处理
        if (null != user) {
            redirect("/jf/room/");
        } else {
            render("/room/login/login.html");
        }
    }

    /**
     * 登录验证
     */
    @Before(LoginValidator.class)
    public void vali() {
        String username = getPara("username");
        String password = getPara("password");
        boolean remember = getParaToBoolean("remember");
        int result = LoginService.service.login(getRequest(), getResponse(), username, password, remember);

        renderJson(result);
    }

    /**
     * 验证账号是否可用
     */
    public void valiUserName() {
        String userIds = getPara("userIds");
        String userName = getPara("userName");
        boolean bool = LoginService.service.valiUserName(userIds, userName);
        renderText(String.valueOf(bool));
    }

    /**
     * 退出
     */
    public void logout() {
        ToolWeb.addCookie(getResponse(), "", "/", true, ConstantWebContext.cookie_authmark, null, 0);
        removeSessionAttr("authList");
        redirect("/jf/room/login");
    }
}
