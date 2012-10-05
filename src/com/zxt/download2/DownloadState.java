package com.zxt.download2;

/**
 * 下载状态 <BR>
 * 
 * @author zxt
 */
public enum DownloadState {
    /**
     * init
     */
    INITIALIZE,

    /**
     * downloading
     */
    DOWNLOADING,
    
    FAILED,
    /**
     * 下载完成
     */
    FINISHED,

    /**
     * 下载暂停状态
     */
    PAUSE
}
