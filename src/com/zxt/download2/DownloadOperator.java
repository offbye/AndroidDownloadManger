
package com.zxt.download2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Download worker
 *
 * @author offbye@gmail.com
 */
public class DownloadOperator extends AsyncTask<Void, Integer, Void> {

    private static final int BUFFER_SIZE = 4096;

    private static final int UPDATE_DB_PER_SIZE = 102400;

    /**
     * debug tag
     */
    private static final String TAG = "DownloadOperator";


    private DownloadTask mDownloadTask;

    /**
     * DownloadTaskManager
     */
    private DownloadTaskManager mDlTaskMng;

    /**
     * pause flag
     */
    private volatile boolean mPause = false;

    /**
     * stop flag, not used now.
     */
    private volatile boolean mStop = false;


    /**
     * Constructor
     *
     * @param dlTaskMng
     * @param downloadTask
     */
    DownloadOperator(DownloadTaskManager dlTaskMng, DownloadTask downloadTask) {
        mDownloadTask = downloadTask;
        mDlTaskMng = dlTaskMng;

        Log.d(TAG, "file path : " + mDownloadTask.getFilePath());
        Log.d(TAG, "file name : " + mDownloadTask.getFileName());
        Log.d(TAG, "download url : " + mDownloadTask.getUrl());
    }

    /**
     * <BR>
     *
     * @param params Void...
     * @return Void
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected Void doInBackground(Void... params) {
        // 1. create file if not exist.
        createFile();

        mDownloadTask.setDownloadState(DownloadState.DOWNLOADING);
        mDlTaskMng.updateDownloadTask(mDownloadTask);

        for (DownloadListener l : mDlTaskMng.getListeners(mDownloadTask)) {
            l.onDownloadStart();
        }

        HttpURLConnection conn = null;
        RandomAccessFile accessFile = null;
        InputStream is = null;
        long finishedSize = 0;
        long totalSize = 0;
        long startSize = 0;
        try {
            URL url = new URL(mDownloadTask.getUrl());
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Accept-Encoding", "musixmatch");
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Range", "bytes=" + mDownloadTask.getFinishedSize() + "-"
                    + mDownloadTask.getTotalSize());
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
//            conn.setRequestProperty("Connection", "Keep-Alive");

            accessFile = new RandomAccessFile(mDownloadTask.getFilePath() + "/"
                    + mDownloadTask.getFileName(), "rwd");
            accessFile.seek(mDownloadTask.getFinishedSize());

            finishedSize = mDownloadTask.getFinishedSize();
            totalSize = mDownloadTask.getTotalSize();
            startSize = finishedSize;

            is = conn.getInputStream();
            Log.d(TAG, "downloadListeners size=" +  mDlTaskMng.getListeners(mDownloadTask).size());

            Log.i(TAG, "start writing data to file.");
            //int size = totalSize / 200 > UPDATE_DB_PER_SIZE ? UPDATE_DB_PER_SIZE : totalSize / 200; //decrease refresh frequency
            byte[] buffer = new byte[BUFFER_SIZE];
            int length = -1;
            long startTime = System.currentTimeMillis();
            int speed = 0;
            while ((length = is.read(buffer)) != -1) {
                // pause download
                if (mPause) {
                    Log.i(TAG, "pause download, exit download loop.");
                    mDownloadTask.setDownloadState(DownloadState.PAUSE);
                    mDownloadTask.setFinishedSize(finishedSize);

                    for (DownloadListener l : mDlTaskMng.getListeners(mDownloadTask)) {
                        l.onDownloadPause();
                    }
                    mDlTaskMng.updateDownloadTask(mDownloadTask);

                    return null;
                }

                // stop download, delete the download task
                /* if (mStop) {
                    Log.i(TAG, "stop download, exit download loop and delete download task.");
                    for (DownloadListener l : mDlTaskMng.getListeners(mDownloadTask)) {
                        l.onDownloadStop();
                    }
                    //mDlTaskMng.deleteDownloadTask(mDownloadTask);
                    return null;
                } */

                finishedSize += length;
                Log.d(TAG, "length=" +length);
                accessFile.write(buffer, 0, length);

