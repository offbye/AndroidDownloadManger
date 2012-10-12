
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

public class DownloadTestActivity extends Activity implements OnClickListener {

    private static final String SDCARD = Environment.getExternalStorageDirectory().getPath();

    protected static final String TAG = "TestActivity";

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mContext = this;
        setContentView(R.layout.download_test);
        findViewById(R.id.download_add).setOnClickListener(this);
        findViewById(R.id.download_add2).setOnClickListener(this);
        findViewById(R.id.download_add3).setOnClickListener(this);
        findViewById(R.id.download_add4).setOnClickListener(this);
        findViewById(R.id.download_add5).setOnClickListener(this);
        findViewById(R.id.download_add6).setOnClickListener(this);
        findViewById(R.id.download_list).setOnClickListener(this);
        findViewById(R.id.downloaded_list).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.download_add) {
            Toast.makeText(mContext, Res.getInstance(mContext).getString("download_deleted_task_ok"), 1).show();
            DownloadTask downloadTask = new DownloadTask(
                    "http://apache.etoak.com/ant/ivy/2.3.0-rc1/apache-ivy-2.3.0-rc1-src.zip",
                    SDCARD, "apache-ivy.zip", "apache-ivy.zip", null);
            DownloadTaskManager.getInstance(this).startDownload(downloadTask);
            Toast.makeText(this, "add task 1", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.download_add2) {
            DownloadTask downloadTask2 = new DownloadTask(
                    "http://mirror.bjtu.edu.cn/apache/axis/axis2/java/core/1.6.2/axis2-eclipse-service-plugin-1.6.2.zip",
                    SDCARD, "axis2.zip", "axis2.zip", null);
            DownloadTaskManager.getInstance(this).startDownload(downloadTask2);
        } else if (id == R.id.download_add3) {
            DownloadTask downloadTask3 = new DownloadTask(
                    "http://flv1.vodfile.m1905.com/movie/1209/120904A8CD8E096BBD237F.flv",
                    null, "就是闹着玩的.flv", "就是闹着玩的.flv", null);
            DownloadTaskManager.getInstance(this).startDownload(downloadTask3);
            DownloadTaskManager.getInstance(this).registerListener(downloadTask3,
                    new DownloadNotificationListener(mContext, downloadTask3));
        } else if (id == R.id.download_add4) {
            DownloadTask downloadTask4 = new DownloadTask(
                    "http://mmsvideopublic1.m1905.com/1209/12090396423D16DAA769B0.flv", SDCARD,
                    "6DAA769B0.flv", "伊能静谈及新片欲“投诉”", null);
            DownloadTaskManager.getInstance(this).registerListener(downloadTask4,
                    new DownloadNotificationListener(mContext, downloadTask4));
            DownloadTaskManager.getInstance(this).startDownload(downloadTask4);
        } else if (id == R.id.download_add5) {
            DownloadTask downloadTask5 = new DownloadTask(
                    "http://mmsvideopublic1.m1905.com/1209/120927A964F8A819E2B963.flv", SDCARD,
                    "9E2B963.flv", "《白鹿原》艰难登顶 整体萎靡国庆发力", null);
            DownloadTaskManager.getInstance(this).startDownload(downloadTask5);
            DownloadTaskManager.getInstance(this).registerListener(downloadTask5,
                    new DownloadNotificationListener(mContext, downloadTask5));
            DownloadTaskManager.getInstance(this).registerListener(downloadTask5,
                    new DownloadListener() {
                        @Override
                        public void onDownloadFinish(final String filepath) {
                            Log.i(TAG, "filepath:" + filepath);
                            DownloadTestActivity.this.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(
                                            mContext,
                                            String.format(
                                                    mContext.getString(R.string.download_file),
                                                    filepath), Toast.LENGTH_LONG).show();
                                }
                            });

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
                        public void onDownloadProgress(int finishedSize, int totalSize,
                                int speed) {
                            Log.i(TAG, "speed：" + speed);

                        }

                    });
        } else if (id == R.id.download_add6) {
            DownloadTask downloadTask6 = new DownloadTask(
                    "http://mirror.bit.edu.cn/apache/empire-db/2.3.0/apache-empire-db-2.3.0-dist.zip",
                    SDCARD, "axis6.zip", "axis6.zip", null);
            DownloadTaskManager.getInstance(this).registerListener(downloadTask6,
                    new DownloadNotificationListener(mContext, downloadTask6));
            DownloadTaskManager.getInstance(this).startDownload(downloadTask6);
        } else if (id == R.id.download_list) {
            Toast.makeText(this, "list", 1).show();
            Intent i = new Intent(this, DownloadListActivity.class);
            i.putExtra(DownloadListActivity.DOWNLOADED, false);
            startActivity(i);
        } else if (id == R.id.downloaded_list) {
            Toast.makeText(this, "list", 1).show();
            Intent i2 = new Intent(this, DownloadListActivity.class);
            i2.putExtra(DownloadListActivity.DOWNLOADED, true);
            startActivity(i2);
        } else {
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
