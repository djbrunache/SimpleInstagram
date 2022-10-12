package com.example.simpleinstagram;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    public static final String TAG = "PostAdapter";
    public static Context context;
    List<Post> postlist;
    private static ArrayList<String> listUserLikes;

    public PostAdapter(Context context, List<Post> postlist) {
        this.context=context;
        this.postlist = postlist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_post,parent,false);
       return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post=postlist.get(position);

        try {
            holder.bind(post);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return postlist.size();
    }

    public void clear(){
        postlist.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> postList){
        postlist.addAll(postList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView ivProfileImage, ivImageP1;
        TextView tvUsername, description, tvDate, tvLike;
        ImageButton btnSettings, btnHeart, btnComment, btnSend, btnSave;
        RelativeLayout container1, container;
        int like;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername=itemView.findViewById(R.id.tvUsername);
            description=itemView.findViewById(R.id.description);
            ivImageP1=itemView.findViewById(R.id.ivImageP1);
            tvLike = itemView.findViewById(R.id.tvLike);
            tvDate = itemView.findViewById(R.id.tvDate);
            description = itemView.findViewById(R.id.description);
            btnSettings = itemView.findViewById(R.id.btnSettings);
            btnHeart = itemView.findViewById(R.id.btnHeart);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnSend = itemView.findViewById(R.id.btnSend);
            btnSave = itemView.findViewById(R.id.btnSave);
            container1 = itemView.findViewById(R.id.container1);
            container = itemView.findViewById(R.id.container);
        }

        public void bind(Post post) throws JSONException {
            ParseUser currentUser = ParseUser.getCurrentUser();
            listUserLikes = Post.fromJsonArray(post.getListLike());

            Glide.with(context).load(post.getUser().getParseFile(User.KEY_PROFILE_IMAGE).getUrl()).transform(new RoundedCorners(100)).into(ivProfileImage);
            tvUsername.setText(post.getUser().getUsername());
            description.setText(post.getDescription());
            tvDate.setText(TimeFormatter.getTimeStamp(post.getCreatedAt().toString()));
            tvLike.setText(String.valueOf(post.getNumberLike()) + " likes");

            ParseFile image = post.getImage();
            if(image != null){
                Glide.with(context).load(image.getUrl()).into(ivImageP1);
            }

            try{
                if (listUserLikes.contains(currentUser.getObjectId())) {
                    Drawable drawable = ContextCompat.getDrawable(context, R.drawable.heart_change);
                    btnHeart.setImageDrawable(drawable);
                }else {
                    Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_heart);
                    btnHeart.setImageDrawable(drawable);
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }

            btnHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    like = post.getNumberLike();
                    int index;

                    if (!listUserLikes.contains(currentUser.getObjectId())){
                        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.heart_change);
                        btnHeart.setImageDrawable(drawable);
                        like++;
                        index = -1;

                    }else {
                        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_heart);
                        btnHeart.setImageDrawable(drawable);
                        like--;
                        index = listUserLikes.indexOf(currentUser.getObjectId());
                    }

                    tvLike.setText(String.valueOf(like) + " likes");
                    saveLike(post, like, index, currentUser);
                }
            });


            container1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, DetailActivity.class);
                    i.putExtra("post", Parcels.wrap(post));
                    context.startActivity(i);
                }
            });

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();

                    // set parameters
                    ProfileFragment profileFragment = ProfileFragment.newInstance("Some Title");
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(MainActivity.POST, Parcels.wrap(post));
                    profileFragment.setArguments(bundle);

                    fragmentManager.beginTransaction().replace(R.id.frame, profileFragment).commit();
                }
            });

            btnComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, CommentActivity.class);
                    i.putExtra("post", Parcels.wrap(post));
                    context.startActivity(i);
                }
            });
        }

        private void saveLike(Post post, int like, int index, ParseUser currentUser) {
            post.setNumberLike(like);

            if (index == -1){
                post.setListLike(currentUser);
                listUserLikes.add(currentUser.getObjectId());
            }else {
                listUserLikes.remove(index);
                post.removeItemListLike(listUserLikes);
            }

            post.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null){
                        Log.e(TAG, "Error while saving", e);
                        Toast.makeText(context, "Error while saving", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Log.i(TAG, listUserLikes.toString());

                }
            });
        }


    }
}
