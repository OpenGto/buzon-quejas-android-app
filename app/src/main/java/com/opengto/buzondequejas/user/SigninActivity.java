package com.opengto.buzondequejas.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.opengto.buzondequejas.BuzonApplication;
import com.opengto.buzondequejas.layout.ContainerActivity;
import com.opengto.buzondequejas.R;
import com.opengto.buzondequejas.model.User;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SigninActivity extends AppCompatActivity {

    // variables
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private CallbackManager callbackManager;
    public static final String TAG = "SigninActivity";

    private TextInputEditText edtUsername, edtPassword;
    private Button btnLogin;
    private LoginButton loginButtonFacebook;
    private ProgressBar progressBar;


    private BuzonApplication app;


    private boolean userLogged = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // hacer el setup de la actividad
        setupActivity();

        //hacer el setup de firebase
        setupFirebase();

        //hacer el setup de facebook
        setupFacebook();

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }


    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }




    // metodos

    public void setupActivity(){

        app = (BuzonApplication) getApplicationContext();

        btnLogin = (Button) findViewById(R.id.btnLogin);
        loginButtonFacebook = (LoginButton) findViewById(R.id.btnLoginFacebook);

        edtUsername = (TextInputEditText) findViewById(R.id.username);
        edtPassword = (TextInputEditText) findViewById(R.id.password);

        progressBar = (ProgressBar) findViewById(R.id.progressbarSignin);

        hideProgressBar();
    }


    public void setupFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if(firebaseUser != null) {
                    Log.w(TAG, "Usuario logueado" + firebaseUser.getEmail());
                    signinSuccess();
                }else {
                    Log.w(TAG, "Usuario No logeado ");
                }

            }
        };
    }


    public void setupFacebook(){
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        loginButtonFacebook.setReadPermissions(Arrays.asList("email"));
        loginButtonFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.w(TAG, "Facebook Login Success Token: " + loginResult.getAccessToken().getApplicationId());
                performSigninFacebookFirebase(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.w(TAG, "Facebook Login Cancelado ");
            }

            @Override
            public void onError(FacebookException error) {
                Log.w(TAG, "Facebook Login Error: " + error.toString());
                error.printStackTrace();
                //FirebaseCrash.report(error);
            }
        });

    }


    public void goSignup(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }


    public void signinSuccess() {
        Intent intent = new Intent(this, ContainerActivity.class);
        startActivity(intent);
    }


    public User getData() {

        try{

            // obtener datos y meterlos en un objeto
            User user = new User(
                    edtUsername.getText().toString(), //email
                    edtPassword.getText().toString()
            );


            if (validateData(user)){
                return user;
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


    public boolean validateData(User user) {


        if (user.getEmail().trim().equals("")){
            showToast("El Email es obligatorio");
            return false;
        }

        if (!validarEmail(user.getEmail().trim())){
            showToast("El Email no es valido");
            return false;
        }

        if (user.getPassword().trim().equals("")){
            showToast("El Password es obligatorio");
            return false;
        }

        return true;
    }

    private boolean validarEmail(String email) {
        //Pattern pattern = Patterns.EMAIL_ADDRESS; return pattern.matcher(email).matches();

        // Patrón para validar el email
        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

        // El email a validar

        Matcher mather = pattern.matcher(email);

        if (mather.find() == true) {
            //System.out.println("El email ingresado es válido.");
            return true;
        } else {
            //System.out.println("El email ingresado es inválido.");
            return false;
        }

    }




    public void onclickLogin(View view) {

        disableInputs();
        showProgressBar();

        // obtener los datos y validarlos
        User user = getData();

        //
        if (user != null){
            performSignin(user);
        }
        else{
            // si no se pudo validar
            enableInputs();
            hideProgressBar();
        }

    }


    public void performSignin(final User user){

        firebaseAuth.signInWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(SigninActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            FirebaseUser user = task.getResult().getUser();

                            setUserSharedPreferences(user);

                            userLogged = true;

                            showToast("Bienvenido: " + user.getDisplayName());

                            signinSuccess();

                        }else {
                            userLogged = false;
                            showToast("Ocurrió un Error, tu usuario existe??");
                            Log.w(TAG, "Error" + task.getException());
                            hideProgressBar();
                            enableInputs();
                        }
                    }
                });

    }


    public void performSigninFacebookFirebase(AccessToken accessToken){

        Log.w(TAG, accessToken.getToken());
        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken.getToken());

        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    FirebaseUser user = task.getResult().getUser();

                    setUserSharedPreferences(user);

                    userLogged = true;

                    showToast("Bienvenido: " + user.getDisplayName());

                    signinSuccess();

                }else {
                   // FirebaseCrash.logcat(Log.WARN, TAG, "Login Facebook NO Exitoso");
                   // Toast.makeText(LoginActivity.this, "Login Facebook NO Exitoso", Toast.LENGTH_SHORT).show();
                    userLogged = false;
                    //showToast("Ocurrió un Error");
                    Log.w(TAG, "Error" + task.getException());
                    hideProgressBar();
                    enableInputs();
                }
            }
        });
    }


    public void setUserSharedPreferences(FirebaseUser user){
        //shared preferences
        BuzonApplication.putPref("email", user.getEmail(), getApplicationContext());
        BuzonApplication.putPref("displayName", user.getDisplayName(), getApplicationContext());
        BuzonApplication.putPref("profilePhotoUrl", user.getPhotoUrl().toString(), getApplicationContext());
        BuzonApplication.putPref("userId", user.getUid(), getApplicationContext());

    }




    // metodos view

    public void enableInputs() {
        edtUsername.setEnabled(true);
        edtPassword.setEnabled(true);
        btnLogin.setClickable(true);
    }


    public void disableInputs() {
        edtUsername.setEnabled(false);
        edtPassword.setEnabled(false);
        btnLogin.setClickable(false);
    }


    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }


    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }


    public void showToast(String str) {
        Toast.makeText(SigninActivity.this, str, Toast.LENGTH_SHORT).show();
    }


}