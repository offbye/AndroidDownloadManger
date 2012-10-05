
package com.zxt.download2;

/**
 * 下载监听器 ,一个下载任务可以添加多个监听器
 * 
 * @author zxt
 */
public interface DownloadListener {
    /**
     * 下载完成 <BR>
     * 
     * @param filepath 文件路径
     */
    void onDownloadFinish(String filepath);

    /**
     * 下载开始 <BR>
     */
    void onDownloadStart();

    /**
     * 下载已暂停 <BR>
     */
    void onDownloadPause();

    /**
     * 下载已停止 <BR>
     */
    void onDownloadStop();

    /**
     * 下载失败 <BR>
     */
    void onDownloadFail();

    /**
     * 更新进度实现方法<BR>
     * 此方法可以用于显示具体的进度百分比
     * 
     * @param finishedSize 已完成的大小
     * @param totalSize 下载的总大小
     * @param progressPercent 进度百分比
     */
    void onDownloadProgress(int finishedSize, int totalSize, int progressPercent);
}
