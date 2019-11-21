package com.example.guiprogramming.recipe_search_engine;

/**
 * Class to save and hold details for Recipe
 * */
public class RecipeData {
    String title, publisher, f2f_url,
            source_url, recipe_id,
            image_url, publisher_url;
    double social_rank;

    public RecipeData() {
    }

    public RecipeData(String title, String publisher,
                      String f2f_url, String source_url, String recipe_id,
                      String image_url, String publisher_url,
                      double social_rank) {
        this.title = title;
        this.publisher = publisher;
        this.f2f_url = f2f_url;
        this.source_url = source_url;
        this.recipe_id = recipe_id;
        this.image_url = image_url;
        this.publisher_url = publisher_url;
        this.social_rank = social_rank;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getF2f_url() {
        return f2f_url;
    }

    public void setF2f_url(String f2f_url) {
        this.f2f_url = f2f_url;
    }

    public String getSource_url() {
        return source_url;
    }

    public void setSource_url(String source_url) {
        this.source_url = source_url;
    }

    public String getRecipe_id() {
        return recipe_id;
    }

    public void setRecipe_id(String recipe_id) {
        this.recipe_id = recipe_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getPublisher_url() {
        return publisher_url;
    }

    public void setPublisher_url(String publisher_url) {
        this.publisher_url = publisher_url;
    }

    public double getSocial_rank() {
        return social_rank;
    }

    public void setSocial_rank(double social_rank) {
        this.social_rank = social_rank;
    }
}
