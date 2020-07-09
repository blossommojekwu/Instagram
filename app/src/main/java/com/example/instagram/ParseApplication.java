package com.example.instagram;

import android.app.Application;
import android.os.Parcel;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //Register parse models
        ParseObject.registerSubclass(Post.class);

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("blossom-parstagram") // should correspond to APP_ID env variable
                .clientKey(String.valueOf(R.string.master_key))  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://blossom-parstagram.herokuapp.com/parse").build());
    }
}
