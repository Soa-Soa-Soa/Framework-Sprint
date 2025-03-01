package com.framework.model;

import com.framework.annotation.RequestField;

public class User {
    @RequestField("firstName")
    private String prenom;
    
    @RequestField("lastName")
    private String nom;
    
    @RequestField(required = false)
    private int age;
    
    @RequestField(required = false)
    private String email;
    
    // Getters et Setters
    public String getPrenom() {
        return prenom;
    }
    
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    @Override
    public String toString() {
        return "User{" +
               "prenom='" + prenom + '\'' +
               ", nom='" + nom + '\'' +
               ", age=" + age +
               ", email='" + email + '\'' +
               '}';
    }
}
