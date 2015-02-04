package com.Pactera.PacteraExercise;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.Pactera.PacteraExercise.model.FactsList;
import com.Pactera.PacteraExercise.retriever.FactsItemsListener;
import com.Pactera.PacteraExercise.retriever.FactsItemsRetriever;

/**
 * Main Activity starts up the application main activity and sets up the ListView
 */
public class MainActivity extends Activity implements FactsItemsListener {

    private FactsItemsRetriever factsItemsRetriever;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // build the adapter for the list view and then inject it into
        // the facts item retriever so that it can be notified of updates
        // from the receiver
        FactsItemListAdapter factsItemListAdapter = setupListView();
        factsItemsRetriever = buildFactsRetriever();
        factsItemsRetriever.registerListener(factsItemListAdapter);
        factsItemsRetriever.registerListener(this);

        setupSyncButtonClick();

        factsItemsRetriever.retrieveFacts();
    }

    // setup the sync button so that when it is pressed the receiver goes
    // and retrieves the data again
    private void setupSyncButtonClick() {
        View button = findViewById(R.id.resync_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                factsItemsRetriever.retrieveFacts();
            }
        });
    }

    private FactsItemListAdapter setupListView() {
        ListView factsItemsListView = (ListView) findViewById(R.id.facts_list);
        FactsItemListAdapter factsItemListAdapter = new FactsItemListAdapter(this);
        factsItemsListView.setAdapter(factsItemListAdapter);
        return factsItemListAdapter;
    }

    private FactsItemsRetriever buildFactsRetriever() {
        return new FactsItemsRetriever();
    }

    /**
     * If we receive an update in the Facts Items then set the application title
     * to be the title in the message
     *
     * @param factsList title and list of facts
     */
    @Override
    public void factItemsUpdated(FactsList factsList) {
        if (factsList != null) {
            setTitle(factsList.getTitle());
        }
    }
}
