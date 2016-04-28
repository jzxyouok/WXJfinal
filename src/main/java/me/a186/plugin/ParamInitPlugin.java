package me.a186.plugin;


import com.jfinal.plugin.IPlugin;
import me.a186.constant.ConstantInit;
import me.a186.mvc.base.BaseService;
import me.a186.mvc.model.AuthRule;
import me.a186.mvc.model.User;
import me.a186.tools.ToolCache;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * 系统初始化缓存操作类
 * Created by Punk on 2016/2/26.
 */
public class ParamInitPlugin implements IPlugin {

    private static Logger log = Logger.getLogger(ParamInitPlugin.class);

    /**
     * 数据批处理大小，每批次处理一万行
     */
    protected static final int splitDataSize = 10000;

    /**
     * 用户缓存key前缀
     */
    public static String cacheStart_user = "user_";

    /**
     * 功能缓存key前缀
     */
    public static String cacheStart_rule = "rule_";

    @Override
    public boolean start() {
        log.info("缓存参数初始化 start ...");

        //1.缓存用户
        cacheUser();

        //2.缓存角色
        cacheGroup();

        //3.缓存功能
        cacheRule();
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    /**
     * 缓存所有功能
     */
    public void cacheRule() {
        log.info("缓存加载：AuthRule start");
        long batchCount = BaseService.service.getBatchCount(ConstantInit.db_dataSource_main, " from t_auth_rule", splitDataSize);
        List<AuthRule> authRules = null;
        for (long i = 0; i < batchCount; i++) {
            authRules = AuthRule.dao.paging(splitDataSize, i * splitDataSize);
            for (AuthRule authRule : authRules) {
                if (authRule.getName().indexOf(",") != -1) {
                    String[] arr = authRule.getName().split(",");
                    for (String a : arr) {
                        ToolCache.set(ParamInitPlugin.cacheStart_rule + a, authRule);
                    }
                } else {
                    ToolCache.set(ParamInitPlugin.cacheStart_rule + authRule.getName(), authRule);
                }
                ToolCache.set(ParamInitPlugin.cacheStart_rule + authRule.getIds(), authRule);
            }
            authRules = null;
        }
        log.info("缓存加载：AuthRule end");
    }

    /**
     * 缓存所有角色
     */
    public void cacheGroup() {
        log.info("缓存加载：Group start");

    }

    /**
     * 缓存所有用户
     */
    public static void cacheUser() {
        log.info("缓存加载：User start");

        long batchCount = BaseService.service.getBatchCount(ConstantInit.db_dataSource_main, " from t_user", splitDataSize);
        List<User> users = null;
        for (long i = 0; i < batchCount; i++) {
            users = User.dao.paging(splitDataSize, i * splitDataSize);
            for (User user : users) {
                ToolCache.set(ParamInitPlugin.cacheStart_user + user.getIds(), user);
                ToolCache.set(ParamInitPlugin.cacheStart_user + user.getUsername(), user);
            }
            users = null;
        }
        log.info("缓存加载：User end");
    }
}
