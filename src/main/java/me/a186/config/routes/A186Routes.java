package me.a186.config.routes;

import com.jfinal.config.Routes;
import me.a186.mvc.group.AuthGroupController;
import me.a186.mvc.index.IndexController;
import me.a186.mvc.login.LoginController;
import me.a186.mvc.modules.ModulesController;
import me.a186.mvc.rule.AuthRuleController;
import me.a186.mvc.user.UserController;

/**
 * 平台路由
 * Created by Punk on 2016/2/25.
 */
public class A186Routes extends Routes {
    @Override
    public void config() {
        add("/jf/room/login", LoginController.class);

        add("/jf/room/", IndexController.class);
        add("/jf/room/index", IndexController.class);

        add("/jf/room/user", UserController.class);
        add("/jf/room/modules", ModulesController.class);
        add("/jf/room/rule", AuthRuleController.class);
        add("/jf/room/group", AuthGroupController.class);
    }
}
