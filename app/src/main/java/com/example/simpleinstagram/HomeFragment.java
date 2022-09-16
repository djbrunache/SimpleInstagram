package com.example.simpleinstagram;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    public static final String TAG="HomeFragment";
    RecyclerView rvRecycle;
    List<Post> postList;
    Context context;
    protected PostAdapter adapter;

    public HomeFragment(){}



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvRecycle=view.findViewById(R.id.rvRecycle);
        postList=new ArrayList<>();

        adapter = new PostAdapter(getContext(),postList);
        rvRecycle.setAdapter(adapter);
        rvRecycle.setLayoutManager(new LinearLayoutManager(context));



        queryPosts();




    }

    private void queryPosts() {

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for(Post post : posts){
                    Log.i(TAG,"Post"+ post.getDescription()+ ",username: " +post.getUser().getUsername());
                }
                postList.addAll(posts);
                adapter.notifyDataSetChanged();

            }
        });

    }
}