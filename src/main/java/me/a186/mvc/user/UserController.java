package me.a186.mvc.user;

import com.jfinal.aop.Before;
import me.a186.constant.ConstantInit;
import me.a186.constant.ConstantWebContext;
import me.a186.mvc.base.BaseController;
import me.a186.mvc.model.AuthGroup;
import me.a186.mvc.model.User;
import org.apache.log4j.Logger;

/**
 * 用户管理
 * Created by Punk on 2016/3/22.
 */
public class UserController extends BaseController {
    private static Logger log = Logger.getLogger(UserController.class);

    /**
     * 默认列表
     */
    public void index() {
        paging(ConstantInit.db_dataSource_main, User.sqlId_splitPageSelect, User.sqlId_splitPageFrom);
        render("/room/user/list.html");
    }

    /**
     * 新增用户
     */
    @Before(UserValidator.class)
    public void save() {
        String password = getPara("password");
        UserService.service.save(password, getModel(User.class));
        redirect("/jf/room/user");
    }

    /**
     * 进入编辑
     */
    public void edit() {
        setAttr("user", User.dao.findById(getPara()));
        render("/room/user/edit.html");
    }

    /**
     * 更新用户
     */
    @Before(UserValidator.class)
    public void update() {
        UserService.service.save(null, getModel(User.class));
        redirect("/jf/room/user");
    }

    /**
     * 删除
     */
    public void delete() {
        UserService.service.delete(getPara() == null ? ids : getPara());
        redirect("/jf/room/user");
    }


    /**
     * 禁用
     */
    public void disable() {
        User user = User.dao.findById(getPara());
        user.setStatus("2");
        user.update();
        redirect("/jf/room/user");
    }

    /**
     * 启用
     */
    public void enable() {
        User user = User.dao.findById(getPara());
        user.setStatus("1");
        user.update();
        redirect("/jf/room/user");
    }

    /**
     * 重置密码
     */
    public void resetPassword() {
        String newPassword = UserService.service.resetPassword(getPara());
        renderText(newPassword);
    }

    /**
     * 修改密码
     */
    public void passChange() {
        String userIds = getAttr(ConstantWebContext.request_cUserIds);
        String passOld = getPara("passOld");
        String passNew = getPara("passNew");
        boolean result = UserService.service.passChange(userIds, passOld, passNew);
        renderText(String.valueOf(result));
    }

    /**
     * 设置用户角色
     */
    public void setGroup() {
        String groupIds = getPara("groupIds");
        UserService.service.setGroup(ids, groupIds);
        renderText("");
    }
}
