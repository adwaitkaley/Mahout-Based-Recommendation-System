package main;

import org.springframework.data.annotation.Id;

/**
 * Created by Adwait on 11/15/2015.
 */
public class User {

    @Id
    private String id;

    private String userId;
    private String userName;
    private String userDob ;
    private String userEmail ;
    private String userGender ;
    private String userStreet;
    private String userCity;
    private String userState;
    private String userCountry;
    private String userZip;


    public User() {
    }

    public User(String userId, String userName, String userDob, String userEmail, String userGender, String userStreet, String userCity, String userState, String userCountry, String userZip) {
        this.userId = userId;
        this.userName = userName;
        this.userDob = userDob;
        this.userEmail = userEmail;
        this.userGender = userGender;
        this.userStreet = userStreet;
        this.userCity = userCity;
        this.userState = userState;
        this.userCountry = userCountry;
        this.userZip = userZip;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDob() {
        return userDob;
    }

    public void setUserDob(String userDob) {
        this.userDob = userDob;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserStreet() {
        return userStreet;
    }

    public void setUserStreet(String userStreet) {
        this.userStreet = userStreet;
    }

    public String getUserCity() {
        return userCity;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
    }

    public String getUserState() {
        return userState;
    }

    public void setUserState(String userState) {
        this.userState = userState;
    }

    public String getUserCountry() {
        return userCountry;
    }

    public void setUserCountry(String userCountry) {
        this.userCountry = userCountry;
    }

    public String getUserZip() {
        return userZip;
    }

    public void setUserZip(String userZip) {
        this.userZip = userZip;
    }
}
