package me.a186.beetl.func;

import me.a186.constant.ConstantWebContext;
import me.a186.interceptor.AuthInterceptor;
import me.a186.mvc.model.Syslog;
import org.apache.log4j.Logger;
import org.beetl.core.Context;
import org.beetl.core.Function;

/**
 * 页面按钮权限验证函数
 * Created by Punk on 2016/2/21.
 */
public class AuthUrl implements Function {
    private static Logger log = Logger.getLogger(AuthUrl.class);

    /**
     * 页面按钮权限验证
     *
     * @param paras
     * @param ctx
     * @return
     */
    @Override
    public Object call(Object[] paras, Context ctx) {
        if (paras.length != 1 || null == paras[0]) {
            log.error("权限标签验证，参数不正确");
            return false;
        }
        String url = null;
        try {
            url = (String) paras[0];
        } catch (Exception e) {
            log.error("权限标签验证，获取参数异常：" + e.getMessage());
            return false;
        }

        boolean bool = AuthInterceptor.hasPrivilegeUrl(url);
        return bool;
    }
}
