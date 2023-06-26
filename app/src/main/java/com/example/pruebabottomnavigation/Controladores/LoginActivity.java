package com.example.pruebabottomnavigation.Controladores;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.pruebabottomnavigation.R;
import com.example.pruebabottomnavigation.Servicios.FirestorePeticiones;
import com.example.pruebabottomnavigation.Servicios.GuardarUser;
import com.example.pruebabottomnavigation.Servicios.OnLoginActivityListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;

public class LoginActivity extends AppCompatActivity {
    //Variable estática para startActivityForResult
    public static final int RQ_CODE = 1;

    private Button btLogin;
    private Button btSignUp;
    private EditText txtUsuario;
    private EditText txtPassword;
    private CheckBox cbRecordar;
    private Button btVerPass;

    private File ruta;
    private GuardarUser gu;

    private FirestorePeticiones firestorePeticiones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseAuth.getInstance().getFirebaseAuthSettings()
                .setAppVerificationDisabledForTesting(false);
        getSupportActionBar().hide();

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        txtUsuario = (EditText) findViewById(R.id.txtUsuario);
        txtPassword = (EditText) findViewById(R.id.txtContra);
        txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        btLogin = (Button) findViewById(R.id.btLogin);
        btSignUp = (Button) findViewById(R.id.btSignUp);
        cbRecordar = (CheckBox) findViewById(R.id.cbRecordar);
        btVerPass = (Button) findViewById(R.id.btVerPass);

        ruta = getExternalFilesDir(null);
        gu = GuardarUser.getGuardarUser(ruta);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = txtUsuario.getText().toString();
                String pass = txtPassword.getText().toString();
                if (user.contains(" ") == false && pass.contains(" ") == false && user.equals("") == false && pass.equals("") == false ) {
                    firestorePeticiones.comprobarUsuario(user, pass);
                }else{
                    showToast("No deje espacios ni campos vacíos");
                }
            }
        });

        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivityForResult(i, RQ_CODE);
            }
        });

        cbRecordar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String user = txtUsuario.getText().toString();
                String pass = txtPassword.getText().toString();
                if (user.contains(" ") == false && pass.contains(" ") == false && user.equals("") == false && pass.equals("") == false ) {
                    if (cbRecordar.isChecked()) {
                        gu.borrarFichero();
                        gu.guardarFichero(user, pass);
                        savePrefs("guardarCambios", true);
                    } else {
                        savePrefs("guardarCambios", false);
                    }
                }
                else{
                    cbRecordar.setChecked(false);
                    showToast("No deje espacios ni campos vacíos");
                }
            }
        });

        //Listener del boton ver contraseña
        //Cuando se activa la muestra, cuando lo desactiva la esconde
        btVerPass.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch ( event.getAction() ) {
                    case MotionEvent.ACTION_DOWN:
                        txtPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    case MotionEvent.ACTION_UP:
                        txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        break;
                }
                return true;
            }
        });

        firestorePeticiones = new FirestorePeticiones();
        firestorePeticiones.setListenerLoginActivity(new OnLoginActivityListener() {
            @Override
            public void onComprobarUsuario(boolean userCorrecto){
                if(userCorrecto == true){
                    //Metodo para que cuando se pulse el boton de login
                    //comprobar si esta checked el combo box o no, para guardar los datos
                    if (cbRecordar.isChecked()) {
                        savePrefs("guardarCambios", true);
                    } else {
                        savePrefs("guardarCambios", false);
                    }
                    showToast("Usuario correcto");
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    showToast("Usuario o contraseña incorrecto");
                }
            }
        });

        loadPreferences();
    }

    //Método para mostrar los Toasts
    public void showToast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    //Metodo para guardar las preferencias de Recordar Contraseña
    public void savePrefs(String key, Boolean value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    public void loadPreferences(){
        SharedPreferences sharedPreferences2 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Boolean data = sharedPreferences2.getBoolean("guardarCambios", false);
        if(data == true){
            gu.leerFichero();
            String user = gu.devolverUser();
            String pass = gu.devolverPass();
            txtUsuario.setText(user);
            txtPassword.setText(pass);
            cbRecordar.setChecked(true);
        }

        String colorFondo = sharedPreferences2.getString("preferences_tema","Light");
        switch (colorFondo){
            case "Light":
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "Night":
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }
    }

    //Método que me devuelve el nombre de usuario al registrarse
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case RQ_CODE:
                if(resultCode==RESULT_OK){
                    String usuario = data.getStringExtra("usuario");
                    txtUsuario.setText(usuario);
                    txtPassword.setText("");
                    cbRecordar.setChecked(false);
                }
                break;
        }
    }
}