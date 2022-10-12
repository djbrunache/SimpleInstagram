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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.parse.ParseFile;


import org.json.JSONException;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    public static final String TAG = "CommentAdapter";
    public static Context context;
    List<Comment> commentlist;

    public CommentAdapter(Context context,  List<Comment> commentlist) {
        this.context=context;
        this.commentlist = commentlist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_post,parent,false);
       return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = commentlist.get(position);

        holder.bind(comment, holder);

    }

    @Override
    public int getItemCount() {
        return commentlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvComment, tvUsername;
        ImageView ivImageP1;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvComment=itemView.findViewById(R.id.tvName2);
            tvUsername=itemView.findViewById(R.id.description);
            ivImageP1=itemView.findViewById(R.id.ivImageP1);
        }



        public void bind(Comment comment, ViewHolder holder) {
            tvUsername.setText(comment.getUser().getUsername());
            tvComment.setText(comment.getComment());
            Glide.with(holder.itemView.getContext()).load(comment.getUser().getParseFile(User.KEY_PROFILE_IMAGE).getUrl()).transform(new RoundedCorners(100)).into(ivImageP1);
        }
    }
}
