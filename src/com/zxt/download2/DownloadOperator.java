
package com.zxt.download2;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Download worker
 * 
 * @author offbye@gmail.com
 */
public class DownloadOperator extends AsyncTask<Void, Integer, Void> {

    private static final int BUFFER_SIZE = 8192;

    private static final int UPDATE_DB_PER_SIZE = 102400;

    /**
     * debug tag
     */
    private static final String TAG = "DownloadOperator";

    /**
     * 下载任务
     */
    private DownloadTask mDownloadTask;

    /**
     * 下载任务管理类
     */
    private DownloadTaskManager mDlTaskMng;

    /**
     * 如果暂停，停止下载，数据库更新
     */
    private volatile boolean mPause = false;

    /**
     * 如果停止，停止下载，删除已下载部分及数据库相关记录
     */
    private volatile boolean mStop = false;


    /**
     * Constructor
     * 
     * @param dlTaskMng 下载任务管理类
     * @param downloadTask 下载任务
     */
    DownloadOperator(DownloadTaskManager dlTaskMng, DownloadTask downloadTask) {
        mDownloadTask = downloadTask;
        mDlTaskMng = dlTaskMng;

        Log.d(TAG, "file path : " + mDownloadTask.getFilePath());
        Log.d(TAG, "file name : " + mDownloadTask.getFileName());
        Log.d(TAG, "download url : " + mDownloadTask.getUrl());
    }

    /**
     * <BR>
     * 
     * @param params Void...
     * @return Void
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected Void doInBackground(Void... params) {
        // 1. create file if not exist.
        createFile();

        mDownloadTask.setDownloadState(DownloadState.DOWNLOADING);
        mDlTaskMng.updateDownloadTask(mDownloadTask);

        for (DownloadListener l : mDlTaskMng.getListeners(mDownloadTask)) {
            l.onDownloadStart();
        }

        HttpURLConnection conn = null;
        RandomAccessFile accessFile = null;
        InputStream is = null;
        int finishedSize = 0;
        int totalSize = 0;
        int startSize = 0;
        try {
            URL url = new URL(mDownloadTask.getUrl());
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Range", "bytes=" + mDownloadTask.getFinishedSize() + "-"
                    + mDownloadTask.getTotalSize());
            //conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
            conn.setRequestProperty("Connection", "Keep-Alive");

            accessFile = new RandomAccessFile(mDownloadTask.getFilePath() + "/"
                    + mDownloadTask.getFileName(), "rwd");
            accessFile.seek(mDownloadTask.getFinishedSize());

            finishedSize = mDownloadTask.getFinishedSize();
            totalSize = mDownloadTask.getTotalSize();
            startSize = finishedSize;

            is = conn.getInputStream();
            Log.d(TAG, "downloadListeners size=" +  mDlTaskMng.getListeners(mDownloadTask).size());

            Log.i(TAG, "start writing data to file.");
            //int size = totalSize / 200 > UPDATE_DB_PER_SIZE ? UPDATE_DB_PER_SIZE : totalSize / 200; //降低刷新频率，下载100k刷新一次页面
            byte[] buffer = new byte[BUFFER_SIZE];
            int length = -1;
            long startTime = System.currentTimeMillis();
            int speed = 0;
            while ((length = is.read(buffer)) != -1) {
                // pause download
                if (mPause) {
                    Log.i(TAG, "pause download, exit download loop.");
                    mDownloadTask.setDownloadState(DownloadState.PAUSE);
                    mDownloadTask.setFinishedSize(finishedSize);

                    for (DownloadListener l : mDlTaskMng.getListeners(mDownloadTask)) {
                        l.onDownloadPause();
                    }
                    mDlTaskMng.updateDownloadTask(mDownloadTask);
                    return null;
                }

                // stop download, delete the download task
                if (mStop) {
                    Log.i(TAG, "stop download, exit download loop and delete download task.");
                    for (DownloadListener l : mDlTaskMng.getListeners(mDownloadTask)) {
                        l.onDownloadStop();
                    }
                    //mDlTaskMng.deleteDownloadTask(mDownloadTask);
                    return null;
                }

                finishedSize += length;
                accessFile.write(buffer, 0, length);

                // update database per 100K.
                if (finishedSize - mDownloadTask.getFinishedSize() > UPDATE_DB_PER_SIZE) {
                    mDownloadTask.setFinishedSize(finishedSize);
                    mDlTaskMng.updateDownloadTask(mDownloadTask);
                    speed =  (int)((finishedSize - startSize)/(int)(System.currentTimeMillis() + 1 - startTime));
                    publishProgress(finishedSize, totalSize, speed);
                } else if (totalSize - finishedSize < UPDATE_DB_PER_SIZE) {//如果剩余下载不足UPDATE_DB_PER_SIZE则继续发出通知
                    mDownloadTask.setFinishedSize(finishedSize);
                    speed =  (int)((finishedSize - startSize)/(int)(System.currentTimeMillis() + 1 - startTime));
                    publishProgress(finishedSize, totalSize, speed);
                }

            }

            mDownloadTask.setDownloadState(DownloadState.FINISHED);
            mDownloadTask.setFinishedSize(finishedSize);
            Log.d(TAG, "finished " +  mDownloadTask);
            mDlTaskMng.updateDownloadTask(mDownloadTask);

            for (DownloadListener l : mDlTaskMng.getListeners(mDownloadTask)) {
                l.onDownloadFinish(mDownloadTask.getFilePath() + "/" + mDownloadTask.getFileName());
            }
            mDlTaskMng.getListeners(mDownloadTask).clear();
            mDlTaskMng.removeListener(mDownloadTask);
            
        } catch (Exception e) {
            Log.e(TAG, "download exception : " + e.getMessage());
            mDownloadTask.setDownloadState(DownloadState.FAILED);
            mDownloadTask.setFinishedSize(finishedSize);

            for (DownloadListener l : mDlTaskMng.getListeners(mDownloadTask)) {
                l.onDownloadFail();
            }
            mDlTaskMng.updateDownloadTask(mDownloadTask);

        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (accessFile != null) {
                    accessFile.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * <BR>
     * 
     * @param values int型数组，0表示已完成大小，1表示总大小
     * @see android.os.AsyncTask#onProgressUpdate(Progress[])
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        int finished = values[0];
        int total = values[1];
        int speed = values[2];

        for (DownloadListener l : mDlTaskMng.getListeners(mDownloadTask)) {
            l.onDownloadProgress(finished, total, speed);
        }
    }

    /**
     * 暂停下载 <BR>
     */
    void pauseDownload() {
        Log.i(TAG, "pause download.");
        mPause = true;
        mStop = false;
    }

