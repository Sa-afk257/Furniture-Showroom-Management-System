package com.furniture.model;

public class Category {
    private int CategoryID ;
    private String CategoryName;


    public Category() {
    }
    public Category(int categoryID, String categoryName) {
        CategoryID = categoryID;
        CategoryName = categoryName;
    }

    public Category(String categoryName) {
        CategoryName = categoryName;
    }

    public int getCategoryID() {
        return CategoryID;
    }
    public void setCategoryID(int categoryID) {
        CategoryID = categoryID;
    }
    public String getCategoryName() {
        return CategoryName;
    }
    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }
    @Override
    public String toString() {
        return "Category [CategoryID=" + CategoryID + ", CategoryName=" + CategoryName + "]";
    }

    
}
