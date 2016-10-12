package com.itheima.mobilesafe.utils;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;

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
    private final static int PATH_ERROR = 1;
    private final static int FILE_NAME_ERROR = 2;

    public static class Result {
        public final static int ERROR = 0;
        public final static int SUCCESS = 1;
        private int what;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getWhat() {
            return what;
        }

        public void setWhat(int what) {
            this.what = what;
        }

        private String message;
    }

    /**
     * 注册以取得结果
     */
    public interface OnResponse {
        void onSuccess();

        void onFail(int what, String errorMessage);
    }

    /**
     * 保存数据到手机ROM的文件里面<br>
     * 路径：data/data/包名/path/fileName
     *
     * @param context    上下文
     * @param path       e.g. backup -- 路径为data/data/包名/backup/config.txt
     * @param fileName   e.g. config.txt...
     * @param content    内容
     * @param onResponse 结果callback
     */
    public static void saveStringToRom(Context context, String path, String fileName, String content, @Nullable OnResponse onResponse) throws IOException {
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
        //path检查
        Result checkPath = formatPath(path);
        if (checkPath.getWhat() == Result.ERROR) {
            if (onResponse != null)
                onResponse.onFail(PATH_ERROR, checkPath.getMessage());
            return;
        }

        //fileName检查
        Result checkFileName = formatFileName(fileName);
        if (checkFileName.getWhat() == Result.ERROR) {
            if (onResponse != null)
                onResponse.onFail(FILE_NAME_ERROR, checkFileName.getMessage());
            return;
        }

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
     * 保存数据到SD card的文件里面
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
    public static void saveStringToSD(String path, String fileName, String content, @Nullable OnResponse onResponse) throws IOException {
        // 代表SD卡可读可写
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {

            //path检查
            Result checkPath = formatPath(path);
            if (checkPath.getWhat() == Result.ERROR) {
                if (onResponse != null)
                    onResponse.onFail(PATH_ERROR, checkPath.getMessage());
                return;
            }

            //fileName检查
            Result checkFileName = formatFileName(fileName);
            if (checkFileName.getWhat() == Result.ERROR) {
                if (onResponse != null)
                    onResponse.onFail(FILE_NAME_ERROR, checkFileName.getMessage());
                return;
            }

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

    /**
     * 保存xml数据到SD card的文件里面
     * <p>
     * Android2.1路径：/sdcard/path/fileName <br>
     * Android2.2以上路径：/mnt/sdcard/path/fileName <br>
     * 例外路径：/excard/path/fileName<br>
     * 所以用Environment.getExternalStorageDirectory()+ "path/fileName"统一
     * <p>
     *
     * @param path       e.g. backup -- 路径为data/data/包名/backup/config.xml
     * @param fileName   e.g. config.xml...
     * @param content    内容
     * @param onResponse 结果callback
     */
    public static void saveXmlToSD(String path, String fileName, String content, @Nullable OnResponse onResponse) throws IOException {
        // 代表SD卡可读可写
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {

            //path检查
            Result checkPath = formatPath(path);
            if (checkPath.getWhat() == Result.ERROR) {
                if (onResponse != null)
                    onResponse.onFail(PATH_ERROR, checkPath.getMessage());
                return;
            }

            //fileName检查
            Result checkFileName = formatFileName(fileName);
            if (checkFileName.getWhat() == Result.ERROR) {
                if (onResponse != null)
                    onResponse.onFail(FILE_NAME_ERROR, checkFileName.getMessage());
                return;
            }

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

    public static Result formatFileName(String fileName) {
        Result result = new Result();
        if (TextUtils.isEmpty(fileName)) {
            result.setWhat(Result.ERROR);
            result.setMessage("文件名为空字串或null");
        } else {
            if (!fileName.contains(".")) {
                result.setWhat(Result.ERROR);
                result.setMessage("请输入副档名");
            } else {
                int indexOfDoc = fileName.lastIndexOf(".");
                String type = fileName.substring(indexOfDoc);
                String name = fileName.substring(0, indexOfDoc);
                if (type.equals(".txt") || type.equals(".text") || type.equals(".TXT") || type.equals(".TEXT"))
                    type = ".txt";
                else if (type.equals(".xml") || type.equals(".XML"))
                    type = ".xml";
                else if (type.equals(".dat") || type.equals(".date") || type.equals(".DAT") || type.equals(".DATA"))
                    type = ".dat";
                result.setWhat(Result.SUCCESS);
                result.setMessage(name + type);
            }
        }
        return result;
    }

    @Nullable
    public static Result formatPath(String path) {
        Result result = new Result();
        if (TextUtils.isEmpty(path)) {
            result.setWhat(Result.ERROR);
            result.setMessage("文件名为空字串或null");
        } else {
            String lastChar = String.valueOf(path.charAt(path.length() - 1));
            if (lastChar.equals("/"))
                path = path.substring(0, path.length());//拿掉最后一个"/"

            result.setWhat(Result.SUCCESS);
            result.setMessage(path);
        }
        return result;
    }
}
