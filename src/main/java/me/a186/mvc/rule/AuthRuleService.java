package me.a186.mvc.rule;

import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.tx.Tx;
import me.a186.dto.ZtreeNode;
import me.a186.mvc.base.BaseService;
import me.a186.mvc.model.AuthRule;
import me.a186.mvc.model.Modules;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Punk on 2016/4/2.
 */
public class AuthRuleService extends BaseService {
    private static Logger logger = Logger.getLogger(AuthRuleService.class);

    public static final AuthRuleService service = Enhancer.enhance(AuthRuleService.class);

    /**
     * 保存功能信息
     *
     * @param authRule
     */
    @Before(Tx.class)
    public void save(AuthRule authRule) {
        String ids = authRule.getIds();
        if (null == ids) {
            authRule.save();
        } else {
            authRule.update();
        }
        if (authRule.getType().equals("2")) {
            Modules modules = Modules.dao.findById(authRule.getMids());
            modules.setUrl(authRule.getName());
            modules.update();
        }
    }

    /**
     * 删除功能
     *
     * @param ids
     */
    @Before(Tx.class)
    public void delete(String ids) {
        String[] idsArr = splitByComma(ids);
        for (String id : idsArr) {
            AuthRule.dao.deleteById(id);
        }
    }

    /**
     * 获取子节点数据
     *
     * @param cxt
     * @param moduleIds
     * @return
     */
    public List<ZtreeNode> treeData(String cxt, String moduleIds) {
        List<Modules> listModules = new ArrayList<>();
        List<AuthRule> authRuleList = new ArrayList<>();

        if (null == moduleIds) {
            String sql = getSql(Modules.sqlId_rootNode);
            listModules = Modules.dao.find(sql);
        } else {
            String sqlModule = getSql(Modules.sqlId_childNode);
            listModules = Modules.dao.find(sqlModule, moduleIds);

            String sqlRule = getSql(AuthRule.sqlId_byModuleIds);
            authRuleList = AuthRule.dao.find(sqlRule, moduleIds);
        }

        List<ZtreeNode> nodeList = new ArrayList<>();
        ZtreeNode node = null;

        for (Modules modules : listModules) {
            node = new ZtreeNode();
            node.setId(modules.getIds());
            node.setName(modules.getName());
            node.setIsParent(true);
            node.setChecked(false);
            node.setIcon(cxt + "/ui/plugins/zTree_v3/css/zTreeStyle/img/diy/3.png");
            nodeList.add(node);
        }

        for (AuthRule authRule : authRuleList) {
            node = new ZtreeNode();
            node.setId(authRule.getIds());
            node.setName(authRule.getTitle());
            node.setIsParent(false);
            node.setChecked(false);
            node.setIcon(cxt + "/ui/plugins/zTree_v3/css/zTreeStyle/img/diy/5.png");
            nodeList.add(node);
        }
        return nodeList;
    }
}
