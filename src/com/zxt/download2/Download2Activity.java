
package com.zxt.download2;

import com.zxt.download2.DownloadTask.DownloadState;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class Download2Activity extends Activity {
    private static final String TAG = "Download2Activity";

    private ListView mDownloadingListView;

    private Context mContext;

    List<DownloadTask> mDownloadlist;

    DownloadingAdapter mDownloadingAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mContext = this;
        mDownloadlist = DownloadTaskManager.getInstance(mContext).getAllDownloadTask();
        mDownloadingListView = (ListView) findViewById(R.id.downloadingListView);
        mDownloadingAdapter = new DownloadingAdapter(Download2Activity.this, 0, mDownloadlist);

        mDownloadingListView.setAdapter(mDownloadingAdapter);
        mDownloadingListView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Toast.makeText(mContext, "remove task", Toast.LENGTH_LONG).show();
                DownloadTaskManager.getInstance(mContext).deleteDownloadTask(
                        mDownloadlist.get(arg2));
                mDownloadlist.remove(arg2);
                mDownloadingAdapter.notifyDataSetChanged();
                return false;
            }
        });

        for (final DownloadTask task : mDownloadlist) {
            if (!task.getDownloadState().equals(DownloadState.FINISHED)) {
                Log.d(TAG, "add listener");
                addListener(task);
            }
        }
    }
    
    class MyDownloadListener implements DownloadListener {
        private DownloadTask task;
        public MyDownloadListener(DownloadTask downloadTask ){
            task = downloadTask;
        }

        @Override
        public void onDownloadFinish(String filepath) {
            Log.d(TAG, "onDownloadFinish");
            task.setDownloadState(DownloadState.FINISHED);
            Download2Activity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDownloadingAdapter.notifyDataSetChanged();
                }
            });

        }

        @Override
        public void onDownloadStart() {
            task.setDownloadState(DownloadState.INITIALIZE);
            Download2Activity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDownloadingAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onDownloadPause() {
          Log.d(TAG, "onDownloadPause");
          task.setDownloadState(DownloadState.PAUSE);
            Download2Activity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDownloadingAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onDownloadStop() {
            Log.d(TAG, "onDownloadStop");
            task.setDownloadState(DownloadState.PAUSE);
            Download2Activity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDownloadingAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onDownloadFail() {
            Log.d(TAG, "onDownloadFail");
            task.setDownloadState(DownloadState.PAUSE);
            Download2Activity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDownloadingAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onDownloadProgress(final int finishedSize,final int totalSize,
                double progressPercent) {
            Log.d(TAG, "download " + finishedSize);
            task.setDownloadState(DownloadState.DOWNLOADING);
            task.setFinishedSize(finishedSize);
            task.setTotalSize(totalSize);
            task.setDlPercent(progressPercent);

            Download2Activity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDownloadingAdapter.notifyDataSetChanged();                            }
            });
        }
    }
    
    public void addListener(DownloadTask task){
        DownloadTaskManager.getInstance(mContext).addListener(task, new MyDownloadListener(task));
    }
    
}
