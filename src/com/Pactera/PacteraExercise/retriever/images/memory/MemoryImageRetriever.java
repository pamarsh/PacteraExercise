package com.Pactera.PacteraExercise.retriever.images.memory;

import android.graphics.Bitmap;
import com.Pactera.PacteraExercise.retriever.images.ImageRetriever;
import com.Pactera.PacteraExercise.retriever.images.ImageRetrieverListener;
import com.Pactera.PacteraExercise.retriever.images.NoOpImageRetriever;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that will store any received images in memory for fast retrieval.
 * Note: This stores in memory but would be better to store in a directory
 */
public class MemoryImageRetriever implements ImageRetriever, ImageRetrieverListener {

    // Contains the list of bitmaps that have been retrieved and indexed based on the uri
    private Map<String, Bitmap> cache = new HashMap<String, Bitmap>();

    // next imageRetriever in the chain.  If "this" does not have then bitmap then past the
    // request on to nextImageRetriever
    private ImageRetriever nextImageRetriever = new NoOpImageRetriever();

    // Contains the list of listeners that need to be notified if the cache is updated
    private List<ImageRetrieverListener> listeners = new ArrayList<ImageRetrieverListener>();

    @Override
    synchronized public  Bitmap getImage(String url) {
        if ( url == null) {
            return null;
        }

        if (cache.containsKey(url)) {
            return cache.get(url);
        }

        return nextImageRetriever.getImage(url);
    }

    @Override
    public void setImageStateChangeListener(ImageRetrieverListener listener) {
        listeners.add(listener);
    }

    @Override
    public void setNextImageRetriever(ImageRetriever retriever) {
        nextImageRetriever = retriever;
    }

    @Override
    synchronized public void reset() {
        cache.clear();
        nextImageRetriever.reset();
    }

    /**
     * Callback from next listener indicating new image available. Store the bitmap and let any
     * listeners know.
     * @param url    url associated with the bitmap
     * @param bitmap the bitmap just retrieved.
     */
    @Override
    synchronized public void newImageAvailable(String url, Bitmap bitmap) {
        cache.remove(url);
        cache.put(url, bitmap);
        for (ImageRetrieverListener listener : listeners) {
            listener.newImageAvailable(url, bitmap);
        }
    }
}
