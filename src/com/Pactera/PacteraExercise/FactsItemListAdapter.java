package com.Pactera.PacteraExercise;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.Pactera.PacteraExercise.model.FactsList;
import com.Pactera.PacteraExercise.model.FactsRecord;
import com.Pactera.PacteraExercise.retriever.data.FactsItemsListener;
import com.Pactera.PacteraExercise.retriever.images.ImageRetriever;
import com.Pactera.PacteraExercise.retriever.images.ImageRetrieverFactory;
import com.Pactera.PacteraExercise.retriever.images.ImageRetrieverListener;

import java.util.ArrayList;

/**
 * Converter class. This will insert the received data into the list view items as the list view
 * requires. It listens for FactsItems Data from the remote server via the FactItemsReceiver and
 * notifies itself of the change. The notification will trigger for all displayed items to redraw
 * themselves with the new data
 */
public class FactsItemListAdapter extends BaseAdapter implements FactsItemsListener, ImageRetrieverListener {

    // Contains the current list of facts that are displayed
    ArrayList<FactsRecord> factsRecords = new ArrayList<FactsRecord>();

    // used to build the item view based on the layout
    private final LayoutInflater layoutInflater;

    // Used to get any image associated with a url
    private final ImageRetriever imageRetriever;

    public FactsItemListAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        imageRetriever = ImageRetrieverFactory.getRetriever();
        imageRetriever.setImageStateChangeListener(this);
    }

    /**
     *
     * @return Number of records int he dataset that can be displayed
     */
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

    /**
     * insert the appropriate data into the given view based on the position of the view in the listView
     * @param position   Position of item view in the listView
     * @param convertView The View that needs to be displayed
     * @param parent      The ListView
     * @return            an Item view with the appropriate information being displayed
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        FactsItemViewContainer factsItemViewContainer;
        FactsRecord record = factsRecords.get(position);

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.facts_item, null);
            factsItemViewContainer = createViewContainer(convertView) ;
            convertView.setTag(factsItemViewContainer);
        } else {
            factsItemViewContainer = (FactsItemViewContainer) convertView.getTag();
        }

        injectInformationIntoViewItems(factsItemViewContainer, record);

        return convertView;
    }


    private FactsItemViewContainer createViewContainer(View convertView) {
        return  new FactsItemViewContainer((TextView) convertView.findViewById(R.id.item_title),
                (TextView) convertView.findViewById(R.id.item_description),
                (ImageView) convertView.findViewById(R.id.item_picture));
    }

    // Insert fact information into the appropriate view items
    private void injectInformationIntoViewItems(FactsItemViewContainer factsItemViewContainer, FactsRecord record) {
        if (record != null) {
            factsItemViewContainer.getTitle().setText(record.getTitle());
            factsItemViewContainer.getDescription().setText(record.getDescription());
            final Bitmap image = imageRetriever.getImage(record.getImageHref());
            updateItemImage(factsItemViewContainer, record, image);
        }
    }

    // change the imageView if the item based on the image availability
    private void updateItemImage(FactsItemViewContainer factsItemViewContainer, FactsRecord record, Bitmap image) {
        if (image != null ) {
            insertNewImage(factsItemViewContainer, record, image);
        } else {
            insertBlankImage(factsItemViewContainer, record);
        }
    }

    // Insert a image into the ImageView of the item.
    private void insertNewImage(FactsItemViewContainer factsItemViewContainer, FactsRecord record, Bitmap image) {
        Log.i("FactsItemListAdapter", "Setting Image for url " + record.getTitle() + "|" + record.getImageHref());
        factsItemViewContainer.getImage().setImageBitmap(image);
    }

    // Turn the image blank
    private void insertBlankImage(FactsItemViewContainer factsItemViewContainer, FactsRecord record) {
        Log.i("FactsItemListAdapter", "No Image for url " + record.getTitle() + "|" + record.getImageHref());
        factsItemViewContainer.getImage().setImageResource(android.R.color.transparent);
    }

    /**
     * Callback from facts item receiver indicating new fact have been received.
     * All retrievals of images are cancelled and past images are cleared
     * The current data is cleared and the new data is inserted. notifyStateChange is called to kick off
     * redrawing of the list items currently viewable.
     * @param factsList List of new items.
     */
    @Override
    public void factItemsUpdated(FactsList factsList) {
        imageRetriever.reset();
        factsRecords.clear();
        for (FactsRecord factsRecord : factsList.getRows()) {
            if (!factsRecord.isEmpty()) {
                factsRecords.add(factsRecord);
            }
        }
        this.notifyDataSetChanged();
    }

    // Callback from imageRetriever let the "getView" handle getting the image
    @Override
    public void newImageAvailable(String uri, Bitmap image) {
        this.notifyDataSetChanged();
    }
}
