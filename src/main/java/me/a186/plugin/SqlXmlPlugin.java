package me.a186.plugin;

import com.jfinal.plugin.IPlugin;
import me.a186.tools.ToolCache;
import me.a186.tools.ToolDirFile;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 加载sql文件
 * Created by Punk on 2016/2/26.
 */
public class SqlXmlPlugin implements IPlugin {

    protected static final Logger log = Logger.getLogger(SqlXmlPlugin.class);

    /**
     * sql放入cache中的key前缀
     */
    public static final String cacheStart_sql = "sql_";

    @Override
    public boolean start() {
        init(true);
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    /**
     * 加载sql语句到cache
     *
     * @param isInit 是否初始化加载
     */
    public static synchronized void init(boolean isInit) {
        //1.查找classes目录
        String classesPath = ToolDirFile.getSqlPath();
        List<File> sqlXmlFiles = new ArrayList<>();
        findByClasses(new File(classesPath), sqlXmlFiles);
        for (File file : sqlXmlFiles) {
            SAXReader reader = new SAXReader();
            try {
                Document doc = reader.read(file);
                parseDoc(isInit, file.getName(), doc);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解析xml
     *
     * @param isInit
     * @param fileName
     * @param doc
     */
    private static void parseDoc(boolean isInit, String fileName, Document doc) {
        Element root = doc.getRootElement();
        String namespace = root.attributeValue("namespace");
        if (null == namespace || namespace.trim().isEmpty()) {
            log.error("sql.xml文件" + fileName + "的命名空间不能为空");
            return;
        }

        for (Iterator<?> iterator = root.elementIterator(); iterator.hasNext(); ) {
            Element element = (Element) iterator.next();
            if (element.getName().toLowerCase().equals("sql")) {
                String id = element.attributeValue("id");
                String key = namespace + "." + id;

                if (null == id || id.trim().isEmpty()) {
                    log.error("sql.xml文件" + fileName + "存在没有id的sql语句");
                    continue;
                }

                String sql = element.getText();
                if (null == sql || sql.trim().isEmpty()) {
                    log.error("sql.xml文件" + fileName + "存在没有内容的sql语句，sqlId = " + key);
                    continue;
                }

                if (isInit && null != ToolCache.get(cacheStart_sql + key)) {
                    log.error("sql.xml文件" + fileName + "的sql语句重复，sqlId = " + key);
                    continue;
                } else if (null != ToolCache.get(cacheStart_sql + key)) {
                    log.error("sql.xml文件" + fileName + "的sql语句重复，sqlId = " + key);
                }

                sql = sql.replaceAll("[\\s]{2,}", " ");
                ToolCache.set(cacheStart_sql + key, sql);
                log.debug("sql加载, sql file = " + fileName + ", sql key = " + key + ", sql content = " + sql);
            }
        }
    }

    /**
     * 加载sql。xml文件
     *
     * @param dirFile
     * @param sqlXmlFiles
     */
    private static void findByClasses(File dirFile, List<File> sqlXmlFiles) {
        File[] fileList = dirFile.listFiles();
        for (File file : fileList) {
            if (file.getName().endsWith(".sql.xml")) {
                sqlXmlFiles.add(file);
            }
        }
    }
}
