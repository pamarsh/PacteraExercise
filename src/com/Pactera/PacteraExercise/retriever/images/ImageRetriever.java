package com.Pactera.PacteraExercise.retriever.images;

import android.graphics.Bitmap;

/**
 * Interface Description of the classes to retrieve an bitmap image from a resource
 * location based in a url key
 */
public interface ImageRetriever {

    /**
     * Get the bitmap image based on the url
     * @param url key to looking up the bitmap image
     * @return    bitmap image based on the url if immediately available otherwise null
     */
    public Bitmap getImage(String url);


    /**
     * Sets the listener that will be notified if a new image is available or images have been removed.
     * @param listener
     */
    public void setImageStateChangeListener(ImageRetrieverListener listener);


    /**
     * Set the next retriever to retrieve the image if this retriever does not contain the image
     * @param retriever
     */
    void setNextImageRetriever(ImageRetriever retriever) ;

    /**
     * clear all pending retrieval and knowledge of current images
     */
    void reset();
}
