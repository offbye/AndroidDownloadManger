package com.zxt.log;

import java.io.File;
import android.os.Environment;
import android.os.StatFs;


public class MemoryStatus
{
    
    private static final int ERROR = -1;
    
    private static final long RESERVED_SIZE = 2097152;
    
    /**
     * 
     * [构造简要说明]
     */
    private MemoryStatus()
    {
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return is external memory available
     */
    public static boolean externalMemoryAvailable()
    {
        return android.os.Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return available internal memory size
     */
    public static long getAvailableInternalMemorySize()
    {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return total internal memory size
     */
    public static long getTotalInternalMemorySize()
    {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return available external memory size
     */
    public static long getAvailableExternalMemorySize()
    {
        if (externalMemoryAvailable())
        {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        }
        else
        {
            return ERROR;
        }
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return total external memory size
     */
    public static long getTotalExternalMemorySize()
    {
        if (externalMemoryAvailable())
        {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        }
        else
        {
            return ERROR;
        }
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param size size
     * @return formated string
     */
    public static String formatSize(long size)
    {
        String suffix = "B";
        
        if (size >= 1024)
        {
            suffix = "KiB";
            size /= 1024;
            if (size >= 1024)
            {
                suffix = "MiB";
                size /= 1024;
            }
        }
        
        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));
        
        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0)
        {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }
        
        if (suffix != null)
        {
            resultBuffer.append(suffix);
        }
        return resultBuffer.toString();
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param size size
     * @return is external memory available
     */
    public static boolean isExternalMemoryAvailable(long size)
    {
        long availableMemory = getAvailableExternalMemorySize();
        return !(size > availableMemory);
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param size  size
     * @return is internal memory available
     */
    public static boolean isInternalMemoryAvailable(long size)
    {
        long availableMemory = getAvailableInternalMemorySize();
        return !(size > availableMemory);
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param size size
     * @return is memory available
     */
    public static boolean isMemoryAvailable(long size)
    {
        size += RESERVED_SIZE;
        if (externalMemoryAvailable())
        {
            return isExternalMemoryAvailable(size);
        }
        return isInternalMemoryAvailable(size);
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param path path
     * @return avaliable specific memory size
     */
    public static long getSpecificMemoryAvaliable(String path)
    {
        StatFs stat = new StatFs(path);
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param size size
     * @param path path
     * @return is specific memory available
     */
    public static boolean isSpecificMemoryAvailable(long size, String path)
    {
        long availableMemory = getSpecificMemoryAvaliable(path);
        return !(size > availableMemory);
    }
    
}
