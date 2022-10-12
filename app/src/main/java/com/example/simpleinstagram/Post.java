package com.example.simpleinstagram;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel(analyze = Post.class)
@ParseClassName("Post")
public class Post extends ParseObject {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_COMMENT= "list_comment";
    public static final String KEY_CREATED_KEY = "createdAt";
    public static final String KEY_NUMBER_LIKE= "number_like";
    public static final String KEY_LIST_LIKE= "list_like";

    public Post() {}

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public int getNumberLike(){return getInt(KEY_NUMBER_LIKE);}

    public void setNumberLike(int nbr){put(KEY_NUMBER_LIKE, nbr);}

    public JSONArray getListLike(){return getJSONArray(KEY_LIST_LIKE);}
    public void setListLike(ParseUser userLike){add(KEY_LIST_LIKE, userLike);}
    public void removeItemListLike(List<String> listUserLike){
        remove(KEY_LIST_LIKE);
        put(KEY_LIST_LIKE, listUserLike);
    }

    public JSONArray getListComment(){
        return getJSONArray(KEY_COMMENT);
    }
    public void setListComment(ParseObject comment){add(KEY_COMMENT, comment);}

    public static ArrayList<String> fromJsonArray(JSONArray jsonArray) throws JSONException {
        ArrayList<String> listUserLike = new ArrayList<String>();
        try {
            for (int i = 0; i < jsonArray.length(); i++){
                listUserLike.add(jsonArray.getJSONObject(i).getString("objectId"));
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return listUserLike;
    }
}
