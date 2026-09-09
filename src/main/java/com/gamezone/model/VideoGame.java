package com.gamezone.model;

public class VideoGame extends Product {
    private String platform;
    private String genre;
    private String ageRating;

    public VideoGame(String id, String title, double price, int stockQuantity, String platform, String genre, String ageRating) {
        super(id, title, price, stockQuantity);
        this.platform = platform;
        this.genre = genre;
        this.ageRating = ageRating;
    }
    public String getPlatform(){
        return platform;
    }
    public String getGenre(){
        return genre;
    }
    public String getAgeRating(){
        return ageRating;
    }
    @Override
    public String getDescription() {
        return getTitle() + " - " + platform + " - " + genre + " - " + ageRating;
    }
}
