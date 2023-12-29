package com.example.tunisiepromo_miniproject;

public class Profile {
    private String uid;
    private String birthdate;
    private String nom;
    private String role;
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Profile(String uid, String birthdate, String nom, String role, String avatar,String location) {
        this.uid = uid;
        this.birthdate = birthdate;
        this.nom = nom;
        this.role = role;
        this.avatar = avatar;
        this.location = location;
    }

    public Profile() {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    private String avatar;
}
