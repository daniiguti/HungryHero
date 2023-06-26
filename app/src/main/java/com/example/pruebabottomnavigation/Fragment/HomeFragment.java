package com.example.pruebabottomnavigation.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pruebabottomnavigation.Adapter.RecyclerAdapterProductos;
import com.example.pruebabottomnavigation.Modelos.Producto;
import com.example.pruebabottomnavigation.R;
import com.example.pruebabottomnavigation.Servicios.FirestorePeticiones;
import com.example.pruebabottomnavigation.Servicios.OnHomeFragmentListener;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private ArrayList<Producto> productosSugerencias;
    private RecyclerView recyclerViewSugerencias;
    private RecyclerAdapterProductos recAdapterSugerencias;

    private ArrayList<Producto> productosNovedades;
    private RecyclerView recyclerViewNovedades;
    private RecyclerAdapterProductos recAdapterNovedades;

    private ArrayList<Producto> productosTopVentas;
    private RecyclerView recyclerViewTopVentas;
    private RecyclerAdapterProductos recAdapterTopVentas;

    private FirestorePeticiones firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        productosSugerencias = new ArrayList<>();
        recAdapterSugerencias = new RecyclerAdapterProductos(productosSugerencias, getContext());
        recyclerViewSugerencias = (RecyclerView) rootView.findViewById(R.id.recyclerViewSugerencias);
        recyclerViewSugerencias.setAdapter(recAdapterSugerencias);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewSugerencias.setLayoutManager(layoutManager);

        productosNovedades = new ArrayList<>();
        recAdapterNovedades = new RecyclerAdapterProductos(productosNovedades, getContext());
        recyclerViewNovedades = (RecyclerView) rootView.findViewById(R.id.recyclerViewNovedades);
        recyclerViewNovedades.setAdapter(recAdapterNovedades);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewNovedades.setLayoutManager(layoutManager2);

        productosTopVentas = new ArrayList<>();
        recAdapterTopVentas = new RecyclerAdapterProductos(productosTopVentas, getContext());
        recyclerViewTopVentas = (RecyclerView) rootView.findViewById(R.id.recyclerViewTopVentas);
        recyclerViewTopVentas.setAdapter(recAdapterTopVentas);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewTopVentas.setLayoutManager(layoutManager3);

        cargarSugerencias();
        cargarNovedades();
        cargarTopVentas();

        return rootView;
    }

    private void showToast(String mensaje) {
        Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
    }

    private void cargarSugerencias(){
        firestore = new FirestorePeticiones();
        firestore.cargarSugerencias();
        firestore.setListenerHomeFragment(new OnHomeFragmentListener() {
            @Override
            public void onListReceive(ArrayList<Producto> productos) {
                for(Producto p: productos){
                    recAdapterSugerencias.insertar(p);
                }
            }
        });
    }
    private void cargarNovedades(){
        firestore = new FirestorePeticiones();
        firestore.cargarNovedades();
        firestore.setListenerHomeFragment(new OnHomeFragmentListener() {
            @Override
            public void onListReceive(ArrayList<Producto> productos) {
                for(Producto p: productos){
                    recAdapterNovedades.insertar(p);
                }
            }
        });
    }
    private void cargarTopVentas(){
        firestore = new FirestorePeticiones();
        firestore.cargarTopVentas();
        firestore.setListenerHomeFragment(new OnHomeFragmentListener() {
            @Override
            public void onListReceive(ArrayList<Producto> productos) {
                for(Producto p: productos){
                    recAdapterTopVentas.insertar(p);
                }
            }
        });
    }

}