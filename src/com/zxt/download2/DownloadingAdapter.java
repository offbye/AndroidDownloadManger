
package com.zxt.download2;

import com.zxt.download2.DownloadTask.DownloadState;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

public class DownloadingAdapter extends ArrayAdapter {

    private static final String TAG = "DownloadItemAdapter";

    private LayoutInflater mLayoutInflater;

    private List<DownloadTask> mTaskList;

    private Download2Activity mContext;

    private String downloadingMsg;

    public DownloadingAdapter(Download2Activity context, int textViewResourceId,
            List<DownloadTask> taskList) {
        super(context, textViewResourceId, taskList);
        mTaskList = taskList;
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    public int getCount() {
        return mTaskList.size();
    }

    public Object getItem(int position) {
        return mTaskList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        DownloadTask task = mTaskList.get(position);
        // if (convertView == null) {
        convertView = mLayoutInflater.inflate(R.layout.list_item_downloading, null);
        holder = new ViewHolder();

        holder.mIcon = (ImageView) convertView.findViewById(R.id.thumbnail);
        holder.mTitle = (TextView) convertView.findViewById(R.id.title);
        holder.mSize = (TextView) convertView.findViewById(R.id.size);
        holder.mStatus = (TextView) convertView.findViewById(R.id.state);
        holder.mImageView = (ImageView) convertView.findViewById(R.id.ic_state);

        holder.mTitle.setText(task.getTitle());
        holder.mSize.setText(formatSize(task.getFinishedSize(),task.getTotalSize()));

        holder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.progress);
        holder.mProgressBar.setMax(100);
        
        if(task.getTotalSize() != 0) {
            holder.mProgressBar.setProgress(task.getFinishedSize()/task.getTotalSize()*100);
        }
                
        switch (mTaskList.get(position).getDownloadState()) {

            case PAUSE:
                holder.mStatus.setText(R.string.download_stopped);
                holder.mImageView.setImageResource(R.drawable.ic_download_ing);
                break;
            case DOWNLOADING:
                holder.mStatus.setText(R.string.download_downloading);
                holder.mImageView.setImageResource(R.drawable.ic_download_pause);
                break;
            case FINISHED:
                holder.mProgressBar.setProgress(100);
                holder.mStatus.setText(R.string.download_downloaded);
                holder.mImageView.setImageResource(R.drawable.ic_launcher);
                break;
            case INITIALIZE:
                holder.mStatus.setText(R.string.download_initial);
                holder.mImageView.setImageResource(R.drawable.ic_download_ing);
                break;
            default:
                break;
        }

        
        DownloadTaskManager.getInstance(mContext).addListener(mTaskList.get(position),
                new DownloadListener() {

                    @Override
                    public void onDownloadFinish(String filepath) {
                        mTaskList.get(position).setDownloadState(DownloadState.FINISHED);
                        Log.d(TAG, "onDownloadFinish");
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.mProgressBar.setProgress(100);
                                holder.mImageView.setImageResource(R.drawable.ic_launcher);
                                holder.mStatus.setText(R.string.download_downloaded);
                            }
                        });
                    }

                    @Override
                    public void onDownloadStart() {
                        Log.d(TAG, "onDownloadStart");
                        mTaskList.get(position).setDownloadState(DownloadState.INITIALIZE);
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.mImageView.setImageResource(R.drawable.ic_download_pause);
                                holder.mStatus.setText(R.string.download_downloading);

                            }
                        });
                    }

                    @Override
                    public void onDownloadPause() {
                        Log.d(TAG, "onDownloadPause");
                        mTaskList.get(position).setDownloadState(DownloadState.PAUSE);
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.mImageView.setImageResource(R.drawable.ic_download_ing);
                                holder.mStatus.setText(R.string.download_stopped);
                            }
                        });
                    }

                    @Override
                    public void onDownloadStop() {
                        mTaskList.get(position).setDownloadState(DownloadState.PAUSE);
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.mImageView.setImageResource(R.drawable.ic_download_ing);
                                holder.mStatus.setText(R.string.download_stopped);

                            }
                        });
                    }

                    @Override
                    public void onDownloadFail() {
                        Log.d(TAG, "onDownloadFail");
                        mTaskList.get(position).setDownloadState(DownloadState.PAUSE);
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.mImageView.setImageResource(R.drawable.ic_download_retry);
                                holder.mStatus.setText(R.string.download_failed);
                            }
                        });
                    }

                    @Override
                    public void onDownloadProgress(int finishedSize, int totalSize,
                            double progressPercent) {
                        Log.d(TAG, "download " + finishedSize);
                        holder.mProgressBar.setProgress((int) progressPercent);
                        holder.mSize.setText(formatSize(finishedSize,totalSize ));
                        holder.mImageView.setImageResource(R.drawable.ic_download_ing);//fast refresh may cause performance problem?

                    }
                });

        holder.mImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                switch (mTaskList.get(position).getDownloadState()) {
                    case PAUSE:
                        Log.i(TAG, "continue " + mTaskList.get(position).getFileName());
                        DownloadTaskManager.getInstance(mContext).continueDownload(
                                mTaskList.get(position));
                        holder.mImageView.setImageResource(R.drawable.ic_download_ing);// ???
                        break;
                    case DOWNLOADING:
                        Log.i(TAG, "pause " + mTaskList.get(position).getFileName());
                        DownloadTaskManager.getInstance(mContext).pauseDownload(
                                mTaskList.get(position));
                        break;
                    case FINISHED:

                        break;
                    case INITIALIZE:

                        break;
                    default:
                        break;
                }

            }
        });
        return convertView;
    }

    private String formatSize(int finishedSize, int totalSize) {
        DecimalFormat df = new DecimalFormat("0.#");
        StringBuilder sb = new StringBuilder(50);
        double finished = finishedSize / 1024 / 1024;
        if (finished < 1){
            sb.append(df.format(finishedSize / 1024)).append("K / ");
        } else {
            sb.append(df.format(finishedSize / 1024 / 1024)).append("M / ");
        }
        
        double total = totalSize / 1024 / 1024;
        if (total < 1){
            sb.append(df.format(totalSize / 1024)).append("K");
        } else {
            sb.append(df.format(total)).append("M");
        }
        return sb.toString();
    }

    private class ViewHolder {
        public ImageView mIcon;

        public TextView mTitle;

        public TextView mStatus;

        public TextView mSize;

        public ProgressBar mProgressBar;

        public ImageView mImageView;

    }

    public void refreshListView() {
        if (mContext != null) {
            // mContext.refreshListView();
        }
    }
}
