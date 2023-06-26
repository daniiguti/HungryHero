package com.example.pruebabottomnavigation.Controladores;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pruebabottomnavigation.Adapter.RecyclerAdapterLineasPedidos;
import com.example.pruebabottomnavigation.Adapter.RecyclerAdapterProductos;
import com.example.pruebabottomnavigation.Modelos.LineasPedido;
import com.example.pruebabottomnavigation.Modelos.Producto;
import com.example.pruebabottomnavigation.R;
import com.example.pruebabottomnavigation.Servicios.Fcm;
import com.example.pruebabottomnavigation.Servicios.FirestorePeticiones;
import com.example.pruebabottomnavigation.Servicios.GuardarUser;
import com.example.pruebabottomnavigation.Servicios.OnRealizarPedidoActivityListener;
import com.example.pruebabottomnavigation.Utils.Utils;


import java.io.File;
import java.util.ArrayList;

public class RealizarPedidoActivity extends AppCompatActivity {

    private RecyclerView recyclerViewProductos;
    private RecyclerAdapterProductos recAdapterProductos;
    private ArrayList<Producto> productos;

    private RecyclerView recyclerViewPedido;
    private RecyclerAdapterLineasPedidos recAdapterPedidos;
    private ArrayList<LineasPedido> pedidos;

    private Button btAceptar;
    private Button btCancelar;

    private int posicionPulsada;

    private ActionMode actionMode;

    private GuardarUser gu;
    private File ruta;

    private Fcm notificacion;
    private FirestorePeticiones firestorePeticiones;

