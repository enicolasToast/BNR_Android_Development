package com.bignerdranch.android.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/*
    Some things to note:
        Looper is assigned to a handler on handler initialization, by getting the looper mapped to the current thread.
 */
public class ThumbnailDownloader<T> extends HandlerThread {
    private final static String TAG = ThumbnailDownloader.class.toString();
    private final static int MESSAGE_DOWNLOAD = 0;
    private final static int MESSAGE_URL_DOWNLOAD = 1;
    private final static int MAX_CACHE_LIMIT = 100;
    private final static int PRELOAD_IMAGE_COUNT = 10;

    private LruCache<String, Bitmap> mThumbnailCache = new LruCache(MAX_CACHE_LIMIT);
    private Handler mRequestHandler;
    private Handler mResponseHandler;
    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;
    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();

    public interface ThumbnailDownloadListener<T> {
        void onThumbnailDownloaded(T target, Bitmap bitmap);
    }

    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener) {
        mThumbnailDownloadListener = listener;
    }

    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }

    public void clearQueue() {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
    }

    public void queueThumbnail(T target, int position, List<String> urls) {
        if(urls == null || urls.isEmpty()) {
            return;
        }


        int startPosition = position - PRELOAD_IMAGE_COUNT;

        if(startPosition < 0) {
            startPosition = 0;
        }

        int endPosition = position + PRELOAD_IMAGE_COUNT;

        if(endPosition >= urls.size()) {
            endPosition = urls.size() - 1;
        }

        for(int i = startPosition; i < endPosition; i++) {
            String url = urls.get(i);
            Log.i(TAG, "Got a URL: " + url);

            if(i == position) {
                if (url == null) {
                    mRequestMap.remove(target);
                } else {
                    mRequestMap.put(target, url);
                    mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
                }
            } else {
                mRequestHandler.obtainMessage(MESSAGE_URL_DOWNLOAD, url).sendToTarget();
            }
        }
    }

    @Override
    protected void onLooperPrepared() {
        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == MESSAGE_DOWNLOAD) {
                    T target = (T) msg.obj;
                    Log.i(TAG, "Got a request URL: " + mRequestMap.get(target));
                    handleRequest(target);
                } else if (msg.what == MESSAGE_URL_DOWNLOAD) {
                    String url = (String) msg.obj;
                    Log.i(TAG, "Got a request URL: " + url);
                    getBitmap(url);
                }
            }
        };
    }

    private void handleRequest(final T target) {
        final String url = mRequestMap.get(target);

        if(url == null) {
            return;
        }

        final Bitmap bitmap = getBitmap(url);

        mResponseHandler.post(new Runnable() {
            @Override
            public void run() {
            if(mRequestMap.get(target) != url) {
                return;
            }

            mRequestMap.remove(target);
            mThumbnailDownloadListener.onThumbnailDownloaded(target, bitmap);
            }
        });
    }

    private Bitmap getBitmap(String url) {
        Bitmap bitmap = null;

        try {
            if(mThumbnailCache.get(url) == null) {
                byte[] bitmapBytes = new FlickrFetcher().getUrlBytes(url);
                bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                mThumbnailCache.put(url, bitmap);
                Log.i(TAG, "Bitmap created");
            } else {
                bitmap = mThumbnailCache.get(url);
                Log.i(TAG, "Bitmap cache hit!");
            }
        } catch (IOException ioe) {
            Log.e(TAG, "Error downloading image", ioe);
        }

        return bitmap;
    }
}
