
package com.zxt.download2;

/**
 * Download listener, you add many listeners to a download task. And the
 * listeners can be auto removed after download task finished or you delete the
 * download task manually.
 * 
 * @author offbye@gmail.com
 */
public interface DownloadListener {
    /**
     * Download Finish
     * 
     * @param filepath
     */
    void onDownloadFinish(String filepath);

    /**
     * Download Start
     */
    void onDownloadStart();

    /**
     * Download Pause
     */
    void onDownloadPause();

    /**
     * Download Stop
     */
    void onDownloadStop();

    /**
     * Download Fail
     */
    void onDownloadFail();

    /**
     * Download Progress update
     * can be used to display speed and percent.
     * 
     * @param finishedSize 已完成的大小
     * @param totalSize 下载的总大小
     * @param speed download speed
     */
    void onDownloadProgress(long finishedSize, long totalSize, int speed);
}
