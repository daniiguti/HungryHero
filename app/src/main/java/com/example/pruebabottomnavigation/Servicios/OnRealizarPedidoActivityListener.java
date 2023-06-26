package com.example.pruebabottomnavigation.Servicios;

import com.example.pruebabottomnavigation.Modelos.Producto;

import java.util.ArrayList;

public interface OnRealizarPedidoActivityListener {
    void onTipoProductoReceive(ArrayList<Producto> productos);
    void onPedidoInsertadoListener();
}
