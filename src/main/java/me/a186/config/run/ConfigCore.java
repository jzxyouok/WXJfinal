package me.a186.config.run;

import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import me.a186.constant.ConstantInit;
import me.a186.dto.DataBase;
import me.a186.tools.ToolBeetl;
import me.a186.tools.ToolDataBase;
import org.apache.log4j.Logger;

import javax.tools.Tool;

/**
 * 独立启动Jfinal中的插件，不依赖web容器调用插件
 * Created by Punk on 2016/2/21.
 */
public class ConfigCore {
    private static Logger log = Logger.getLogger(ConfigCore.class);

    private static volatile ConfigCore configCore;

    public static ConfigCore getSingleton() {
        if (configCore == null) {
            synchronized (ConfigCore.class) {
                if (configCore == null) {
                    configCore = new ConfigCore();
                }
            }
        }
        return configCore;
    }

    private ConfigCore() {
        PropKit.use("init.properties");

        log.info("Beetl设置");
        ToolBeetl.register();

        log.info("configPlugin 配置Druid数据库连接池连接属性");
        DataBase db = ToolDataBase.getDbInfo();
        String driverClass = db.getDriverClass();
        String jdbcUrl = db.getJdbcUrl();
        String userName = db.getUserName();
        String password = db.getPassWord();
        DruidPlugin druidPlugin = new DruidPlugin(jdbcUrl, userName, password, driverClass);

        log.info("configPlugin 配置Druid数据库连接池大小");
        druidPlugin.set(
                PropKit.getInt(ConstantInit.db_initialSize),
                PropKit.getInt(ConstantInit.db_minIdle),
                PropKit.getInt(ConstantInit.db_maxActive)
        );

        log.info("configPlugin 配置ActiveRecord插件");
        ActiveRecordPlugin arp = new ActiveRecordPlugin(ConstantInit.db_dataSource_main, druidPlugin);
        boolean devMode = Boolean.parseBoolean(PropKit.get(ConstantInit.config_devMode));
        arp.setDevMode(devMode);//设置开发模式
        arp.setShowSql(devMode);//设计是否显示SQL
        arp.setContainerFactory(new CaseInsensitiveContainerFactory(true));//大小写不敏感

        log.info("configPlugin 数据库类型判断");
        String db_type = PropKit.get(ConstantInit.db_type_key);
        if (db_type.equals(ConstantInit.db_type_mysql)) {
            log.info("configPlugin 使用数据库类型是 mysql");
            arp.setDialect(new MysqlDialect());
        }

        druidPlugin.start();

        //TODO 表扫描注册


//        arp.addMapping("pt_user", "ids", User.class);
        arp.start();
    }
}
