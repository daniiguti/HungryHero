package com.example.pruebabottomnavigation.Controladores;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.pruebabottomnavigation.Fragment.CestaFragment;
import com.example.pruebabottomnavigation.Fragment.HomeFragment;
import com.example.pruebabottomnavigation.R;
import com.example.pruebabottomnavigation.Utils.Utils;
import com.example.pruebabottomnavigation.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private BottomNavigationView bottomNavigationView;
    private HomeFragment hf;
    private CestaFragment cf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        hf = new HomeFragment();
        cf = new CestaFragment();

        replaceFragment(hf);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.btCesta:
                    replaceFragment(cf);
                    break;
                case R.id.btRealizar:
                    Intent i = new Intent(getApplicationContext(), FamiliasActivity.class);
                    startActivity(i);
                    break;
                case R.id.btHome:
                    replaceFragment(hf);
                    break;
            }
            return true;
        });


    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPreferences();

        Intent aux = getIntent();
        String action = aux.getStringExtra("action");
        if(action != null){
            if (action.equals("home")) {
                //al hacer esto directamente se va al listener implementado arriba, no repetimos codigo
                //y no da el fallo que daba(estabe en fragment home y salia pulsado el boton de fragment cesta)
                bottomNavigationView.setSelectedItemId(R.id.btHome);
            } else {
                bottomNavigationView.setSelectedItemId(R.id.btCesta);
            }
        }

    }

    //MENÚ SIMPLE
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_simple_main,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId){
            case R.id.btCerrarSesion:
                createAlertDialog().show();
                break;
            case R.id.btAjustes:
                Intent i2 = new Intent(getApplicationContext(), PreferencesActivity.class);
                startActivity(i2);
                break;
        }
        return true;
    }

    //AlertDialog para eliminar
    public AlertDialog createAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String msg = "¿Seguro que desea cerrar sesión?";
        builder.setMessage(msg);
        builder.setTitle("Cerrar Sesión");

        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
                Intent aux = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(aux);
                showToast("SESIÓN CERRADA");
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        return builder.create();
    }

    public void showToast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
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
}