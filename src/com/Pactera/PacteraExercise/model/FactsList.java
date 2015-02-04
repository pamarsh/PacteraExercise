package com.Pactera.PacteraExercise.model;

import java.util.ArrayList;

/**
 * Model used to extract the title and list of facts from the json string
 */
public class FactsList {

    private String title;
    private ArrayList<FactsRecord> rows;

    public String getTitle() {
        return title;
    }

    public ArrayList<FactsRecord> getRows() {
        return rows;
    }
}
