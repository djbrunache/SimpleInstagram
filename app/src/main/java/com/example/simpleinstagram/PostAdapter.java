package com.example.simpleinstagram;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    public static Context context;
    List<Post> postlist;

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

        holder.bind(post);

    }

    @Override
    public int getItemCount() {
        return postlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName2;
        EditText description;
        ImageView ivImageP1;
        TextView ic_more;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName2=itemView.findViewById(R.id.tvName2);
            description=itemView.findViewById(R.id.description);
            ivImageP1=itemView.findViewById(R.id.ivImageP1);
            ic_more=itemView.findViewById(R.id.ic_more);
        }

        public void bind(Post post) {
            tvName2.setText(post.getUser().getUsername());
            description.setText(post.getDescription());
            ParseFile image=post.getImage();


            if(image!=null) {
                Glide.with(context).
                        load(post.getImage().getUrl()).into(ivImageP1);
            }




        }
    }
}
