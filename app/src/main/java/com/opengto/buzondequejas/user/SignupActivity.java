package com.opengto.buzondequejas.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.opengto.buzondequejas.BuzonApplication;
import com.opengto.buzondequejas.layout.ContainerActivity;
import com.opengto.buzondequejas.R;
import com.opengto.buzondequejas.model.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity{

    // variables
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    public static final String TAG = "SignupActivity";


    private TextInputEditText edtName, edtEmail, edtPassword, edtPasswordConfirm;
    private ProgressBar progressBar;
    private Button btnJoinUs;

    private final String URL_DEFAULT = "https://sumarketing.com.mx/media/files/no-profile.jpeg";
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
                    "",
                    edtName.getText().toString(),
                    edtEmail.getText().toString(),
                    edtPassword.getText().toString(),
                    URL_DEFAULT
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

        if(!validarEmail(user.getEmail().trim())){
            showToast("El email no es valido");
        }

        if (user.getDisplayName().trim().equals("")){
            showToast("El Nombre es obligatorio");
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

        if (!validarContrasena(edtPassword.getText().toString(), edtPasswordConfirm.getText().toString())){
            Toast.makeText(this, "Ocurrio un error", Toast.LENGTH_SHORT).show();
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


    public boolean validarContrasena(String password1, String password2)
    {
        if (password1.isEmpty())
        {
            showToast("El campo Contraseña no puede ser vacio");
            return false;
        }
        if (password2.isEmpty())
        {
            showToast("Confirma la contraseña");
            return false;
        }
        if (!password1.equals(password2))
        {

            showToast("Las contraseñas no coinciden");
            return false;
        }
        if (password1.length() <= 6)
        {
            showToast("La contraseña tiene muy pocos caracteres");
            return false;
        }
        try
        {
            Integer.parseInt(password1);

            showToast("La contraseña debe contener letras");

            return false;
        }
        catch (Exception e){

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

                            FirebaseUser firebaseUser = task.getResult().getUser();

                            user.setId(firebaseUser.getUid()); //agregar el uuid

                            if(firebaseUser != null){
                                final boolean successful = false;
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(user.getDisplayName().trim())
                                        .setPhotoUri(Uri.parse(URL_DEFAULT)) // here you can set image link also.
                                        .build();

                                firebaseUser.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("TESTING", "User profile updated.");
                                                    setUserSharedPreferences(user.getEmail(), user.getDisplayName(), URL_DEFAULT, user.getId());
                                                }
                                            }
                                        });

                            }


                            userLogged = true;

                            showToast("Bienvenido: " + user.getDisplayName());

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


    public void setUserSharedPreferences(String email, String displayName, String profilePhotoUrl, String userId){
        //shared preferences
        BuzonApplication.putPref("email", email, getApplicationContext());
        BuzonApplication.putPref("displayName", displayName, getApplicationContext());
        BuzonApplication.putPref("profilePhotoUrl", profilePhotoUrl, getApplicationContext());
        BuzonApplication.putPref("userId", userId, getApplicationContext());
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
