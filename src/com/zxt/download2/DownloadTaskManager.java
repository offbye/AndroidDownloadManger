
package com.zxt.download2;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;

/**
 * A single instance Download Manager, we use this class manage all download task.
 * 
 * @author offbye@gmail.com
 */
public class DownloadTaskManager {

    private static final String TAG = "DownloadTaskManager";

    /**
     * default  save path: /sdcard/download
     */
    private static final String DEFAULT_FILE_PATH = Environment.getExternalStorageDirectory().getPath() + "/download/";

    /**
     * single instance
     */
    private static DownloadTaskManager sMe;

    private static int mMaxTask = 0;

    /**
     * Download Database Helper
     */
    private DownloadDBHelper mDownloadDBHelper;

    /**
     * one download task own a download worker
     */
    private HashMap<DownloadTask, DownloadOperator> mDownloadMap;

    private HashMap<DownloadTask, CopyOnWriteArraySet<DownloadListener>> mDownloadListenerMap;

    /**
     * private constructor
     * 
     * @param context
     */
    private DownloadTaskManager(Context context) {
        mDownloadMap = new HashMap<DownloadTask, DownloadOperator>();
        mDownloadListenerMap = new HashMap<DownloadTask, CopyOnWriteArraySet<DownloadListener>>();
        // 数据库操作对象实例化
        mDownloadDBHelper = new DownloadDBHelper(context, "download.db");
    }

    /**
     * Get a single instance of DownloadTaskManager
     * 
     * @param context Context
     * @return DownloadTaskManager instance
     */
    public static synchronized DownloadTaskManager getInstance(Context context) {
        if (sMe == null) {
            sMe = new DownloadTaskManager(context);
//            if (DownloadOperator.check(context)< 2){
//                mMaxTask  = 5;
//            }
        }
        return sMe;
    }

    /**
     * Start new download Task, if a same download Task already existed,it will exit and leave a "task existed" log. 
     * 
     * @param downloadTask DownloadTask
     */
    public void startDownload(DownloadTask downloadTask) {
        if (downloadTask.getFilePath() == null || downloadTask.getFilePath().trim().length() == 0) {
            Log.w(TAG, "file path is invalid. file path : " + downloadTask.getFilePath()
                    + ", use default file path : " + DEFAULT_FILE_PATH);
            downloadTask.setFilePath(DEFAULT_FILE_PATH);
        }

        if (downloadTask.getFileName() == null || downloadTask.getFileName().trim().length() == 0) {
            Log.w(TAG, "file name is invalid. file name : " + downloadTask.getFileName());
            throw new IllegalArgumentException("file name is invalid");
        }
        
        if (null == downloadTask.getUrl() || !URLUtil.isHttpUrl(downloadTask.getUrl())) {
            Log.w(TAG, "invalid http url: " + downloadTask.getUrl());
            throw new IllegalArgumentException("invalid http url");
        }

        if (mDownloadMap.containsKey(downloadTask)) {
            Log.w(TAG, "task existed");
            return;
        }
        
        if( mMaxTask > 0 && mDownloadMap.size() > mMaxTask) {
            Log.w(TAG, "trial version can only add " + mMaxTask + " download task, please buy  a lincense");
            return;
        }

        if (null == mDownloadListenerMap.get(downloadTask)) {
            CopyOnWriteArraySet<DownloadListener> set = new CopyOnWriteArraySet<DownloadListener>();
            mDownloadListenerMap.put(downloadTask, set);
        }

        downloadTask.setDownloadState(DownloadState.INITIALIZE);

        // save to database if the download task is valid, and start download.
        if (!downloadTask.equals(queryDownloadTask(downloadTask.getUrl()))) {
            insertDownloadTask(downloadTask);
        }

        DownloadOperator dlOperator = new DownloadOperator(this, downloadTask);
        mDownloadMap.put(downloadTask, dlOperator);
        dlOperator.startDownload();

    }

    /**
     * Pause a downloading task
     * 
     * @param downloadTask DownloadTask
     */
    public void pauseDownload(DownloadTask downloadTask) {
        if (mDownloadMap.containsKey(downloadTask)) {
            mDownloadMap.get(downloadTask).pauseDownload();
            //mDownloadMap.remove(downloadTask);
        }

    }

    /**
     * Continue or restart a downloadTask.
     * 
     * @param downloadTask DownloadTask
     */
    public void continueDownload(DownloadTask downloadTask) {
        if (downloadTask.getFilePath() == null || downloadTask.getFilePath().trim().length() == 0) {
            Log.w(TAG, "file path is invalid. file path : " + downloadTask.getFilePath()
                    + ", use default file path : " + DEFAULT_FILE_PATH);
            downloadTask.setFilePath(DEFAULT_FILE_PATH);
        }

        if (downloadTask.getFileName() == null || downloadTask.getFileName().trim().length() == 0) {
            Log.w(TAG, "file name is invalid. file name : " + downloadTask.getFileName());
            throw new IllegalArgumentException("file name is invalid");
        }
        
        if (null == downloadTask.getUrl() || !URLUtil.isHttpUrl(downloadTask.getUrl())) {
            Log.w(TAG, "invalid http url: " + downloadTask.getUrl());
            throw new IllegalArgumentException("invalid http url");
        }
        
        if (null == mDownloadListenerMap.get(downloadTask)) {
            CopyOnWriteArraySet<DownloadListener> set = new CopyOnWriteArraySet<DownloadListener>();
            mDownloadListenerMap.put(downloadTask, set);
        }

        downloadTask.setDownloadState(DownloadState.INITIALIZE);

        // save to database if the download task is valid, and start download.
        if (!downloadTask.equals(queryDownloadTask(downloadTask.getUrl()))) {
            insertDownloadTask(downloadTask);
        }

        DownloadOperator dlOperator = new DownloadOperator(this, downloadTask);
        mDownloadMap.put(downloadTask, dlOperator);
        dlOperator.startDownload();

    }

