package com.Pactera.PacteraExercise.retriever.images.network;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.Pactera.PacteraExercise.retriever.images.ImageRetriever;
import com.Pactera.PacteraExercise.retriever.images.ImageRetrieverListener;
import com.Pactera.PacteraExercise.retriever.images.NoOpImageRetriever;

import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Network image retriever used to get the image off the network based on the given url. This used A thread pool
 * so that multiple threads could be retrieving different images and reducing the changes of blocking due to
 * unreachable url locations causing long timeouts.
 */
public class ImageDownloadManager implements ImageRetriever {



    static final int DOWNLOAD_FAILED = -1;
    static final int DOWNLOAD_STARTED = 1;
    static final int DOWNLOAD_COMPLETE = 2;

    // recievers to notify when a image is retrieved
    private List<ImageRetrieverListener> listeners = new ArrayList<ImageRetrieverListener>();

    // Used to ensure that we do not have two threads getting the same image
    private Map<String, ImageDownloadTask> imageDownloadTaskMap = new HashMap<String, ImageDownloadTask>();

    // next retriever in the chain
    ImageRetriever nextRetriever = new NoOpImageRetriever();


    // Sets the amount of time an idle thread will wait for a task before terminating
    private static final int KEEP_ALIVE_TIME = 1;

    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;

    // Sets the initial threadpool size to 8
    private static final int CORE_POOL_SIZE = 8;

    // Sets the maximum threadpool size to 8
    private static final int MAXIMUM_POOL_SIZE = 8;

    // A queue of Runnables for the image download pool
    private final BlockingQueue<Runnable> downloadWorkQueue;

    // A queue of tasks nut currently being used.
    private final Queue<ImageDownloadTask> imageDownloadTaskWorkQueue;

    // A managed pool of background download threads
    private final ThreadPoolExecutor mDownloadThreadPool;

    // An object that manages Messages in a Thread
    private Handler mHandler;

    // A single instance of ImageDownloadManager, used to implement the singleton pattern
    private static ImageDownloadManager sInstance = null;

    static {
        // The time unit for "keep alive" is in seconds
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        sInstance = new ImageDownloadManager();
    }

    /**
     * Constructs the work queues and thread pools used to download and decode images.
     */
    private ImageDownloadManager() {

        /*
         * Creates a work queue for the pool of Thread objects used for downloading, using a linked
         * list queue that blocks when the queue is empty.
         */
        downloadWorkQueue = new LinkedBlockingQueue<Runnable>();

        /*
         * Creates a work queue for the set of of task objects that control downloading
          * using a linked list queue that blocks when the queue is empty.
         */
        imageDownloadTaskWorkQueue = new LinkedBlockingQueue<ImageDownloadTask>();

        /*
         * Creates a new pool of Thread objects for the download work queue
         */
        mDownloadThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, downloadWorkQueue);


        // Anonymous object used to retrieve messags froma worker thread and pass
        // them to the UI thread
        mHandler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message inputMessage) {

                ImageDownloadTask imageDownloadTask = (ImageDownloadTask) inputMessage.obj;

                switch (inputMessage.what) {
                    case DOWNLOAD_STARTED:
                        break;

                    case DOWNLOAD_COMPLETE:
                        notifyListeners(imageDownloadTask);
                        returnTaskToFreePool(imageDownloadTask);
                        break;
                    case DOWNLOAD_FAILED:
                        returnTaskToFreePool(imageDownloadTask);
                        break;
                    default:
                        super.handleMessage(inputMessage);
                }
            }
        };
    }


    /**
     * Get the singleton instance if the ImageDownloader.
     *
     * @return ImageDownloader
     */
    public static ImageDownloadManager getInstance() {
        return sInstance;
    }

    /**
     * Handles state messages for a particular task object
     *
     * @param imageDownloadTask A task object
     * @param state             The state of the task
     */
    public void handleState(ImageDownloadTask imageDownloadTask, int state) {
        switch (state) {
            case DOWNLOAD_COMPLETE:
                Message completeMessage = mHandler.obtainMessage(state, imageDownloadTask);
                completeMessage.sendToTarget();
                break;
            default:
                mHandler.obtainMessage(state, imageDownloadTask).sendToTarget();
                break;
        }
    }

    /**
     * Cancels all Threads in the ThreadPool
     */
    public static void cancelAll() {

        ImageDownloadTask[] taskArray = new ImageDownloadTask[sInstance.downloadWorkQueue.size()];

        // Populates the array with the task objects in the queue
        sInstance.downloadWorkQueue.toArray(taskArray);

        /*
         * Locks on the singleton to ensure that other processes aren't mutating Threads, then
         * iterates over the array of tasks and interrupts the task's current Thread.
         */
        synchronized (sInstance) {
            for (ImageDownloadTask imageDownloadTask : taskArray) {
                Thread thread = imageDownloadTask.getCurrentThread();
                if (null != thread) {
                    thread.interrupt();
                }
            }
        }
    }

    /**
     * set the task up to retrieve the image based un the given url and then
     * execute the task in the background
     *
     * @return The task instance that will handle the work
     */
    static public ImageDownloadTask startDownload(String url) throws MalformedURLException {

        // Gets a task from the pool or if none available create a new task
        ImageDownloadTask downloadTask = sInstance.imageDownloadTaskWorkQueue.poll();
        if (null == downloadTask) {
            downloadTask = new ImageDownloadTask();
        }

        downloadTask.setStringUrl(url);

        // Set the task to run in the next available thread queue.
        sInstance.mDownloadThreadPool.execute(downloadTask.getDownloadRunnable());

        return downloadTask;
    }

    /**
     * Recycles tasks by calling their internal cleanup() method and then putting them back into
     * the task queue.
     *
     * @param downloadTask The task to cleanup
     */
    void returnTaskToFreePool(ImageDownloadTask downloadTask) {
        downloadTask.cleanup();
        imageDownloadTaskWorkQueue.offer(downloadTask);
    }

    /**
     * Get the image for the given url.  As this is a asynchronous task then the
     * we would not have the image to return from this function.  Images will be in a call
     * back to the registered listeners once the runnable has completed retrieving the image
     * or an error occurs.
     *
     * @return null - as no image can be instantly retrieved
     */
    @Override
    public Bitmap getImage(String url) {
        try {
            if (!taskForUrlAlreadyRunning(url)) {
                imageDownloadTaskMap.put(url, startDownload(url));
            }
        } catch (MalformedURLException e) {
            Log.i("ImageDownloadManager", "Malformed url" + url);
        }
        return null;
    }

    // There is already a task retrieving this image
    private boolean taskForUrlAlreadyRunning(String url) {
        return imageDownloadTaskMap.get(url) != null;
    }

    @Override
    public void setImageStateChangeListener(ImageRetrieverListener listener) {
        listeners.add(listener);
    }

    // If a new image has been retrieved then give the image to all the registered listeners
    private void notifyListeners(ImageDownloadTask imageDownloadTask) {
        imageDownloadTaskMap.remove(imageDownloadTask.getStringUrl());
        if (imageDownloadTask.getBitmapImage() != null) {
            for (ImageRetrieverListener listener : listeners) {
                listener.newImageAvailable(imageDownloadTask.getStringUrl(), imageDownloadTask.getBitmapImage());
            }
        }
    }

    /**
     * Set the next image retriever in the chain
     */
    @Override
    public void setNextImageRetriever(ImageRetriever retriever) {
        nextRetriever = retriever;
    }

    /**
     * reset any tasks currently in progress to retrieve images
     */
    @Override
    public void reset() {
        cancelAll();
    }
}
