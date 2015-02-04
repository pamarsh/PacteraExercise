package com.Pactera.PacteraExercise.retriever.images.network;

import android.graphics.Bitmap;

import java.net.MalformedURLException;
import java.net.URL;

public class ImageDownloadTask {

    private URL imageUrl;

    private String stringUrl;

    private Runnable downloadRunnable;

    private Bitmap bitmapImage;

    private Thread currentThread;

    private static ImageDownloadManager imageDownloadManager;

    public ImageDownloadTask() {
        downloadRunnable = new ImageDownloadRunnable(this);
        imageDownloadManager = ImageDownloadManager.getInstance();
    }

    /**
     * Recycles an task object before it's put back into the pool.
     */
    void cleanup() {
        bitmapImage = null;
    }

    public void setStringUrl(String stringUrl) throws MalformedURLException {
        this.stringUrl = stringUrl;
        imageUrl = new URL(stringUrl);
    }

    public void setDownloadRunnable(Runnable downloadRunnable) {
        this.downloadRunnable = downloadRunnable;
    }

    public void setBitmapImage(Bitmap bitmapImage) {
        this.bitmapImage = bitmapImage;
    }

    /*
    * Sets the identifier for the current Thread. This must be a synchronized operation; see the
    * notes for getCurrentThread()
    */
    public void setCurrentThread(Thread thread) {
        synchronized (imageDownloadManager) {
            currentThread = thread;
        }
    }

    public static void setImageDownloadManager(ImageDownloadManager imageDownloadManager) {
        ImageDownloadTask.imageDownloadManager = imageDownloadManager;
    }

    public URL getImageUrl() {
        return imageUrl;
    }

    public String getStringUrl() {
        return stringUrl;
    }

    public Runnable getDownloadRunnable() {
        return downloadRunnable;
    }

    public Bitmap getBitmapImage() {
        return bitmapImage;
    }

    public Thread getCurrentThread() {
        synchronized (imageDownloadManager) {
            return currentThread;
        }
    }

    public static ImageDownloadManager getImageDownloadManager() {
        return imageDownloadManager;
    }



    public void handleDownloadState(int state) {
        imageDownloadManager.handleState(this, state);
    }
}

