package com.abdul.jigsawpuzzle.Model;

public class PuzzleItem {
    public String imageUrl;
    public String categoryId;
    public long played;

    public PuzzleItem() {
    }

    public PuzzleItem(String imageUrl, String categoryId) {
        this.imageUrl= imageUrl;
        this.categoryId = categoryId;
    }

    public String getimageUrl() {
        return imageUrl;
    }

    public void setImageLink(String imageUrl) {
        this.imageUrl= imageUrl;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public long getPlayed() {
        return played;
    }

    public void setPlayed(long played) {
        this.played = played;
    }
}
