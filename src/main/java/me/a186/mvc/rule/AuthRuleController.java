package me.a186.mvc.rule;

import com.jfinal.aop.Before;
import me.a186.constant.ConstantInit;
import me.a186.dto.ZtreeNode;
import me.a186.mvc.base.BaseController;
import me.a186.mvc.model.AuthRule;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * 功能管理
 * Created by Punk on 2016/3/27.
 */
public class AuthRuleController extends BaseController {
    private static Logger logger = Logger.getLogger(AuthRuleController.class);

    private String moduleIds;//功能对应的模块

    /**
     * 默认列表
     */
    public void index() {
        paging(ConstantInit.db_dataSource_main, AuthRule.sqlId_splitPageSelect, AuthRule.sqlId_splitPageFrom);
        render("/room/rule/list.html");
    }

    /**
     * 保存新增功能
     */
    @Before(AuthRuleValidator.class)
    public void save() {
        AuthRule authRule = getModel(AuthRule.class);
        AuthRuleService.service.save(authRule);
        redirect("/jf/room/rule");
    }

    /**
     * 更新
     */
    @Before(AuthRuleValidator.class)
    public void update() {
        AuthRuleService.service.save(getModel(AuthRule.class));
        redirect("/jf/room/rule");
    }

    /**
     * 编辑
     */
    public void edit() {
        AuthRule authRule = AuthRule.dao.findById(getPara());
        setAttr("rule", authRule);
        render("/room/rule/edit.html");
    }

    /**
     * 删除
     */
    public void delete() {
        AuthRuleService.service.delete(getPara() == null ? ids : getPara());
        redirect("/jf/room/rule");
    }

    /**
     * 功能treeData
     */
    public void treeData() {
        List<ZtreeNode> nodeList = AuthRuleService.service.treeData(getCxt(), moduleIds);
        renderJson(nodeList);
    }
}
