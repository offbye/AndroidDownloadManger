
package com.zxt.download2;

import android.text.TextUtils;
import android.webkit.URLUtil;

/**
 * 下载任务类
 * 
 * @author zxt
 */
public class DownloadTask {

    /**
     * 下载地址
     */
    private String url;

    /**
     * 文件名
     */
    private String fileName;

    private String title;

    private String thumbnail;

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 文件保存路径（默认路径为sdcard下download路径）
     */
    private String filePath;

    /**
     * 文件已完成下载大小
     */
    private int finishedSize;

    /**
     * 文件总大小
     */
    private int totalSize;

    /**
     * 已完成百分比
     */
    private int percent;

    /**
     * 下载的状态
     */
    private volatile DownloadState downloadState;

    public DownloadTask(String url, String filePath, String fileName, String title, String thumbnail) {
        if (!URLUtil.isHttpUrl(url)) {
            throw new IllegalArgumentException("invalid url,nust start with http://");
        }
        if (TextUtils.isEmpty(fileName)) {
            throw new IllegalArgumentException("invalid fileName");
        }
        this.url = url;
        this.fileName = fileName;
        this.title = title;
        this.thumbnail = thumbnail;
        this.filePath = filePath;
    }

    /**
     * get url
     * 
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * set url
     * 
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * get fileName
     * 
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * set fileName
     * 
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * get filePath
     * 
     * @return the filePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * set filePath
     * 
     * @param filePath the filePath to set
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * get finishedSize
     * 
     * @return the finishedSize
     */
    public int getFinishedSize() {
        return finishedSize;
    }

    /**
     * set finishedSize
     * 
     * @param finishedSize the finishedSize to set
     */
    public void setFinishedSize(int finishedSize) {
        this.finishedSize = finishedSize;
    }

    /**
     * get totalSize
     * 
     * @return the totalSize
     */
    public int getTotalSize() {
        return totalSize;
    }

    /**
     * set totalSize
     * 
     * @param totalSize the totalSize to set
     */
    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    /**
     * get percent
     * 
     * @return the percent
     */
    public double getPercent() {
        return percent;
    }

    /**
     * set percent
     * 
     * @param dlPercent the dlPercent to set
     */
    public void setPercent(int percent) {
        this.percent = percent;
    }

    /**
     * get downloadState
     * 
     * @return the downloadState
     */
    public DownloadState getDownloadState() {
        return downloadState;
    }

    /**
     * set downloadState
     * 
     * @param downloadState the downloadState to set
     */
    public void setDownloadState(DownloadState downloadState) {
        this.downloadState = downloadState;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
        result = prime * result + ((filePath == null) ? 0 : filePath.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DownloadTask other = (DownloadTask) obj;
        if (fileName == null) {
            if (other.fileName != null)
                return false;
        } else if (!fileName.equals(other.fileName))
            return false;
        if (filePath == null) {
            if (other.filePath != null)
                return false;
        } else if (!filePath.equals(other.filePath))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "DownloadTask [url=" + url + ", finishedSize=" + finishedSize + ", totalSize="
                + totalSize + ", dlPercent=" + percent + ", downloadState=" + downloadState
                + ", fileName=" + fileName + ", title=" + title + "]";
    }

}
