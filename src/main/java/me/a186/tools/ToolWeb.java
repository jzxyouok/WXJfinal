package me.a186.tools;

import org.apache.log4j.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Pack200;

/**
 * WEB工具类
 * Created by Punk on 2016/3/14.
 */
public abstract class ToolWeb {

    private static Logger log = Logger.getLogger(ToolWeb.class);

    /**
     * 获取请求参数
     *
     * @param request
     * @param name
     * @return
     */
    public static String getParam(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (null != value && !value.isEmpty()) {
            try {
                return URLDecoder.decode(value, ToolString.encoding).trim();
            } catch (UnsupportedEncodingException e) {
                log.error("decode异常：" + value);
                return value;
            }
        }
        return value;
    }

    /**
     * 获取完整请求路径(含内容路径及请求参数)
     *
     * @param request
     * @return
     */
    public static String getRequestURIWithParam(HttpServletRequest request) {
        return request.getRequestURI() + (request.getQueryString() == null ? "" : "?" + request.getQueryString());
    }

    /**
     * 获取客户端IP地址
     *
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 获取上下文URL全路径
     *
     * @param request
     * @return
     */
    public static String getContextPath(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getScheme()).append("://").append(request.getServerName()).append(":").append(request.getServerPort()).append(request.getContextPath());
        String path = sb.toString();
        sb = null;
        return path;
    }

    /**
     * 获得所有cookie
     *
     * @param request
     * @return
     */
    public static Map<String, Cookie> readCookieMap(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<>();
        //从request范围中得到cookie数组 然后遍历放入map集合中
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (int i = 0; i < cookies.length; i++) {
                cookieMap.put(cookies[i].getName(), cookies[i]);
            }
        }
        return cookieMap;
    }

    /**
     * 获取ParameterMap
     *
     * @param request
     * @return
     */
    public static Map<String, String> getParamMap(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            map.put(name, request.getParameter(name));
        }
        return map;
    }

    /**
     * @param response
     * @param domain     设置cookie所在域
     * @param path       设置cookie所在路径
     * @param isHttpOnly 是否只读
     * @param name       cookie 的名称
     * @param value      cookie的值
     * @param maxAge     cookie存放的时间(以秒为单位,假如存放三天,即3*24*60*60; 如果值为0,cookie将随浏览器关闭而清除)
     */
    public static void addCookie(HttpServletResponse response, String domain, String path, boolean isHttpOnly, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);

        // 所在域：比如a1.4bu4.com 和 a2.4bu4.com 共享cookie
        if (null != domain && !domain.isEmpty()) {
            cookie.setDomain(domain);
        }

        //设置cookie所在路径
        cookie.setPath("/");
        if (null != path && !path.isEmpty()) {
            cookie.setPath(path);
        }

        //是否只读
        try {
            cookie.setHttpOnly(isHttpOnly);
        } catch (Exception e) {
            log.error("servlet容器版本太低，servlet3.0以前不支持设置cookie只读" + e.getMessage());
        }

        //设置cookie的过期时间
        if (maxAge > 0) {
            cookie.setMaxAge(maxAge);
        }
        response.addCookie(cookie);
    }

    /**
     * 获取cookie的值
     *
     * @param request
     * @param name
     * @return
     */
    public static String getCookieValueByName(HttpServletRequest request, String name) {
        Map<String, Cookie> cookieMap = ToolWeb.readCookieMap(request);
        //判断cookie集合中是否有我们想要的cookie对象 如果有返回它的值
        if (cookieMap.containsKey(name)) {
            Cookie cookie = cookieMap.get(name);
            return cookie.getValue();
        } else {
            return null;
        }
    }
}
