package com.opengto.buzondequejas.post;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.opengto.buzondequejas.BuzonApplication;
import com.squareup.picasso.Picasso;
import com.opengto.buzondequejas.R;

import java.io.ByteArrayOutputStream;

public class NewPostActivity extends AppCompatActivity {

    // variables
    private ImageView imgPhoto;
    private TextInputEditText edtTitle, edtDescription;
    private ProgressBar progressBar;
    private Button btnCreatePost;
    private String photoPath;
    private BuzonApplication app;
    private StorageReference storageReference;
    private String TAG = "NewPostActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        setupActivity();
    }




    // metodos
    public void setupActivity(){

        app = (BuzonApplication) getApplicationContext();

        storageReference = app.getStorageReference();

        imgPhoto = (ImageView) findViewById(R.id.imgPhoto);

        btnCreatePost = (Button) findViewById(R.id.btnCreatePost);

        edtTitle = (TextInputEditText) findViewById(R.id.edtTitle);
        edtDescription = (TextInputEditText) findViewById(R.id.edtDescription);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        hideProgressBar();

        //obtener el extra
        if (getIntent().getExtras() != null){
            photoPath = getIntent().getExtras().getString("PHOTO_PATH_TEMP");
            showPhoto();
        }
    }


    public void showPhoto() {
        Picasso.with(this).load(photoPath).into(imgPhoto);
    }


    public void onclickCreatePost(View view) {

        disableInputs();
        showProgressBar();

        try{

            imgPhoto.setDrawingCacheEnabled(true);
            imgPhoto.buildDrawingCache();

            Bitmap bitmap = imgPhoto.getDrawingCache();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream); // comprimir
            byte [] photoByte = byteArrayOutputStream.toByteArray();

            String photoName = photoPath.substring(photoPath.lastIndexOf("/")+1, photoPath.length());

            // llamar al metodo que la sube a firebase
            performUploadPhoto(photoName, photoByte);


        } catch (Exception e) {
            enableInputs();
            hideProgressBar();
            showToast("Ocurrió un error: " + e.getMessage().toString());
        }

    }


    public void performUploadPhoto(String photoName, byte [] photoByte) {

        StorageReference photoReference = storageReference.child("post_images/" + photoName);

        UploadTask uploadTask = photoReference.putBytes(photoByte);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                enableInputs();
                hideProgressBar();
                Log.w(TAG, "Error al subir la foto " + e.toString());
                e.printStackTrace();
                showToast("Ocurrió un error: " + e.getMessage().toString());
                //FirebaseCrash.report(e);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri uriPhoto = taskSnapshot.getDownloadUrl();
                String photoURL = uriPhoto.toString();
                Log.w(TAG, "URL Photo > " + photoURL);
                finish();
            }
        });
    }


    // metodos view

    public void enableInputs() {
        edtTitle.setEnabled(true);
        edtDescription.setEnabled(true);
        btnCreatePost.setClickable(true);
    }


    public void disableInputs() {
        edtTitle.setEnabled(false);
        edtDescription.setEnabled(false);
        btnCreatePost.setClickable(false);
    }


    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }


    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }


    public void showToast(String str) {
        Toast.makeText(NewPostActivity.this, str, Toast.LENGTH_SHORT).show();
    }

}
