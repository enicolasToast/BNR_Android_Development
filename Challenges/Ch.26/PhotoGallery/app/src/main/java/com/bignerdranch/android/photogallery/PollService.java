package com.bignerdranch.android.photogallery;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;


public class PollService {
    private static final String TAG = "PollService";
    private static PollIntentService mPollIntentService;
    private static PollJobService mPollJobService;

    public static void init() {
        if(usePollJobService()) {
            mPollJobService = new PollJobService();
        } else {
            mPollIntentService = new PollIntentService();
        }
    }

    public static boolean isRunning(Context context) {
        if(usePollJobService()) {
            return PollJobService.isRunning(context);
        } else {
            return PollIntentService.isServiceAlarmOn(context);
        }
    }

    public static void start(Context context, boolean shouldTrigger) {
        if(usePollJobService()) {
            PollJobService.startOrStopJob(context, shouldTrigger);
        } else {
            PollIntentService.setAlarm(context, shouldTrigger);
        }
    }

    private static boolean usePollJobService() {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    }


    public static class PollIntentService extends IntentService {
        private static final String TAG = "PollIntentService";
        private static final int POLL_INTERVAL = 1000 * 60; // 60 seconds

        private static Intent newIntent(Context context) {
            return new Intent(context, PollIntentService.class);
        }

        public PollIntentService() {
            super(TAG);
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            executePoll(this);
        }

        public static void setAlarm(Context context, boolean isOn) {
            Intent intent = PollIntentService.newIntent(context);
            PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            if(isOn) {
                alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                        SystemClock.elapsedRealtime(), POLL_INTERVAL, pi);
            } else {
                alarmManager.cancel(pi);
                pi.cancel();
            }
        }

        public static boolean isServiceAlarmOn(Context context) {
            Intent i = PollIntentService.newIntent(context);
            PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
            return pi != null;
        }
    }

    @TargetApi(21)
    public static class PollJobService extends JobService {

        private static final String TAG = "PollJobService";
        protected static final int JOB_ID = 10;

        private PollTask mCurrentTask;

        private class PollTask extends AsyncTask<JobParameters, Void, Void> {
            Context mContext;

            public PollTask(Context context) {
                mContext = context;
            }

            @Override
            public Void doInBackground(JobParameters... params) {
                JobParameters jobParameters = params[0];
                executePoll(mContext);
                jobFinished(jobParameters, false);

                return null;
            }
        }

        public static boolean isRunning(Context context) {
            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

            for(JobInfo jobInfo : scheduler.getAllPendingJobs()) {
                if(jobInfo.getId() == PollJobService.JOB_ID) {
                    return true;
                }
            }

            return false;
        }

        public static void startOrStopJob(Context context, boolean isOn) {
            JobScheduler scheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);

            if(isOn) {
                JobInfo jobInfo = new JobInfo.Builder(JOB_ID, new ComponentName(context, PollJobService.class))
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                        .setPeriodic(1000 * 60 * 15)
                        .setPersisted(true)
                        .build();

                scheduler.schedule(jobInfo);
            } else {
                scheduler.cancel(JOB_ID);
            }
        }

        @Override
        public boolean onStartJob(JobParameters params) {
            mCurrentTask = new PollTask(this);
            mCurrentTask.execute(params);
            return true;
        }

        @Override
        public boolean onStopJob(JobParameters params) {
            if(mCurrentTask != null) {
                mCurrentTask.cancel(true);
            }

            return false;
        }
    }

    private static void executePoll(Context context) {
        if (!isNetworkAvailableAndConnected(context)) {
            return;
        }

        String query = QueryPreferences.getStoredQuery(context);

        List<GalleryItem> items;

        if (query == null) {
            items = new FlickrFetcher().fetchRecentPhotos();
        } else {
            items = new FlickrFetcher().searchPhotos(query);
        }

        String resultId = items.get(0).getId();

        String lastResultId = QueryPreferences.getLastResultId(context);
        if(resultId.equals(lastResultId)) {
            Log.i(TAG, "Got an old result: " + resultId);
        } else {
            Log.i(TAG, "Got a new result: " + resultId);

            Resources resources = context.getResources();
            Intent i = PhotoGalleryActivity.newIntent(context);
            PendingIntent pi = PendingIntent.getActivity(context, 0, i, 0);

            Notification notification = new NotificationCompat.Builder(context)
                    .setTicker(resources.getString(R.string.new_pictures_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(resources.getString(R.string.new_pictures_title))
                    .setContentText(resources.getString(R.string.new_pictures_text))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(1, notification);
        }

        QueryPreferences.setLastResultId(context, resultId);
    }

    private static boolean isNetworkAvailableAndConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
    }
}
