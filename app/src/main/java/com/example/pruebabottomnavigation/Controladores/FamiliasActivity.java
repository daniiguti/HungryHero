package com.example.pruebabottomnavigation.Controladores;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.pruebabottomnavigation.Adapter.RecyclerAdapterFamilias;
import com.example.pruebabottomnavigation.Modelos.Familia;
import com.example.pruebabottomnavigation.Modelos.Producto;
import com.example.pruebabottomnavigation.R;
import com.example.pruebabottomnavigation.Servicios.FirestorePeticiones;
import com.example.pruebabottomnavigation.Servicios.OnFamiliasActivityListener;
import com.example.pruebabottomnavigation.Servicios.OnRealizarPedidoActivityListener;

import java.util.ArrayList;

public class FamiliasActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFamilias;
    private RecyclerAdapterFamilias recAdapterFamilias;
    private ArrayList<Familia> familias;

    private FirestorePeticiones firestorePeticiones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_familias);

        familias = new ArrayList<>();
        recAdapterFamilias = new RecyclerAdapterFamilias(familias, getApplicationContext());
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getApplicationContext());
        recyclerViewFamilias = (RecyclerView) findViewById(R.id.recyclerViewFamilias);
        recyclerViewFamilias.setAdapter(recAdapterFamilias);
        recyclerViewFamilias.setLayoutManager(layoutManager1);

        recAdapterFamilias.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int posicionPulsada = recyclerViewFamilias.getChildAdapterPosition(v);
                Familia familiaSeleccionada = recAdapterFamilias.devolverFamilia(posicionPulsada);
                Intent i = new Intent(getApplicationContext(), RealizarPedidoActivity.class);
                i.putExtra("familia", familiaSeleccionada.getId());
                i.putExtra("nombreFamilia", familiaSeleccionada.getNombre());
                startActivity(i);
            }
        });

        firestorePeticiones = new FirestorePeticiones();
        firestorePeticiones.setListenerFamiliasActivity(new OnFamiliasActivityListener() {
            @Override
            public void onFamiliasReceive(ArrayList<Familia> familias) {
                for(Familia f: familias){
                    recAdapterFamilias.insertar(f);
                }
            }
        });

        firestorePeticiones.cargarFamilias();
        loadPreferences();
    }

    //Hacemos esto para que se pulse desde donde se pulse se vaya a HomeFragment y darle una buena
    //experiencia al usuario
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.out.println("HOLA");
        finishAffinity();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.putExtra("action", "home");
        startActivity(i);
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