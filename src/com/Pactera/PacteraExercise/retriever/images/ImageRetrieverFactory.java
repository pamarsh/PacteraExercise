package com.Pactera.PacteraExercise.retriever.images;


import com.Pactera.PacteraExercise.retriever.images.memory.MemoryImageRetriever;
import com.Pactera.PacteraExercise.retriever.images.network.ImageDownloadManager;

/**
 * Image retrievers use the Chain pattern to retrieve a image
 * If the image is not stored in memory then the network retriever
 * is called to retrieve it.
 * This Class builds the chain. It allows only one chain to be built
 */
public class ImageRetrieverFactory {

    private static final ImageRetrieverFactory instance = new ImageRetrieverFactory();
    private ImageRetriever retriever;

    private ImageRetrieverFactory() {
    }

    public static ImageRetriever getRetriever() {
        return instance.buildRetriever();
    }

    private ImageRetriever buildRetriever() {
        if (retriever == null) {
            ImageDownloadManager imageDownloadManager = ImageDownloadManager.getInstance();

            MemoryImageRetriever memoryImageRetriever = new MemoryImageRetriever();

            imageDownloadManager.setImageStateChangeListener(memoryImageRetriever);
            memoryImageRetriever.setNextImageRetriever(imageDownloadManager);

            retriever = memoryImageRetriever;
        }
        return retriever;
    }
}

