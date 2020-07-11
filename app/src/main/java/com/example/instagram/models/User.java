package com.example.instagram.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("User")
public class User extends ParseObject {

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "image";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_PROFILE_PICTURE = "profilePicture";


    public String getUsername() {
        return KEY_USERNAME;
    }

    public void setUsername(String username){
        put(KEY_USERNAME, username);
    }

    public String getPassword() {
        return KEY_PASSWORD;
    }

    public void setPassword(String password){
        put(KEY_PASSWORD, password);
    }

    public ParseFile getProfilePicture() {
        return getParseFile(KEY_PROFILE_PICTURE);
    }

    public void setProfilePicture(ParseFile parseFile){
        put(KEY_PROFILE_PICTURE, parseFile);
    }
}