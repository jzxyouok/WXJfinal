package me.a186.mvc.user;

import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;
import me.a186.mvc.base.BaseService;
import me.a186.mvc.model.AuthGroup;
import me.a186.mvc.model.AuthGroupAccess;
import me.a186.mvc.model.AuthRule;
import me.a186.mvc.model.User;
import me.a186.tools.ToolRandoms;
import me.a186.tools.security.ToolPbkdf2;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by Punk on 2016/4/12.
 */
public class UserService extends BaseService {
    public static final UserService service = Enhancer.enhance(UserService.class);


    /**
     * 保存用户
     *
     * @param user
     */
    public void save(String password, User user) {
        String ids = user.getIds();
        if (null == ids) {
            //密码加密
            try {
                byte[] salt = ToolPbkdf2.generateSalt();//密码盐
                byte[] encryptedPassword = ToolPbkdf2.getEncryptedPassword(password, salt);
                user.setSalt(salt);
                user.setPassword(encryptedPassword);
            } catch (Exception e) {
                throw new RuntimeException("保存用户密码加密操作异常", e);
            }
            //保存用户
            user.setErrorcount(0L);
            user.setStatus("1");
            user.save();
        } else {
            user.update();
        }

        // 缓存
        User.dao.cacheAdd(user.getIds());
    }

    /**
     * 删除用户
     *
     * @param ids
     */
    @Before(Tx.class)
    public void delete(String ids) {
        String[] idsArr = splitByComma(ids);
        for (String id : idsArr) {
            User.dao.deleteById(id);
        }
    }

    /**
     * 重置密码
     *
     * @param ids
     * @return
     */
    public String resetPassword(String ids) {
        User user = User.dao.cacheGet(ids);

        byte[] salt = user.getSalt();
        String newPassword = ToolRandoms.getAuthCode(4);
        try {
            byte[] encryptedPassword = ToolPbkdf2.getEncryptedPassword(newPassword, salt);

            user.setPassword(encryptedPassword);
            user.update();
        } catch (Exception e) {
            throw new RuntimeException("重置用户密码加密操作异常", e);
        }
        return newPassword;
    }

    /**
     * 密码变更
     *
     * @param ids
     * @param passOld
     * @param passNew
     * @return
     */
    public boolean passChange(String ids, String passOld, String passNew) {
        User user = User.dao.findById(ids);

        //验证密码
        byte[] salt = user.getSalt();
        byte[] encryptedPassword = user.getPassword();
        try {
            boolean bool = ToolPbkdf2.authenticate(passOld, encryptedPassword, salt);
            if (bool) {
                byte[] saltNew = ToolPbkdf2.generateSalt();// 密码盐
                byte[] encryptedPasswordNew = ToolPbkdf2.getEncryptedPassword(passNew, saltNew);
                user.setSalt(saltNew);
                user.setPassword(encryptedPasswordNew);

                //更新用户
                user.update();
                //缓存
                User.dao.cacheAdd(ids);

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置用户角色
     *
     * @param userIds
     * @param groupIds
     */
    @Before(Tx.class)
    public void setGroup(String userIds, String groupIds) {
        //删除用户角色
        String delete = getSql(AuthGroup.sqlId_deleteByUser);
        Db.update(delete, userIds);

        //设置用户角色
        String[] groupIdsArr = splitByComma(groupIds);
        if(groupIdsArr!=null){
            for (String ids : groupIdsArr) {
                AuthGroupAccess access = new AuthGroupAccess();
                access.setUid(userIds);
                access.setGroupId(ids);
                access.save();
            }
        }
    }
}
