package com.example.pruebabottomnavigation.Servicios;

import androidx.annotation.NonNull;

import com.example.pruebabottomnavigation.Modelos.Familia;
import com.example.pruebabottomnavigation.Modelos.LineasPedido;
import com.example.pruebabottomnavigation.Modelos.Pedido;
import com.example.pruebabottomnavigation.Modelos.Producto;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Patrón delegado -> para no tener toda la gestion del acceso a firebase en los activity o controladores
 * nos creamos esta clase para independizar tod esto, pero nos ocurre el problema de que las peticiones se
 * realizan en segundo plano, por lo que no podemos hacer return convencionales (ya que devolvera el objeto vacio)
 * por lo que hay que implementar listener propios para "avisar" a nuestras activity/controladores de que la peticion ya está lista
 */
public class FirestorePeticiones {

    //listeners para avisar a nuestros controladores
    private OnHomeFragmentListener listenerHomeFragment;
    private OnRealizarPedidoActivityListener listenerRealizarPedidoActivity;
    private OnFamiliasActivityListener listenerFamiliasActivity;
    private OnPedidosActivityListener listenerPedidosActivity;
    private OnDetalleActivityListener listenerDetalleActivity;
    private OnLoginActivityListener listenerLoginActivity;
    private OnRegisterActivityListener listenerRegisterActivity;
    //firebase
    private FirebaseFirestore mibase;

    //constructor
    public FirestorePeticiones(){

    }

    //getters y setters
    public OnHomeFragmentListener getListenerHomeFragment() {
        return listenerHomeFragment;
    }
    public void setListenerHomeFragment(OnHomeFragmentListener listenerHomeFragment) {
        this.listenerHomeFragment = listenerHomeFragment;
    }

    public OnRealizarPedidoActivityListener getListenerRealizarPedidoActivity() {
        return listenerRealizarPedidoActivity;
    }
    public void setListenerRealizarPedidoActivity(OnRealizarPedidoActivityListener listenerRealizarPedidoActivity) {
        this.listenerRealizarPedidoActivity = listenerRealizarPedidoActivity;
    }

    public OnFamiliasActivityListener getListenerFamiliasActivity() {
        return listenerFamiliasActivity;
    }
    public void setListenerFamiliasActivity(OnFamiliasActivityListener listenerFamiliasActivity) {
        this.listenerFamiliasActivity = listenerFamiliasActivity;
    }

    public OnPedidosActivityListener getListenerPedidosActivity() {
        return listenerPedidosActivity;
    }
    public void setListenerPedidosActivity(OnPedidosActivityListener listenerPedidosActivity) {
        this.listenerPedidosActivity = listenerPedidosActivity;
    }

    public OnDetalleActivityListener getListenerDetalleActivity() {
        return listenerDetalleActivity;
    }
    public void setListenerDetalleActivity(OnDetalleActivityListener listenerDetalleActivity) {
        this.listenerDetalleActivity = listenerDetalleActivity;
    }

    public OnLoginActivityListener getListenerLoginActivity() {
        return listenerLoginActivity;
    }
    public void setListenerLoginActivity(OnLoginActivityListener listenerLoginActivity) {
        this.listenerLoginActivity = listenerLoginActivity;
    }

    public OnRegisterActivityListener getListenerRegisterActivity() {
        return listenerRegisterActivity;
    }
    public void setListenerRegisterActivity(OnRegisterActivityListener listenerRegisterActivity) {
        this.listenerRegisterActivity = listenerRegisterActivity;
    }

    //metodos de esta clase, en vez de devolver objetos, utilizamos el patron delegado, las peticiones al hacerse en
    //segundo plano, no controlamos cuando acaban, por lo que utilizamos un listener para avisar a nuestros controladores
    //de cuando su peticion ya se ha realizado

