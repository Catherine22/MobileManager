package com.itheima.mobilesafe.utils;

import android.content.Context;

import com.itheima.mobilesafe.db.dao.BlacklistDao;
import com.itheima.mobilesafe.db.dao.DaoConstants;
import com.itheima.mobilesafe.db.dao.BaseDao;

/**
 * Created by Catherine on 2016/10/6.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class DaoFactory {
    public BaseDao createDao(Context ctx, int type) {
        BaseDao baseDao;
        switch (type) {
            case DaoConstants.BLACKLIST:
                baseDao = new BlacklistDao(ctx);
                break;
            default:
                baseDao = null;
        }
        return baseDao;
    }
}