    /**
     * Stop a task,this method  not used now。Please use pauseDownload instead.
     * 
     * @param downloadTask DownloadTask
     */
    @Deprecated
    public void stopDownload(DownloadTask downloadTask) {
        mDownloadMap.get(downloadTask).stopDownload();
        mDownloadMap.remove(downloadTask);
    }

    /**
     * get all Download task from database
     * 
     * @return DownloadTask list
     */
    public List<DownloadTask> getAllDownloadTask() {
        return mDownloadDBHelper.queryAll();
    }

    /**
     * get all Downloading task from database
     * @return DownloadTask list
     */
    public List<DownloadTask> getDownloadingTask() {
        return mDownloadDBHelper.queryUnDownloaded();
    }

    /**
     * get all download finished task from database
     * @return DownloadTask list
     */
    public List<DownloadTask> getFinishedDownloadTask() {
        return mDownloadDBHelper.queryDownloaded();
    }

    /**
     * insert a download task to database
     * 
     * @param downloadTask
     */
    void insertDownloadTask(DownloadTask downloadTask) {
        mDownloadDBHelper.insert(downloadTask);
    }

    /**
     * update a download task to database
     * 
     * @param downloadTask
     */
    void updateDownloadTask(DownloadTask downloadTask) {
        mDownloadDBHelper.update(downloadTask);
    }

    /**
     * delete a download task from download queue, remove it's listeners, and delete it from database.
     * 
     * @param downloadTask
     */
    public void deleteDownloadTask(DownloadTask downloadTask) {
        if(downloadTask.getDownloadState() != DownloadState.FINISHED){
            for (DownloadListener l : getListeners(downloadTask)) {
                l.onDownloadStop();
            }
            getListeners(downloadTask).clear();
        }
        mDownloadMap.remove(downloadTask);
        mDownloadListenerMap.remove(downloadTask);
        mDownloadDBHelper.delete(downloadTask);
    }
    
    /**
     * delete a download task's download file.
     * 
     * @param downloadTask
     */
    public void deleteDownloadTaskFile(DownloadTask downloadTask) {
        deleteFile(downloadTask.getFilePath() + "/" + downloadTask.getFileName());
    }

    /**
     * query a download task from database according url. 
     * 
     * @param url 下载url
     * @return DownloadTask
     */
    DownloadTask queryDownloadTask(String url) {
        return mDownloadDBHelper.query(url);
    }

    /**
     * query a download task is already running.
     * @param downloadTask
     * @return
     */
    public boolean existRunningTask(DownloadTask downloadTask) {
        return mDownloadMap.containsKey(downloadTask);
    }

    /**
     * Get all Listeners of a download task 
     * @param downloadTask
     * @return
     */
    CopyOnWriteArraySet<DownloadListener> getListeners(DownloadTask downloadTask) {
        if(null != mDownloadListenerMap.get(downloadTask)){
            return mDownloadListenerMap.get(downloadTask);
        } else {
            return new CopyOnWriteArraySet<DownloadListener>();//avoid null pointer exception
        }
    }

    /**
     * Register a DownloadListener to a downloadTask.
     * You can register many DownloadListener to a downloadTask in any time.
     * Such as register a listener to update you own progress bar, do something after file download finished.
     * 
     * @param downloadTask
     * @param listener
     */
    public void registerListener(DownloadTask downloadTask, DownloadListener listener) {
        if (null != mDownloadListenerMap.get(downloadTask)) {
            mDownloadListenerMap.get(downloadTask).add(listener);
            Log.d(TAG, downloadTask.getFileName() + " addListener ");
        } else {
            CopyOnWriteArraySet<DownloadListener> set = new CopyOnWriteArraySet<DownloadListener>();
            mDownloadListenerMap.put(downloadTask, set);
            mDownloadListenerMap.get(downloadTask).add(listener);
        }
    }
    
    /**
     * Remove Listeners from  a downloadTask, you do not need manually call this method.
     * @param downloadTask
     */
    public void removeListener(DownloadTask downloadTask) {
        mDownloadListenerMap.remove(downloadTask);
    }

    /**
     * delete a file 
     * @param filePath
     * @return
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * If url exist in database and the download state is  FINISHED, and the file existed, return true.
     * @param url
     * @return
     */
    public boolean isUrlDownloaded(String url) {
        boolean re = false;

        DownloadTask task = mDownloadDBHelper.query(url);
        if (null != task) {
            if (task.getDownloadState() == DownloadState.FINISHED) {
                File file = new File(task.getFilePath() + "/" + task.getFileName());
                if (file.exists()) {
                    re = true;
                }
            }
        }
        return re;
    }
}
