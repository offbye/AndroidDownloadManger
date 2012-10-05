
package com.zxt.download2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class TestActivity extends Activity implements OnClickListener {

    private static final String SDCARD =  "/sdcard";

    protected static final String TAG = "TestActivity";

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mContext = this;
        setContentView(R.layout.test);
        findViewById(R.id.download_add).setOnClickListener(this);
        findViewById(R.id.download_add2).setOnClickListener(this);
        findViewById(R.id.download_add3).setOnClickListener(this);
        findViewById(R.id.download_add4).setOnClickListener(this);
        findViewById(R.id.download_add5).setOnClickListener(this);
        findViewById(R.id.download_add6).setOnClickListener(this);
        findViewById(R.id.download_list).setOnClickListener(this);
        findViewById(R.id.downloaded_list).setOnClickListener(this);

        DownloadListener mDownLoadListener = new DownloadListener() {
            @Override
            public void onDownloadFinish(String filepath) {
                Log.e(TAG, "filepath:" + filepath);

            }

            @Override
            public void onDownloadStart() {
                Log.i(TAG, "----开始下载");
            }

            @Override
            public void onDownloadPause() {
                Log.i(TAG, "----暂停下载");
            }

            @Override
            public void onDownloadStop() {
                Log.i(TAG, "----停止下载");
            }

            @Override
            public void onDownloadFail() {
                Log.i(TAG, "----下载失败");
            }

            @Override
            public void onDownloadProgress(int finishedSize, int totalSize, double progressPercent) {
                Log.i(TAG, "已下载：" + progressPercent);
            }

        };

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download_add:
                DownloadTask downloadTask = new DownloadTask(
                        "http://apache.etoak.com/ant/ivy/2.3.0-rc1/apache-ivy-2.3.0-rc1-src.zip");
                downloadTask.setFileName("apache-ivy.zip");
                downloadTask.setTitle("apache-ivy");
                downloadTask.setFilePath(SDCARD);
                DownloadTaskManager.getInstance(this).startDownload(downloadTask);

                Toast.makeText(this, "add task 1", Toast.LENGTH_SHORT).show();
                break;
            case R.id.download_add2:
                DownloadTask downloadTask2 = new DownloadTask(
                        "http://mirror.bjtu.edu.cn/apache/axis/axis2/java/core/1.6.2/axis2-eclipse-service-plugin-1.6.2.zip");
                downloadTask2.setFileName("axis2.zip");
                downloadTask2.setTitle("axis2");
                downloadTask2.setFilePath(SDCARD);

                DownloadTaskManager.getInstance(this).startDownload(downloadTask2);
                break;

            case R.id.download_add3:
                DownloadTask downloadTask3 = new DownloadTask(
                        "http://flv1.vodfile.m1905.com/movie/1209/120904A8CD8E096BBD237F.flv");
                downloadTask3.setFileName("就是闹着玩的.flv");
                downloadTask3.setTitle("就是闹着玩的");
                downloadTask3.setFilePath(SDCARD);

                DownloadTaskManager.getInstance(this).startDownload(downloadTask3);
                DownloadTaskManager.getInstance(this).addListener(downloadTask3,
                        new DownloadNotificationListener(mContext, downloadTask3));
                break;

            case R.id.download_add4:
                DownloadTask downloadTask4 = new DownloadTask(
                        "http://192.168.1.100/gw2.ts");
                downloadTask4.setFileName("gw2u.ts");
                downloadTask4.setTitle("gw2u");
                downloadTask4.setFilePath(SDCARD);

                DownloadTaskManager.getInstance(this).startDownload(downloadTask4);
                DownloadTaskManager.getInstance(this).addListener(downloadTask4,
                        new DownloadNotificationListener(mContext, downloadTask4));
                break;
            case R.id.download_add5:
                DownloadTask downloadTask5 = new DownloadTask(
                        "http://f.youku.com/player/getFlvPath/sid/00_00/st/flv/fileid/0300020100505A7B45AD42061A186666286CC5-D7C8-D5ED-D564-25FE9D457D5C?K=f15c750e224f337a261c9827,k2:1aa0d089ebbe44422");
                downloadTask5.setFileName("qufen.flv");
                downloadTask5.setTitle("《飓风营救2》发中文预告 为家人血战引发讨论.flv");
                downloadTask5.setFilePath(SDCARD);

                DownloadTaskManager.getInstance(this).startDownload(downloadTask5);
                DownloadTaskManager.getInstance(this).addListener(downloadTask5,
                        new DownloadNotificationListener(mContext, downloadTask5));

                break;
            case R.id.download_add6:
                DownloadTask downloadTask6 = new DownloadTask(
                        "http://mirror.bit.edu.cn/apache/empire-db/2.3.0/apache-empire-db-2.3.0-dist.zip");
                downloadTask6.setFileName("axis6.zip");
                downloadTask6.setTitle("axis6");
                downloadTask6.setFilePath(SDCARD);
                DownloadTaskManager.getInstance(this).addListener(downloadTask6,
                        new DownloadNotificationListener(mContext, downloadTask6));
                DownloadTaskManager.getInstance(this).startDownload(downloadTask6);

                break;

            case R.id.download_list:
                Toast.makeText(this, "list", 1).show();
                Intent i = new Intent(this, DownloadListActivity.class);
                i.putExtra(DownloadListActivity.DOWNLOADED, false);
                startActivity(i);
                break;
            case R.id.downloaded_list:
                Toast.makeText(this, "list", 1).show();
                Intent i2 = new Intent(this, DownloadListActivity.class);
                i2.putExtra(DownloadListActivity.DOWNLOADED, true);
                startActivity(i2);
                break;
            default:
                break;
        }

    }

    public static boolean isNetWorkOn(Context context) {
        ConnectivityManager cManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            info.getTypeName();
            return true;
        } else {
            return false;
        }
    }

}
