package com.natsirasrafi.barangku;

import android.content.ClipData;

/**
 * Created by natsirasrafi on 4/28/17.
 */

public class ItemCategory {
    private String name;
    private String description;
    public ItemCategory(){

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private String image;

    public ItemCategory(String name, String description, String image) {
        this.name = name;
        this.description = description;
        this.image = image;
    }
}
