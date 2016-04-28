package me.a186.mvc.modules;

import com.jfinal.aop.Before;
import me.a186.constant.ConstantInit;
import me.a186.dto.ZtreeNode;
import me.a186.mvc.base.BaseController;
import me.a186.mvc.model.Modules;

import java.util.List;

/**
 * 功能模块
 * Created by Punk on 2016/3/28.
 */
public class ModulesController extends BaseController {

    /**
     * 功能模块列表
     */
    public void index() {
        paging(ConstantInit.db_dataSource_main, Modules.sqlId_splitPageSelect, Modules.sqlId_splitPageFrom);
        render("/room/modules/list.html");
    }

    /**
     * 新增模块
     */
    public void add() {
        setAttr("parents", ModulesService.service.getParents());
        render("/room/modules/add.html");
    }

    /**
     * 编辑
     */
    public void edit() {
        Modules modules = Modules.dao.findById(getPara());
        setAttr("parents", ModulesService.service.getParents());
        setAttr("modules", modules);
        render("/room/modules/edit.html");
    }

    /**
     * 更新
     */
    @Before(ModulesValidator.class)
    public void update() {
        ModulesService.service.save(getModel(Modules.class));
        redirect("/jf/room/modules");
    }

    /**
     * 保存
     */
    @Before(ModulesValidator.class)
    public void save() {
        ModulesService.service.save(getModel(Modules.class));
        redirect("/jf/room/modules");
    }

    /**
     * 删除
     */
    public void delete() {
        ModulesService.service.delete(getPara() == null ? ids : getPara());
        redirect("/jf/room/modules");
    }

    /**
     * tree节点数据
     */
    public void treeData() {
        List<ZtreeNode> nodeList = ModulesService.service.childNodeData(getCxt(), ids);
        renderJson(nodeList);
    }
}
