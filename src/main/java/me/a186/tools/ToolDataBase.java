package me.a186.tools;

import com.jfinal.kit.PropKit;
import me.a186.constant.ConstantInit;
import me.a186.dto.DataBase;

/**
 * 数据库导入导出处理
 * Created by Punk on 2016/2/21.
 */
public abstract class ToolDataBase {
    /**
     * 分解数据库连接url
     */
    public static DataBase getDbInfo() {
        String driverClass = null;
        String jdbcUrl = null;
        String userName = null;
        String passWord = null;
        String ip = null;
        String port = null;
        String dbName = null;

        //判断数据库类型
        String db_type = PropKit.get(ConstantInit.db_type_key);
        if (db_type.equals(ConstantInit.db_type_mysql)) {//mysql数据库连接信息
            driverClass = PropKit.get(ConstantInit.db_connection_mysql_driverClass);
            jdbcUrl = PropKit.get(ConstantInit.db_connection_mysql_jdbcUrl);
            userName = PropKit.get(ConstantInit.db_connection_mysql_userName);
            passWord = PropKit.get(ConstantInit.db_connection_mysql_passWord);

            //解析数据库连接URL，获取数据库名称
            dbName = jdbcUrl.substring(jdbcUrl.indexOf("//") + 2);
            dbName = dbName.substring(dbName.indexOf("/") + 1, dbName.indexOf("?"));

            // 解析数据库连接URL，获取数据库地址IP
            ip = jdbcUrl.substring(jdbcUrl.indexOf("//") + 2);
            ip = ip.substring(0, ip.indexOf(":"));

            // 解析数据库连接URL，获取数据库地址端口
            port = jdbcUrl.substring(jdbcUrl.indexOf("//") + 2);
            port = port.substring(port.indexOf(":") + 1, port.indexOf("/"));
        }

        //把数据库连接信息写入常用map
        DataBase db = new DataBase();
        db.setDriverClass(driverClass);
        db.setJdbcUrl(jdbcUrl);
        db.setUserName(userName);
        db.setPassWord(passWord);
        db.setIp(ip);
        db.setPort(port);
        db.setDbName(dbName);
        return db;
    }
}
