package com.Pactera.PacteraExercise.retriever;

import com.Pactera.PacteraExercise.model.FactsList;

/**
 * Listener interface for any class that need to be informed when the Json stream has been
 * read and converted
 */
public interface FactsItemsListener {

    void factItemsUpdated(FactsList factsList);

}
