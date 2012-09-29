package com.zxt.download2;

/**
 * 下载状态 <BR>
 * 
 * @author zxt
 */
public enum DownloadState {
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
