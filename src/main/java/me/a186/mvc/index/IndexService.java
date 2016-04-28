package me.a186.mvc.index;

import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import me.a186.mvc.base.BaseService;
import me.a186.mvc.model.Modules;
import me.a186.mvc.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Punk on 2016/3/20.
 */
public class IndexService extends BaseService {

    public static final IndexService service = Enhancer.enhance(IndexService.class);

    /**
     * 查询用户可操作的菜单
     *
     * @param user
     * @return
     */
    public List<Modules> menu(User user) {
        String userIds = user.getIds();
        //查询根菜单节点
        String rootSql = getSql(Modules.sqlId_rootNode);
        List<Modules> oneList = Modules.dao.find(rootSql);

        //查询二级菜单
        String twoSql = getSql(Modules.sqlId_twoLevelMenu);
        for (Modules modules : oneList) {
            List<Modules> twoList = Modules.dao.find(twoSql, userIds, modules.getIds());
            if (twoList.size() != 0) {
                modules.put("subList", twoList);
            }
        }

        return oneList;
    }

    /**
     * 获取用户权限列表
     *
     * @param userIds
     * @return
     */
    public List<String> getAuthList(String userIds) {
        String sql = getSql(User.sqlId_getAuthList);
        List<String> authList = Db.query(sql, userIds);
        List<String> resultList = new ArrayList<>();
        for (String auth : authList) {
            String[] arr = auth.split(",");
            for (String a : arr) {
                resultList.add(a);
            }
        }
        return resultList;
    }
}
