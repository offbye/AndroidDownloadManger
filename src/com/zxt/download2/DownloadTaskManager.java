package com.zxt.download2;

import com.zxt.download2.DownloadTask.DownloadState;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 下载任务管理类，单例模式<BR>
 * 提供添加、更新、查询、暂停、取消（停止）下载任务
 * 
 * @author zxt
 */
public class DownloadTaskManager
{
    /**
     * debug tag.
     */
    private static final String TAG = "DownloadTaskManager";
    
    /**
     * 默认 文件保存路径/sdcard/download
     */
    private static final String DEFAULT_FILE_PATH = "/sdcard/download";
    
    /**
     * 单例本类实例
     */
    private static DownloadTaskManager sMe;
    
    /**
     * 下载任务数据库操作对象
     */
    private DownloadDBHelper mDownloadDBHelper;
    
    /**
     * 一个下载任务对应一个下载操作实例
     */
    private Map<DownloadTask, DownloadOperator> mDownloadMap;
    
    /**
     * 私有默认构造
     * 
     * @param context
     */
    private DownloadTaskManager(Context context)
    {
        mDownloadMap = new HashMap<DownloadTask, DownloadOperator>();
        // 数据库操作对象实例化
        mDownloadDBHelper = new DownloadDBHelper(context, "download.db");
    }
    
    /**
     * 获取该类单例实例对象 <BR>
     * 
     * @param context Context
     * @return DownloadTaskManager instance
     */
    public static synchronized DownloadTaskManager getInstance(Context context)
    {
        if (sMe == null)
        {
            sMe = new DownloadTaskManager(context);
        }
        return sMe;
    }
    
    /**
     * 
     * 开始下载<BR>
     * 
     * @param downloadTask DownloadTask
     */
    public void startDownload(DownloadTask downloadTask)
    {
        if (downloadTask.getFilePath() == null
                || downloadTask.getFilePath().trim().length() == 0)
        {
            Log.i(TAG,
                    "file path is invalid. file path : "
                            + downloadTask.getFilePath()
                            + ", use default file path : " + DEFAULT_FILE_PATH);
            downloadTask.setFilePath(DEFAULT_FILE_PATH);
        }
        
        if (downloadTask.getFileName() == null
                || downloadTask.getFileName().trim().length() == 0)
        {
            Log.e(TAG,
                    "file name is invalid. file name : "
                            + downloadTask.getFileName());
            return;
        }
        downloadTask.setDownloadState(DownloadState.INITIALIZE);
        
        // save to database if the download task is valid, and start download.
        if (!downloadTask.equals(queryDownloadTask(downloadTask.getUrl())))
        {
            insertDownloadTask(downloadTask);
        }
        
        DownloadOperator dlOperator = new DownloadOperator(this, downloadTask);
        mDownloadMap.put(downloadTask, dlOperator);
        dlOperator.startDownload();
        
    }
    
    /**
     * 暂停下载 <BR>
     * 
     * @param downloadTask DownloadTask
     */
    public void pauseDownload(DownloadTask downloadTask)
    {
        if(mDownloadMap.containsKey(downloadTask)){
            mDownloadMap.get(downloadTask).pauseDownload();
            mDownloadMap.remove(downloadTask);
        }

    }
    
    /**
     * 
     * 继续下载、续传<BR>
     * 
     * @param downloadTask DownloadTask
     */
    public void continueDownload(DownloadTask downloadTask)
    {
        /*
         * 由于DownloadOperator继承自AsyncTask,其execute方法只能被执行一次，
         * 因此在继续下载时需要重新创建DownloadOperator对象进行下载操作
         */
        startDownload(downloadTask);
    }
    
    /**
     * 停止下载 <BR>
     * 
     * @param downloadTask DownloadTask
     */
    public void stopDownload(DownloadTask downloadTask)
    {
        mDownloadMap.get(downloadTask).stopDownload();
        mDownloadMap.remove(downloadTask);
    }
    
    /**
     * 获取全部下载任务 <BR>
     * 
     * @return DownloadTask list
     */
    public List<DownloadTask> getAllDownloadTask()
    {
        return mDownloadDBHelper.queryAll();
    }
    
    public List<DownloadTask> getDownloadingTask()
    {
        return mDownloadDBHelper.queryUnDownloaded();
    }
    
    public List<DownloadTask> getDownloadedTask()
    {
        return mDownloadDBHelper.queryDownloaded();
    }
    
    /**
     * 
     * 存入一条下载任务<BR>
     * 
     * @param downloadTask
     */
    void insertDownloadTask(DownloadTask downloadTask)
    {
        mDownloadDBHelper.insert(downloadTask);
    }
    
    /**
     * 更新下载任务 <BR>
     * 
     * @param downloadTask
     */
    void updateDownloadTask(DownloadTask downloadTask)
    {
        mDownloadDBHelper.update(downloadTask);
    }
    
    /**
     * 删除下载任务 <BR>
     * 
     * @param downloadTask
     */
    void deleteDownloadTask(DownloadTask downloadTask)
    {
        mDownloadDBHelper.delete(downloadTask);
        deleteFile(downloadTask.getFilePath()+"/"+ downloadTask.getFileName());
    }
    
    /**
     * 查询指定url的下载任务 <BR>
     * 
     * @param url 下载url
     * @return DownloadTask
     */
    DownloadTask queryDownloadTask(String url)
    {
        return mDownloadDBHelper.query(url);
    }
    
    boolean existRunningTask(DownloadTask downloadTask){
        return mDownloadMap.containsKey(downloadTask);
    }
    
    boolean addListener(DownloadTask downloadTask, DownloadListener listener ){
        if(null != mDownloadMap.get(downloadTask)) {
            mDownloadMap.get(downloadTask).addDownloadListener(listener);
            Log.d(TAG, downloadTask.getFileName() + " addListener " );
            return true;
        }
        return false;
    }
    
    private boolean deleteFile(String filePath){
        File file = new File(filePath);
        if (file.exists())
        {
            return file.delete();
        }
        return false;
    }
}
