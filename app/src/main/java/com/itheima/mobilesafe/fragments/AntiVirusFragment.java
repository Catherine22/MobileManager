package com.itheima.mobilesafe.fragments;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.Encryption;

import java.util.List;

/**
 * Created by Catherine on 2016/8/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class AntiVirusFragment extends Fragment {

    private static final String TAG = "AntiVirusFragment";
    private ImageView iv_scan;
    private ProgressBar pb;
    private PackageManager pm;

    public static AntiVirusFragment newInstance() {
        return new AntiVirusFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anti_virus, container, false);
        iv_scan = (ImageView) view.findViewById(R.id.iv_scan);
        Animation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(1000);
        iv_scan.startAnimation(animation);

        pb = (ProgressBar) view.findViewById(R.id.pb);

        new Thread() {
            public void run() {
                scan();
            }
        }.start();

        return view;
    }

    /**
     * 杀毒软件的工作原理：
     * <p>
     * 1.什么是计算机病毒？
     * 特殊的程序，运行起来才能成为病毒，如果没有运行就是一个普通的文件。
     * 是否具有恶意性。
     * <p>
     * 盗号的木马。
     * 窃取隐私。
     * 敲诈
     * 具有恶意性的特殊的软件，如果没有运行，就是一个特殊的文件。
     * <p>
     * 2. 如何查杀病毒。
     * kv-20 ： kill-virus 20个
     * kv300+，也是一个病毒。
     * <p>
     * 传统杀毒软件的工作原理：
     * 扫描硬盘上的每个文件，分析这些文件特征码，
     * 查看病毒文件的特征是否在病毒数据库存在。
     * 校验文件的md5 sha1签名。
     * 算法提权软件的关键信息。
     * 遍历文件里面的字符串。http://
     * <p>
     * 3.查杀已知的病毒，不能查杀新的未知病毒。
     * 主动防御：检查软件的行为。
     * <p>
     * <p>
     * 杀毒软件的病毒库是怎么收集的。
     * 服务器集群，主节点。蜜罐。
     * 互联网云安全计划。
     * <p>
     * 杀毒引擎，优化后的一个数据库查询算法：
     * 病毒数据库2000万条数据。
     * 电脑上900万个文件 分类 .mp3 .jpg .txt .exe .cmd .msi .dll
     */
    private void scan() {
        pm = getActivity().getPackageManager();
        List<ApplicationInfo> installedApplications = pm.getInstalledApplications(0);
        pb.setMax(installedApplications.size());
        for (int i = 0; i < installedApplications.size(); i++) {
//            String dataDir = installedApplications.get(i).dataDir;//对应的是资源目录
            String sourceDir = installedApplications.get(i).sourceDir;//对应的是apk的全路径

            String signature = Encryption.doMd5Securly(sourceDir);
            pb.setProgress(i + 1);
            CLog.d(TAG, installedApplications.get(i).loadLabel(pm) + ":" + signature);
        }
    }
}
