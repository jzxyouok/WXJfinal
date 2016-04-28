package me.a186.mvc.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;
import me.a186.dto.SplitPage;
import me.a186.tools.ToolRandoms;

/**
 * Model基础类
 * Created by Punk on 2016/2/25.
 */
public abstract class BaseModel<M extends Model<M>> extends Model<M> {

    /**
     * 字段描述：版本号
     * 字段类型 ：bigint
     */
    public static final String column_version = "version";

    /**
     * 重写save设置主键
     *
     * @return
     */
    @Override
    public boolean save() {
        String[] pkArr = getTable().getPrimaryKey();
        for (String pk : pkArr) {
            this.set(pk, ToolRandoms.getUuid(true));// 设置主键值
        }

        if (getTable().hasColumnLabel(column_version)) {// 是否需要乐观锁控制
            this.set(column_version, Long.valueOf(0)); // 初始化乐观锁版本号
        }
        return super.save();
    }

    /**
     * 获取主键值：非复合主键
     *
     * @return
     */
    public String getPKValue() {
        String[] pkNameArr = getTable().getPrimaryKey();
        if (pkNameArr.length == 1) {
            return this.getStr(pkNameArr[0]);
        } else {
            String pk = "";
            for (String pkName : pkNameArr) {
                pk += this.get(pkName) + ",";
            }
            return pk;
        }
    }

    /**
     * 获取表映射对象
     *
     * @return
     */
    protected Table getTable() {
        return TableMapping.me().getTable(getClass());
    }


}
