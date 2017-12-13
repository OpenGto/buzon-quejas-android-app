package com.opengto.buzondequejas.layout;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.Toast;

// importar nuestros fragments
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.opengto.buzondequejas.BuzonApplication;
import com.opengto.buzondequejas.R;
import com.opengto.buzondequejas.user.SigninActivity;


public class ContainerActivity extends AppCompatActivity {

    // barra de navegacion inferior
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    public static final String TAG = "ContainerActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        setupFirebase();

        // declarar los fragments
        final Fragment searchFragment = new SearchFragment();
        final Fragment homeFragment = new HomeFragment();
        final Fragment profileFragment = new ProfileFragment();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottombar);


        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, homeFragment).commit();
            //fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
            bottomNavigationView.setSelectedItemId(R.id.homeItem);
        }


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentManager fragmentManager = getSupportFragmentManager();

                if (item.getItemId() == R.id.homeItem) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, homeFragment);
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                } else if (item.getItemId() == R.id.searchItem) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, searchFragment);
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                } else if (item.getItemId() == R.id.profileItem) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, profileFragment);
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                }
                return true;
            }
        });


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_options,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.mSignOut:
                firebaseAuth.signOut();

                if (LoginManager.getInstance() != null){
                    LoginManager.getInstance().logOut();
                }
                showToast("Se cerró la sesión");

                resetSharedPreferences();

                Intent i = new Intent(ContainerActivity.this, SigninActivity.class);
                startActivity(i);


                break;

            case R.id.mAbout:
                showToast("Desarrollado por OpenGto");
                break;

        }

        return super.onOptionsItemSelected(item);
    }



    //metodos
    public void setupFirebase(){

        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if(firebaseUser != null) {
                    Log.w(TAG, "Usuario logueado" + firebaseUser.getEmail());
                }else {
                    Log.w(TAG, "Usuario No logeado ");

                    Intent intent = new Intent(ContainerActivity.this, SigninActivity.class);
                    startActivity(intent);
                }

            }
        };
    }

    public void resetSharedPreferences(){

        //shared preferences
        BuzonApplication.putPref("email", "", getApplicationContext());
        BuzonApplication.putPref("displayName", "", getApplicationContext());
        BuzonApplication.putPref("profilePhotoUrl", "", getApplicationContext());
        BuzonApplication.putPref("userId", "", getApplicationContext());


    }


    // metodos view
    public void showToast(String str) {
        Toast.makeText(ContainerActivity.this, str, Toast.LENGTH_SHORT).show();
    }

}
