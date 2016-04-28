package me.a186.tools.code;

import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import me.a186.config.run.ConfigCore;
import me.a186.constant.ConstantInit;
import me.a186.dto.DataBase;
import me.a186.tools.ToolDataBase;
import me.a186.tools.ToolString;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Punk on 2016/2/21.
 */
public class GenerateMySQL extends GenerateBase {
    private static Logger log = Logger.getLogger(GenerateMySQL.class);

    public static void main(String[] args) {
        log.info("启动ConfigCore start ......");
        ConfigCore.getSingleton();
        log.info("启动ConfigCore end ......");

        String db_type = PropKit.get(ConstantInit.db_type_key);
        log.info("db_type = " + db_type);
        if (!db_type.equals(ConstantInit.db_type_mysql)) {
            throw new RuntimeException("请设置init.properties配置文件db.type = mysql");
        }

        log.info("configPlugin 配置Druid数据库连接池连接属性");
        DataBase db = ToolDataBase.getDbInfo();
        String username = db.getUserName();
        String password = db.getPassWord();
        String jdbcUrl = db.getJdbcUrl();
        String dbName = ToolDataBase.getDbInfo().getDbName();
        jdbcUrl = jdbcUrl.replace(dbName, "information_schema");
        DruidPlugin druidPluginIS = new DruidPlugin(jdbcUrl, username, password, "com.mysql.jdbc.Driver");
        druidPluginIS.start();

        log.info("configPlugin 配置ActiveRecord插件");
        ActiveRecordPlugin arpIS = new ActiveRecordPlugin("information_schema", druidPluginIS);
        arpIS.setDevMode(true); // 设置开发模式
        arpIS.setShowSql(true); // 是否显示SQL
        arpIS.setContainerFactory(new CaseInsensitiveContainerFactory(true));// 大小写不敏感
        arpIS.setDialect(new MysqlDialect());
        arpIS.start();

        GenerateBase base = new GenerateMySQL();
        for (int i = 0; i < tableArr.length; i++) {
            //数据源名称
            String dataSource = tableArr[i][0];
            //表名
            String tableName = tableArr[i][1];
            //主键
            String pkName = tableArr[i][2];
            //类名
            String className = tableArr[i][3];
            //类名首字母小写
            String classNameSmall = ToolString.toLowerCaseFirstOne(className);

            List<TableColumnDto> columnDtoList = base.getColumn(tableName, dbName);

            //1.生成sql文件
            base.sql(classNameSmall, tableName);
            //2.生成model
            base.model(className, classNameSmall, dataSource, tableName, pkName, columnDtoList);
            //3.生成validator
//            base.validator(className, classNameSmall);
//            //4.生成controller
//            base.controller(className, classNameSmall, tableName);
//            //5.生成service
//            base.service(className, classNameSmall);
        }
        System.exit(0);
    }

    @Override
    public List<TableColumnDto> getColumn(String tableName, String dbName) {
        List<TableColumnDto> list = new ArrayList<>();

        String tableDesc = Db.use("information_schema").findFirst("select * from tables where table_schema = ? and table_name = ?", dbName, tableName).getStr("table_COMMENT");

        List<Record> listColumn = Db.use("information_schema").find("select * from columns where table_schema = ? and table_name = ?", dbName, tableName);
        Map<String, String> columnJavaTypeMap = getJavaType(tableName);

        for (Record record : listColumn) {
            String column_name = record.getStr("column_name");
            String column_type = record.getStr("column_type");
            String character_maximum_length = String.valueOf(record.getNumber("CHARACTER_MAXIMUM_LENGTH"));
            String column_comment = record.getStr("COLUMN_COMMENT");

            //需要跳过的字段
            if ("xxx".equals(column_name) || "yyy".equals(column_name) || "zzz".equals(column_name)) {
                continue;
            }

            TableColumnDto table = new TableColumnDto();
            table.setTable_name(tableName);
            table.setTable_desc(tableDesc);

            table.setColumn_name(column_name);
            table.setColumn_name_upperCaseFirstOne(ToolString.toUpperCaseFirstOne(column_name));
            table.setColumn_type(column_type);
            table.setColumn_length(character_maximum_length);
            table.setColumn_desc(column_comment);

            table.setColumn_className(columnJavaTypeMap.get(column_name.toLowerCase()));

            list.add(table);
        }
        return list;
    }
}
