package com.itheima.mobilesafe.db.dao;

/**
 * Created by Catherine on 2016/10/6.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public interface BaseDao {
    boolean find(String key) throws Exception;

    void remove(String key) throws Exception;

    Object queryAll() throws Exception;
}
