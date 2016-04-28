package me.a186.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.PropKit;
import me.a186.constant.ConstantAuth;
import me.a186.constant.ConstantInit;
import me.a186.constant.ConstantWebContext;
import me.a186.mvc.base.BaseController;
import me.a186.mvc.model.AuthRule;
import me.a186.mvc.model.Syslog;
import me.a186.mvc.model.User;
import me.a186.tools.ToolDateTime;
import me.a186.tools.ToolWeb;
import me.a186.tools.security.ToolIDEA;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * 权限认证拦截器
 * Created by Punk on 2016/2/28.
 * 描述：
 * 1.处理权限验证
 * 2.处理全局异常
 * 3.处理权限相关的工具类方法
 */
public class AuthInterceptor implements Interceptor {

    private static Logger log = Logger.getLogger(AuthInterceptor.class);

    private static List<String> authList = null;

    @Override
    public void intercept(Invocation inv) {
        BaseController controller = (BaseController) inv.getController();
        HttpServletRequest request = controller.getRequest();
        HttpServletResponse response = controller.getResponse();

        log.info("获取reqSysLog!");
        Syslog reqSysLog = controller.getAttr(ConstantWebContext.reqSysLogKey);
        controller.setReqSysLog(reqSysLog);

        log.info("获取用户请求的URI，两种形式，参数传递和直接request获取");
        String uri = inv.getActionKey();//默认就是ActionKey
        if (inv.getMethodName().equals(ConstantWebContext.request_toUrl)) {
            uri = ToolWeb.getParam(request, ConstantWebContext.request_toUrl); // 否则就是toUrl的值
        }

        log.info("获取当前用户！");
        boolean userAgentVail = true;//是否验证userAgent，默认是
        User user = getCurrentUser(request, response, userAgentVail);//当前登录用户
        if (null != user) {
            String userIds = user.getIds();
            controller.setAttr(ConstantWebContext.request_cUser, user);
            controller.setAttr(ConstantWebContext.request_cUserIds, userIds);

            MDC.put("userId", userIds);
            MDC.put("userName", user.getUsername());
        } else {
            MDC.put("userId", "*unknown userId*");
            MDC.put("userName", "*unknown userName*");
        }

        log.info("获取URL对象");
        AuthRule authRule = AuthRule.dao.cacheGet(uri);

        if (null == authRule) {
            log.info("URL对象不存在！");

            reqSysLog.setStatus("0");
            reqSysLog.setDescription("URL不存在");
            reqSysLog.setCause("1");

            toView(controller, ConstantAuth.auth_no_url, "权限认证过滤器检测：URL不存在");
            return;
        }

        reqSysLog.setRuleIds(authRule.getIds());
        if (authRule.getPermission().equals("1")) {//是否需要权限验证
            if (user == null) {
                log.info("权限认证过滤器检测:未登录!");

                reqSysLog.setStatus("0");
                reqSysLog.setDescription("未登录");
                reqSysLog.setCause("2");

                toView(controller, ConstantAuth.auth_no_login, "权限认证过滤器检测：未登录");
                return;
            }
            authList = controller.getAuthList();
            if (!hasPrivilegeUrl(uri)) {//权限验证
                log.info("权限验证失败，没有权限!");

                reqSysLog.setStatus("0");// 失败
                reqSysLog.setDescription("没有权限!");
                reqSysLog.setCause("0");// 没有权限

                toView(controller, ConstantAuth.auth_no_permissions, "权限验证失败，您没有操作权限");
                return;
            }
        }

        log.info("权限认真成功更新日志对象属性!");
        reqSysLog.setStatus("1");// 成功
        Date actionStartDate = ToolDateTime.getDate();// action开始时间
        reqSysLog.setActionstartdate(ToolDateTime.getSqlTimestamp(actionStartDate));
        reqSysLog.setActionstarttime(actionStartDate.getTime());

        try {
            inv.invoke();
        } catch (Exception e) {

        } finally {
            MDC.remove("userId");
            MDC.remove("userName");
        }
    }

    /**
     * 判断用户是否拥有某个url的操作权限
     *
     * @param url
     * @return
     */
    public static boolean hasPrivilegeUrl(String url) {
        boolean bool = authList.contains(url);
        return bool;
    }

