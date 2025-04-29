package com.example.e_almawar.viewmodel;

public class Ekskul {
    private String title;

    private String imageUrl;

    public Ekskul() {
        // diperlukan oleh Firestore
    }

    public Ekskul(String nama, String deskripsi, String imageUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
