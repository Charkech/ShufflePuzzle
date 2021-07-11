package com.shufflepuzzle;

import java.io.Serializable;

public class LevelModel implements Serializable {

    private int id;
    private int cat_id;
    private int level;
    private String image;
    private String image_name;
    private int rating;
    private int temp_rating;
    private int status;
    private int coin;
    private int high_coin;
    private int mode;
    private String update_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCat_id() {
        return cat_id;
    }

    public void setCat_id(int cat_id) {
        this.cat_id = cat_id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }



    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public int getHigh_coin() {
        return high_coin;
    }

    public void setHigh_coin(int high_coin) {
        this.high_coin = high_coin;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(String update_at) {
        this.update_at = update_at;
    }

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public int getTemp_rating() {
        return temp_rating;
    }

    public void setTemp_rating(int temp_rating) {
        this.temp_rating = temp_rating;
    }
}
