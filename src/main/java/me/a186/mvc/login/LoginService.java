package me.a186.mvc.login;

import com.jfinal.aop.Enhancer;
import com.jfinal.kit.PropKit;
import me.a186.constant.ConstantInit;
import me.a186.constant.ConstantLogin;
import me.a186.interceptor.AuthInterceptor;
import me.a186.mvc.base.BaseService;
import me.a186.mvc.model.User;
import me.a186.tools.ToolDateTime;
import me.a186.tools.security.ToolPbkdf2;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.List;

/**
 * Created by Punk on 2016/3/17.
 */
public class LoginService extends BaseService {
    private static Logger log = Logger.getLogger(LoginService.class);

    public static final LoginService service = Enhancer.enhance(LoginService.class);


    /**
     * 用户登录后台验证
     *
     * @param request
     * @param response
     * @param username  账号
     * @param password  密码
     * @param autoLogin 是否自动登录
     * @return
     */
    public int login(HttpServletRequest request, HttpServletResponse response, String username, String password, boolean autoLogin) {
        //1.获取用户
        User user = null;
        Object userObj = User.dao.cacheGet(username);
        if (null != userObj) {
            user = (User) userObj;
        } else {
            user = User.dao.findByUsername(username);
            if (null == user) {
                return ConstantLogin.login_info_0;//用户不存在
            }
        }


        //2.停用账户
        String status = user.getStatus();
        if (status.equals("0")) {
            return ConstantLogin.login_info_1;
        }

        String ids = user.getIds();

        //3,密码错误次数超限
        long errorCount = user.getErrorcount();
        int passErrorCount = PropKit.getInt(ConstantInit.config_passErrorCount_key);
        if (errorCount >= passErrorCount) {
            Date stopDate = user.getStopdate();
            int hourSpace = ToolDateTime.getDateHourSpace(stopDate, ToolDateTime.getDate());
            int passErrorHour = PropKit.getInt(ConstantInit.config_passErrorHour_key);
            if (hourSpace < passErrorHour) {
                return ConstantLogin.login_info_2;// 密码错误次数超限，几小时内不能登录
            } else {
                User.dao.start(ids);
                //更新缓存
                User.dao.cacheAdd(ids);
            }
        }

        //4.验证密码
        byte[] salt = user.getSalt();//密码盐
        byte[] encryptedPassword = user.getPassword();
        boolean bool = false;
        try {
            bool = ToolPbkdf2.authenticate(password, encryptedPassword, salt);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (bool) {
            //密码验证成功
            AuthInterceptor.setCurrentUser(request, response, user, autoLogin);//设置登录账号
            return ConstantLogin.login_info_3;
        } else {
            //密码验证失败
            User.dao.stop(ToolDateTime.getSqlTimestamp(ToolDateTime.getDate()), errorCount + 1, ids);
            //更新缓存
            User.dao.cacheAdd(ids);
            return ConstantLogin.login_info_4;
        }
    }

    /**
     * 验证账号是否存在
     *
     * @param userIds
     * @param userName
     * @return 描述：新增用户时userIds为空，修改用户时userIds传值
     */
    public boolean valiUserName(String userIds, String userName) {
        User user = User.dao.findByUsername(userName);
        if (user == null) {
            return true;
        } else {
            if (userIds != null && !userIds.isEmpty()) {
                User user1 = User.dao.findById(userIds);
                if (userName.equals(user1.getUsername())) {
                    return true;
                }
            }
        }
        return false;
    }

}
