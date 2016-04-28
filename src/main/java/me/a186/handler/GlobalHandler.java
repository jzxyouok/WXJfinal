package me.a186.handler;

import com.jfinal.handler.Handler;
import com.jfinal.kit.PropKit;
import me.a186.constant.ConstantInit;
import me.a186.constant.ConstantWebContext;
import me.a186.mvc.model.Syslog;
import me.a186.plugin.I18NPlugin;
import me.a186.thread.ThreadSysLog;
import me.a186.tools.ToolDateTime;
import me.a186.tools.ToolRandoms;
import me.a186.tools.ToolWeb;
import org.apache.log4j.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Map;

/**
 * 全局Handler，设置一些通用功能
 * Created by Punk on 2016/2/28.
 * 描述：主要是一些全局变量的设置，再就是日志记录开始和结束操作
 */
public class GlobalHandler extends Handler {

    private static Logger log = Logger.getLogger(GlobalHandler.class);

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        log.info("初始化访问系统功能日志");
        Syslog reqSysLog = getSysLog(request);
        long startTime = ToolDateTime.getDateByTime();
        reqSysLog.setStartdate(ToolDateTime.getSqlTimestamp(startTime));
        request.setAttribute(ConstantWebContext.reqSysLogKey, reqSysLog);

        log.info("设置web路径");
        String cxt = ToolWeb.getContextPath(request);
        request.setAttribute(ConstantWebContext.request_cxt, cxt);

        log.info("request 随机分配一个请求id");
        request.setAttribute(ConstantWebContext.request_id, ToolRandoms.getUuid(true));

        log.debug("request cookie 处理");
        Map<String, Cookie> cookieMap = ToolWeb.readCookieMap(request);
        request.setAttribute(ConstantWebContext.request_cookieMap, cookieMap);

        log.debug("request param 请求参数处理");
        request.setAttribute(ConstantWebContext.request_paramMap, ToolWeb.getParamMap(request));

        log.debug("request 国际化");
        String localPram = request.getParameter(ConstantWebContext.request_localePram);
        if (null != localPram && !localPram.isEmpty()) {
            int maxAge = PropKit.getInt(ConstantInit.config_maxAge_key);
            localPram = I18NPlugin.localParse(localPram);
            ToolWeb.addCookie(response, "", "/", true, ConstantWebContext.cookie_language, localPram, maxAge);
        } else {
            localPram = ToolWeb.getCookieValueByName(request, ConstantWebContext.cookie_language);
            if (null == localPram || localPram.isEmpty()) {
                Locale locale = request.getLocale();
                String language = locale.getLanguage();
                localPram = language;
                String country = locale.getCountry();
                if (null != country && !country.isEmpty()) {
                    localPram += "_" + country;
                }
            }
            localPram = I18NPlugin.localParse(localPram);
        }
        Map<String, String> i18nMap = I18NPlugin.get(localPram);
        request.setAttribute(ConstantWebContext.request_localePram, localPram);
        request.setAttribute(ConstantWebContext.request_i18nMap, i18nMap);

        log.info("设置Header");
        request.setAttribute("decorator", "none");
        response.setHeader("Cache-Control", "no-cache");//HTTP 1.1
        response.setHeader("Pragma", "no-cache");//HTTP 1.0
        response.setDateHeader("Expires", 0);//prevents caching at the proxy server

        next.handle(target, request, response, isHandled);

        log.info("请求处理完毕，计算耗时");

        //结束时间
        long endTime = ToolDateTime.getDateByTime();
        reqSysLog.setEnddate(ToolDateTime.getSqlTimestamp(endTime));

        //总耗时
        Long totalTime = endTime - startTime;
        reqSysLog.setHaoshi(totalTime);

        //视图耗时
        long renderTime = 0;
        if (null != request.getAttribute(ConstantWebContext.renderTimeKey)) {
            renderTime = (long) request.getAttribute(ConstantWebContext.renderTimeKey);
        }
        reqSysLog.setViewhaoshi(renderTime);

        //action耗时
        reqSysLog.setActionhaoshi(totalTime - renderTime);

        log.info("日志添加到入库队列");
        ThreadSysLog.add(reqSysLog);
    }

    public Syslog getSysLog(HttpServletRequest request) {
        String requestPath = ToolWeb.getRequestURIWithParam(request);
        String ip = ToolWeb.getIpAddr(request);
        String referer = request.getHeader("Referer");
        String userAgent = request.getHeader("User-Agent");
        String cookie = request.getHeader("Cookie");
        String method = request.getMethod();
        String xRequestWith = request.getHeader("X-Requested-With");
        String host = request.getHeader("Host");
        String acceptLanguage = request.getHeader("Accept-Language");
        String acceptEncoding = request.getHeader("Accept-Encoding");
        String accept = request.getHeader("Accept");
        String connection = request.getHeader("Connection");

        Syslog reqSysLog = new Syslog();

        reqSysLog.setIps(ip);
        reqSysLog.setRequestpath(requestPath);
        reqSysLog.setReferer(referer);
        reqSysLog.setUseragent(userAgent);
        reqSysLog.setCookie(cookie);
        reqSysLog.setMethod(method);
        reqSysLog.setXrequestedwith(xRequestWith);
        reqSysLog.setHost(host);
        reqSysLog.setAcceptlanguage(acceptLanguage);
        reqSysLog.setAcceptencoding(acceptEncoding);
        reqSysLog.setAccept(accept);
        reqSysLog.setConnection(connection);

        return reqSysLog;
    }
}