                // update database per 100K.
                if (finishedSize - mDownloadTask.getFinishedSize() > UPDATE_DB_PER_SIZE) {
                    mDownloadTask.setFinishedSize(finishedSize);
                    mDlTaskMng.updateDownloadTask(mDownloadTask);
                    speed =  (int)((finishedSize - startSize)/(int)(System.currentTimeMillis() + 1 - startTime));
                    publishProgress((int)finishedSize, (int)totalSize, speed);
                } else if (totalSize - finishedSize < UPDATE_DB_PER_SIZE) {//send message in this case
                    mDownloadTask.setFinishedSize(finishedSize);
                    speed =  (int)((finishedSize - startSize)/(int)(System.currentTimeMillis() + 1 - startTime));
                    publishProgress((int)finishedSize, (int)totalSize, speed);
                }

            }
            conn.disconnect();

            mDownloadTask.setDownloadState(DownloadState.FINISHED);
            mDownloadTask.setFinishedSize(finishedSize);
            Log.d(TAG, "finished " +  mDownloadTask);
            mDlTaskMng.updateDownloadTask(mDownloadTask);

            for (DownloadListener l : mDlTaskMng.getListeners(mDownloadTask)) {
                l.onDownloadFinish(mDownloadTask.getFilePath() + "/" + mDownloadTask.getFileName());
            }
            mDlTaskMng.getListeners(mDownloadTask).clear();
            mDlTaskMng.removeListener(mDownloadTask);

        } catch (Exception e) {
            Log.e(TAG, "download exception : " + e.getMessage());
            e.printStackTrace();
            mDownloadTask.setDownloadState(DownloadState.FAILED);
            mDownloadTask.setFinishedSize(finishedSize);

            for (DownloadListener l : mDlTaskMng.getListeners(mDownloadTask)) {
                l.onDownloadFail();
            }
            mDlTaskMng.updateDownloadTask(mDownloadTask);

        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (accessFile != null) {
                    accessFile.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * <BR>
     *
     * @param values int array
     * @see android.os.AsyncTask#onProgressUpdate(Progress[])
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        int finished = values[0];
        int total = values[1];
        int speed = values[2];

        for (DownloadListener l : mDlTaskMng.getListeners(mDownloadTask)) {
            l.onDownloadProgress(finished, total, speed);
        }
    }

    /**
     * pauseDownload
     */
    void pauseDownload() {
        Log.i(TAG, "pause download.");
        mPause = true;
        mStop = false;
    }

    /**
     * stopDownload
     */
    @Deprecated
    void stopDownload() {
        Log.i(TAG, "stop download.");
        mStop = true;
        mPause = false;
    }

    /**
     * continueDownload
     */
    void continueDownload() {
        Log.i(TAG, "continue download.");
        mPause = false;
        mStop = false;
        execute();
    }

    /**
     * startDownload
     */
    void startDownload() {
        Log.i(TAG, "start download.");
        mPause = false;
        mStop = false;
        execute();
    }

    /**
     * createFile
     */
    private void createFile() {
        HttpURLConnection conn = null;
        RandomAccessFile accessFile = null;
        try {
            URL url = new URL(mDownloadTask.getUrl());
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Accept-Encoding","musixmatch");
//            conn.setRequestProperty("Accept-Encoding", "identity");
            conn.setRequestMethod("HEAD");

//            conn.setRequestMethod("GET");

            int fileSize = conn.getContentLength();
            Log.i(TAG, "total size[" + fileSize + "]");
            mDownloadTask.setTotalSize(fileSize);
            conn.disconnect();

            File downFilePath = new File(mDownloadTask.getFilePath());
            if (!downFilePath.exists()) {
                downFilePath.mkdirs();
            }

            File file = new File(mDownloadTask.getFilePath() + "/" + mDownloadTask.getFileName());
            if (!file.exists()) {
                file.createNewFile();

                // if new file created, then reset the finished size.
                mDownloadTask.setFinishedSize(0);
            }

            accessFile = new RandomAccessFile(file, "rwd");
            Log.d(TAG, "fileSize:" + fileSize);
            if(fileSize > 0) {
                accessFile.setLength(fileSize);
            }
            accessFile.close();
        } catch (MalformedURLException e) {
            Log.e(TAG, "createFile MalformedURLException",e);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "createFile FileNotFoundException",e);
            for (DownloadListener l : mDlTaskMng.getListeners(mDownloadTask)) {
                l.onDownloadFail();
            }
        } catch (IOException e) {
            Log.e(TAG, "createFile IOException",e);
            for (DownloadListener l : mDlTaskMng.getListeners(mDownloadTask)) {
                l.onDownloadFail();
            }
        }
    }


    protected static String md5(String string) {
        byte[] hash = null;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (Exception e) {
        	Log.e(TAG, "NoSuchAlgorithm");
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    protected static String getKey(String aKey) {

		char[] aKeyChars = { 49, 87, 89, 90, 86, 50, 74, 78, 88, 82, 72, 51,
				79, 73, 71, 53, 67, 52, 80, 54, 65, 76, 55, 85, 70, 56, 83, 69,
				68, 57, 84, 66, 48, 81, 75, 77 };
		byte[] keyBytes;
		int patternLength;
		int keyCharsOffset;
		int i;
		int j;
		StringBuilder result = new StringBuilder("#####-#####-#####-#####-#####");
		keyBytes = aKey.getBytes();
		patternLength = result.length();
		keyCharsOffset = 0;
		i = 0;
		j = 0;
		while ((i < keyBytes.length) && (j < patternLength)) {
			keyCharsOffset = keyCharsOffset + Math.abs(keyBytes[i]);
			while (keyCharsOffset >= aKeyChars.length) {
				keyCharsOffset = keyCharsOffset - aKeyChars.length;
			}
			while ((result.charAt(j) != 35) && (j < patternLength)) {
				j++;
			}
			result.setCharAt(j, aKeyChars[keyCharsOffset]);
			if (i == (keyBytes.length - 1)) {
				i = -1;
			}
			i++;
			j++;
		}
		return result.toString();
	}

    protected static int check(Context context) {
        String key = ManifestMetaData.getString(context, "DOWNLOAD_KEY");
        String pack = context.getPackageName();
        StringBuilder sb = new StringBuilder();
        sb.append(pack);
        sb.reverse();
        sb.append(pack);
        if(key.equals(getKey(md5(sb.toString())))){
            return 2;
        } else if (key.equals("testkey")){
            Toast.makeText(context, "The download manger you use is a trial version,please buy a license", Toast.LENGTH_LONG).show();
            return 1;
        } else {
            Toast.makeText(context, "The download manger key you use is invalid,please buy a license", Toast.LENGTH_LONG).show();
            return -1;
        }
    }

}
