package me.a186.config.mapping;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.config.Plugins;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import me.a186.constant.ConstantInit;
import me.a186.dto.DataBase;
import me.a186.mvc.model._MappingKit;
import me.a186.tools.ToolDataBase;
import org.apache.log4j.Logger;

/**
 * 数据库连接和映射配置
 * Created by Punk on 2016/2/25.
 */
public class PlatformMapping {
    private static Logger log = Logger.getLogger(PlatformMapping.class);

    public PlatformMapping(Plugins plugins) {
        log.info("configPlugin 配置Druid数据库连接池连接属性");
        DataBase db = ToolDataBase.getDbInfo();
        String driverClass = db.getDriverClass();
        String jdbcUrl = db.getJdbcUrl();
        String username = db.getUserName();
        String password = db.getPassWord();
        DruidPlugin druidPlugin = new DruidPlugin(jdbcUrl, username, password, driverClass);

        log.info("configPlugin 配置Druid数据库连接池大小");
        druidPlugin.set(PropKit.getInt(ConstantInit.db_initialSize), PropKit.getInt(ConstantInit.db_minIdle), PropKit.getInt(ConstantInit.db_maxActive));

        log.info("configPlugin 配置Druid数据库连接池过滤配置");
        druidPlugin.addFilter(new StatFilter());
        WallFilter wall = new WallFilter();
        wall.setDbType(PropKit.get(ConstantInit.db_type_key));
        WallConfig config = new WallConfig();
        config.setFunctionCheck(false);//支持数据库函数
        wall.setConfig(config);
        druidPlugin.addFilter(wall);

        log.info("configPlugin 配置ActiveRecordPlugin插件");
        ActiveRecordPlugin arp = new ActiveRecordPlugin(ConstantInit.db_dataSource_main, druidPlugin);
        boolean devMode = Boolean.parseBoolean(PropKit.get(ConstantInit.config_devMode));
        arp.setDevMode(devMode);//设置开发模式
        arp.setShowSql(devMode);//是否显示SQL
        arp.setContainerFactory(new CaseInsensitiveContainerFactory(true));//大小写不敏感

        log.info("configPlugin 使用数据库类型是Mysql");
        arp.setDialect(new MysqlDialect());

        log.info("configPlugin 注册druidPlugin插件");
        plugins.add(druidPlugin);//多数据源继续添加

        log.info("configPlugin 注册ActiveRecordPlugin插件");
        plugins.add(arp);

        log.info("configPlugin 表手工注册");
        _MappingKit.mapping(arp);
    }
}
