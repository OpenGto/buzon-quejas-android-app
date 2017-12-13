package com.opengto.buzondequejas.post;

import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.transition.Fade;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.opengto.buzondequejas.BuzonApplication;
import com.opengto.buzondequejas.R;
import com.opengto.buzondequejas.model.Post;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;


public class PostDetailActivity extends AppCompatActivity {

    private ImageView postImage;
    private TextView userNameDetail, likeNumberDetail, disLikeNumberDetail, titleImage, textContentImageDetail;

    private BuzonApplication app;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private String TAG = "PostDetailActivity";
    private String POST_ID = "";
    private final String POST_REF ="Posts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        setupFirebase();

        setupActivity();

    }


    //metodos

    public void setupActivity(){

        postImage = (ImageView) findViewById(R.id.imageHeader);
        userNameDetail = (TextView) findViewById(R.id.userNameDetail);
        likeNumberDetail = (TextView) findViewById(R.id.likeNumberDetail);
        disLikeNumberDetail = (TextView) findViewById(R.id.dislikeDetail);
        titleImage = (TextView) findViewById(R.id.titleImage);
        textContentImageDetail = (TextView) findViewById(R.id.textContentImageDetail);


        showToolbar("", true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setEnterTransition(new Fade());
        }

        //obtener el extra
        if (getIntent().getExtras() != null){
            POST_ID = getIntent().getExtras().getString("POST_ID");

            //llamar
            performGetData();
        }
    }


    public void setupFirebase(){
        //firebase
        app = (BuzonApplication) getApplicationContext();

        storageReference = app.getStorageReference();
        databaseReference = app.getDatabaseReference();

    }


    public void showFirebaseImage(final String _photoName){
        storageReference.child("post_images/" + _photoName)
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(PostDetailActivity.this).load(uri.toString()).into(postImage); // mostrar la foto
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast("Ocurrió un error al traer la foto: "+e.getMessage().toString());
                e.printStackTrace();
                //FirebaseCrash.report(e);
            }
        });
    }



    public void performGetData() {
        databaseReference.child(POST_REF).child(POST_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()){

                    Post postFirebase = dataSnapshot.getValue(Post.class);

                    try{

                        Picasso.with(PostDetailActivity.this).load(postFirebase.getPictureUrl()).into(postImage); // mostrar la foto
                        userNameDetail.setText(postFirebase.getUser().getDisplayName());
                        likeNumberDetail.setText(String.valueOf(postFirebase.getLikes()));
                        disLikeNumberDetail.setText(String.valueOf(postFirebase.getDislikes()));
                        titleImage.setText(postFirebase.getTitle());
                        textContentImageDetail.setText(postFirebase.getDescription());

                    } catch (Exception e){
                        //showToast("Ocurrió un error: " + e.getMessage().toString());
                        Log.w(TAG, "error "+e.getMessage().toString());
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //showToast("Ocurrió un error: "+databaseError.getMessage());
                Log.w(TAG, "error "+databaseError.getMessage());
            }
        });
    }


    public void performSomething() {

    }



    // metodos view


    public void showToolbar(String tittle, boolean upButton){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(tittle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);

    }


    public void showToast(String str) {
        Toast.makeText(PostDetailActivity.this, str, Toast.LENGTH_SHORT).show();
    }

}
