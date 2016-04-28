package me.a186.mvc.base;

/**
 * 定义Model缓存
 * Created by Punk on 2016/3/16.
 */
public interface BaseModelCache<M> {
    /**
     * 添加或者更新缓存
     */
    public void cacheAdd(String ids);

    /**
     * 删除缓存
     */
    public void cacheRemove(String ids);

    /**
     * 获取缓存
     *
     * @param key
     * @return
     */
    public M cacheGet(String key);
}
