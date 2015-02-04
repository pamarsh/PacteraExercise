package com.Pactera.PacteraExercise.retriever.images;

import android.graphics.Bitmap;

/**
 * Notifier interface for any object needing to know when a new image has been retrieved
 */
public interface ImageRetrieverListener {

    /**
     * Notifies when a requested image has become newly available
     */
    public void newImageAvailable(String uri, Bitmap image);

}
