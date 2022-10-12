package com.example.simpleinstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final String POST = "post";
    BottomNavigationView bottomNavigation;
    final FragmentManager fragmentManager = getSupportFragmentManager();
    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;
    RecyclerView rvInsta;
    List<Post> posts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final FragmentManager fragmentManager = getSupportFragmentManager();

        // define your fragments here
        final Fragment fragmentHome = new HomeFragment();
        final Fragment fragmentCompose = new MoreFragment();
        final Fragment fragmentAccount= new ProfileFragment();

        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "fetching new data!!");
//                populateHometimeline();
            }
        });

        rvInsta = findViewById(R.id.rvInsta);
        posts = new ArrayList<>();
        PostAdapter adapter = new PostAdapter(this, posts);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // Recyclerview setup : layout manager and the adapter
        rvInsta.setLayoutManager(layoutManager);
        rvInsta.setAdapter(adapter);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore: " + page);
//                loadMoreData();
            }
        };
        rvInsta.addOnScrollListener(scrollListener);


        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.it_home:
                        fragment = fragmentHome;
                        break;

                    case R.id.it_plus:
                        fragment = fragmentCompose;
                        break;

                    case R.id.it_account:
                    default:
                        fragment = fragmentAccount;
                        break;

                }
                fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();
                return true;
            }
        });

        bottomNavigation.setSelectedItemId(R.id.it_home);

    }
}