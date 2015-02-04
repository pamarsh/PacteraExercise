package com.Pactera.PacteraExercise.retriever.data;

import com.Pactera.PacteraExercise.model.FactsList;
import com.Pactera.PacteraExercise.retriever.data.AsyncFactsRetriever;
import com.Pactera.PacteraExercise.retriever.data.FactsItemsListener;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to retrieve Facts records from the remote server
 * and give it to any interested listener. The retrieval will be handled in an asyncTask
 * to avoid UI response issues.  Also network access can not occur on main
 * thread.
 */
public class FactsItemsRetriever implements FactsItemsListener {

    // Address where to find the Json
    private final String JSON_FEED_URL = "https://dl.dropboxusercontent.com/u/746330/facts.json";

    // Run the retriever in another thread
    AsyncFactsRetriever asyncFactsRetriever;

    List<FactsItemsListener> factsItemsListeners = new ArrayList<FactsItemsListener>();

    public void retrieveFacts() {
       if ( notCurrentlyRetrieving() ) {
           retrieve(JSON_FEED_URL);
       }
    }

    /**
     * Checks to see if we are already receiving the json data.
     * @return true if we are not actively retrieving the jason data.
     */
    private boolean notCurrentlyRetrieving() {
        return (asyncFactsRetriever == null);
    }


    private void setFactRetrieverNotRetrieving() {
        asyncFactsRetriever = null;
    }


    private void retrieve(String uri) {
        asyncFactsRetriever = new AsyncFactsRetriever();
        asyncFactsRetriever.fetch(uri, this);
    }


    public void registerListener( FactsItemsListener factsItemsListener) {
        factsItemsListeners.add(factsItemsListener) ;
    }

    @Override
    public void factItemsUpdated(FactsList factsList) {

        for (FactsItemsListener factsItemsListener : factsItemsListeners) {
            factsItemsListener.factItemsUpdated(factsList);
        }
        setFactRetrieverNotRetrieving();
    }


}
