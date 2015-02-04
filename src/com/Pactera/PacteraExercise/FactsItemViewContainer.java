package com.Pactera.PacteraExercise;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Simple container to hold all element views in a ListView item.
 */
public class FactsItemViewContainer {
    TextView title;
    TextView description;
    ImageView image;

    public FactsItemViewContainer(TextView title, TextView description, ImageView image) {
        this.title = title;
        this.description = description;
        this.image = image;
    }

    public TextView getTitle() {
        return title;
    }

    public TextView getDescription() {
        return description;
    }

    public ImageView getImage() {
        return image;
    }
}
