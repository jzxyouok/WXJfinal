package me.a186.beetl.func;

import me.a186.constant.ConstantWebContext;
import org.apache.log4j.Logger;
import org.beetl.core.Context;
import org.beetl.core.Function;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.Map;

/**
 * 国际化输出
 * Created by Punk on 2016/2/21.
 * 说明：beetl本身自带了strutil.format ${ strutil.format (“hello,{0}, my age is {1}”,”joeli”,15),输出是hello,joelli, my age is 15. 具体请参考
 * http://docs.oracle.com/javase/6/docs/api/java/text/MessageFormat.html
 */
public class I18nFormat implements Function {
    private static Logger log = Logger.getLogger(I18nFormat.class);

    @Override
    public Object call(Object[] arg, Context context) {
        if (arg.length == 0) {
            return null;
        }

        // 第一个参数时国际化模板的key
        Map<String, String> i18nMap = (Map<String, String>) context.getGlobal(ConstantWebContext.request_i18nMap);
        String formatTemplateKey = (String) arg[0];
        String formatTemplate = i18nMap.get(formatTemplateKey);

        // 格式化参数数据
        LinkedList<Object> paramValue = new LinkedList<>();
        for (int i = 1, length = arg.length; i < length; i++) {
            paramValue.add(arg[i]);
        }

        // 格式化
        String value = MessageFormat.format(formatTemplate, paramValue.toArray());    // {0} {1} ...

        log.debug("I18nFormat，国际化模板格式化处理，value=" + value);

        return value;
    }
}
