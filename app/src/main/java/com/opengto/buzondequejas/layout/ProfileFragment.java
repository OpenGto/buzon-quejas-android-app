package com.opengto.buzondequejas.layout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.opengto.buzondequejas.R;
import com.opengto.buzondequejas.adapter.PostAdapterRecyclerView;
import com.opengto.buzondequejas.model.Post;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        showToolbar("", false, view);


        // instancial el recycler view
        RecyclerView postsRecycler = (RecyclerView) view.findViewById(R.id.postProfileRecycler);

        // instanciar el linear layout manager del
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        // definir que sea vertical
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        // asignarle al "recycler view" el "linear layout manager"
        postsRecycler.setLayoutManager(linearLayoutManager);

        // instanciar el adapter y construir los post
        PostAdapterRecyclerView postsAdapterRecyclerView =
                new PostAdapterRecyclerView(buidPosts(), R.layout.cardview_post, getActivity());

        // asignar el adapter al recycler
        postsRecycler.setAdapter(postsAdapterRecyclerView);

        // retornar la vista
        return view;

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
