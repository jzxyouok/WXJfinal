package me.a186.plugin;

import com.jfinal.plugin.IPlugin;
import com.jfinal.upload.OreillyCos;
import com.oreilly.servlet.multipart.FileRenamePolicy;
import me.a186.tools.ToolRandoms;

import java.io.File;

/**
 * 配置文件上传命名策略
 * Created by Punk on 2016/2/26.
 */
public class FileRenamePlugin implements IPlugin {
    @Override
    public boolean start() {
        OreillyCos.setFileRenamePolicy(new FileRenamePolicy() {
            @Override
            public File rename(File f) {
                String ext = "";
                int pot = f.getName().lastIndexOf(".");
                if (pot != -1) {
                    ext = f.getName().substring(pot);
                } else {
                    ext = "";
                }
                String newName = ToolRandoms.getUuid(true) + ext;
                f = new File(f.getParent(), newName);
                return f;
            }
        });
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }
}
