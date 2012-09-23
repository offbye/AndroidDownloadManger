
package com.zxt.download2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class DownloadNotificationListener implements DownloadListener {
    private Context mContext;

    private Notification mNotification;

    private int mId;

    private NotificationManager mNotificationManager;

    public DownloadNotificationListener(Context context, DownloadTask task) {
        mContext = context;
        mId = task.getUrl().hashCode();
        mNotificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotification = initNotifiction(task.getTitle());
    }

    @Override
    public void onDownloadStop() {
        mNotification.contentView.setTextViewText(R.id.notify_state,
                mContext.getString(R.string.download_stopped));

        mNotificationManager.notify(mId, mNotification);
    }

    @Override
    public void onDownloadStart() {
        mNotificationManager.notify(mId, mNotification);
    }

    @Override
    public void onDownloadProgress(int finishedSize, int totalSize, double progressPercent) {
        mNotification.contentView.setTextViewText(R.id.notify_state,
                mContext.getString(R.string.downloading_msg) + (int) Math.round(progressPercent)
                        + "%");
        mNotification.contentView.setProgressBar(R.id.notify_processbar, 100,
                (int) Math.round(progressPercent), false);
        mNotificationManager.notify(mId, mNotification);
    }

    @Override
    public void onDownloadPause() {
        mNotification.contentView.setTextViewText(R.id.notify_state,
                mContext.getString(R.string.download_paused));
        mNotificationManager.notify(mId, mNotification);
    }

    @Override
    public void onDownloadFinish(String filepath) {
        mNotification.contentView.setTextViewText(R.id.notify_state,
                mContext.getString(R.string.download_finished));
        mNotificationManager.notify(mId, mNotification);
    }

    @Override
    public void onDownloadFail() {
        mNotification.contentView.setTextViewText(R.id.notify_state,
                mContext.getString(R.string.download_failed));
        mNotificationManager.notify(mId, mNotification);
        mNotificationManager.cancel(mId);
    }

    public Notification initNotifiction(String title) {
        Notification notification = new Notification(R.drawable.ic_download_ing,
                mContext.getString(R.string.downloading_msg) + title, System.currentTimeMillis());
        notification.icon = R.drawable.ic_download_ing;

        notification.contentView = new RemoteViews(mContext.getPackageName(),
                R.layout.download_notify);
        notification.contentView.setProgressBar(R.id.notify_processbar, 100, 0, false);
        notification.contentView.setTextViewText(R.id.notify_state,
                mContext.getString(R.string.downloading_msg));

        notification.contentView.setTextViewText(R.id.notify_text, title);

        notification.contentIntent = PendingIntent.getActivity(mContext, 0, new Intent(mContext,
                Download2Activity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        return notification;

    }
}
