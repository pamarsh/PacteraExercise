package com.Pactera.PacteraExercise;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

/**
 * Main Activity starts up the application main activity and sets up the ListView
 */
public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setupListView();
    }

    private void setupListView() {
        ListView factsItemsListView = (ListView) findViewById(R.id.facts_list);
        FactsItemListAdapter factsItemListAdapter = new FactsItemListAdapter(this);
        factsItemsListView.setAdapter(factsItemListAdapter);
    }


}
