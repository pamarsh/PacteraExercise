package com.Pactera.PacteraExercise;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.Pactera.PacteraExercise.model.FactsRecord;

import java.util.ArrayList;

/**
 * Created by paul on 5/02/15.
 */
public class FactsItemListAdapter  extends BaseAdapter {

    // Contains the current list of facts that are displayed
    ArrayList<FactsRecord> factsRecords = new ArrayList<FactsRecord>();

    private final LayoutInflater layoutInflater;

    public FactsItemListAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);

        for (int i = 0 ; i < 20 ; i++ ) {
            factsRecords.add(new FactsRecord("title" + i, "Description " + i + " The quick brown fox jumped over the lazy dog over and over again", "URI"));
        }
    }

    @Override
    public int getCount() {
        return factsRecords.size();
    }

    @Override
    public Object getItem(int position) {
        return factsRecords.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        FactsRecord record = factsRecords.get(position);

        if ( convertView == null ) {
            convertView = layoutInflater.inflate(R.layout.facts_item, null);
            ((TextView) convertView.findViewById(R.id.item_title)).setText(record.getTitle());
            ((TextView) convertView.findViewById(R.id.item_description)).setText(record.getDescription());
            ((ImageView) convertView.findViewById(R.id.item_picture)).setImageResource(android.R.color.transparent);
        }
        return convertView;
    }
}
