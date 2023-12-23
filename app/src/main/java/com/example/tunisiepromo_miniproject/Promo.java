package com.example.tunisiepromo_miniproject;

public class Promo {
    private String productId;
    private String name;
    private double price;
    private int discount;
    private String imageUrl;
    private float rating;
    private String merchant;
    private String merchantId;
    private String category;

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

    public Promo(String category,String productId,String merchantId, String name, double price, int discount, String imageUrl, float rating, String merchant) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.discount = discount;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.merchant = merchant;
        this.merchantId = merchantId;
        this.category = category;
    }

    public Promo() {
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
// Constructor, getters, and setters


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
