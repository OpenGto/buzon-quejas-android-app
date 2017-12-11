package com.opengto.buzondequejas.user;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.opengto.buzondequejas.layout.ContainerActivity;
import com.opengto.buzondequejas.R;
import com.opengto.buzondequejas.model.User;

public class SignupActivity extends AppCompatActivity{

    // variables
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    public static final String TAG = "SignupActivity";


    private TextInputEditText edtName, edtEmail, edtUsername, edtPassword, edtPasswordConfirm;
    private ProgressBar progressBar;
    private Button btnJoinUs;

    private boolean userLogged = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // hacer el setup de la actividad
        setupActivity();

        //hacer el setup de firebase
        setupFirebase();

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



    // metodos

    public void setupActivity(){
        showToolbar(getResources().getString(R.string.toolbar_tittle_createaccount), true);

        btnJoinUs = (Button) findViewById(R.id.joinUs);

        edtName = (TextInputEditText) findViewById(R.id.name);
        edtEmail = (TextInputEditText) findViewById(R.id.email);
        edtUsername = (TextInputEditText) findViewById(R.id.username);
        edtPassword = (TextInputEditText) findViewById(R.id.password);
        edtPasswordConfirm = (TextInputEditText) findViewById(R.id.confirmPassword);

        progressBar = (ProgressBar) findViewById(R.id.progressbarSignup);

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
                    signupSuccess();
                }else {
                    Log.w(TAG, "Usuario No logeado ");
                }

            }
        };
    }


    public void signupSuccess() {
        Intent intent = new Intent(this, ContainerActivity.class);
        startActivity(intent);
    }


    public User getData() {

        try{

            // obtener datos y meterlos en un objeto
            User user = new User(
                    edtName.getText().toString(),
                    edtUsername.getText().toString(),
                    edtEmail.getText().toString(),
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

        if (user.getName().trim().equals("")){
            showToast("El Nombre es obligatorio");
            return false;
        }

        if (user.getUsername().trim().equals("")){
            showToast("El Username es obligatorio");
            return false;
        }

        if (user.getPassword().trim().equals("")){
            showToast("El Password es obligatorio");
            return false;
        }

        if (edtPasswordConfirm.getText().toString().trim().equals("")){
            showToast("La confirmación del Password es obligatoria");
            return false;
        }

        if (!user.getPassword().equals(edtPasswordConfirm.getText().toString())){
            Toast.makeText(this, "Los Passwords deben ser iguales", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    public void onClickCreateAccount(View view) {

        disableInputs();
        showProgressBar();

        // obtener los datos y validarlos
        User user = getData();

        //
        if (user != null){
            performCreateAccount(user);
        }
        else{
            // si no se pudo validar
            enableInputs();
            hideProgressBar();
        }

    }


    public void performCreateAccount(final User user){
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(),user.getPassword())
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            showToast("Cuenta Creada Exitosamente");
                            userLogged = true;
                            hideProgressBar();
                            enableInputs();
                            signupSuccess(); //enviar a la actividad home
                        }else {
                            userLogged = false;
                            showToast("Ocurrió un Error al crear la cuenta");
                            Log.w(TAG, "Error" + task.getException());
                            hideProgressBar();
                            enableInputs();
                        }
                    }
                });

    }



    // metodos view

    public void enableInputs() {
        edtEmail.setEnabled(true);
        edtPassword.setEnabled(true);
        edtPasswordConfirm.setEnabled(true);
        btnJoinUs.setClickable(true);
    }


    public void disableInputs() {
        edtEmail.setEnabled(false);
        edtPassword.setEnabled(false);
        edtPasswordConfirm.setEnabled(false);
        btnJoinUs.setClickable(false);
    }


    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }


    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }


    public void showToast(String str) {
        Toast.makeText(SignupActivity.this, str, Toast.LENGTH_SHORT).show();
    }


    public void showToolbar(String tittle, boolean upButton){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(tittle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);

    }

}
