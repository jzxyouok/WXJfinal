package me.a186.mvc.login;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

/**
 * Created by Punk on 2016/3/17.
 */
public class LoginValidator extends Validator {
    @Override
    protected void validate(Controller c) {
        validateRequiredString("username", "usernameMsg", "请输入用户名！");
        validateRequiredString("password", "passwordMsg", "请输入密码！");
    }

    @Override
    protected void handleError(Controller c) {
        c.keepPara("username", "password");
        c.render("/room/login/login.html");
    }
}
