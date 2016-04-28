package me.a186.mvc.modules;

import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.tx.Tx;
import me.a186.dto.ZtreeNode;
import me.a186.mvc.base.BaseService;
import me.a186.mvc.model.Modules;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Punk on 2016/3/29.
 */
public class ModulesService extends BaseService {

    private static Logger log = Logger.getLogger(ModulesService.class);

    public static final ModulesService service = Enhancer.enhance(ModulesService.class);

    /**
     * 获取上级模块
     */
    public List<Modules> getParents() {
        return Modules.dao.find(getSql(Modules.sqlId_getParents));
    }

    /**
     * 保存
     */
    public void save(Modules modules) {
        String ids = modules.getIds();
        if (null == ids) {
            modules.save();
        } else {
            modules.update();
        }
    }

    /**
     * 删除模块信息
     *
     * @param ids
     */
    @Before(Tx.class)
    public void delete(String ids) {
        String[] idsArr = splitByComma(ids);
        for (String modulesIds : idsArr) {
            Modules.dao.deleteById(modulesIds);
        }
    }

    /**
     * 获取子节点数据
     *
     * @param cxt
     * @param parentIds
     * @return
     */
    public List<ZtreeNode> childNodeData(String cxt, String parentIds) {
        List<Modules> list = null;
        if (null != parentIds) {
            String sql = getSql(Modules.sqlId_childNode);
            list = Modules.dao.find(sql, parentIds);
        } else {
            String sql = getSql(Modules.sqlId_rootNode);
            list = Modules.dao.find(sql);
        }

        List<ZtreeNode> nodeList = new ArrayList<>();
        ZtreeNode node = null;
        for (Modules modules : list) {
            node = new ZtreeNode();
            node.setId(modules.getIds());
            node.setName(modules.getName());
            node.setIsParent(true);
            node.setIcon(cxt + "/ui/plugins/zTree_v3/css/zTreeStyle/img/diy/3.png");
            nodeList.add(node);
        }
        return nodeList;
    }
}
