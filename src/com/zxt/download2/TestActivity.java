
package com.zxt.download2;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RemoteViews;
import android.widget.Toast;

public class TestActivity extends Activity implements OnClickListener {

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
                downloadTask.setFilePath("/sdcard/");
                DownloadTaskManager.getInstance(this).startDownload(downloadTask);

                Toast.makeText(this, "add 1", 1).show();
                break;
            case R.id.download_add2:
                DownloadTask downloadTask2 = new DownloadTask(
                        "http://mirror.bjtu.edu.cn/apache/axis/axis2/java/core/1.6.2/axis2-eclipse-service-plugin-1.6.2.zip");
                downloadTask2.setFileName("axis2.zip");
                downloadTask2.setTitle("axis2");
                downloadTask2.setFilePath("/sdcard/");

                DownloadTaskManager.getInstance(this).startDownload(downloadTask2);
                break;

            case R.id.download_add3:
                DownloadTask downloadTask3 = new DownloadTask(
                        "http://f.youku.com/player/getFlvPath/sid/00_00/st/flv/fileid/0300020100500279A8EDF201FB2AC9CED4C7EB-F1FB-4534-271A-55641E7C9F00?K=53d7cd473c48d14928280838,k2:1b49ac957c23135ef");
                downloadTask3.setFileName("骑车去伦敦-一个梦想照进现实的故事.flv");
                downloadTask3.setTitle("骑车去伦敦-一个梦想照进现实的故事.flv");
                downloadTask3.setFilePath("/sdcard/");

                DownloadTaskManager.getInstance(this).startDownload(downloadTask3);
                DownloadTaskManager.getInstance(this).addListener(downloadTask3,
                        new DownloadNotificationListener(mContext, downloadTask3));
                break;

            case R.id.download_add4:
                DownloadTask downloadTask4 = new DownloadTask(
                        "http://f.youku.com/player/getFlvPath/sid/00_00/st/flv/fileid/03000201004F9583B02DC700FC8CA43B252233-0E5F-9E51-80B6-7BD6084094C9?K=e411d5183fd588e724112818,k2:1e45a2103b6b6f972");
                downloadTask4.setFileName("《摩尔庄园2海妖宝藏》预告 黄渤王珞丹献声3D进军暑期档.flv");
                downloadTask4.setTitle("《摩尔庄园2海妖宝藏》预告 黄渤王珞丹献声3D进军暑期档.flv");
                downloadTask4.setFilePath("/sdcard/");

                DownloadTaskManager.getInstance(this).startDownload(downloadTask4);
                DownloadTaskManager.getInstance(this).addListener(downloadTask4,
                        new DownloadNotificationListener(mContext, downloadTask4));
                break;
            case R.id.download_add5:
                DownloadTask downloadTask5 = new DownloadTask(
                        "http://f.youku.com/player/getFlvPath/sid/00_00/st/flv/fileid/0300020100505A7B45AD42061A186666286CC5-D7C8-D5ED-D564-25FE9D457D5C?K=f15c750e224f337a261c9827,k2:1aa0d089ebbe44422");
                downloadTask5.setFileName("qufen.flv");
                downloadTask5.setTitle("《飓风营救2》发中文预告 为家人血战引发讨论.flv");
                downloadTask5.setFilePath("/sdcard/");

                DownloadTaskManager.getInstance(this).startDownload(downloadTask5);
                DownloadTaskManager.getInstance(this).addListener(downloadTask5,
                        new DownloadNotificationListener(mContext, downloadTask5));

                break;
            case R.id.download_add6:
                DownloadTask downloadTask6 = new DownloadTask(
                        "http://mirror.bit.edu.cn/apache/empire-db/2.3.0/apache-empire-db-2.3.0-dist.zip");
                downloadTask6.setFileName("axis6.zip");
                downloadTask6.setTitle("axis6");
                downloadTask6.setFilePath("/sdcard/");

                DownloadTaskManager.getInstance(this).startDownload(downloadTask6);
                DownloadTaskManager.getInstance(this).addListener(downloadTask6,
                        new DownloadNotificationListener(mContext, downloadTask6));
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
