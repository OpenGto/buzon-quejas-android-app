package com.opengto.buzondequejas;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by joel_barron on 09/12/17.
 */

public class BuzonApplication extends Application {


    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private String TAG = "BuzonApplication";
    private FirebaseStorage firebaseStorage;

    @Override
    public void onCreate() {

        super.onCreate();

        FacebookSdk.sdkInitialize(getApplicationContext());

        authStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if(firebaseUser != null) {
                    Log.w(TAG, "Usuario logueado" + firebaseUser.getEmail());
                }else {
                    Log.w(TAG, "Usuario No logeado ");
                }

            }
        };


        firebaseStorage = FirebaseStorage.getInstance();
    }


    public StorageReference getStorageReference(){
        return firebaseStorage.getReference();
    }
}