    /**
     * 停止下载 <BR>
     */
    @Deprecated
    void stopDownload() {
        Log.i(TAG, "stop download.");
        mStop = true;
        mPause = false;
    }

    /**
     * 续传<BR>
     */
    void continueDownload() {
        Log.i(TAG, "continue download.");
        mPause = false;
        mStop = false;
        execute();
    }

    /**
     * 开始下载 <BR>
     */
    void startDownload() {
        Log.i(TAG, "start download.");
        mPause = false;
        mStop = false;
        execute();
    }

    /**
     * 创建文件 <BR>
     */
    private void createFile() {
        HttpURLConnection conn = null;
        RandomAccessFile accessFile = null;
        try {
            URL url = new URL(mDownloadTask.getUrl());
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");

            int fileSize = conn.getContentLength();
            Log.d(TAG, "total size[" + fileSize + "]");
            mDownloadTask.setTotalSize(fileSize);
            conn.disconnect();

            File downFilePath = new File(mDownloadTask.getFilePath());
            if (!downFilePath.exists()) {
                downFilePath.mkdirs();
            }
            
            File file = new File(mDownloadTask.getFilePath() + "/" + mDownloadTask.getFileName());
            if (!file.exists()) {
                file.createNewFile();

                // if new file created, then reset the finished size.
                mDownloadTask.setFinishedSize(0);
            }

            accessFile = new RandomAccessFile(file, "rwd");
            Log.d(TAG, "fileSize:" + fileSize);
            if(fileSize > 0) {
                accessFile.setLength(fileSize);
            }
            accessFile.close();
        } catch (MalformedURLException e) {
            Log.e(TAG, "createFile MalformedURLException",e);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "createFile FileNotFoundException",e);
            for (DownloadListener l : mDlTaskMng.getListeners(mDownloadTask)) {
                l.onDownloadFail();
            }
        } catch (IOException e) {
            Log.e(TAG, "createFile IOException",e);
            for (DownloadListener l : mDlTaskMng.getListeners(mDownloadTask)) {
                l.onDownloadFail();
            }
        } 
    }

}
