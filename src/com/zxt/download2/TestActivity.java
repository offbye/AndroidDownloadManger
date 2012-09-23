
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

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
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
                        "http://mirror.bit.edu.cn/apache/buildr/1.4.7/buildr-1.4.7.zip");
                downloadTask3.setFileName("axis3.zip");
                downloadTask3.setTitle("axis3");
                downloadTask3.setFilePath("/sdcard/");

                DownloadTaskManager.getInstance(this).startDownload(downloadTask3);
                break;

            case R.id.download_add4:
                DownloadTask downloadTask4 = new DownloadTask(
                        "http://mirror.bit.edu.cn/apache/empire-db/2.4.0/apache-empire-db-2.4.0-dist.zip");
                downloadTask4.setFileName("axis4.zip");
                downloadTask4.setTitle("axis4");
                downloadTask4.setFilePath("/sdcard/");

                DownloadTaskManager.getInstance(this).startDownload(downloadTask4);
                final Notification n = addNotifiction(downloadTask4.getTitle());
                final int nid = downloadTask4.getUrl().hashCode();
                DownloadTaskManager.getInstance(this).addListener(downloadTask4,
                        new DownloadListener() {

                            @Override
                            public void onDownloadStop() {
                                n.contentView.setTextViewText(R.id.notify_state,
                                        mContext.getString(R.string.download_stopped));

                                mNotificationManager.notify(nid, n);
                            }

                            @Override
                            public void onDownloadStart() {
                                mNotificationManager.notify(nid, n);
                            }

                            @Override
                            public void onDownloadProgress(int finishedSize, int totalSize,
                                    double progressPercent) {
                                n.contentView.setTextViewText(
                                        R.id.notify_state,
                                        mContext.getString(R.string.downloading_msg)
                                                + (int) Math.round(progressPercent) + "%");
                                n.contentView.setProgressBar(R.id.notify_processbar, 100,
                                        (int) Math.round(progressPercent), false);
                                mNotificationManager.notify(nid, n);
                            }

                            @Override
                            public void onDownloadPause() {
                                n.contentView.setTextViewText(R.id.notify_state,
                                        mContext.getString(R.string.download_paused));
                                mNotificationManager.notify(nid, n);
                            }

                            @Override
                            public void onDownloadFinish(String filepath) {
                                n.contentView.setTextViewText(R.id.notify_state,
                                        mContext.getString(R.string.download_finished));
                                mNotificationManager.notify(nid, n);
                            }

                            @Override
                            public void onDownloadFail() {
                                n.contentView.setTextViewText(R.id.notify_state,
                                        mContext.getString(R.string.download_failed));
                                mNotificationManager.notify(nid, n);
                            }
                        });
                break;
            case R.id.download_add5:
                DownloadTask downloadTask5 = new DownloadTask(
                        "http://mirror.bit.edu.cn/apache/gora/0.2.1/apache-gora-0.2.1-src.zip");
                downloadTask5.setFileName("axis5.zip");
                downloadTask5.setTitle("axis5");
                downloadTask5.setFilePath("/sdcard/");

                DownloadTaskManager.getInstance(this).startDownload(downloadTask5);
                break;
            case R.id.download_add6:
                DownloadTask downloadTask6 = new DownloadTask(
                        "http://mirror.bit.edu.cn/apache/empire-db/2.3.0/apache-empire-db-2.3.0-dist.zip");
                downloadTask6.setFileName("axis6.zip");
                downloadTask6.setTitle("axis6");
                downloadTask6.setFilePath("/sdcard/");

                DownloadTaskManager.getInstance(this).startDownload(downloadTask6);
                break;

            case R.id.download_list:
                Toast.makeText(this, "list", 1).show();
                Intent i = new Intent(this, Download2Activity.class);
                i.putExtra(Download2Activity.DOWNLOADED, false);
                startActivity(i);
                break;
            case R.id.downloaded_list:
                Toast.makeText(this, "list", 1).show();
                Intent i2 = new Intent(this, Download2Activity.class);
                i2.putExtra(Download2Activity.DOWNLOADED, true);
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
            return true;
        } else {
            return false;
        }
    }

    private NotificationManager mNotificationManager = null;

    public Notification addNotifiction(String title) {
        Notification notification = new Notification(R.drawable.ic_download_ing,
                getString(R.string.downloading_msg), System.currentTimeMillis());
        notification.icon = R.drawable.ic_download_ing;

        notification.contentView = new RemoteViews(getApplication().getPackageName(),
                R.layout.download_notify);
        notification.contentView.setProgressBar(R.id.notify_processbar, 100, 0, false);
        notification.contentView.setTextViewText(R.id.notify_state,
                getString(R.string.downloading_msg));

        notification.contentView.setTextViewText(R.id.notify_text, title);

        notification.contentIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                Download2Activity.class), 0);
        return notification;

    }

}
