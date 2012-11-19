
package com.zxt.download2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * DownloadNotificationListener implements DownloadListener and finished the job
 * of show download progress and messages in notification bar.You can use it
 * show messages in notification bar, or you can write own DownloadListener.
 * 
 * @author offbye@gmail.com
 */

public class DownloadNotificationListener implements DownloadListener {

    private Context mContext;

    private Notification mNotification;

    private int mId;

    private NotificationManager mNotificationManager;

    private int mProgress = 0;

    public DownloadNotificationListener(Context context, DownloadTask task) {
        mContext = context;
        mId = task.getUrl().hashCode();
        mNotificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotification = initNotifiction(task.getTitle());
    }

    @Override
    public void onDownloadStop() {
        mNotification.contentView.setTextViewText(Res.getInstance(mContext).getId("notify_state"),
                mContext.getString(Res.getInstance(mContext).getString("download_stopped")));

        mNotificationManager.notify(mId, mNotification);
        mNotificationManager.cancel(mId);
    }

    @Override
    public void onDownloadStart() {
        mNotificationManager.notify(mId, mNotification);
    }

    @Override
    public void onDownloadProgress(int finishedSize, int totalSize, int speed) {
        int percent = finishedSize * 100 / totalSize;
        if (percent - mProgress > 1) { // 降低状态栏进度刷新频率，性能问题
            mProgress = percent;
            mNotification.contentView.setTextViewText(Res.getInstance(mContext).getId("notify_state"),
                    String.format(mContext.getString(Res.getInstance(mContext).getString(
                            "download_speed")), mProgress, speed));
            mNotification.contentView.setProgressBar(Res.getInstance(mContext).getId("notify_processbar"), 100, percent, false);
            mNotificationManager.notify(mId, mNotification);
        }
    }

    @Override
    public void onDownloadPause() {
        mNotification.contentView.setTextViewText(Res.getInstance(mContext).getId("notify_state"),
                mContext.getString(Res.getInstance(mContext).getString("download_paused")));
        mNotification.contentView.setProgressBar(Res.getInstance(mContext).getId("notify_processbar"), 100, 0, true);
        mNotificationManager.notify(mId, mNotification);
    }

    @Override
    public void onDownloadFinish(String filepath) {
        mNotification.icon = android.R.drawable.stat_sys_download_done;
        mNotification.contentView.setTextViewText(Res.getInstance(mContext).getId("notify_state"),
                mContext.getString(Res.getInstance(mContext).getString("download_finished")));
        mNotification.contentView.setProgressBar(Res.getInstance(mContext).getId("notify_processbar"), 100, 100, false);
        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotification.defaults |= Notification.DEFAULT_SOUND;
        mNotification.defaults |= Notification.DEFAULT_LIGHTS;

        Intent intent = new Intent( mContext.getString(Res.getInstance(mContext).getString("download_list_action")));
        intent.putExtra("isDownloaded", true);

        mNotification.contentIntent = PendingIntent.getActivity(mContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mNotificationManager.notify(mId, mNotification);
        // Toast.makeText(mContext,
        // String.format(mContext.getString(Res.getInstance(mContext).getString("downloaded_file),
        // filepath), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDownloadFail() {
        mNotification.contentView.setTextViewText(Res.getInstance(mContext).getId("notify_state"),
                mContext.getString(Res.getInstance(mContext).getString("download_failed")));
        mNotification.contentView.setProgressBar(Res.getInstance(mContext).getId("notify_processbar"), 100, 0, true);
        mNotificationManager.notify(mId, mNotification);
        mNotificationManager.cancel(mId);
    }

    public Notification initNotifiction(String title) {
        Notification notification = new Notification(android.R.drawable.stat_sys_download,
                mContext.getString(Res.getInstance(mContext).getString("downloading_msg")) + title, System.currentTimeMillis());

        notification.contentView = new RemoteViews(mContext.getPackageName(),
                Res.getInstance(mContext).getLayout("download_notify"));
        notification.contentView.setProgressBar(Res.getInstance(mContext).getId("notify_processbar"), 100, 0, false);
        notification.contentView.setTextViewText(Res.getInstance(mContext).getId("notify_state"),
                mContext.getString(Res.getInstance(mContext).getString("downloading_msg")));

        notification.contentView.setTextViewText(Res.getInstance(mContext).getId("notify_text"), title);

        notification.contentIntent = PendingIntent.getActivity(mContext, 0, new Intent( mContext.getString(Res.getInstance(mContext).getString("download_list_action"))), PendingIntent.FLAG_UPDATE_CURRENT);
        return notification;

    }
}
