package com.example.instagram.feed;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.instagram.databinding.ActivityDetailsBinding;
import com.example.instagram.models.Post;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.Date;


public class PostDetailsActivity extends AppCompatActivity {

    public static final String TAG = "PostDetailsActivity";

    //the post to display
    Post post;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this, "Post Details", Toast.LENGTH_SHORT).show();
        ActivityDetailsBinding detailsBinding = ActivityDetailsBinding.inflate(getLayoutInflater());

        //layout of activity is stored in a special property called root
        View detailsView = detailsBinding.getRoot();
        setContentView(detailsView);

        //the view objects
        TextView tvDetailsUsername = detailsBinding.tvDetailsUsername;
        ImageView ivDetailsImage = detailsBinding.ivDetailsImage;
        TextView tvDetailsDescription = detailsBinding.tvDetailsDescription;
        TextView tvDetailsTimeStamp = detailsBinding.tvDetailsTimeStamp;
        ImageView ivDetailsProfileIcon = detailsBinding.ivDetailsProfileIcon;

        //unwrap the tweet passed in via intent using its simple name key
        post = (Post) Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));
        Log.d(TAG, String.format("Showing post content '%s'", post.getDescription()));

        //set objects
        tvDetailsUsername.setText(post.getUser().getUsername());
        tvDetailsDescription.setText(post.getDescription());
        Date absoluteCreatedAt = post.getCreatedAt();
        String timeAgo =  String.valueOf(DateUtils.getRelativeTimeSpanString(absoluteCreatedAt.getTime(), System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_TIME));
        tvDetailsTimeStamp.setText(timeAgo);
        ParseFile image = post.getImage();
        ParseFile detailsProfilePic = post.getUser().getParseFile("profilePicture");
        if (image != null) {
            Glide.with(this).load(post.getImage().getUrl()).into(ivDetailsImage);
            Glide.with(this).load(detailsProfilePic.getUrl()).transform(new CircleCrop()).into(ivDetailsProfileIcon);
        }
    }
}