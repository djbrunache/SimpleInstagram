package com.example.simpleinstagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.parceler.Parcels;


import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    public static final String TAG = "DetailActivity";
    protected ImageView ivProfileImage, ivPost;
    TextView tvUsername, tvDescription, tvDate, tvLikeDetail;
    ImageButton imgBtnSettings, imgBtnHeart, imgBtnComment, imgBtnSend, imgBtnSave;
    RecyclerView rvComment;
    protected List<String> commentsParse;
    protected List<Comment> comments;
    protected CommentAdapter adapter;
    Context context;
    public static int like;
    public static ArrayList<String> listUserLike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ParseUser currentUser = ParseUser.getCurrentUser();
        ivProfileImage = findViewById(R.id.imageProfile);
        ivPost = findViewById(R.id.ivMedia);
        tvUsername = findViewById(R.id.username);
        tvDate = findViewById(R.id.tvDate);
        tvLikeDetail = findViewById(R.id.tvLike);
        tvDescription = findViewById(R.id.tvDescription);
        imgBtnSettings = findViewById(R.id.btnSettings);
        imgBtnHeart = findViewById(R.id.btnHeart);
        imgBtnComment = findViewById(R.id.btnComment);
        imgBtnSend = findViewById(R.id.btnSend);
        imgBtnSave = findViewById(R.id.btnSave);
        rvComment = findViewById(R.id.rvComment);

        Toolbar toolbar = findViewById(R.id.toolbar_detail);
        ImageButton btnBack = findViewById(R.id.iconBack);
        setSupportActionBar(toolbar);


        Post post = Parcels.unwrap(getIntent().getParcelableExtra(MainActivity.POST));
        post.getListComment();
        try {
            commentsParse = Comment.fromJsonArray(post.getListComment());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // get list user who liked this post
        try {
            listUserLike = Post.fromJsonArray(post.getListLike());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // set color for heart
        try{
            if (listUserLike.contains(currentUser.getObjectId())) {
                Drawable drawable = ContextCompat.getDrawable(DetailActivity.this, R.drawable.heart_change);
                imgBtnHeart.setImageDrawable(drawable);
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        comments = new ArrayList<>();

        //Create the adapter
        adapter = new CommentAdapter(context, comments);

        //Set the adapter on the recyclerView
        rvComment.setAdapter(adapter);

        //Set The layout manager on the recyclerView
        rvComment.setLayoutManager(new LinearLayoutManager(context));

        tvUsername.setText(post.getUser().getUsername());
        tvDescription.setText(post.getDescription());
        tvLikeDetail.setText(String.valueOf(post.getNumberLike()) + " likes");
        tvDate.setText(TimeFormatter.getTimeStamp(post.getCreatedAt().toString()));
        Glide.with(DetailActivity.this).load(post.getUser().getParseFile(User.KEY_PROFILE_IMAGE).getUrl()).transform(new RoundedCorners(100)).into(ivProfileImage);

        ParseFile image = post.getImage();
        if(image != null){
            Glide.with(DetailActivity.this).load(image.getUrl()).transform(new RoundedCorners(30)).into(ivPost);
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DetailActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(i, 0);
            }
        });

        imgBtnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DetailActivity.this, CommentActivity.class);
                i.putExtra("post", Parcels.wrap(post));
                startActivity(i);
            }
        });

        imgBtnHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                like = post.getNumberLike();
                int index;

                if (!listUserLike.contains(currentUser.getObjectId())){
                    Drawable drawable = ContextCompat.getDrawable(DetailActivity.this, R.drawable.heart_change);
                    imgBtnHeart.setImageDrawable(drawable);
                    like++;
                    index = -1;

                }else {
                    Drawable drawable = ContextCompat.getDrawable(DetailActivity.this, R.drawable.ic_heart);
                    imgBtnHeart.setImageDrawable(drawable);
                    like--;
                    index = listUserLike.indexOf(currentUser.getObjectId());
                }

                tvLikeDetail.setText(String.valueOf(like) + " likes");
                saveLike(post, like, index, currentUser);
            }
        });

        queryPost();
    }

    protected void queryPost() {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.include(Comment.KEY_USER);
        query.whereContainedIn("objectId", commentsParse);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> commentList, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Issue with getting Posts", e);
                    Toast.makeText(context, "Issue with getting Posts", Toast.LENGTH_SHORT).show();
                    return;
                }

                comments.addAll(commentList);
                adapter.notifyDataSetChanged();

            }
        });
    }


    // method to save a like
    private void saveLike(Post post, int like, int index, ParseUser currentUser) {
        post.setNumberLike(like);

        if (index == -1){
            post.setListLike(currentUser);
            listUserLike.add(currentUser.getObjectId());
        }else {
            listUserLike.remove(index);
            post.removeItemListLike(listUserLike);
        }

        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(context, "Error while saving", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, listUserLike.toString());
            }
        });
    }
}