package me.a186.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import me.a186.mvc.base.BaseController;
import me.a186.tools.ToolDateTime;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.math.BigDecimal;

/**
 * 参数封装拦截器
 * Created by Punk on 2016/2/28.
 */
public class ParamPkgInterceptor implements Interceptor {
    private static Logger log = Logger.getLogger(ParamPkgInterceptor.class);

    @Override
    public void intercept(Invocation inv) {
        log.debug("********* 反射获取 controller 全局变量  start *********");

        BaseController controller = (BaseController) inv.getController();

        Class<?> controllerClass = controller.getClass();
        Class<?> superControllerClass = controllerClass.getSuperclass();

        Field[] fields = controllerClass.getDeclaredFields();
        Field[] parentFields = superControllerClass.getDeclaredFields();

        log.debug("********* 反射获取 controller 全局变量  end *********");

        log.debug("********* 封装参数值到 controller 全局变量  start *********");


        //封装controller变量值
        for (Field field : fields) {
            setControllerFieldValue(controller, field);
        }

        //封装baseController变量值
        for (Field field : parentFields) {
            setControllerFieldValue(controller, field);
        }

        log.debug("********* 封装参数值到 controller 全局变量  end *********");

        inv.invoke();

        log.debug("********* 设置全局变量值到 request start *********");

        //封装controller变量值
        for (Field field : fields) {
            setRequestValue(controller, field);
        }

        // 封装baseController变量值
        for (Field field : parentFields) {
            setRequestValue(controller, field);
        }

        log.debug("********* 设置全局变量值到 request end *********");
    }
    /**
     * 反射set值到全局变量
     *
     * @param controller
     * @param field
     */
    private void setControllerFieldValue(BaseController controller, Field field) {
        try {
            field.setAccessible(true);
            String name = field.getName();
            String value = controller.getPara(name);
            if (null == value || value.trim().isEmpty()) {// 参数值为空直接结束
                log.debug("封装参数值到全局变量：field name = " + name + " value = 空");
                return;
            }
            log.debug("封装参数值到全局变量：field name = " + name + " value = " + value);

            String fieldType = field.getType().getSimpleName();
            if (fieldType.equals("String")) {
                field.set(controller, value);
            } else if (fieldType.equals("int")) {
                field.set(controller, Integer.parseInt(value));
            } else if (fieldType.equals("Date")) {
                int dateLength = value.length();
                if (dateLength == ToolDateTime.pattern_ymd.length()) {
                    field.set(controller, ToolDateTime.parse(value, ToolDateTime.pattern_ymd));
                } else if (dateLength == ToolDateTime.pattern_ymd_hms.length()) {
                    field.set(controller, ToolDateTime.parse(value, ToolDateTime.pattern_ymd_hms));
                } else if (dateLength == ToolDateTime.pattern_ymd_hms_s.length()) {
                    field.set(controller, ToolDateTime.parse(value, ToolDateTime.pattern_ymd_hms_s));
                }
            } else if (fieldType.equals("BigDecimal")) {
                BigDecimal bigDecimal = new BigDecimal(value);
                field.set(controller, bigDecimal);
            } else {
                log.debug("没有解析到有效字段类型");
            }
        } catch (Exception e) {
            log.error("反射set值到全局变量异常");
        } finally {
            field.setAccessible(false);
        }
    }

    /**
     * 反射全局变量到request
     *
     * @param controller
     * @param field
     */
    private void setRequestValue(BaseController controller, Field field) {
        try {
            field.setAccessible(true);
            String name = field.getName();
            Object value = field.get(controller);
            if (null == value || (value instanceof String && ((String) value).isEmpty()) || value instanceof Logger) {
                log.debug("设置全局变量到request：field name = " + name + " value = 空");
                return;
            }
            log.debug("设置全局变量到request：field name = " + name + " value = " + value);
            controller.setAttr(name, value);
        } catch (Exception e) {
            log.error("反射全局变量到request异常");
        }
    }
}
