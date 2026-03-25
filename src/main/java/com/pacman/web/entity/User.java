package com.pacman.web.entity;

import java.sql.Date;

public class User {

    private Long id;
    private String email;
    private String password;
    private String username;
    private Date registrationDate;
    
    // couleur du pacman, à rempalcer par des skins
    private String color = "yellow";
    
    // determine le rank du joueur
    private int victories;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getColor() {
    	return color;
    }

    public void setColor(String color) {
    	this.color = color;
    }
    
    public void incrVictories() {
    	victories++;
    }
    
    public int getVictories() {
    	return victories;
    }
    
    public void setVictories(int victories) {
    	this.victories = victories;
    }
    
    public Rank getRank() {
		if (victories > 15) return Rank.GOLD;
		if (victories >= 1) return Rank.SILVER; // volontrairement bas pour la démonstration
		return Rank.BRONZE;
    }
    
    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }
}
