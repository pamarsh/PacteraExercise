package com.Pactera.PacteraExercise.retriever.images;

import android.graphics.Bitmap;

/**
 * Empty retriever so that we do not have to be checking for null pointers
 */
public class NoOpImageRetriever implements ImageRetriever {


    /**
     * NOP Image retriever used so that we don't have to hassle about checking for null all the time
     * This would be placed at the end of the retriever chain.
     */
    @Override
    public Bitmap getImage(String url) {
        return null;
    }

    @Override
    public void setImageStateChangeListener(ImageRetrieverListener listener) {
        // NOP
    }

    @Override
    public void setNextImageRetriever(ImageRetriever retriever) {
        // NOP
    }

    @Override
    public void reset() {
        // NOP
    }
}