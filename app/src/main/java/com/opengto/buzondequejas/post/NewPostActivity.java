package com.opengto.buzondequejas.post;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.opengto.buzondequejas.BuzonApplication;
import com.opengto.buzondequejas.model.Post;
import com.opengto.buzondequejas.model.User;
import com.squareup.picasso.Picasso;
import com.opengto.buzondequejas.R;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NewPostActivity extends AppCompatActivity {

    // variables
    private ImageView imgPhoto;
    private TextInputEditText edtTitle, edtDescription;
    private ProgressBar progressBar;
    private Button btnCreatePost;
    private String photoPath;

    private BuzonApplication app;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private final String POST_REF ="Posts";

    private final String URL_DEFAULT = "https://sumarketing.com.mx/media/files/no-profile.jpeg";
    private String TAG = "NewPostActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        setupFirebase();

        setupActivity();
    }




    // metodos
    public void setupActivity(){

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


    public void setupFirebase(){
        //firebase
        app = (BuzonApplication) getApplicationContext();

        storageReference = app.getStorageReference();
        databaseReference = app.getDatabaseReference();

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


                //guardar el post en la base de datos
                CreateNewPost(photoURL);

            }
        });
    }


    public void CreateNewPost(String url){

        // obtener los datos y validarlos
        Post post = getData();

        //
        if (post != null){

            //agregar la url
            post.setPictureUrl(url);
            performCreateNewPost(post);
        }
        else{
            // si no se pudo validar
            enableInputs();
            hideProgressBar();
        }

    }


    public void performCreateNewPost(Post post){

        //agregar el id
        post.setId(databaseReference.push().getKey());

        //subir a database
        try{
            databaseReference.child(POST_REF).child(post.getId()).setValue(post);
            finish();
        } catch (Exception e){
            showToast("Error: " + e.getMessage().toString());
        }
    }


    public Post getData() {

        try{

            String tags = "Queja";

            String userId = BuzonApplication.getPref("userId", getApplicationContext());
            String displayName = BuzonApplication.getPref("displayName", getApplicationContext()); // obtener de las shared preferences
            String profilePhotoUrl = BuzonApplication.getPref("profilePhotoUrl", getApplicationContext());


            Log.w(TAG, "userId > " + userId);
            Log.w(TAG, "displayName > " + displayName);
            Log.w(TAG, "profilePhotoUrl > " + profilePhotoUrl);

            User user = new User(userId, displayName, profilePhotoUrl);


            // obtener datos y meterlos en un objeto
            Post post = new Post(
                    null,
                    null,
                    edtTitle.getText().toString(),
                    edtDescription.getText().toString(),
                    getTimestamp(),
                    "Queja",
                    "Nuevo",
                    user,
                    tags,
                    0,
                    0
            );


            if (validateData(post)){
                return post;
            }
            else{
                return null;
            }


        }catch (Exception ex){
            String error = "Ocurrió un error: " + ex.getMessage().toString();
            showToast(error);
            return null;
        }

    }


    public boolean validateData(Post post) {


        if (post.getTitle().trim().equals("")){
            showToast("El Titulo es obligatorio");
            return false;
        }

        if (post.getDescription().trim().equals("")){
            showToast("La descripción es obligatoria");
            return false;
        }

        if (post.getUser().getId().trim().equals("")){
            showToast("No se pudo obtener un usuario");
            return false;
        }


        return true;
    }


    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", new Locale("es", "MX"));
        sdf.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
        return sdf.format(new Date());
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
