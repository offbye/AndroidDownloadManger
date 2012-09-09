package com.zxt.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;


import android.util.Log;

public class LogWriter
{
    /**
     * Log tag
     */
    public static final String TAG = "LogWriter";
    
    private final Comparator<File> c = new Comparator<File>()
    {
        public int compare(File f1, File f2)
        {
            return String.CASE_INSENSITIVE_ORDER.compare(f1.getName(),
                    f2.getName());
        }
    };
    
    /** 
     * the file being logged into 
     */
    private File current;
    
    /** 
     * the amount of log files in loop 
     */
    private int fileAmount = 2;
    
    /** 
     * one log file's size limited
     */
    private long maxSize = 1048576;
    
    /**
     *  history logs exist in the sdcard 
     */
    private ArrayList<File> historyLogs = null;
    
    private DateFormat timestampOfName = new SimpleDateFormat(
            "yyyyMMddHHmmssSSS");
    
    /** logging writer */
    private PrintWriter writer = null;
    
    /**
     * 
     * [构造简要说明]
     * @param current is always the original file
     * @param fileAmount log file total number
     * @param maxSize one log file max size
     */
    public LogWriter(File current, int fileAmount, long maxSize)
    {
        this.current = current;
        this.fileAmount = fileAmount <= 0 ? this.fileAmount : fileAmount;
        this.maxSize = (maxSize <= 0) ? this.maxSize : maxSize;
        initialize();
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return result of initialize
     */
    public synchronized boolean initialize()
    {
        Log.v(TAG, "initializing... ");
        try
        {
            if (!current.getParentFile().exists())
            {
                //zxt changed, if file path not exist,don't write log
                return false;
                //                if (!current.getParentFile().mkdirs())
                //                {
                //                    return false;
                //                }
            }
            else if (null == historyLogs)
            {
                File[] fs = current.getParentFile()
                        .listFiles(new FilenameFilter()
                        {
                            public boolean accept(File dir, String filename)
                            {
                                final String curName = LogWriter.this.current.getName();
                                String patt = curName.replace(curName.substring(curName.lastIndexOf(".")),
                                        "_");
                                return filename.contains(patt);
                            }
                        });
                if (fs != null && fs.length != 0)
                {
                    historyLogs = new ArrayList<File>(Arrays.asList(fs));
                }
                else
                {
                    historyLogs = new ArrayList<File>();
                }
            }
            writer = new PrintWriter(new FileOutputStream(current,
                    current.exists() && isCurrentAvailable()), true);
            Log.v(TAG, "initialized.");
            return true;
        }
        catch (FileNotFoundException e)
        {
            Log.e("LogWriter", "", e);
            return false;
        }
    }
    
    // TODO [Log] File Name Suggest:
    // 1. The name of the current log file is always the same name, for example:
    // widget_manager.log
    // 2. The name of the rotated log file will append the time stamp, for
    // example: widget_manager_2010031412112289.log
    
    private File getTheEarliest()
    {
        Collections.sort(historyLogs, c);
        return historyLogs.get(0);
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return deleteResult
     */
    public boolean rotate()
    {
        File des = new File(newName());
        if (historyLogs.size() >= fileAmount - 1)
        {
            Log.v(TAG, "begin to delete the redundant log file...");
            boolean deleteResult = FileUtil.forceDeleteFile(getTheEarliest());
            if (deleteResult)
            {
                Log.i(TAG, "old historyLogs: " + historyLogs);
                Log.i(TAG, "delete " + historyLogs.get(0).getName()
                        + "successfully.");
                historyLogs.remove(0);
            }
            else
            {
                Log.i(TAG, "delete " + historyLogs.get(0).getName()
                        + "abortively.");
                return false;
            }
        }
        
        try
        {
            close();
            boolean result = current.renameTo(des);
            if (!result || !initialize())
            {
                Log.v(TAG, "rename or initialize error!");
                return false;
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "", e);
            return false;
        }
        historyLogs.add(des);
        Log.i(TAG, "new historyLogs: " + historyLogs);
        
        // TODO [Add Log Observer here]
        return true;
    }
    
    public boolean isCurrentExist()
    {
        return current.exists();
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param msg msg
     * @return isCurrentAvailable
     */
    public boolean isCurrentAvailable(String msg)
    {
        return msg.getBytes().length + current.length() < maxSize;
    }
    
    public boolean isCurrentAvailable()
    {
        return current.length() < maxSize;
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return newName
     */
    public String newName()
    {
        String name = current.getAbsolutePath();
        int dox = name.lastIndexOf('.');
        String prefix = name.substring(0, dox) + "_";
        String suffix = name.substring(dox);
        return prefix + timestampOfName.format(System.currentTimeMillis())
                + suffix;
    }
    
    /**
     * delete the earliest log file
     * 
     * @return true if delete successfully, false otherwise
     */
    private boolean deleteTheEarliest()
    {
        return (historyLogs.size() == 0) ? false : getTheEarliest().delete();
    }
    
    /**
     * delete all other logs
     * 
     * @return true if delete successfully, false otherwise
     */
    @SuppressWarnings("unused")
    private boolean deleteAllOthers()
    {
        for (File file : historyLogs)
        {
            if (!file.delete())
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     * flush the msg into the log file
     * 
     * @param msg msg
     */
    public void println(String msg)
    {
        if (null == writer)
        {
            initialize();
        }
        else
        {
            writer.println(msg);
        }
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param des des
     * @throws IOException IOException
     */
    public void copyTo(File des) throws IOException
    {
        FileChannel fi = new FileInputStream(current).getChannel();
        FileChannel fo = new FileOutputStream(des, false).getChannel();
        ByteBuffer bf = ByteBuffer.allocateDirect(1024);
        while (fi.read(bf) != -1)
        {
            bf.flip();
            fo.write(bf);
            bf.clear();
        }
        fi.close();
        fo.close();
    }
    
    /**
     * 
     * retrieve the log text<BR>
     * [功能详细描述]
     * @param logFile logFile
     * @return log text 
     * @throws IOException IOException
     */
    public String getTextInfo(File logFile) throws IOException
    {
        BufferedReader bReader = null;
        StringBuilder sbr = new StringBuilder();
        String line;
        bReader = new BufferedReader(new InputStreamReader(new FileInputStream(
                logFile)));
        while (null != (line = bReader.readLine()))
        {
            sbr.append(line).append("\n");
        }
        bReader.close();
        return sbr.toString();
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return true if delete successfully, false otherwise
     */
    public boolean clearSpace()
    {
        return deleteTheEarliest();
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     */
    public synchronized void close()
    {
        if (null != writer)
        {
            writer.close();
        }
    }
}
