package com.gamezone.model;

/**
 * Represents a video game product in the GameZone inventory.
 */
public class VideoGame extends Product {

    private String platform;
    private String genre;
    private String ageRating;

    /**
     * Creates a new video game.
     *
     * @param id unique identifier of the video game
     * @param title title of the video game
     * @param price price of the video game
     * @param stockQuantity available quantity in stock
     * @param platform platform of the video game
     * @param genre genre of the video game
     * @param ageRating age rating of the video game
     */
    public VideoGame(String id, String title, double price, int stockQuantity, String platform, String genre, String ageRating) {
        super(id, title, price, stockQuantity);
        this.platform = platform;
        this.genre = genre;
        this.ageRating = ageRating;
    }

    /**
     * Returns the platform of the video game.
     *
     * @return video game platform
     */
    public String getPlatform(){
        return platform;
    }

    /**
     * Returns the genre of the video game.
     *
     * @return video game genre
     */
    public String getGenre(){
        return genre;
    }

    /**
     * Returns the age rating of the video game.
     *
     * @return video game age rating
     */
    public String getAgeRating(){
        return ageRating;
    }

    /**
     * Updates the video game platform.
     *
     * @param platform new video game platform
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /**
     * Updates the video game genre.
     *
     * @param genre new video game genre
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Updates the age rating of the video game.
     *
     * @param ageRating new age rating
     */
    public void setAgeRating(String ageRating) {
        this.ageRating = ageRating;
    }

    /**
     * Returns a description of the video game.
     *
     * @return video game description
     */
    @Override
    public String getDescription() {
        return getTitle() + " - " + platform + " - " + genre + " - " + ageRating;
    }
}