    /**
     * 提示信息展示页
     *
     * @param controller
     * @param type
     * @param msg
     */
    private void toView(BaseController controller, String type, String msg) {
        if (type.equals(ConstantAuth.auth_no_login)) {//未登录处理
            controller.redirect("/jf/room/login");
            return;
        }

        controller.setAttr("msg", msg);

        String isAjax = controller.getRequest().getHeader("X-Requested-With");
        if (isAjax != null && isAjax.equalsIgnoreCase("XMLHttpRequest")) {
            controller.render("/common/msgAjax.html");//ajax页面
        } else {
            controller.render("/common/msg.html");
        }
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @param response
     * @param userAgentVail 是否验证 User-Agent
     * @return
     */
    public static User getCurrentUser(HttpServletRequest request, HttpServletResponse response, boolean userAgentVail) {
        String loginCookie = ToolWeb.getCookieValueByName(request, ConstantWebContext.cookie_authmark);
        if (null != loginCookie && !loginCookie.equals("")) {
            // 1.解密认证数据
            String data = ToolIDEA.decrypt(loginCookie);
            if (null == data || data.isEmpty()) {
                ToolWeb.addCookie(response, "", "/", true, ConstantWebContext.cookie_authmark, null, 0);
                return null;
            }
            String[] datas = data.split(".#.");    //arr[0]：时间戳，arr[1]：USERID，arr[2]：USER_IP， arr[3]：USER_AGENT

            // 2. 分解认证数据
            long loginDateTimes;
            String userIds = null;
            String ips = null;
            String userAgent = null;
            boolean autoLogin = false;
            try {
                loginDateTimes = Long.parseLong(datas[0]); // 时间戳
                userIds = datas[1]; // 用户id
                ips = datas[2]; // ip地址
                userAgent = datas[3]; // USER_AGENT
                autoLogin = Boolean.valueOf(datas[4]); // 是否自动登录
            } catch (Exception e) {
                ToolWeb.addCookie(response, "", "/", true, ConstantWebContext.cookie_authmark, null, 0);
                return null;
            }

            // 3.用户当前数据
            String newIp = ToolWeb.getIpAddr(request);
            String newUserAgent = request.getHeader("User-Agent");

            Date start = ToolDateTime.getDate();
            start.setTime(loginDateTimes); // 用户自动登录开始时间
            int day = ToolDateTime.getDateDaySpace(start, ToolDateTime.getDate()); // 已经登录多少天

            int maxAge = PropKit.getInt(ConstantInit.config_maxAge_key);

            // 4. 验证数据有效性
            if (ips.equals(newIp) && (userAgentVail ? userAgent.equals(newUserAgent) : true) && day <= maxAge) {
                // 如果不记住密码，单次登陆有效时间验证
                if (!autoLogin) {
                    int minute = ToolDateTime.getDateMinuteSpace(start, new Date());
                    int session = PropKit.getInt(ConstantInit.config_session_key);
                    if (minute > session) {
                        return null;
                    } else {
                        // 重新生成认证cookie，目的是更新时间戳
                        long date = ToolDateTime.getDateByTime();
                        StringBuilder token = new StringBuilder();// 时间戳.#.USERID.#.USER_IP.#.USER_AGENT.#.autoLogin
                        token.append(date).append(".#.").append(userIds).append(".#.").append(ips).append(".#.").append(userAgent).append(".#.").append(autoLogin);
                        String authmark = ToolIDEA.encrypt(token.toString());

                        // 添加到Cookie
                        int maxAgeTemp = -1; // 设置cookie有效时间
                        ToolWeb.addCookie(response, "", "/", true, ConstantWebContext.cookie_authmark, authmark, maxAgeTemp);
                    }
                }

                // 返回用户数据
                Object userObj = User.dao.cacheGet(userIds);
                if (null != userObj) {
                    User user = (User) userObj;
                    return user;
                }
            }
        }

        return null;
    }

    /**
     * 设置当前登录用户到cookie
     *
     * @param request
     * @param response
     * @param user
     * @param autoLogin
     */
    public static void setCurrentUser(HttpServletRequest request, HttpServletResponse response, User user, boolean autoLogin) {
        // 1.设置cookie有效时间
        int maxAgeTemp = -1;
        if (autoLogin) {
            maxAgeTemp = PropKit.getInt(ConstantInit.config_maxAge_key);
        }

        // 2.设置用户名到cookie
        ToolWeb.addCookie(response, "", "/", true, "userName", user.getStr("username"), maxAgeTemp);

        // 3.生成登陆认证cookie
        String userIds = user.getPKValue();
        String ips = ToolWeb.getIpAddr(request);
        String userAgent = request.getHeader("User-Agent");
        long date = ToolDateTime.getDateByTime();

        StringBuilder token = new StringBuilder();// 时间戳.#.USERID.#.USER_IP.#.USER_AGENT.#.autoLogin
        token.append(date).append(".#.").append(userIds).append(".#.").append(ips).append(".#.").append(userAgent).append(".#.").append(autoLogin);
        String authmark = ToolIDEA.encrypt(token.toString());

        // 4. 添加到Cookie
        ToolWeb.addCookie(response, "", "/", true, ConstantWebContext.cookie_authmark, authmark, maxAgeTemp);
    }
}
