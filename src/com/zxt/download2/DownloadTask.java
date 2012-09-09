
package com.zxt.download2;

/**
 * 下载任务类
 * 
 * @author zxt
 */
public class DownloadTask {
    /**
     * 下载状态 <BR>
     * 
     * @author 杨凡
     * @version [RCS Client V100R001C03, 2012-2-8]
     */
    enum DownloadState {
        /**
         * 初始化
         */
        INITIALIZE,

        /**
         * 正在下载中
         */
        DOWNLOADING,

        /**
         * 下载完成
         */
        FINISHED,

        /**
         * 下载暂停状态
         */
        PAUSE
    }

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
    private double dlPercent;

    /**
     * 下载的状态
     */
    private volatile DownloadState downloadState;

    /**
     * 构造函数
     * 
     * @param url 文件下载地址（必须）
     */
    public DownloadTask(String url) {
        if (url == null || url.trim().length() == 0) {
            throw new IllegalArgumentException("the input url is null or invalid.");
        }
        this.url = url;
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
     * get dlPercent
     * 
     * @return the dlPercent
     */
    public double getDlPercent() {
        return dlPercent;
    }

    /**
     * set dlPercent
     * 
     * @param dlPercent the dlPercent to set
     */
    public void setDlPercent(double dlPercent) {
        this.dlPercent = dlPercent;
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
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }

}
