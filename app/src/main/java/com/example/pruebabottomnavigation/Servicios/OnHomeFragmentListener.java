package com.example.pruebabottomnavigation.Servicios;

import com.example.pruebabottomnavigation.Modelos.Producto;

import java.util.ArrayList;

/**
 * Listener propio, para avisar de cuando nos llega los datos, al hacerse en segundo plano necesitamos
 * el patron delegado
 */
public interface OnHomeFragmentListener {

    void onListReceive(ArrayList<Producto> productos);
}
