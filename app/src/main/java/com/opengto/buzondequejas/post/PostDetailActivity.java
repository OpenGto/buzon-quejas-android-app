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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.opengto.buzondequejas.BuzonApplication;
import com.opengto.buzondequejas.R;
import com.squareup.picasso.Picasso;


public class PostDetailActivity extends AppCompatActivity {

    private ImageView postImage;

    private BuzonApplication app;
    private StorageReference storageReference;
    private String TAG = "PostDetailActivity";
    private String photoName = "JPEG_20171210_22-56-32_-2131319337.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        setupActivity();

        showData();
    }


    //metodos

    public void setupActivity(){

        app = (BuzonApplication) getApplicationContext();

        storageReference = app.getStorageReference();

        postImage = (ImageView) findViewById(R.id.imageHeader);

        showToolbar("", true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setEnterTransition(new Fade());
        }
    }


    public void showData(){
        storageReference.child("post_images/" + photoName)
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                showPhoto(uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast("Ocurrió un error al traer la foto");
                e.printStackTrace();
                //FirebaseCrash.report(e);
            }
        });
    }


    public void showPhoto(String url) {
        Picasso.with(this).load(url).into(postImage);
    }


    public void onclickSomething(View view) {

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
