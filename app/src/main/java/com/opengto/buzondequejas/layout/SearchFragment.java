package com.opengto.buzondequejas.layout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.opengto.buzondequejas.BuzonApplication;
import com.opengto.buzondequejas.R;
import com.opengto.buzondequejas.model.Post;
import com.opengto.buzondequejas.post.PostAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;


public class SearchFragment extends Fragment {


    private BuzonApplication app;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private final String POST_REF ="Posts";
    public static final String TAG = "ProfileFragment";

    private PostAdapter adapter;
    private RecyclerView mPostList;

    private String userId, displayName, profilePhoto;


    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        showToolbar("Buscar", false, view);


        setupFirebase();

        setupActivity(view);

        setupPostList();

        performGetPosts();



        // retornar la vista
        return view;
    }




    public void setupActivity(View view){


        userId = BuzonApplication.getPref("userId", getActivity().getApplicationContext());
        displayName = BuzonApplication.getPref("displayName", getActivity().getApplicationContext()); // obtener de las shared preferences
        profilePhoto = BuzonApplication.getPref("profilePhotoUrl", getActivity().getApplicationContext());


        mPostList = (RecyclerView) view.findViewById(R.id.postsList);
    }


    public void setupFirebase(){
        //firebase
        app = (BuzonApplication) getApplicationContext();

        storageReference = app.getStorageReference();
        databaseReference = app.getDatabaseReference();

    }


    public void setupPostList(){

        // instanciar el linear layout manager del
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);

        mPostList.setLayoutManager(gridLayoutManager);


        adapter = new PostAdapter(getActivity(), true); // inicalizar

        mPostList.setAdapter(adapter); //pasarle el adapter a la lista
    }


    public void performGetPosts(){

        databaseReference.child(POST_REF).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()){

                    ArrayList<Post> postsTemporal = new ArrayList<>();

                    for (DataSnapshot snapshot:dataSnapshot.getChildren()) {

                        Post postFirebase = snapshot.getValue(Post.class);

                        if(postFirebase.getUser().getId().equals(userId)){

                            // si coincide con el usuario
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



    // metodo para mostrar el toolbar
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
