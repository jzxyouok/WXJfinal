package me.a186.tools;

import com.jfinal.kit.PropKit;
import me.a186.beetl.format.DateFormat;
import me.a186.beetl.func.AuthUrl;
import me.a186.beetl.func.EscapeXml;
import me.a186.beetl.func.I18nFormat;
import me.a186.beetl.func.OrderBy;
import me.a186.beetl.tag.DictTag;
import me.a186.beetl.tag.ParamTag;
import me.a186.constant.ConstantInit;
import org.apache.log4j.Logger;
import org.beetl.core.BeetlKit;
import org.beetl.core.GroupTemplate;
import org.beetl.ext.jfinal.BeetlRenderFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Beetl工具类
 * Created by Punk on 2016/2/21.
 */
public abstract class ToolBeetl {
    private static Logger log = Logger.getLogger(ToolBeetl.class);

    /**
     * 模版扩展
     *
     * @return
     */
    public static GroupTemplate register() {
        Map<String, Object> sharedVars = new HashMap<>();
        sharedVars.put("db_type", PropKit.get(ConstantInit.db_type_key));

        log.debug("注册全局web视图模版解析");
        GroupTemplate mainGT = BeetlRenderFactory.groupTemplate;
        if (mainGT == null) {
            mainGT = new BeetlRenderFactory().groupTemplate;
        }
        mainGT.registerFunction("authUrl", new AuthUrl());
        mainGT.registerFunction("orderBy", new OrderBy());
        mainGT.registerFunction("escapeXml", new EscapeXml());
        mainGT.registerFunction("i18nFormat", new I18nFormat());
        mainGT.registerTag("dict", DictTag.class);
        mainGT.registerTag("param", ParamTag.class);
        mainGT.registerFormat("dateFormat", new DateFormat());
        mainGT.setSharedVars(sharedVars);

        log.debug("注册全局BeetlKit模版解析");
        GroupTemplate kitGT = BeetlKit.gt;
        kitGT.registerFunction("authUrl",new AuthUrl());
        kitGT.registerFunction("orderBy",new OrderBy());
        kitGT.registerFunction("escapeXml",new EscapeXml());
        kitGT.registerFunction("i18nFormat", new I18nFormat());
        kitGT.registerTag("dict", DictTag.class);
        kitGT.registerTag("param", ParamTag.class);
        kitGT.registerFormat("dateFormat", new DateFormat());
        kitGT.setSharedVars(sharedVars);

        return mainGT;
    }
}
