package com.itheima.mobilesafe.utils;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Catherine on 2016/10/11.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class MemoryUtils {
    private final static String TAG = "MemoryUtils";
    private final static int SD_DISABLE = 0;

    /**
     * 注册以取得结果
     */
    public interface OnResponse {
        void onSuccess();

        void onFail(int what, String errorMessage);
    }

    /**
     * 保存用户密码到手机ROM的文件里面<br>
     * 路径：data/data/包名/path/fileName
     *
     * @param context    上下文
     * @param path       e.g. backup -- 路径为data/data/包名/backup/config.txt
     * @param fileName   e.g. config.txt...
     * @param content    内容
     * @param onResponse 结果callback
     */
    public static void saveToRom(Context context, String path, String fileName, String content, @Nullable OnResponse onResponse) throws IOException {
        // 私有的权限创建一个文件，并且获取他的输出流

/**
 * Context.MODE_PRIVATE代表只有同一个包可以用
 * 可以换成Context.MODE_WORLD_READABLE|Context.MODE_WORLD_WRITEABLE全局可读可写，其他程序可读写
 * Context.MODE_APPEND代表追加模式，写入数据会存成下一笔数据，不会复写，默认权限为MODE_PRIVATE
 *
 * 以私有方式创建的文件 permissions -rw-rw----
 * 全局可读 -rw-rw-r--
 * 全局可写 -rw-rw--w-
 * 全局可读写 -rw-rw-rw-
 */
        File dir = new File(
                Environment.getDataDirectory() + "/" + path + "/");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, fileName);
        if (!file.exists())
            file.createNewFile();

        FileOutputStream fos = context.openFileOutput(path + fileName,
                Context.MODE_PRIVATE);
        fos.write(content.getBytes());
        fos.flush();
        fos.close();
        if (onResponse != null)
            onResponse.onSuccess();

    }

    /**
     * 保存用户密码到SD card的文件里面
     * <p>
     * Android2.1路径：/sdcard/path/fileName <br>
     * Android2.2以上路径：/mnt/sdcard/path/fileName <br>
     * 例外路径：/excard/path/fileName<br>
     * 所以用Environment.getExternalStorageDirectory()+ "path/fileName"统一
     * <p>
     *
     * @param path       e.g. backup -- 路径为data/data/包名/backup/config.txt
     * @param fileName   e.g. config.txt...
     * @param content    内容
     * @param onResponse 结果callback
     */
    public static void saveToSD(String path, String fileName, String content, @Nullable OnResponse onResponse) throws IOException {
        // 代表SD卡可读可写
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {

            File dir = new File(
                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + path + "/");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(dir, fileName);
            if (!file.exists())
                file.createNewFile();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            fos.flush();
            fos.close();
            if (onResponse != null)
                onResponse.onSuccess();

        } else {
            if (onResponse != null)
                onResponse.onFail(SD_DISABLE, "SD卡不可用");
        }

    }
}
