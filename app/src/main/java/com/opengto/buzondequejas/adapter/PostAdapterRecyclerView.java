package com.opengto.buzondequejas.adapter;



import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.opengto.buzondequejas.model.Post;
import com.opengto.buzondequejas.post.PostDetailActivity;
import com.squareup.picasso.Picasso;
import com.opengto.buzondequejas.R;

import java.util.ArrayList;


/**
 * Created by joel_barron on 08/12/17.
 */

public class PostAdapterRecyclerView extends RecyclerView.Adapter<PostAdapterRecyclerView.PostViewHolder> {

    private ArrayList<Post> posts;
    private int resource;
    private Activity activity;

    // constructor de la clase
    public PostAdapterRecyclerView(ArrayList<Post> posts, int resource, Activity activity) {
        this.posts = posts;
        this.resource = resource;
        this.activity = activity;
    }


    // asociar los datos
    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        return new PostViewHolder(view);
    }


    // ir recorriendo la lista e ir generando cada card
    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.titleCard.setText(post.getTitle()); // title
        holder.usernameCard.setText(post.getUsername()); // username
        holder.timeCard.setText(post.getTime()); // time
        holder.typeCard.setText(post.getType()); // type
        holder.statusCard.setText(post.getStatus()); // status
        holder.likeNumberCard.setText(Integer.toString(post.getLikes())); //likes
        holder.dislikeNumberCard.setText(Integer.toString(post.getDislikes())); //dislikes
        Picasso.with(activity).load(post.getPicture()).into(holder.postCardImage); //imagen


        holder.postCardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, PostDetailActivity.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                   // activity.getWindow().setExitTransition(new Slide());
                    activity.startActivity(intent,
                            ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, activity.getString(R.string.transitionname_picture)).toBundle());

                }else {
                    activity.startActivity(intent);
                }


            }
        });



    }


    // numero de posts en el array
    @Override
    public int getItemCount() {
        return posts.size();
    }


    // clase
    public class PostViewHolder extends RecyclerView.ViewHolder{

        private ImageView postCardImage;
        private TextView titleCard;
        private TextView usernameCard;
        private TextView timeCard;
        private TextView typeCard;
        private TextView statusCard;
        private TextView likeNumberCard;
        private TextView dislikeNumberCard;

        // constructor
        public PostViewHolder(View itemView) {
            super(itemView);

            postCardImage = (ImageView) itemView.findViewById(R.id.postCardImage);
            titleCard = (TextView) itemView.findViewById(R.id.titleCard);
            usernameCard    = (TextView) itemView.findViewById(R.id.userNameCard);
            timeCard        = (TextView) itemView.findViewById(R.id.timeCard);
            typeCard        = (TextView) itemView.findViewById(R.id.typeCard);
            statusCard        = (TextView) itemView.findViewById(R.id.statusCard);
            likeNumberCard  = (TextView) itemView.findViewById(R.id.likeNumberCard);
            dislikeNumberCard  = (TextView) itemView.findViewById(R.id.dislikeNumberCard);

        }

    }

}
