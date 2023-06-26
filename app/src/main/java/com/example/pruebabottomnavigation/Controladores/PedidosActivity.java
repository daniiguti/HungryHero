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
import android.widget.Toast;

import com.example.pruebabottomnavigation.Adapter.RecyclerAdapterPedidos;
import com.example.pruebabottomnavigation.Modelos.Pedido;

import com.example.pruebabottomnavigation.R;
import com.example.pruebabottomnavigation.Servicios.FirestorePeticiones;
import com.example.pruebabottomnavigation.Servicios.GuardarUser;
import com.example.pruebabottomnavigation.Servicios.OnPedidosActivityListener;
import com.google.firebase.firestore.FirebaseFirestore;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class PedidosActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPedidos;
    private RecyclerAdapterPedidos recyclerAdapterPedidos;
    private ArrayList<Pedido> pedidos;

    private FirebaseFirestore mibase;

    private String consulta;
    private GuardarUser gu;
    private File ruta;
    private String usuario;

    private FirestorePeticiones firestorePeticiones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);

        mibase = FirebaseFirestore.getInstance();

        ruta = getExternalFilesDir(null);
        gu = GuardarUser.getGuardarUser(ruta);
        gu.leerFichero();
        usuario = gu.devolverUser();

        pedidos = new ArrayList<>();
        recyclerAdapterPedidos = new RecyclerAdapterPedidos(pedidos, getApplicationContext());
        recyclerViewPedidos = (RecyclerView) findViewById(R.id.recyclerViewPedidos);
        recyclerViewPedidos.setAdapter(recyclerAdapterPedidos);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewPedidos.setLayoutManager(layoutManager);

        Intent i = getIntent();
        consulta = i.getStringExtra("consulta");

        recyclerAdapterPedidos.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int posicionPulsada = recyclerViewPedidos.getChildAdapterPosition(v);
                Pedido aux = recyclerAdapterPedidos.devolverPedido(posicionPulsada);
                Intent i = new Intent(getApplicationContext(), DetalleActivity.class);
                i.putExtra("idPedido", aux.getIdPedido());
                startActivity(i);
            }
        });

        //listeners firestore peticiones
        firestorePeticiones = new FirestorePeticiones();
        firestorePeticiones.setListenerPedidosActivity(new OnPedidosActivityListener() {
            @Override
            public void onPedidosReceive(ArrayList<Pedido> pedidos) {
                for(Pedido p: pedidos){
                    recyclerAdapterPedidos.insertar(p);
                }
            }
        });

        loadPreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //limpiamos el adapter
        recyclerAdapterPedidos.clear();

        //hacemos la consulta
        firestorePeticiones.cargarPedidos(consulta);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.out.println("HOLA");
        finishAffinity();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.putExtra("action", "cesta");
        startActivity(i);
    }

    //Método para mostrar los Toasts
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