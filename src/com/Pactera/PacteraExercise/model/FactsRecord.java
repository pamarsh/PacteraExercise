package com.Pactera.PacteraExercise.model;

/**
 * Container class holding the information about a single fact
 */
public class FactsRecord {

    private String title ;
    private String description ;
    private String imageHref ;


    public FactsRecord(String title, String description, String imageHref) {
        this.title = title;
        this.description = description;
        this.imageHref = imageHref;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageHref() {
        return imageHref;
    }
}
