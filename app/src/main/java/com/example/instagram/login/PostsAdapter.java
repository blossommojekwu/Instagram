package com.example.instagram.login;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.instagram.feed.PostDetailsActivity;
import com.example.instagram.R;
import com.example.instagram.models.Post;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.Date;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    Context mContext;
    List<Post> mPosts;

    public PostsAdapter(Context context, List<Post> posts){
        this.mContext = context;
        this.mPosts = posts;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = mPosts.get(position);
        //take viewholder passed in and pass data of the post into that viewholder
        holder.bind(post);

    }
    // Clean all elements of the recycler
    public void clear() {
        mPosts.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvUsername;
        ImageView ivImage;
        TextView tvDescription;
        ImageView ivProfileIcon;
        TextView tvTimeStamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //populate view
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivProfileIcon = itemView.findViewById(R.id.ivProfileIcon);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
            itemView.setOnClickListener(this);
        }

        //when the user clicks on a row, show Details Activity for the selected post
        @Override
        public void onClick(View view) {
            //get post position
            int position = getAdapterPosition();
            //position must be valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION){
                //get post at position
                Post post = mPosts.get(position);
                //create intent for new activity
                Intent intent = new Intent(mContext, PostDetailsActivity.class);
                //serialize tweet using parceler w/ short name as key
                intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
                //show the activity
                mContext.startActivity((intent));
            }
        }

        public void bind(Post post) {
            //Bind the post data to the view elements
            tvUsername.setText((post.getUser().getUsername()));
            tvDescription.setText(post.getDescription());
            Date absoluteCreatedAt = post.getCreatedAt();
            String timeAgo =  String.valueOf(DateUtils.getRelativeTimeSpanString(absoluteCreatedAt.getTime(), System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_TIME));
            tvTimeStamp.setText(timeAgo);
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(mContext).load(image.getUrl()).into(ivImage);
                Glide.with(mContext).load(post.getUser().getParseFile("profilePicture")).transform(new CircleCrop()).into(ivProfileIcon);
            }
        }
    }
}