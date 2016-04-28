package me.a186.mvc.model;

import me.a186.mvc.base.BaseModelCache;
import me.a186.mvc.model.base.BaseAuthRule;
import me.a186.plugin.ParamInitPlugin;
import me.a186.tools.ToolCache;

import java.util.List;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class AuthRule extends BaseAuthRule<AuthRule> implements BaseModelCache<AuthRule> {
    public static final AuthRule dao = new AuthRule();

    /**
     * 分页Select
     */
    public static final String sqlId_splitPageSelect = "authRule.splitPageSelect";

    /**
     * 分页from
     */
    public static final String sqlId_splitPageFrom = "authRule.splitPageFrom";

    /**
     * 获取模块下面的功能
     */
    public static final String sqlId_byModuleIds = "authRule.byModuleIds";


    /**
     * 分页查询
     *
     * @param start
     * @param end
     * @return
     */
    public List<AuthRule> paging(long start, long end) {
        return AuthRule.dao.find("select * from t_auth_rule order by ids limit ? offset ?", start, end);
    }


    @Override
    public void cacheAdd(String ids) {
        AuthRule authRule = AuthRule.dao.findById(ids);
        ToolCache.set(ParamInitPlugin.cacheStart_rule + ids, authRule);
        ToolCache.set(ParamInitPlugin.cacheStart_rule + authRule.getName(), authRule);
    }

    @Override
    public void cacheRemove(String ids) {
        AuthRule authRule = AuthRule.dao.findById(ids);
        ToolCache.remove(ParamInitPlugin.cacheStart_rule + ids);
        ToolCache.remove(ParamInitPlugin.cacheStart_rule + authRule.getName());
    }

    @Override
    public AuthRule cacheGet(String key) {
        AuthRule authRule = ToolCache.get(ParamInitPlugin.cacheStart_rule + key);
        return authRule;
    }
}
