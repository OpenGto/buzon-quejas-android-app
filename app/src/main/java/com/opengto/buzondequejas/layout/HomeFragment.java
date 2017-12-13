package com.opengto.buzondequejas.layout;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.opengto.buzondequejas.BuzonApplication;
import com.opengto.buzondequejas.R;
import com.opengto.buzondequejas.model.Post;
import com.opengto.buzondequejas.model.User;
import com.opengto.buzondequejas.post.NewPostActivity;
import com.opengto.buzondequejas.post.PostAdapter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.facebook.FacebookSdk.getApplicationContext;


public class HomeFragment extends Fragment {

    // variables
    private BuzonApplication app;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private final String POST_REF ="Posts";
    public static final String TAG = "HomeFragment";

    //views
    private PostAdapter adapter;
    private FloatingActionButton fabCamera;
    private RecyclerView mPostList;
    private static final int REQUEST_CAMERA = 1;
    private String photoPathTemp = "";


    // constructor del fragment
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        setupFirebase();

        setupActivity(view);

        setupPostList();

        //setFixturesContent();

        performGetPosts();

        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onclickTakePicture();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // evento de el resultado de la actividad
        // para saber si se tomo la foto correctamente

        if(requestCode == REQUEST_CAMERA && resultCode == getActivity().RESULT_OK) {
            Log.d("Home fragment", "CAMERA OK !! :)");
            Intent i = new Intent(getActivity(), NewPostActivity.class);
            i.putExtra("PHOTO_PATH_TEMP", photoPathTemp);
            startActivity(i);
        }
    }




    // metodos

    public void setupActivity(View view){

        showToolbar(getResources().getString(R.string.tab_home), false, view);

        fabCamera = (FloatingActionButton) view.findViewById(R.id.fabCamera);
        mPostList= (RecyclerView) view.findViewById(R.id.postsList);

    }


    public void setupFirebase(){
        //firebase
        app = (BuzonApplication) getApplicationContext();

        storageReference = app.getStorageReference();
        databaseReference = app.getDatabaseReference();

    }


    public void setupPostList(){

        // instanciar el linear layout manager del
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mPostList.setLayoutManager(linearLayoutManager);


        adapter = new PostAdapter(getActivity()); // inicalizar

        mPostList.setAdapter(adapter); //pasarle el adapter a la lista
    }


    private File createImageFile() throws IOException {
        // metodo para crear la imagen temporal
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HH-mm-ss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storangeDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photo = File.createTempFile(imageFileName, ".jpg", storangeDir);

        photoPathTemp = "file:" + photo.getAbsolutePath();

        return photo;
    }


    public void onclickTakePicture() {
        // abrir una actividad con la camara
        Intent intentTakePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intentTakePicture.resolveActivity(getActivity().getPackageManager()) != null) {

            File photoFile = null;

            try {
                photoFile = createImageFile();
            }catch (Exception e){
                e.printStackTrace();;
            }

            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(getActivity(), "com.opengto.buzondequejas", photoFile);
                intentTakePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); // pasarle el parametro
                startActivityForResult(intentTakePicture, REQUEST_CAMERA);
            }

        }
    }


    public void performGetPosts(){

        databaseReference.child(POST_REF).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    if (dataSnapshot.exists()){

                        ArrayList<Post> postsTemporal = new ArrayList<>();

                        for (DataSnapshot snapshot:dataSnapshot.getChildren()) {

                            Post postFirebase = snapshot.getValue(Post.class);

                            Post postToAdd = new Post(
                                    postFirebase.getUser(),
                                    postFirebase.getId(),
                                    postFirebase.getCreatedAt(),
                                    postFirebase.getPictureUrl(),
                                    postFirebase.getTitle(),
                                    postFirebase.getLikes(),
                                    postFirebase.getDislikes());

                            //agregarlo a la lista
                            postsTemporal.add(postToAdd);
                        }

                        //agregarlos al adaptador
                        adapter.addAll(postsTemporal);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //showToast("Ocurrió un error: "+databaseError.getMessage());
                    Log.w(TAG, "error "+databaseError.getMessage());
                }
            });

    }




    //metodos view

    public void showToolbar(String tittle, boolean upButton, View view){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(tittle);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);


    }


    public void showToast(String str) {
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
    }
}

/*
    private void setFixturesContent(){
        ArrayList<Post> posts = new ArrayList<>();


        String urlAvatar = "https://sumarketing.com.mx/media/files/no-profile.jpeg";
        String urlPhoto = "https://firebasestorage.googleapis.com/v0/b/buzon-de-quejas.appspot.com/o/post_images%2FJPEG_20171211_11-50-36_2131899332.jpg?alt=media&token=c65dac47-9cff-4cde-bae1-8c7520b55a97";

        //dato fixture
        User user = new User("12345211ff", "Usuario ejemplo", urlAvatar);

        Post p = new Post(
                user,
                "id-ejemplo",
                "Mon Dec 11 11:51:03 CST 2017",
                urlPhoto,
                "Primera publcación",
                0,
                0);



        for (int i = 0; i < 10; i++) {
            posts.add(p);
        }

        adapter.addAll(posts);
    }
*/