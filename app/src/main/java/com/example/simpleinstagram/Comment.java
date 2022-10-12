package com.example.simpleinstagram;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel(analyze = Comment.class)
@ParseClassName("Comment")
public class Comment extends ParseObject {
    public static final String KEY_COMMENT = "comment";
    public static final String KEY_USER = "user";

    public Comment() {}

    public String getComment(){return getString(KEY_COMMENT);}
    public void setComment(String comment){put(KEY_COMMENT, comment);}

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }
    public void setUser(ParseUser parseUser){
        put(KEY_USER, parseUser);
    }

    public static List<String> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<String> commentList = new ArrayList<String>();

        try {
            for (int i = 0; i < jsonArray.length(); i++){
                commentList.add(jsonArray.getJSONObject(i).getString("objectId"));
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        return commentList;
    }
}