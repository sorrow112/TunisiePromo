package com.example.tunisiepromo_miniproject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Promo {
    private String productId;
    private String name;
    private double price;
    private int discount;
    private String imageUrl;
    private List<Rating> ratings;
    private String merchant;
    private String merchantId;
    private String category;
    private Date endDate;

    private int ratingsCount;


    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getMerchant() {
        return merchant;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public Promo(Date endDate,String category,String productId,String merchantId, String name, double price, int discount, String imageUrl,  String merchant) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.discount = discount;
        this.imageUrl = imageUrl;
        this.ratings = new ArrayList<Rating>();
        this.merchant = merchant;
        this.merchantId = merchantId;
        this.category = category;
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public int getRatingsCount() {
        return ratingsCount;
    }

    public void setRatingsCount(int ratingsCount) {
        this.ratingsCount = ratingsCount;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public Promo() {
    }

    public float getRatingAvg() {
        if(ratingsCount==0){
            return 0;
        }
        float avg = 0;
        for (int i = 0; i < ratingsCount; i++) {
            avg+=ratings.get(i).getRating();
        }
        return avg/ratingsCount;
    }

// Constructor, getters, and setters
    public void addRating(Rating rating){
        this.ratings.add(rating);
        ratingsCount++;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
