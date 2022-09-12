package com.example.simpleinstagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("4M00Fa4Co6htjEegEgLUhGN5rapN8eQxlQJJ9xmE")
                .clientKey("quiZraaHzbPS053d9F9h2vNiKEK60mTk5DI4o9rL")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
