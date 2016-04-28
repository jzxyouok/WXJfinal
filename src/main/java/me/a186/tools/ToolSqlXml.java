package me.a186.tools;

import me.a186.constant.ConstantRender;
import me.a186.plugin.SqlXmlPlugin;
import org.apache.log4j.Logger;
import org.beetl.core.BeetlKit;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理sql Map
 * 说明：加载sql map中的sql到map中，并提供动态长度sql处理
 * Created by Punk on 2016/3/23.
 */
public abstract class ToolSqlXml {
    protected static final Logger log = Logger.getLogger(ToolSqlXml.class);


    /**
     * 获取SQL，固定SQL
     *
     * @param sqlId
     * @return
     */
    public static String getSql(String sqlId) {
        String sql = ToolCache.get(SqlXmlPlugin.cacheStart_sql + sqlId);
        if (null == sql || sql.isEmpty()) {
            log.error("sql语句不存在：sql id是" + sqlId);
            return null;
        }

        return sql.replaceAll("[\\s]{2,}", " ");
    }


    /**
     * 获取SQL，动态SQL
     *
     * @param sqlId      xml文件中的sql id
     * @param param      xml sql中的变量map
     * @param renderType 解析sql和param的类型，默认是beetl，还可以是Velocity、FreeMarker，还需其他请自行参考实现
     * @return
     */
    public static String getSql(String sqlId, Map<String, Object> param, String renderType) {
        String sqlTemp = ToolCache.get(SqlXmlPlugin.cacheStart_sql + sqlId);

        if (null == sqlTemp || sqlTemp.isEmpty()) {
            log.error("sql语句不存在：sql id是" + sqlId);
            return null;
        }

        String sql = render(sqlTemp, param, renderType);

        Set<String> keySet = param.keySet();
        for (String key : keySet) {
            if (param.get(key) == null) {
                break;
            }

            Object paramValue = param.get(key);
            if (paramValue instanceof String) {
                String value = (String) paramValue;
                value = value.replace("'", "").replace(";", "").replace("--", "");
                sql = sql.replace("#" + key + "#", value);
            }
        }

        return sql.replaceAll("[\\s]{2,}", " ");
    }

    /**
     * 获取SQL，动态SQL
     *
     * @param sqlId
     * @param param      查询参数
     * @param renderType 解析sql和param的类型，默认是beetl，还可以是Velocity、FreeMarker，还需其他请自行参考实现
     * @param list       用于接收预处理的值
     * @return
     */
    public static String getSql(String sqlId, Map<String, Object> param, String renderType, LinkedList<Object> list) {
        String sqlTemp = ToolCache.get(SqlXmlPlugin.cacheStart_sql + sqlId);
        if (null == sqlTemp || sqlTemp.isEmpty()) {
            log.error("sql语句不存在：sql id是" + sqlId);
            return null;
        }
        String sql = render(sqlTemp, param, renderType);

        //匹配模式为 #'%$names$%'#
        Pattern pattern = Pattern.compile("#[\\w\\d\\$\\'\\%\\_]+#");

        //匹配模式为 $names$
        Pattern pattern1 = Pattern.compile("\\$[\\w\\d\\_]+\\$");

        Matcher matcher = pattern.matcher(sql);

        while (matcher.find()) {
            String column = matcher.group(0);// 得到的结果形式：#'%$names$%'#

            Matcher matcher1 = pattern1.matcher(column);
            matcher1.find();
            String column1 = matcher1.group(0);// 得到的结果形式：$names$

            String column2 = column1.replace("$", "");

            if (column.equals("#" + column1 + "#")) {// 数值型，可以对应处理int、long、bigdecimal、double等等
                String val = String.valueOf(param.get(column2));

                try {
                    Integer.parseInt(val);
                    sql = sql.replace(column, val);
                } catch (NumberFormatException e) {
                    log.error("查询参数值错误，整型值传入了字符串，非法字符串是：" + val);
                    return null;
                }
            } else {// 字符串，主要是字符串模糊查询、日期比较的查询
                String val = (String) param.get(column2);

                String column3 = column.replace("#", "").replace("'", "").replace(column1, val);
                list.add(column3);

                sql = sql.replace(column, "?");
            }
        }
        return sql.replaceAll("[\\s]{2,}", " ");
    }

    public static String render(String sqlTemp, Map<String, Object> param, String renderType) {
        String sql = null;
        if (null == renderType || renderType.equals(ConstantRender.sql_renderType_beetl)) {
            log.debug("beetl解析sql");
            sql = BeetlKit.render(sqlTemp, param);
        } else if (renderType.equals(ConstantRender.sql_renderType_freeMarker)) {
            log.debug("FreeMarker解析sql");
//            sql = ToolFreeMarker.render(sqlTemplete, param);

        } else if (renderType.equals(ConstantRender.sql_renderType_velocity)) {
            log.debug("Velocity解析sql");
//            sql = ToolVelocity.render(sqlTemplete, param);
        }
        return sql;
    }
}
