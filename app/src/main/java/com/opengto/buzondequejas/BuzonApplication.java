package com.opengto.buzondequejas;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    private FirebaseDatabase firebaseDatabase;


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
        FirebaseDatabase.getInstance().setPersistenceEnabled(true); // para cuando no hay conexion
        firebaseDatabase = FirebaseDatabase.getInstance();

    }


    public StorageReference getStorageReference(){
        return firebaseStorage.getReference();
    }

    public DatabaseReference getDatabaseReference(){
        return firebaseDatabase.getReference();
    }

    public FirebaseAuth getFirebaseAuth(){
        return FirebaseAuth.getInstance();
    }

    public static void putPref(String key, String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getPref(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }


}

