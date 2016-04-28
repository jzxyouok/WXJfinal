package me.a186.beetl.format;

import org.apache.log4j.Logger;
import org.beetl.core.Format;

/**
 * beetl页面日期格式化，重写是为了处理Oracle的TIMESTAMP
 * Created by Punk on 2016/2/21.
 */
public class DateFormat implements Format {
    private static Logger log = Logger.getLogger(DateFormat.class);

    @Override
    public Object format(Object data, String pattern) {
        return null;
    }
}
