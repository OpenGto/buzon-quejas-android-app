package com.opengto.buzondequejas.post;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.opengto.buzondequejas.model.Post;
import com.squareup.picasso.Picasso;
import com.opengto.buzondequejas.R;

import java.util.ArrayList;
import java.util.Date;

import android.widget.CalendarView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.opengto.buzondequejas.post.PostViewHolder;

/**
 * Created by joel_barron on 08/12/17.
 */

public class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {

    private static final String TAG = "PostAdapter";
    ArrayList<Post> posts;
    Context context;
    private boolean isGrid = false;


    public PostAdapter(Context context) {
        this.context = context;
        this.posts = new ArrayList<>(); // para evitar null pointer exception
    }

    public PostAdapter(Context context, boolean isGrid) {
        this.isGrid = true;
        this.context = context;
        this.posts = new ArrayList<>(); // para evitar null pointer exception
    }


    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (!isGrid){
            View card = LayoutInflater.from(context)
                    .inflate(R.layout.cardview_post, parent, false);
            return new PostViewHolder(card);
        }else {
            View card = LayoutInflater.from(context)
                    .inflate(R.layout.cardview_post_small, parent, false);
            return new PostViewHolder(card);
        }

    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {

        Post currentPost = posts.get(position);

        holder.setId(currentPost.getId());
        holder.setTitleCard(currentPost.getTitle());
        holder.setUsernameCard(currentPost.getUser().getDisplayName());
        holder.setLikeNumberCard(Integer.toString(currentPost.getLikes()));
        holder.setDislikeNumberCard(Integer.toString(currentPost.getDislikes()));
        holder.setPostCardImage(context, currentPost.getPictureUrl());


        String timestampDiff = getTimestampDifference(currentPost.getCreatedAt()); //se llama al metodo para que nos regrese el formateado

        if(!timestampDiff.equals("0")){
            holder.setTimeCard(" Hace " + timestampDiff + " Dias");
        }else{
            holder.setTimeCard("Hoy");
        }


        // setear el onclick
        holder.setOnClick(context);


    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    //metodos
    public void addAll(@NonNull ArrayList<Post> posts){

        if(posts == null){
            throw new NullPointerException("Los post no puden ser nulos");
        }

        this.posts.clear();
        this.posts.addAll(posts);
        //notifyItemRangeInserted(getItemCount() - 1, posts.size());
        notifyDataSetChanged();
    }


    // fecha

    /**
     * Returns a string representing the number of days ago the post was made
     * @return
     */
    private String getTimestampDifference(String postDate){

        //Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));//google 'android list of timezones'
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = postDate;
        try{
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24 )));
        }catch (ParseException e){
            Log.e(TAG, "getTimestampDifference: ParseException: " + e.getMessage() );
            difference = "0";
        }
        return difference;
    }


}
