package com.example.pruebabottomnavigation.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.pruebabottomnavigation.Controladores.PedidosActivity;
import com.example.pruebabottomnavigation.R;


public class CestaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public CestaFragment() {

    }

    public static CestaFragment newInstance(String param1, String param2) {
        CestaFragment fragment = new CestaFragment();
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

    private Button btPedidosEnEspera;
    private Button btPedidosParaRecoger;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cesta, container, false);

        btPedidosEnEspera = (Button) rootView.findViewById(R.id.btPedidosEnEspera);
        btPedidosParaRecoger = (Button) rootView.findViewById(R.id.btPedidosRecoger);

        //si se pulsa un botón lo mandaremos al activity de los pedidos, con un extra, para saber
        //que consulta utilizar
        btPedidosEnEspera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), PedidosActivity.class);
                i.putExtra("consulta", "En espera");
                startActivity(i);
            }
        });
        btPedidosParaRecoger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), PedidosActivity.class);
                i.putExtra("consulta", "Para recoger");
                startActivity(i);
            }
        });

        return rootView;
    }
}