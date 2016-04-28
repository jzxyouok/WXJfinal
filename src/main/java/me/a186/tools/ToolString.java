package me.a186.tools;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理常用方法
 * Created by Punk on 2016/2/21.
 */
public abstract class ToolString {
    /**
     * 字符编码
     */
    public final static String encoding = "UTF-8";

    /**
     * 常用正则表达式：匹配由数字、26个英文字母或者下划线组成的字符串
     */
    public final static String regExp_letter_5 = "^\\w+$";

    /**
     * 验证字符串是否匹配指定正则表达式
     *
     * @param content
     * @param regExp
     * @return
     */
    public static boolean regExpVali(String content, String regExp) {
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(content);
        return matcher.matches();
    }

    /**
     * Url Base64解码
     *
     * @param data
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String decode(String data) throws UnsupportedEncodingException {
        //执行解码
        byte[] b = Base64.decodeBase64(data.getBytes(encoding));
        return new String(b, encoding);
    }

    /**
     * Url Base64编码
     *
     * @param data
     * @return
     */
    public static String encode(String data) throws UnsupportedEncodingException {
        //执行编码
        byte[] b = Base64.encodeBase64URLSafe(data.getBytes(encoding));
        return new String(b, encoding);
    }


    /**
     * 首字母转小写
     *
     * @param s
     * @return
     */
    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }

    /**
     * 首字母转大写
     *
     * @param s
     * @return
     */
    public static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder().append(Character.toUpperCase(s.charAt(0))).append(s.substring(1))).toString();
        }
    }
}