    //METODOS DE: HomeFragment
    public void cargarSugerencias(){
        ArrayList<Producto> productos = new ArrayList<>();
        mibase = FirebaseFirestore.getInstance();

        //obtenemos los productos, cuyo campo en sugerencias sea si
        mibase.collection("productos")
                .whereEqualTo("sugerencias", "si")
                .whereEqualTo("eliminado", "no")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Map<String, Object> data = documentSnapshot.getData();
                            String nombre = data.get("name").toString();
                            double precio = Double.valueOf(data.get("precio").toString());
                            String url = data.get("imgURL").toString();
                            String id = documentSnapshot.getId();
                            String tipo = data.get("tipo").toString();
                            Producto producto = new Producto(id, nombre, precio, url, tipo);
                            productos.add(producto);
                        }
                        listenerHomeFragment.onListReceive(productos);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e.getMessage());
                    }
                });
    }
    public void cargarNovedades(){
        ArrayList<Producto> productos = new ArrayList<>();
        mibase = FirebaseFirestore.getInstance();

        mibase.collection("productos")
                .whereEqualTo("eliminado", "no")
                .orderBy("fecha", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Map<String, Object> data = documentSnapshot.getData();
                            String nombre = data.get("name").toString();
                            double precio = Double.valueOf(data.get("precio").toString());
                            String url = data.get("imgURL").toString();
                            String id = documentSnapshot.getId();
                            String tipo = data.get("tipo").toString();
                            Producto producto = new Producto(id, nombre, precio, url, tipo);
                            productos.add(producto);
                        }
                        //cuando se han creado todos los objetos y añadido a nuestro arraylist utilizamos
                        //nuestro listener propio para avisar a nuestra actividad
                        listenerHomeFragment.onListReceive(productos);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    public void cargarTopVentas(){
        ArrayList<Producto> productos = new ArrayList<>();
        mibase = FirebaseFirestore.getInstance();

        //obtenemos las top ventas de la siguiente forma:
        //hacemos una consulta a nuestra tabla productos, primero le decimos que nos lo ordene de mayor a menor
        //por el campo veces_pedido(para saber cuales fueron los productos que mas se compran)
        //y que solo nos muestre 10 productos
        mibase.collection("productos")
                .whereEqualTo("eliminado", "no")
                .orderBy("veces_pedido", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Map<String, Object> data = documentSnapshot.getData();
                            String nombre = data.get("name").toString();
                            double precio = Double.valueOf(data.get("precio").toString());
                            String url = data.get("imgURL").toString();
                            String id = documentSnapshot.getId();
                            String tipo = data.get("tipo").toString();
                            Producto producto = new Producto(id, nombre, precio, url, tipo);
                            productos.add(producto);
                        }
                        listenerHomeFragment.onListReceive(productos);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    //METODOS DE: RealizarPedidoActivity
    public void obtenerProductos(String familia){
        ArrayList<Producto> productos = new ArrayList<>();
        mibase = FirebaseFirestore.getInstance();

        //obtener los productos bocadillos
        mibase.collection("productos")
                .whereEqualTo("eliminado", "no")
                .whereEqualTo("tipo", familia)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Map<String, Object> data = documentSnapshot.getData();
                            String nombre = data.get("name").toString();
                            double precio = Double.valueOf(data.get("precio").toString());
                            String url = data.get("imgURL").toString();
                            String id = documentSnapshot.getId();
                            String tipo = data.get("tipo").toString();
                            Producto producto = new Producto(id, nombre, precio, url, tipo);
                            productos.add(producto);
                        }
                        listenerRealizarPedidoActivity.onTipoProductoReceive(productos);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
    public void insertarBdd(ArrayList<LineasPedido> lineasPedidos){
        mibase = FirebaseFirestore.getInstance();

        //Subimos a la tabla pedidos, para posteriormente obtener su id y subir las lineas pedido a su tabla
        Map<String, Object> datos = new HashMap<>();
        //obtener la fecha
        Date fechaActual = Calendar.getInstance().getTime();
        datos.put("fecha", fechaActual);
        //obtener el usuario con el patron singleton
        GuardarUser.getGuardarUser(null).leerFichero();
        String user = GuardarUser.getGuardarUser(null).devolverUser();

        datos.put("usuario", user);
        datos.put("estado", "En espera");

        mibase.collection("pedidos")
                .add(datos)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String documento = documentReference.getId().toString();
                        mibase = FirebaseFirestore.getInstance();
                        for(LineasPedido pedido: lineasPedidos){

                            Map<String, Object> datos = new HashMap<>();
                            datos.put("idProducto", pedido.getIdProducto());
                            datos.put("cantidad", pedido.getCantidad());
                            datos.put("nota", pedido.getNota());

                            //Añadimos las lineas a la tabla
                            mibase.collection("pedidos")
                                    .document(documento)
                                    .collection("lineas")
                                    .add(datos)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            //cuando se inserten, lo mandamos a la actividad de ver los pedidos, para que vea
                                            //que ya está el suyo ahí, aquí si es necesario que espero puesto que si lo mandamos
                                            //a la actividad de ver los pedidos antes de que acabe la inserccion en firebase, puede
                                            //que no le salga.
                                            listenerRealizarPedidoActivity.onPedidoInsertadoListener();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        //recorremos todas las lineas
        for (LineasPedido pedido: lineasPedidos){
            //actualizamos las veces que se pidió un producto
            mibase.collection("productos")
                    .document(pedido.getIdProducto())
                    .update("veces_pedido", FieldValue.increment(1))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //cuando se acabe no hará nada puesto que no es necesario que el usuario espere a esto
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
    }

    //METODOS DE: FamiliasActivity
    public void cargarFamilias(){
        mibase = FirebaseFirestore.getInstance();
        ArrayList<Familia> familias = new ArrayList<>();
        //obtener los productos bocadillos
        mibase.collection("familias")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Map<String, Object> data = documentSnapshot.getData();
                            String nombre = data.get("nombre").toString();
                            String info = data.get("info").toString();
                            String url = data.get("imgURL").toString();
                            String id = documentSnapshot.getId();
                            Familia familia = new Familia(id, nombre, info, url);
                            familias.add(familia);
                        }
                        listenerFamiliasActivity.onFamiliasReceive(familias);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    //METODOS DE: PedidosActivity
    public void cargarPedidos(String consulta) {
        mibase = FirebaseFirestore.getInstance();
        ArrayList<Pedido> pedidos = new ArrayList<>();

        GuardarUser.getGuardarUser(null).leerFichero();
        String user = GuardarUser.getGuardarUser(null).devolverUser();

        mibase.collection("pedidos")
                .orderBy("fecha", Query.Direction.DESCENDING)
                .whereEqualTo("estado", consulta)
                .whereEqualTo("usuario", user)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Map<String, Object> data = documentSnapshot.getData();
                            //fecha
                            Timestamp timestamp = documentSnapshot.getTimestamp("fecha");
                            // Crear un objeto de fecha a partir del valor de timestamp
                            Date date = timestamp.toDate();
                            // Crear un objeto SimpleDateFormat para el formato deseado
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                            // Formatear la fecha como una cadena en el formato deseado
                            String fecha = sdf.format(date);

                            String estado = data.get("estado").toString();
                            String usuario = data.get("usuario").toString();
                            String id = documentSnapshot.getId();
                            Pedido pedido = new Pedido(fecha, usuario, estado, id);
                            pedidos.add(pedido);
                        }
                        listenerPedidosActivity.onPedidosReceive(pedidos);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    //METODOS DE: DetalleActivity
    public void cargarInfo(String idPedido){
        mibase = FirebaseFirestore.getInstance();

        mibase.collection("pedidos")
                .document(idPedido)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> data = documentSnapshot.getData();
                        String id = documentSnapshot.getId().toString();
                        //fecha
                        Timestamp timestamp = documentSnapshot.getTimestamp("fecha");
                        // Crear un objeto de fecha a partir del valor de timestamp
                        Date date = timestamp.toDate();
                        // Crear un objeto SimpleDateFormat para el formato deseado
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        // Formatear la fecha como una cadena en el formato deseado
                        String fecha = sdf.format(date);
                        String usuario = data.get("usuario").toString();
                        String estado = data.get("estado").toString();
                        //Pedido que vamos a devolver
                        Pedido pedido = new Pedido(fecha, usuario, estado, id);

                        mibase = FirebaseFirestore.getInstance();
                        ArrayList<LineasPedido> lineas = new ArrayList<>();

                        //obtenemos las lineas asociadas a ese pedido
                        mibase.collection("pedidos")
                                .document(idPedido)
                                .collection("lineas")
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            Map<String, Object> data = documentSnapshot.getData();
                                            int cantidad = Integer.valueOf(data.get("cantidad").toString());
                                            String idProducto = data.get("idProducto").toString();
                                            String nota = data.get("nota").toString();

                                            LineasPedido lineaPedido = new LineasPedido(idProducto, cantidad, nota);
                                            lineas.add(lineaPedido);
                                        }
                                        listenerDetalleActivity.onInfoReceive(pedido, lineas);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
    public void calcularTotal(ArrayList<LineasPedido> lineas){
        mibase = FirebaseFirestore.getInstance();
        for(LineasPedido linea: lineas){
            mibase.collection("productos")
                    .document(linea.getIdProducto())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            double total = 0;
                            if (documentSnapshot.exists()) {
                                Map<String, Object> data = documentSnapshot.getData();
                                double precio = Double.valueOf(data.get("precio").toString());
                                total = total + (precio * linea.getCantidad());
                                listenerDetalleActivity.onTotalCalculated(total);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        }

    }
    public void eliminarPedido(String idPedido){
        mibase = FirebaseFirestore.getInstance();
        //eliminamos de la tabla pedidos
        mibase.collection("pedidos")
                .document(idPedido)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listenerDetalleActivity.onEliminatePedido();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    //METODOS DE: LoginActivity
    public void comprobarUsuario(String user, String password){
        mibase = FirebaseFirestore.getInstance();
        mibase.collection("usuarios")
                .document(user)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // El documento existe
                            Map<String, Object> data = documentSnapshot.getData();
                            String passCorrecta = data.get("password").toString();
                            if(passCorrecta.equals(password)){
                                GuardarUser.getGuardarUser(null).borrarFichero();
                                GuardarUser.getGuardarUser(null).guardarFichero(user, password);
                                Fcm fcm = new Fcm();
                                fcm.guardarToken(user);
                                listenerLoginActivity.onComprobarUsuario(true);
                            }
                            else{
                                listenerLoginActivity.onComprobarUsuario(false);
                            }
                        } else {
                            listenerLoginActivity.onComprobarUsuario(false);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    //METODOS DE: RegisterActivity
    public void comprobarUsuario(String user) {
        mibase = FirebaseFirestore.getInstance();
        mibase.collection("usuarios")
                .document(user)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        //usuario existente
                        if (documentSnapshot.exists()) {
                            listenerRegisterActivity.onComprobarUsuario(true);
                        } else {
                            listenerRegisterActivity.onComprobarUsuario(false);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
    public void registrarUsuario(String user, String password){
        mibase = FirebaseFirestore.getInstance();

        //usuario no existente, lo damos de alta
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("password", password);
        usuario.put("telefono", "no tiene");

        mibase.collection("usuarios")
                .document(user)
                .set(usuario)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listenerRegisterActivity.onUsuarioRegistrado();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
    public void registrarUsuarioConTelefono(String user, String password, String telefono){
        mibase = FirebaseFirestore.getInstance();

        //usuario no existente, lo damos de alta
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("password", password);
        usuario.put("telefono", telefono);

        mibase.collection("usuarios")
                .document(user)
                .set(usuario)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listenerRegisterActivity.onUsuarioRegistradoConTelefono();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
}
