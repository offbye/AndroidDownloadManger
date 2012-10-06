
package com.zxt.download2;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public class DownloadingAdapter extends ArrayAdapter<DownloadTask> {

    private static final String TAG = "DownloadItemAdapter";

    private LayoutInflater mLayoutInflater;

    private List<DownloadTask> mTaskList;

    private Context mContext;

    public DownloadingAdapter(Context context, int textViewResourceId, List<DownloadTask> taskList) {
        super(context, textViewResourceId, taskList);
        mTaskList = taskList;
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    public int getCount() {
        return mTaskList.size();
    }

    public DownloadTask getItem(int position) {
        return mTaskList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        final DownloadTask task = mTaskList.get(position);
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.download_list_item, null);
            holder = new ViewHolder();

            holder.mThumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
            holder.mTitle = (TextView) convertView.findViewById(R.id.title);
            holder.mSize = (TextView) convertView.findViewById(R.id.size);
            holder.mStatusText = (TextView) convertView.findViewById(R.id.state);
            holder.mStateImageView = (ImageView) convertView.findViewById(R.id.ic_state);
            holder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.progress);
            holder.mProgressBar.setMax(100);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTitle.setText(task.getTitle());
        holder.mSize.setText(formatSize(task.getFinishedSize(), task.getTotalSize()));

        if(!TextUtils.isEmpty(task.getThumbnail())){
            holder.mThumbnail.setImageURI(Uri.parse(task.getThumbnail()));
        }
        // ImageUtil.loadImage(holder.mIcon, task.getThumbnail());

        if (task.getPercent() > 0) {
            holder.mProgressBar.setProgress(task.getPercent());
        }

        switch (mTaskList.get(position).getDownloadState()) {

            case PAUSE:
                holder.mStatusText.setText(R.string.download_paused);
                holder.mStateImageView.setImageResource(R.drawable.ic_download_ing);
                holder.mProgressBar.setIndeterminate(true);
                break;
            case FAILED:
                holder.mStatusText.setText(R.string.download_failed);
                holder.mStateImageView.setImageResource(R.drawable.ic_download_retry);
                holder.mProgressBar.setIndeterminate(true);
                break;
            case DOWNLOADING:
                holder.mStatusText.setText(R.string.download_downloading);
                holder.mStateImageView.setImageResource(R.drawable.ic_download_pause);
                holder.mProgressBar.setIndeterminate(false);
                break;
            case FINISHED:
                holder.mProgressBar.setProgress(100);
                holder.mProgressBar.setIndeterminate(false);
                holder.mStatusText.setText(R.string.download_finished);
                holder.mStateImageView.setImageResource(R.drawable.download_finished_do);
                break;
            case INITIALIZE:
                holder.mProgressBar.setIndeterminate(false);
                holder.mStatusText.setText(R.string.download_initial);
                holder.mStateImageView.setImageResource(R.drawable.ic_download_ing);
                break;
            default:
                break;
        }

        return convertView;
    }

    private String formatSize(int finishedSize, int totalSize) {
        StringBuilder sb = new StringBuilder(50);

        float finished = ((float) finishedSize) / 1024 / 1024;
        if (finished < 1) {
            sb.append(String.format("%1$.2f K / ", ((float) finishedSize) / 1024));
        } else {
            sb.append((String.format("%1$.2f M / ", finished)));
        }

        float total = ((float) totalSize) / 1024 / 1024;
        if (total < 1) {
            sb.append(String.format("%1$.2f K ", ((float) totalSize) / 1024));
        } else {
            sb.append(String.format("%1$.2f M ", total));
        }
        return sb.toString();
    }

    static class ViewHolder {
        public ImageView mThumbnail;

        public TextView mTitle;

        public TextView mStatusText;

        public TextView mSize;

        public ProgressBar mProgressBar;

        public ImageView mStateImageView;

    }

}
