package me.a186.tools;

import com.jfinal.kit.PropKit;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import me.a186.constant.ConstantCache;
import me.a186.constant.ConstantInit;

/**
 * Created by Punk on 2016/2/26.
 */
public class ToolCache {

    /**
     * 缓存类型
     */
    private static String cacheType;


    /**
     * 获取缓存类型
     *
     * @return
     */
    public static String getCacheType() {
        if (cacheType == null) {
            cacheType = PropKit.get(ConstantInit.config_cache_type);
        }
        return cacheType;
    }

    /**
     * 设置缓存
     *
     * @param key
     * @param value
     */
    public static void set(Object key, Object value) {
        if (getCacheType().equals(ConstantCache.cache_type_ehcache)) {
            CacheKit.put(ConstantCache.cache_name_ehcache_system, key, value);
        } else if (getCacheType().equals(ConstantCache.cache_type_redis)) {
            Cache cache = Redis.use(ConstantCache.cache_name_redis_system);
            cache.set(key, value);
        }
    }

    /**
     * 获取缓存
     *
     * @param key
     * @param <T>
     * @return
     */
    public static <T> T get(Object key) {
        if (getCacheType().equals(ConstantCache.cache_type_ehcache)) {
            return CacheKit.get(ConstantCache.cache_name_ehcache_system, key);
        } else if (getCacheType().equals(ConstantCache.cache_type_redis)) {
            Cache cache = Redis.use(ConstantCache.cache_name_redis_system);
            return cache.get(key);
        }
        return null;
    }

    /**
     * 删除缓存
     *
     * @param key
     */
    public static void remove(Object key) {
        if (getCacheType().equals(ConstantCache.cache_type_ehcache)) {
            CacheKit.remove(ConstantCache.cache_name_ehcache_system, key);

        } else if (getCacheType().equals(ConstantCache.cache_type_redis)) {
            Cache cache = Redis.use(ConstantCache.cache_name_redis_system);
            cache.del(key);
        }
    }
}
