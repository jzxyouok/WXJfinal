package me.a186.tools.code;

import com.jfinal.plugin.activerecord.DbKit;
import org.beetl.core.BeetlKit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

/**
 * 简易辅助开发代码生产器
 * 描述：根据表，生成对应的.sql.xml文件、Model类、Service类、validator类、Controller类，
 * 不包含业务处理逻辑，考虑到开发的业务个性化，通用的生成意义不是太大，只做辅助开发
 * Created by Punk on 2016/2/20.
 */
public abstract class GenerateBase {
    /**
     * 二维数组说明：
     * 数据源（默认可以是null）
     * 表名
     * 主键名（默认可以是null）
     * 类名（不包含后缀.java）
     */
    public static String[][] tableArr = {
            {null, "t_user", null, "User"}
    };

    /**
     * 生成的包和类所在的源码根目录
     */
    public static String srcFolder = "src/main/java";

    /**
     * 生产的文件存放的包，公共基础包
     */
    public static String packageBase = "me.a186.mvc";

    /**
     * controller基础路径
     * render基础路径
     */
    public static String basePath = "a186";

    /**
     * 获取表的所有字段信息
     *
     * @param tableName
     * @return
     */
    public abstract List<TableColumnDto> getColumn(String tableName, String dbName);

    /**
     * 获取表所有数据类型
     *
     * @param tableName
     * @return
     */
    public Map<String, String> getJavaType(String tableName) {
        //获取字段数
        Map<String, String> columnJavaTypeMap = new HashMap<>();
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = DbKit.getConfig().getConnection();
            st = conn.createStatement();
            String sql = "select * from " + tableName + " where 1 !=1";
            rs = st.executeQuery(sql);
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            int columns = resultSetMetaData.getColumnCount();
            for (int i = 1; i <= columns; i++) {
                //获取字段名
                String columnName = resultSetMetaData.getColumnName(i).toLowerCase();
                String columnClassName = resultSetMetaData.getColumnClassName(i);
                if (columnClassName.equals("[B")) {
                    columnClassName = "byte[]";
                }
                columnJavaTypeMap.put(columnName, columnClassName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbKit.getConfig().close(rs, st, conn);
        }
        return columnJavaTypeMap;
    }

    /**
     * 获取所有数据类型
     *
     * @param tableName
     * @return
     */
    public Set<String> getJavaTypeList(String tableName) {
        Map<String, String> map = getJavaType(tableName);
        Set<String> keys = map.keySet();
        Set<String> typeSet = new HashSet<>();
        for (String key : keys) {
            String type = map.get(key);
            if (type.equals("byte[]") || type.startsWith("java.lang.")) {
                continue;
            }
            typeSet.add(map.get(key));
        }
        return typeSet;
    }

    /**
     * 生成Model
     *
     * @param className
     * @param classNameSmall
     * @param dataSource
     * @param tableName
     * @param pkName
     * @param columnDtoList
     */
    public void model(String className, String classNameSmall, String dataSource, String tableName, String pkName, List<TableColumnDto> columnDtoList) {
        Map<String, Object> paraMap = new HashMap<>();
        String packages = packageBase + "." + className.toLowerCase();
        paraMap.put("package", packages);
        paraMap.put("className", className);
        paraMap.put("dataSource", dataSource);
        paraMap.put("tableName", tableName);
        paraMap.put("pkName", pkName);
        paraMap.put("namespace", basePath + "." + classNameSmall);
        paraMap.put("columnList", columnDtoList);
        paraMap.put("dataTypes", getJavaTypeList(tableName));
        String path = System.getProperty("user.dir") + "/" + srcFolder + "/" + packages.replace(".", "/") + "/";
        String filePath = path + className + ".java";
        String baseFilePath = path + "Base" + className + ".java";
        createFileByTemplate("basemodel.html", paraMap, baseFilePath);
        createFileByTemplate("model.html", paraMap, filePath);
    }

    /**
     * 生成DTO
     *
     * @param className
     * @param dataSource
     * @param tableName
     * @param columnDtoList
     */
    public void dto(String className, String dataSource, String tableName, List<TableColumnDto> columnDtoList) {
        Map<String, Object> paraMap = new HashMap<>();
        String packages = packageBase + "." + className.toLowerCase();
        paraMap.put("package", packages);
        paraMap.put("className", className);
        paraMap.put("dataSource", dataSource);
        paraMap.put("tableName", tableName);
        paraMap.put("columnList", columnDtoList);
        paraMap.put("dataTypes", getJavaTypeList(tableName));
        String filePath = System.getProperty("user.dir") + "/" + srcFolder + "/" + packages.replace(".", "/") + className + "Dto.java";
        createFileByTemplate("dto,html", paraMap, filePath);
    }

    /**
     * 生成。sql。xml
     *
     * @param classNameSmall
     * @param tableName
     */
    public void sql(String classNameSmall, String tableName) {
        Map<String, Object> paraMap = new HashMap<>();
        String packages = packageBase + "." + classNameSmall.toLowerCase();
        paraMap.put("namespace", basePath + "." + classNameSmall);
        paraMap.put("tableName", tableName);
        String filePath = System.getProperty("user.dir") + "/" + srcFolder + "/" + packages.replace(".", "/") + "/" + classNameSmall + ".sql.xml";
        createFileByTemplate("sql.html", paraMap, filePath);
    }

    /**
     * 生成Controller
     *
     * @param className
     * @param classNameSmall
     * @param tableName
     */
    public void controller(String className, String classNameSmall, String tableName) {
        Map<String, Object> paraMap = new HashMap<>();
        String packages = packageBase + "." + classNameSmall.toLowerCase();
        paraMap.put("package", packages);
        paraMap.put("className", className);
        paraMap.put("classNameSmall", classNameSmall);
        paraMap.put("basePath", basePath);
        paraMap.put("tableName", tableName);
        String filePath = System.getProperty("user.dir") + "/" + srcFolder + "/" + packages.replace(".", "/") + "/" + className + "Controller.java";
        createFileByTemplate("controller.html", paraMap, filePath);
    }

    /**
     * 生成validator
     *
     * @param className
     * @param classNameSmall
     */
    public void validator(String className, String classNameSmall) {
        Map<String, Object> paraMap = new HashMap<>();
        String packages = packageBase + "." + classNameSmall.toLowerCase();
        paraMap.put("package", packages);
        paraMap.put("className", className);
        paraMap.put("classNameSmall", classNameSmall);
        paraMap.put("basePath", basePath);
        String filePath = System.getProperty("user.dir") + "/" + srcFolder + "/" + packages.replace(".", "/") + "/" + className + "Validator.java";
        createFileByTemplate("validator.html", paraMap, filePath);
    }

    /**
     * 生成Service
     *
     * @param className
     * @param classNameSmall
     */
    public void service(String className, String classNameSmall) {
        Map<String, Object> paraMap = new HashMap<>();
        String packages = packageBase + "." + classNameSmall.toLowerCase();
        paraMap.put("package", packages);
        paraMap.put("className", className);
        paraMap.put("classNameSmall", classNameSmall);
        paraMap.put("namespace", srcFolder + "." + classNameSmall);

        String filePath = System.getProperty("user.dir") + "/" + srcFolder + "/" + packages.replace(".", "/") + "/" + className + "Service.java";
        createFileByTemplate("service.html", paraMap, filePath);
    }

    /**
     * 生成form.html
     *
     * @param classNameSmall
     */
    public void form(String classNameSmall, List<TableColumnDto> colunmList) {
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("classNameSmall", classNameSmall);
        paraMap.put("colunmList", colunmList);

        String filePath = System.getProperty("user.dir") + "/WebContent/WEB-INF/view/" + basePath + "/" + classNameSmall + "/form.html";
        createFileByTemplate("form.html", paraMap, filePath);
    }

    /**
     * 生成view.html
     *
     * @param classNameSmall
     */
    public void view(String classNameSmall, List<TableColumnDto> colunmList) {
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("classNameSmall", classNameSmall);
        paraMap.put("colunmList", colunmList);

        String filePath = System.getProperty("user.dir") + "/WebContent/WEB-INF/view/" + basePath + "/" + classNameSmall + "/view.html";
        createFileByTemplate("view.html", paraMap, filePath);
    }

    /**
     * 根据具体模版生产文件
     *
     * @param templateFileName
     * @param paraMap
     * @param filePath
     */
    public static void createFileByTemplate(String templateFileName, Map<String, Object> paraMap, String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                return;
            }
            Class<?> classes = Class.forName("me.a186.tools.code.GenerateBase");
            InputStream controllerInputStream = classes.getResourceAsStream(templateFileName);
            int count = 0;
            while (count == 0) {
                count = controllerInputStream.available();
            }
            byte[] bytes = new byte[count];
            int readCount = 0;//已经成功读取的字节的个数
            while (readCount < count) {
                readCount += controllerInputStream.read(bytes, readCount, count - readCount);
            }
            String template = new String(bytes);
            String javaSrc = BeetlKit.render(template, paraMap);

            File path = new File(file.getParent());
            if (!path.exists()) {
                path.mkdirs();
            }
            BufferedWriter output = new BufferedWriter(new FileWriter(file));
            output.write(javaSrc);
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