    private Intent i;
    private String familia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realizar_pedido);

        Intent aux = getIntent();
        String nombreFamilia = aux.getStringExtra("nombreFamilia");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(nombreFamilia);

        notificacion = new Fcm();

        btAceptar = (Button) findViewById(R.id.btAceptar);
        btCancelar = (Button) findViewById(R.id.btCancelar);



        ruta = getExternalFilesDir(null);
        gu = GuardarUser.getGuardarUser(ruta);

        productos = new ArrayList<>();
        recAdapterProductos = new RecyclerAdapterProductos(productos, getApplicationContext());
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getApplicationContext());
        recyclerViewProductos = (RecyclerView) findViewById(R.id.recyclerViewProductos);
        recyclerViewProductos.setAdapter(recAdapterProductos);
        recyclerViewProductos.setLayoutManager(new GridLayoutManager(this, 2));

        pedidos = new ArrayList<>();
        recAdapterPedidos = new RecyclerAdapterLineasPedidos(pedidos, getApplicationContext(), "info");
        LinearLayoutManager layoutManager5 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewPedido = (RecyclerView) findViewById(R.id.recyclerView5);
        recyclerViewPedido.setAdapter(recAdapterPedidos);
        recyclerViewPedido.setLayoutManager(layoutManager5);

        recAdapterProductos.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int posicionPulsada = recyclerViewProductos.getChildAdapterPosition(v);
                showAlertDialog(posicionPulsada);
            }
         });
        recAdapterPedidos.setLongListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                posicionPulsada = recyclerViewPedido.getChildAdapterPosition(v);
                //Hay que pasarle la interfaz implementada mas abajo
                actionMode = startActionMode(actionModeCallback);
                v.setSelected(true);
                return true;
            }
        });

        btAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = createAlertDialogInsertar();
                alertDialog.show();
            }
        });
        btCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //listeners firestore peticiones
        firestorePeticiones = new FirestorePeticiones();
        firestorePeticiones.setListenerRealizarPedidoActivity(new OnRealizarPedidoActivityListener() {
            @Override
            public void onTipoProductoReceive(ArrayList<Producto> productos) {
                for(Producto p: productos){
                    recAdapterProductos.insertar(p);
                }
            }

            @Override
            public void onPedidoInsertadoListener(){
                finishAffinity();
                Intent i = new Intent(getApplicationContext(), PedidosActivity.class);
                i.putExtra("consulta", "En espera");
                startActivity(i);
            }
        });

        loadPreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //limpiamos el arraylist
        recAdapterProductos.clear();
        //cargamos los productos que va seleccionando el usuario
        cargarPedidos();

        i = getIntent();
        familia = i.getStringExtra("familia");

        firestorePeticiones.obtenerProductos(familia);
    }

    private int cantidadProducto = 0;
    private Producto producto;
    private AlertDialog alertDialog;
    private void showAlertDialog(int posicion){
        alertDialog = createAlertDialogInsert(posicion);

        alertDialog.show();
    }
    public AlertDialog createAlertDialogInsert(int pos){
        producto = recAdapterProductos.devolverProducto(pos);

        AlertDialog.Builder builder = new AlertDialog.Builder(RealizarPedidoActivity.this);

        builder.setTitle("Seleccione la cantidad");

        View view = getLayoutInflater().inflate(R.layout.layout_custom_dialog, null);
        TextView cantidad;
        Button btAceptar, btCancelar, btMas, btMenos;
        EditText nota;

        cantidad = (TextView) view.findViewById(R.id.tvCantidadDialog);
        btAceptar = (Button) view.findViewById(R.id.btAceptarDialog);
        btCancelar = (Button) view.findViewById(R.id.btCancelarDialog);
        btMas = (Button) view.findViewById(R.id.btMas);
        btMenos = (Button) view.findViewById(R.id.btMenos);
        nota = (EditText) view.findViewById(R.id.txtNota);

        btMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cantidadProducto < 10){
                    cantidadProducto++;
                    cantidad.setText(String.valueOf(cantidadProducto));
                }
            }
        });
        btMenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cantidadProducto > 0){
                    cantidadProducto--;
                    cantidad.setText(String.valueOf(cantidadProducto));
                }
            }
        });

        //si pulsa el boton aceptar, añadimos el pedido a nuestro arraylist del recAdapter
        btAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cantidadProducto > 0){
                    if(nota.getText().toString().length() < 20){
                        recAdapterPedidos.insertar(new LineasPedido(producto.getId(), cantidadProducto, nota.getText().toString()));
                        Utils.CONTROLAR_PEDIDOS.add(new LineasPedido(producto.getId(), cantidadProducto, nota.getText().toString()));
                        cantidadProducto = 0;
                        alertDialog.dismiss();
                    }else{
                        showToast("Los pedidos no pueden llevar una nota más larga de 20 carácteres");
                    }
                }else{
                    showToast("No se pueden pedir cosas negativas");
                }

            }
        });
        //si pulsa cancelar no hacemos nada
        btCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cantidadProducto = 0;
                alertDialog.dismiss();
            }
        });

        builder.setView(view);

        return builder.create();
    }

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_accion, menu);
            return true;
        }
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.btEliminar:
                    AlertDialog alertDialog = createAlertDialogEliminar();
                    alertDialog.show();
                    mode.finish();
                    break;
            }
            return true;
        }
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
        }
    };

    //AlertDialog para eliminar
    public AlertDialog createAlertDialogEliminar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String msg = "¿Seguro que desea eliminar este producto?";
        builder.setMessage(msg);
        builder.setTitle("Eliminar producto");

        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                recAdapterPedidos.eliminar(posicionPulsada);
                Utils.CONTROLAR_PEDIDOS.remove(posicionPulsada);
                showToast("PRODUCTO ELIMINADO");
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showToast("PRODUCTO NO ELIMINADO");
            }
        });

        return builder.create();
    }

    //AlertDialog para aceptar
    public AlertDialog createAlertDialogInsertar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String msg = "¿Seguro que desea realizar este pedido?";
        builder.setMessage(msg);
        builder.setTitle("Confirmar pedido");

        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(Utils.CONTROLAR_PEDIDOS.size() > 0){
                    //insertamos en nuestra bdd
                    firestorePeticiones.insertarBdd(recAdapterPedidos.devolverLineas());
                    //mandamos una notificación
                    notificacion.enviarNotificacionPedido(getApplicationContext());
                    //avisamos de que el pedido se ha confirmado
                    showToast("PEDIDO CONFIRMADO");
                    //limpiamos nuestro array
                    Utils.CONTROLAR_PEDIDOS.clear();
                }else{
                    showToast("Seleccione al menos un producto");
                }
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showToast("PEDIDO NO REALIZADO");
            }
        });

        return builder.create();
    }

    //para cargar los productos que ha ido añadiendo anteriormente
    private void cargarPedidos(){
        this.recAdapterPedidos.clear();
        for(LineasPedido p: Utils.CONTROLAR_PEDIDOS){
            recAdapterPedidos.insertar(p);
        }
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