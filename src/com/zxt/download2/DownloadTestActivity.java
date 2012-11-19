
package com.zxt.download2;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.Toast;

public class DownloadTestActivity extends Activity implements OnClickListener {

    private static final String SDCARD = Environment.getExternalStorageDirectory().getPath();

    protected static final String TAG = "TestActivity";

    private Context mContext;

    private EditText mUrlTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mContext = this;
        setContentView(R.layout.download_test);

        mUrlTV = (EditText) findViewById(R.id.download_url_text);
        findViewById(R.id.download_add1).setOnClickListener(this);
        findViewById(R.id.download_add2).setOnClickListener(this);
        findViewById(R.id.download_add3).setOnClickListener(this);
        findViewById(R.id.download_add4).setOnClickListener(this);
        findViewById(R.id.download_add5).setOnClickListener(this);
        findViewById(R.id.download_add6).setOnClickListener(this);

        findViewById(R.id.download_list).setOnClickListener(this);
        findViewById(R.id.downloaded_list).setOnClickListener(this);
        Res.getInstance(mContext);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.download_add1) {
            Toast.makeText(mContext,
                    Res.getInstance(mContext).getString("download_deleted_task_ok"), 1).show();
            DownloadTask downloadTask = new DownloadTask(
                    "http://apache.etoak.com/ant/ivy/2.3.0-rc1/apache-ivy-2.3.0-rc1-src.zip",
                    null, "apache-ivy.zip", "apache-ivy.zip", null);
            DownloadTaskManager.getInstance(this).startDownload(downloadTask);
            Toast.makeText(this, R.string.download_task1, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.download_add2) {
            DownloadTask downloadTask2 = new DownloadTask(
                    "http://www.us.apache.org/dist/axis/axis2/java/core/1.6.2/axis2-eclipse-service-plugin-1.6.2.zip",
                    null, "axis2-eclipse-service-plugin-1.6.2.zip", "axis2  zip", null);
            DownloadTaskManager.getInstance(this).registerListener(downloadTask2,
                    new DownloadNotificationListener(mContext, downloadTask2));
            DownloadTaskManager.getInstance(this).startDownload(downloadTask2);
            Toast.makeText(this, R.string.download_task2, Toast.LENGTH_SHORT).show();

        } else if (id == R.id.download_add3) { // apk
            DownloadTask downloadTask3 = new DownloadTask(
                    "http://d2.eoemarket.com/upload/2012/0220/apps/5631/apks/157006/3edc770c-5d19-d052-bce4-b5d073894030.apk",
                    null, "tvguide.apk","China TVGuide",  null);
            downloadTask3.setThumbnail("file:///android_asset/download.png");//use asset image
            DownloadTaskManager.getInstance(this).registerListener(downloadTask3,
                    new DownloadNotificationListener(mContext, downloadTask3));
            DownloadTaskManager.getInstance(this).registerListener(downloadTask3,
                    new DownloadListener() {

                        @Override
                        public void onDownloadFinish(final String filepath) {
                            // install apk
                        	DownloadTestActivity.this.runOnUiThread(new Runnable(){

								@Override
								public void run() {
									Intent intent = new Intent();
			                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			                        intent.setAction(android.content.Intent.ACTION_VIEW);
			                        Uri uri = Uri.fromFile(new File(filepath));
			                        intent.setDataAndType(uri, "application/vnd.android.package-archive");
			                        startActivity(intent);									
								}});
   
                        }

                        @Override
                        public void onDownloadStart() {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onDownloadPause() {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onDownloadStop() {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onDownloadFail() {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onDownloadProgress(int finishedSize, int totalSize, int speed) {
                            // TODO Auto-generated method stub

                        }
                    });
            DownloadTaskManager.getInstance(this).startDownload(downloadTask3);

        } else if (id == R.id.download_add4) {
            DownloadTask downloadTask4 = new DownloadTask(
                    "http://vf1.mtime.cn/Video/2012/11/17/flv/121117084047608344.flv", SDCARD,
                    "Hobbit.flv", "The Hobbit: An Unexpected Journey", null);
            downloadTask4.setThumbnail("file:///sdcard/hobbit.jpg"); //use image file uri
            DownloadTaskManager.getInstance(this).registerListener(downloadTask4,
                    new DownloadNotificationListener(mContext, downloadTask4));
            DownloadTaskManager.getInstance(this).startDownload(downloadTask4);
        } else if (id == R.id.download_add5) {
            DownloadTask downloadTask5 = new DownloadTask(
                    "http://vf1.mtime.cn/Video/2012/11/17/flv/121117084112530978.flv", SDCARD,
                   "JackReacher.flv", "Jack Reacher ",  null);
            downloadTask5.setThumbnail("http://img31.mtime.cn/mt/2012/10/17/103856.98537644_75X100.jpg"); //use url image
            DownloadTaskManager.getInstance(this).registerListener(downloadTask5,
                    new DownloadNotificationListener(mContext, downloadTask5));
            DownloadTaskManager.getInstance(this).startDownload(downloadTask5);
        } else if (id == R.id.download_add6) {
            String url = mUrlTV.getText().toString().trim();
            if (!URLUtil.isHttpUrl(url)) {
                Toast.makeText(mContext, "not valid http url", Toast.LENGTH_SHORT).show();
                return;
            }
            DownloadTask downloadTask6 = new DownloadTask(url, SDCARD, url.substring(url
                    .lastIndexOf("/")), url.substring(url.lastIndexOf("/")), null);
            DownloadTaskManager.getInstance(this).registerListener(downloadTask6,
                    new DownloadNotificationListener(mContext, downloadTask6));
            DownloadTaskManager.getInstance(this).startDownload(downloadTask6);
        } else if (id == R.id.download_list) {
            Toast.makeText(this, R.string.go_to_downloading_list, 1).show();
            Intent i = new Intent(this, DownloadListActivity.class);
            i.putExtra(DownloadListActivity.DOWNLOADED, false);
            startActivity(i);
        } else if (id == R.id.downloaded_list) {
            Toast.makeText(this, R.string.go_to_downloaded_list, 1).show();
            Intent i2 = new Intent(this, DownloadListActivity.class);
            i2.putExtra(DownloadListActivity.DOWNLOADED, true);
            startActivity(i2);
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
