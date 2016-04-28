package me.a186.mvc.group;

import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;
import me.a186.mvc.base.BaseService;
import me.a186.mvc.model.AuthGroup;
import me.a186.mvc.model.AuthGroupAccess;
import me.a186.mvc.model.AuthGroupModules;
import me.a186.mvc.model.AuthGroupRule;

import java.util.*;

/**
 * Created by Punk on 2016/4/11.
 */
public class AuthGroupService extends BaseService {

    public static final AuthGroupService service = Enhancer.enhance(AuthGroupService.class);

    /**
     * 保存
     *
     * @param authGroup
     */
    public void save(AuthGroup authGroup) {
        String ids = authGroup.getIds();
        if (ids == null) {
            authGroup.save();
        } else {
            authGroup.update();
        }
    }

    /**
     * 删除角色信息
     *
     * @param ids
     */
    @Before(Tx.class)
    public void delete(String ids) {
        String[] idsArr = splitByComma(ids);
        for (String groupIds : idsArr) {
            AuthGroup.dao.deleteById(groupIds);
        }
    }

    /**
     * 查询角色拥有的功能和模块
     *
     * @param ids
     * @return
     */
    public Map<String, String> getRule(String ids) {
        Map<String, String> map = new HashMap<>();

        String ruleSql = getSql(AuthGroup.sqlId_getRule);
        List<AuthGroupRule> authGroupRules = AuthGroupRule.dao.find(ruleSql, ids);

        String modulesSql = getSql(AuthGroup.sqlId_getModules);
        List<AuthGroupModules> authGroupModules = AuthGroupModules.dao.find(modulesSql, ids);

        StringBuilder ruleIds = new StringBuilder();
        for (AuthGroupRule authGroupRule : authGroupRules) {
            ruleIds.append(authGroupRule.getRuleIds()).append(",");
        }

        StringBuilder modulesIds = new StringBuilder();
        for (AuthGroupModules modules : authGroupModules) {
            modulesIds.append(modules.getModulesIds()).append(",");
        }

        map.put("modulesIds", modulesIds.toString());
        map.put("ruleIds", ruleIds.toString());

        return map;
    }

    /**
     * 设置角色功能
     *
     * @param groupIds
     * @param ruleIds
     * @param moduleIds
     */
    @Before(Tx.class)
    public void setRuleAndModules(String groupIds, String ruleIds, String moduleIds) {
        //删除所有角色功能
        String sqlRule = getSql(AuthGroup.sqlId_deleteRule);
        Db.update(sqlRule, groupIds);
        String sqlModules = getSql(AuthGroup.sqlId_deleteModules);
        Db.update(sqlModules, groupIds);

        String[] ruleIdsArr = splitByComma(ruleIds);
        String[] moduleIdsArr = splitByComma(moduleIds);

        //添加角色功能
        if (ruleIdsArr != null) {
            for (String rule : ruleIdsArr) {
                AuthGroupRule authGroupRule = new AuthGroupRule();
                authGroupRule.setGroupIds(groupIds);
                authGroupRule.setRuleIds(rule);
                authGroupRule.save();
            }
        }
        if (moduleIdsArr != null) {
            for (String moduleId : moduleIdsArr) {
                AuthGroupModules authGroupModules = new AuthGroupModules();
                authGroupModules.setGroupIds(groupIds);
                authGroupModules.setModulesIds(moduleId);
                authGroupModules.save();
            }
        }
    }


    /**
     * 选择角色
     *
     * @param ids
     * @return
     */
    public Map<String, List<AuthGroup>> select(String ids) {
        List<AuthGroup> allList = new ArrayList<>();
        List<AuthGroup> checkedList = new ArrayList<>();

        String sqlAuthGroupAll = getSql(AuthGroup.sqlId_selectAll);
        allList = AuthGroup.dao.find(sqlAuthGroupAll);

        String sql = getSql(AuthGroup.sqlId_selectByUser);
        checkedList = AuthGroup.dao.find(sql, ids);

        List<AuthGroup> noCheckedList = new ArrayList<>(allList);
        List<AuthGroup> copyCheckedList = new ArrayList<>(checkedList);
        for (AuthGroup noCheck : allList) {
            String noCheckIds = noCheck.getIds();
            for (AuthGroup check : copyCheckedList) {
                String checkIds = check.getIds();
                if (noCheckIds.equals(checkIds)) {
                    noCheckedList.remove(noCheck);
                    copyCheckedList.remove(check);
                    break;
                }
            }
        }

        Map<String, List<AuthGroup>> map = new HashMap<>();
        map.put("noCheckedList", noCheckedList);
        map.put("checkedList", checkedList);
        return map;
    }
}
