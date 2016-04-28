package me.a186.tools;

import com.jfinal.kit.PathKit;

import java.io.File;

/**
 * Created by Punk on 2016/2/26.
 */
public class ToolDirFile {

    private static String libPath;
    private static String classesPath;

    /**
     * 获取classes目录
     *
     * @return
     */
    public static String getClassesPath() {
        if (classesPath == null) {
            classesPath = PathKit.getWebRootPath() + File.separator + "WEB-INF" + File.separator + "classes";
        }
        return classesPath;
    }

    /**
     * 获取sql。xml存放目录
     *
     * @return
     */
    public static String getSqlPath() {
        return Thread.currentThread().getContextClassLoader().getResource("sql").getPath();
    }
}
