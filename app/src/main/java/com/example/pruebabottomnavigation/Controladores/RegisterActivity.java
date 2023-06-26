package com.example.pruebabottomnavigation.Controladores;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;


import com.example.pruebabottomnavigation.R;
import com.example.pruebabottomnavigation.Servicios.FirestorePeticiones;
import com.example.pruebabottomnavigation.Servicios.OnRegisterActivityListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    private Button btComprobar;
    private EditText usuario;
    private EditText telefono;
    private EditText contra;
    private EditText repetirContra;
    private TextView contraIncorrecta;

    private String verificacion;

    private boolean userCorrecto = false;

    private FirestorePeticiones firestorePeticiones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        btComprobar = (Button) findViewById(R.id.btComprobar);
        btComprobar.setEnabled(false);
        usuario = (EditText) findViewById(R.id.txtUsuario2);
        contra = (EditText) findViewById(R.id.txtContra2);
        contra.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        repetirContra = (EditText) findViewById(R.id.txtRepetirContra);
        repetirContra.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        telefono = (EditText) findViewById(R.id.txtTelefono);
        contraIncorrecta = (TextView) findViewById(R.id.tvNoCoinciden);
        contraIncorrecta.setVisibility(View.INVISIBLE);

        Intent i = getIntent();

        //Listener del formulario, para controlar la informacion correcta
        usuario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(usuario.getText().toString().isEmpty() == false && usuario.getText().toString().length() <= 20){
                    userCorrecto = true;
                    if(contra.getText().toString().isEmpty() == false && repetirContra.getText().toString().isEmpty() == false && userCorrecto == true && contra.getText().toString().equals(repetirContra.getText().toString())){
                        contraIncorrecta.setVisibility(View.INVISIBLE);
                        btComprobar.setEnabled(true);
                    }else{
                        if(userCorrecto == false){
                            contraIncorrecta.setVisibility(View.INVISIBLE);
                            btComprobar.setEnabled(false);
                        }
                        if(contra.getText().toString().equals(repetirContra.getText().toString()) == false){
                            contraIncorrecta.setText("Las contraseñas no coinciden");
                            contraIncorrecta.setVisibility(View.VISIBLE);
                            btComprobar.setEnabled(false);
                        }
                    }
                }else{
                    userCorrecto = false;
                    btComprobar.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        contra.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(contra.getText().toString().isEmpty() == false && repetirContra.getText().toString().isEmpty() == false && userCorrecto == true && contra.getText().toString().equals(repetirContra.getText().toString())){
                    contraIncorrecta.setVisibility(View.INVISIBLE);
                    btComprobar.setEnabled(true);
                }else{
                    if(userCorrecto == false){
                        contraIncorrecta.setVisibility(View.INVISIBLE);
                        btComprobar.setEnabled(false);
                    }
                    if(contra.getText().toString().equals(repetirContra.getText().toString()) == false){
                        contraIncorrecta.setText("Las contraseñas no coinciden");
                        contraIncorrecta.setVisibility(View.VISIBLE);
                        btComprobar.setEnabled(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        repetirContra.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(contra.getText().toString().isEmpty() == false && repetirContra.getText().toString().isEmpty() == false && userCorrecto == true && contra.getText().toString().equals(repetirContra.getText().toString())){
                    contraIncorrecta.setVisibility(View.INVISIBLE);
                    btComprobar.setEnabled(true);
                }else{
                    if(userCorrecto == false){
                        contraIncorrecta.setVisibility(View.INVISIBLE);
                        btComprobar.setEnabled(false);
                    }
                    if(contra.getText().toString().equals(repetirContra.getText().toString()) == false){
                        contraIncorrecta.setText("Las contraseñas no coinciden");
                        contraIncorrecta.setVisibility(View.VISIBLE);
                        btComprobar.setEnabled(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btComprobar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(telefono.getText().toString().length() == 9){
                    showToast("COMPROBANDO EL TELÉFONO... ESPERE");
                }
                //comprobamos si ese usuario existe
                firestorePeticiones.comprobarUsuario(usuario.getText().toString());
            }
        });

        firestorePeticiones = new FirestorePeticiones();
        firestorePeticiones.setListenerRegisterActivity(new OnRegisterActivityListener() {
            @Override
            public void onComprobarUsuario(boolean usuarioExiste) {
                //si el usuario existe
                if(usuarioExiste == true){
                    showToast("USUARIO EXISTENTE - PRUEBE CON OTRO NOMBRE");
                    usuario.setText("");
                    btComprobar.setEnabled(false);
                }
                else{
                    //si el usuario no existia,
                    // comprobamos si introdujo un telefono (tiene que demostrar que es el suyo)
                    if(telefono.getText().toString().length() == 9){
                        enviarCodigo("+34"+telefono.getText().toString());
                    }
                    //si el usuario no queria registrarse con un telefono, lo registramos de forma normal
                    else{
                        firestorePeticiones.registrarUsuario(usuario.getText().toString(), contra.getText().toString());
                    }

                }
            }

            @Override
            public void onUsuarioRegistrado() {
                showToast("USUARIO REGISTRADO CORRECTAMENTE SIN TÉLEFONO");
                //(donde tenemos nuestro adapter) añada al arrayList del recAdapter
                Intent returnIntent = new Intent();

                returnIntent.putExtra("usuario", usuario.getText().toString());

                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }

            @Override
            public void onUsuarioRegistradoConTelefono(){
                Thread espera = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showToast("USUARIO REGISTRADO CORRECTAMENTE CON TÉLEFONO");
                                    //(donde tenemos nuestro adapter) añada al arrayList del recAdapter
                                    Intent returnIntent = new Intent();

                                    returnIntent.putExtra("usuario", usuario.getText().toString());

                                    setResult(Activity.RESULT_OK,returnIntent);
                                    finish();
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });

                espera.start();
            }
        });

        loadPreferences();
    }

    //Metodos relacionados con el envio del mensaje al dispositivo
    public void enviarCodigo(String telefono){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("es");
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(telefono)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                showToast("VERIFICACION COMPLETA");
                                signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                showToast("VERIFICACION FALLIDA: " + e.getMessage());
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                System.out.println("CODIGO ENVIADO");
                                verificacion = verificationId;
                                createAlertDialogInsert().show();
                            }

                            @Override
                            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {

                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    public AlertDialog createAlertDialogInsert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);

        builder.setTitle("Confirmar número");

        View view = getLayoutInflater().inflate(R.layout.insertar_codigo, null);
        
        EditText codigo;

        codigo = (EditText) view.findViewById(R.id.txtCode);

        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("CODIGO INTRODUCIDO: " + codigo.getText().toString());
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificacion, codigo.getText().toString());
                signInWithPhoneAuthCredential(credential);
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setView(view);

        return builder.create();
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("es");
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //Cuando el usuario demuestre que ese es su telefono, lo registramos con todos los datos, si el codigo era incorrecto no lo registramos
                            showToast("CODIGO CORRECTO");
                            firestorePeticiones.registrarUsuarioConTelefono(usuario.getText().toString(), contra.getText().toString(), "+34" + telefono.getText().toString());
                        }else{
                            showToast("CODIGO INCORRECTO - USUARIO NO REGISTRADO");
                        }
                    }
                });
    }

    //Para cargar las preferencias
    public void loadPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String colorFondo = sharedPreferences.getString("preferences_tema","Light");
        switch (colorFondo){
            case "Light":
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "Night":
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }
    }
    //Toast para dar informacion al usuario
    public void showToast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }
}