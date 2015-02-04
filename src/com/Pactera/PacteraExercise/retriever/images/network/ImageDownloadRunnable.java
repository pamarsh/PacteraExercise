package com.Pactera.PacteraExercise.retriever.images.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Runnable used to download images from the network based in the url given in the ImageDownloadTask
 * object given during construction. The result upon completion of the run is that the  ImageDownloadTask
 * will contain a bitmap of the image that was to be retrieved or null of the retrieval was unsuccessful.
 */
class ImageDownloadRunnable implements Runnable {

    public static final int CONNECT_TIMEOUT = 10000;

    public static final String CLASSNAME = "AsyncImageLoaderTask";

    // Defines a field that contains the calling object of type PhotoTask.
    final ImageDownloadTask imageDownloadTask;

    /**
     * @param imageDownloadTask Task to service in this runnable
     */
    ImageDownloadRunnable(ImageDownloadTask imageDownloadTask) {
        this.imageDownloadTask = imageDownloadTask;
    }

    /*
    * run in given thread . retrieves an image from a remote server based on the
    * stored url and then converts that image into a bitmap.
     */
    @Override
    public void run() {

        imageDownloadTask.setCurrentThread(Thread.currentThread());

        // Moves the current Thread into the background
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        imageDownloadTask.handleDownloadState(ImageDownloadManager.DOWNLOAD_STARTED);
        imageDownloadTask.setBitmapImage(getBitmapFromUrl(imageDownloadTask.getImageUrl()));
        imageDownloadTask.handleDownloadState(ImageDownloadManager.DOWNLOAD_COMPLETE);
        imageDownloadTask.setCurrentThread(null);
        Thread.interrupted();
    }

    private Bitmap getBitmapFromUrl(URL url) {
        try {
            HttpURLConnection connection = setupHttpConnection(url);
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            input.close();
            return myBitmap;
        } catch (MalformedURLException e) {
            Log.i(CLASSNAME, "malformed Url" + url);
        } catch (IOException e) {
            Log.i(CLASSNAME, "Image retrieval failure" + e.getMessage());
        }
        return null;
    }

    private HttpURLConnection setupHttpConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.connect();
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        return connection;
    }
}
