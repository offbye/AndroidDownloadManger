
package com.zxt.download2;

import com.zxt.download2.DownloadTask.DownloadState;
import com.zxt.log.Logger;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 下载操作类 <BR>
 * 
 * @author zxt
 */
public class DownloadOperator extends AsyncTask<Void, Integer, Void> {

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

    private List<DownloadListener> downloadListeners;

    /**
     * Constructor
     * 
     * @param dlTaskMng 下载任务管理类
     * @param downloadTask 下载任务
     */
    DownloadOperator(DownloadTaskManager dlTaskMng, DownloadTask downloadTask) {
        mDownloadTask = downloadTask;
        mDlTaskMng = dlTaskMng;

        Logger.d(TAG, "file path : " + mDownloadTask.getFilePath());
        Logger.d(TAG, "file name : " + mDownloadTask.getFileName());
        Logger.d(TAG, "download url : " + mDownloadTask.getUrl());
        downloadListeners = Collections.synchronizedList(new ArrayList<DownloadListener>());
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

        for (DownloadListener l : downloadListeners) {
            l.onDownloadStart();
        }

        HttpURLConnection conn = null;
        RandomAccessFile accessFile = null;
        InputStream is = null;
        int finishedSize = 0;
        int totalSize = 0;
        try {
            URL url = new URL(mDownloadTask.getUrl());
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Range", "bytes=" + mDownloadTask.getFinishedSize() + "-"
                    + mDownloadTask.getTotalSize());

            accessFile = new RandomAccessFile(mDownloadTask.getFilePath() + "/"
                    + mDownloadTask.getFileName(), "rwd");
            accessFile.seek(mDownloadTask.getFinishedSize());

            finishedSize = mDownloadTask.getFinishedSize();
            totalSize = mDownloadTask.getTotalSize();

            is = conn.getInputStream();
            Log.d(TAG, "downloadListeners size=" + downloadListeners.size());

            Logger.i(TAG, "start writing data to file.");
            byte[] buffer = new byte[4096];
            int length = -1;
            while ((length = is.read(buffer)) != -1) {
                // pause download
                if (mPause) {
                    Logger.i(TAG, "pause download, exit download loop.");
                    mDownloadTask.setDownloadState(DownloadState.PAUSE);
                    mDownloadTask.setFinishedSize(finishedSize);
                    mDlTaskMng.updateDownloadTask(mDownloadTask);

                    for (DownloadListener l : downloadListeners) {
                        l.onDownloadPause();
                    }
                    return null;
                }

                // stop download, delete the download task
                if (mStop) {
                    Logger.i(TAG, "stop download, exit download loop and delete download task.");
                    mDlTaskMng.deleteDownloadTask(mDownloadTask);

                    for (DownloadListener l : downloadListeners) {
                        l.onDownloadStop();
                    }

                    return null;
                }

                finishedSize += length;
                accessFile.write(buffer, 0, length);

                // update database per 10K.
                if (finishedSize - mDownloadTask.getFinishedSize() > 10240) {
                    mDownloadTask.setFinishedSize(finishedSize);
                    mDlTaskMng.updateDownloadTask(mDownloadTask);
                }

                // if (finishedSize - mDownloadTask.getFinishedSize() >
                // mDownloadTask.getTotalSize() / 200) {

                // update progress.
                publishProgress(finishedSize, totalSize);
                // }
            }

            mDownloadTask.setDownloadState(DownloadState.FINISHED);
            mDownloadTask.setFinishedSize(finishedSize);
            mDlTaskMng.updateDownloadTask(mDownloadTask);

            for (DownloadListener l : downloadListeners) {
                l.onDownloadFinish(mDownloadTask.getFilePath() + "/" + mDownloadTask.getFileName());
            }
        } catch (Exception e) {
            Logger.e(TAG, "download exception : " + e.getMessage());
            e.printStackTrace();
            mDownloadTask.setDownloadState(DownloadState.PAUSE);
            mDownloadTask.setFinishedSize(finishedSize);
            mDlTaskMng.updateDownloadTask(mDownloadTask);

            for (DownloadListener l : downloadListeners) {
                l.onDownloadFail();
            }

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

        for (DownloadListener l : downloadListeners) {
            l.onDownloadProgress(finished, total, Math.ceil(finished * 100 / total));
        }
    }

    /**
     * 暂停下载 <BR>
     */
    void pauseDownload() {
        Logger.i(TAG, "pause download.");
        mPause = true;
        mStop = false;
    }

    /**
     * 停止下载 <BR>
     */
    void stopDownload() {
        Logger.i(TAG, "stop download.");
        mStop = true;
        mPause = false;
    }

    /**
     * 续传<BR>
     */
    void continueDownload() {
        Logger.i(TAG, "continue download.");
        mPause = false;
        mStop = false;
        execute();
    }

    /**
     * 开始下载 <BR>
     */
    void startDownload() {
        Logger.i(TAG, "start download.");
        mPause = false;
        mStop = false;
        execute();
    }

    /**
     * 创建文件 <BR>
     */
    private void createFile() {

        try {
            URL url = new URL(mDownloadTask.getUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");

            int fileSize = conn.getContentLength();
            Logger.d(TAG, "total size[" + fileSize + "]");
            mDownloadTask.setTotalSize(fileSize);
            File file = new File(mDownloadTask.getFilePath() + "/" + mDownloadTask.getFileName());
            if (!file.exists()) {
                file.createNewFile();

                // if new file created, then reset the finished size.
                mDownloadTask.setFinishedSize(0);
            }

            RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
            accessFile.setLength(fileSize);
            accessFile.close();
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addDownloadListener(DownloadListener downloadListener) {
        this.downloadListeners.add(downloadListener);
    }
}
