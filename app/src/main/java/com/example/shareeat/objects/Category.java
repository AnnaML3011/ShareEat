package com.example.shareeat.objects;

public class Category {
    String category_Name;
    String category_image;

    public Category(){
    }

    public Category(String category_Name, String category_image) {
        this.category_Name = category_Name;
        this.category_image = category_image;
    }

    public String getCategory_Name() {
        return category_Name;
    }

    public void setCategory_Name(String category_Name) {
        this.category_Name = category_Name;
    }

    public String getCategory_image() {
        return category_image;
    }

    public void setCategory_image(String category_image) {
        this.category_image = category_image;
    }
}
