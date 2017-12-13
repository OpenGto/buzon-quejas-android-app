package com.opengto.buzondequejas.post;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.opengto.buzondequejas.R;
import com.opengto.buzondequejas.layout.HomeFragment;
import com.squareup.picasso.Picasso;

/**
 * Created by joel_barron on 11/12/17.
 */

public class PostViewHolder extends RecyclerView.ViewHolder {

    private ImageView postCardImage;
    private TextView usernameCard;
    private TextView titleCard;
    private TextView timeCard;
    private TextView likeNumberCard;
    private TextView dislikeNumberCard;
    private String id="";

    public PostViewHolder(View itemView) {
        super(itemView);

        //bindear las vistas
        postCardImage = (ImageView) itemView.findViewById(R.id.postCardImage);
        usernameCard = (TextView) itemView.findViewById(R.id.userNameCard);
        titleCard = (TextView) itemView.findViewById(R.id.titleCard);
        timeCard = (TextView) itemView.findViewById(R.id.timeCard);
        likeNumberCard = (TextView) itemView.findViewById(R.id.likeNumberCard);
        dislikeNumberCard = (TextView) itemView.findViewById(R.id.dislikeNumberCard);
    }


    // metodos set
    public void setPostCardImage (Context context, String str) {
        //postCardImage.
        Picasso.with(context).load(str).into(postCardImage); //imagen
    }


    public void setUsernameCard(String str) {
        usernameCard.setText(str);
    }


    public void setTitleCard(String str) {
        titleCard.setText(str);
    }


    public void setTimeCard(String str) {
        timeCard.setText(str);
    }


    public void setLikeNumberCard(String str) {
        likeNumberCard.setText(str);
    }


    public void setDislikeNumberCard(String str) {
        dislikeNumberCard.setText(str);
    }


    public void setOnClick(final Context context){
        postCardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("POST_ID", id);
/*

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    // activity.getWindow().setExitTransition(new Slide());
                    context.startActivity(intent,
                            ActivityOptionsCompat.makeSceneTransitionAnimation(context, view, HomeFragment.getString(R.string.transitionname_picture)).toBundle());

                }else {
                    context.startActivity(intent);
                }
*/

                context.startActivity(intent);


            }
        });
    }


    public void setId(String _id){
        id = _id;
    }


}
