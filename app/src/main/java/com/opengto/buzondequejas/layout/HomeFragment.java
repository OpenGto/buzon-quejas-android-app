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


import com.opengto.buzondequejas.model.Post;
import com.opengto.buzondequejas.R;
import com.opengto.buzondequejas.adapter.PostAdapterRecyclerView;
import com.opengto.buzondequejas.post.NewPostActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class HomeFragment extends Fragment {

    // variables
    private FloatingActionButton fabCamera;
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

        // mostrar el toolbar
        showToolbar(getResources().getString(R.string.tab_home), false, view);

        // instancial el recycler view
        RecyclerView postsRecycler = (RecyclerView) view.findViewById(R.id.postRecycler);

        fabCamera = (FloatingActionButton) view.findViewById(R.id.fabCamera);


        // instanciar el linear layout manager del
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);


        postsRecycler.setLayoutManager(linearLayoutManager); // asignarle al "recycler view" el "linear layout manager"


        PostAdapterRecyclerView postsAdapterRecyclerView =
                new PostAdapterRecyclerView(buidPosts(), R.layout.cardview_post, getActivity()); // instanciar el adapter y construir los post


        postsRecycler.setAdapter(postsAdapterRecyclerView); // asignar el adapter al recycler


        //evento onclick fabcamera
        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        return view;
    }

    private void takePicture() {
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

    // metodo para crear la imagen temporal
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HH-mm-ss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storangeDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photo = File.createTempFile(imageFileName, ".jpg", storangeDir);

        photoPathTemp = "file:" + photo.getAbsolutePath();

        return photo;
    }

    // evento de el resultado de la actividad
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_CAMERA && resultCode == getActivity().RESULT_OK) {
            Log.d("Home fragment", "CAMERA OK !! :)");
            Intent i = new Intent(getActivity(), NewPostActivity.class);
            i.putExtra("PHOTO_PATH_TEMP", photoPathTemp);
            startActivity(i);
        }
    }

    // metodo que construye los Fixtures de post
    public ArrayList<Post> buidPosts(){
        ArrayList<Post> posts = new ArrayList<>();
        posts.add(new Post("https://www.itleon.edu.mx/images/tecnoticias/Ago-dic15/21-1/4.jpg", "Ejemplo","Joel Barrón", "2 días", "nueva","no asignada", 1, 9));
        posts.add(new Post("http://archivo.unionguanajuato.mx/sites/default/files/leon%20tecnologico.jpg", "Otra","Raymundo", "14 días", "nueva","no asignada", 18, 3));
        posts.add(new Post("https://www.itleon.edu.mx/images/itl/4.jpg", "Ultima","Faer torres", "40 días", "nueva","no asignada", 99, 61));
        return posts;
    }


    // metodo para mostrar el toolbar
    public void showToolbar(String tittle, boolean upButton, View view){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(tittle);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);


    }

}
