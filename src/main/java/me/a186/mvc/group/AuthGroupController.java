package me.a186.mvc.group;

import com.jfinal.aop.Before;
import me.a186.constant.ConstantInit;
import me.a186.mvc.base.BaseController;
import me.a186.mvc.model.AuthGroup;

import java.util.List;
import java.util.Map;

/**
 * 角色模块
 * Created by Punk on 2016/4/10.
 */
public class AuthGroupController extends BaseController {

    /**
     * 角色管理列表
     */
    public void index() {
        paging(ConstantInit.db_dataSource_main, AuthGroup.sqlId_splitPageSelect, AuthGroup.sqlId_splitPageFrom);
        render("/room/group/list.html");
    }


    /**
     * 编辑
     */
    public void edit() {
        AuthGroup authGroup = AuthGroup.dao.findById(getPara());
        setAttr("authGroup", authGroup);
        render("/room/group/edit.html");
    }


    /**
     * 新增
     */
    @Before(AuthGroupValidator.class)
    public void save() {
        AuthGroupService.service.save(getModel(AuthGroup.class));
        redirect("/jf/room/group");
    }

    /**
     * 更新
     */
    @Before(AuthGroupValidator.class)
    public void update() {
        AuthGroupService.service.save(getModel(AuthGroup.class));
        redirect("/jf/room/group");
    }

    /**
     * 删除
     */
    public void delete() {
        AuthGroupService.service.delete(getPara() == null ? ids : getPara());
        redirect("/jf/room/group");
    }

    /**
     * 查询角色拥有的功能
     */
    public void getRule() {
        Map<String, String> map = AuthGroupService.service.getRule(ids);
        renderJson(map);
    }

    /**
     * 设置角色拥有的功能
     */
    public void setRuleAndModules() {
        String ruleIds = getPara("ruleIds");
        String moduleIds = getPara("moduleIds");

        AuthGroupService.service.setRuleAndModules(ids, ruleIds, moduleIds);

        renderJson(ids);
    }

    /**
     * 分组弹框
     */
    public void select() {
        Map<String, List<AuthGroup>> map = AuthGroupService.service.select(ids);
        setAttr("noCheckedList", map.get("noCheckedList"));
        setAttr("checkedList", map.get("checkedList"));
        render("/room/group/select.html");
    }
}
