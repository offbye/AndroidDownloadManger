
package com.zxt.download2;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class Download2Activity extends Activity {
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
    }
}
