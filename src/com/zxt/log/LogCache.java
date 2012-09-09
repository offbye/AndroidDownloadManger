package com.zxt.log;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


import android.util.Log;

public class LogCache
{
    /**
     * 日志标记
     */
    public static final String TAG = "LogCache";
    
    /** 
     * file amount limitation
      */
    public static final int FILE_AMOUNT = 5;
    
    /** 
     * file size limitation per log file 
     */
    public static final long MAXSIZE_PERFILE = 1048576;
    
    /**
     * instance of  LogCache
     */
    private static final LogCache INSTANCE = new LogCache();
    
    private static final GregorianCalendar CALENDAR_INSTANCE = new GregorianCalendar();
    
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
    
    private volatile boolean started;
    
    private volatile Thread logWorkerThread;
    
    private LogWriter logWriter = null;
    
    private int counter = 0;
    
    /**
     * 
     * 构造器
     */
    private LogCache()
    {
        this(Logger.LOG_FILE_PATH, FILE_AMOUNT, MAXSIZE_PERFILE);
    }
    
    private LogCache(String filePath)
    {
        this(filePath, 0, 0);
    }
    
    private LogCache(String filePath, int fileAmount, long maxSize)
    {
        this.logWriter = new LogWriter(new File(filePath), fileAmount, maxSize);
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return
     */
    static LogCache getInstance()
    {
        return INSTANCE;
    }
    
    /**
     * put log info into the synchronized queue
     * 
     * @param msg msg
     */
    public void write(String msg)
    {
        if (started)
        {
            try
            {
                queue.put(msg);
            }
            catch (InterruptedException e)
            {
                Log.e("LogCache", "", e);
            }
        }
        
    }
    
    /**
     * construct log info into the queue
     * 
     * @param level level
     * @param tag tag
     * @param msg msg
     */
    public void write(String level, String tag, String msg)
    {
        CALENDAR_INSTANCE.setTimeInMillis(System.currentTimeMillis());
        int pid = android.os.Process.myPid();
        int month = CALENDAR_INSTANCE.get(Calendar.MONTH) + 1;
        int date = CALENDAR_INSTANCE.get(Calendar.DATE);
        int hour = CALENDAR_INSTANCE.get(Calendar.HOUR_OF_DAY);
        int minute = CALENDAR_INSTANCE.get(Calendar.MINUTE);
        int seconds = CALENDAR_INSTANCE.get(Calendar.SECOND);
        
        StringBuilder sbr = new StringBuilder();
        sbr.append(month).append('-').append(date).append(' ');
        sbr.append(hour).append(':').append(minute).append(':').append(seconds);
        sbr.append('\t').append(level).append('\t').append(pid);
        sbr.append('\t')
                .append('[')
                .append(Thread.currentThread().getName())
                .append(']');
        sbr.append('\t').append(tag).append('\t').append(msg);
        write(sbr.toString());
    }
    
    /**
     * judge whether the external memory writable or not
     * 
     * @param text text
     * @return isExternalMemoryAvailable
     */
    public boolean isExternalMemoryAvailable(String text)
    {
        return MemoryStatus.isExternalMemoryAvailable(text.getBytes().length);
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return CacheSize
     */
    public synchronized long getCacheSize()
    {
        long size = 0;
        for (String text : queue)
        {
            size += text.getBytes().length;
        }
        return size;
    }
    
    public boolean isStarted()
    {
        return started;
    }
    
    public boolean isLogThreadNull()
    {
        return null == logWorkerThread;
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     */
    public synchronized void start()
    {
        if (null == logWorkerThread)
        {
            logWorkerThread = new Thread(new LogTask(), "Log Worker Thread - "
                    + counter);
        }
        if (started || !logWriter.initialize())
        {
            return;
        }
        Log.v(TAG, "Log Cache instance is starting ...");
        started = true;
        logWorkerThread.start();
        Log.v("LogCache", "Log Cache instance is started");
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     */
    public synchronized void stop()
    {
        Log.v("LogCache", "Log Cache instance is stopping...");
        started = false;
        queue.clear();
        logWriter.close();
        if (null != logWorkerThread)
        {
            logWorkerThread.interrupt();
            logWorkerThread = null;
        }
        Log.v("LogCache", "Log Cache instance is stopped");
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @author 盛兴亚
     * @version [RCS Client V100R001C03, 2012-2-15]
     */
    private final class LogTask implements Runnable
    {
        
        public LogTask()
        {
            counter++;
        }
        
        private void dealMsg() throws InterruptedException
        {
            String msg = null;
            while (started && !Thread.currentThread().isInterrupted())
            {
                msg = queue.take();
                synchronized (logWriter)
                {
                    Log.v(TAG,
                            "AvailableExternalMemorySize:"
                                    + MemoryStatus.getAvailableExternalMemorySize());
                    if (isExternalMemoryAvailable(msg))
                    {
                        // if current file is deleted, rebuild it
                        if (!logWriter.isCurrentExist())
                        {
                            Log.v(TAG, "current is initialing...");
                            if (!logWriter.initialize())
                            {
                                continue;
                            }
                        }
                        // if current log file reaches size limitation, log into
                        // next log file
                        else if (!logWriter.isCurrentAvailable())
                        {
                            Log.v(TAG, "current is rotating...");
                            if (!logWriter.rotate())
                            {
                                continue;
                            }
                        }
                        logWriter.println(msg);
                    }
                    else if (logWriter.clearSpace())
                    {
                        if (!logWriter.rotate())
                        {
                            continue;
                        }
                        logWriter.println(msg);
                    }
                    else
                    {
                        Log.e(TAG, "can't log into sdcard.");
                    }
                }
            }
        }
        
        public void run()
        {
            try
            {
                dealMsg();
            }
            catch (InterruptedException e)
            {
                Log.e(TAG, Thread.currentThread().toString(), e);
            }
            catch (RuntimeException e)
            {
                Log.e(TAG, Thread.currentThread().toString(), e);
                logWorkerThread = new Thread(new LogTask(),
                        "Log Worker Thread - " + counter);
                started = false;
            }
            finally
            {
                Log.v(TAG, "Log Worker Thread is terminated.");
            }
        }
        
    }
    
}
