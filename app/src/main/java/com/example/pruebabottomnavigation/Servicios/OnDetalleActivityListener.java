package com.example.pruebabottomnavigation.Servicios;

import com.example.pruebabottomnavigation.Modelos.LineasPedido;
import com.example.pruebabottomnavigation.Modelos.Pedido;

import java.util.ArrayList;

public interface OnDetalleActivityListener {
    void onInfoReceive(Pedido pedido, ArrayList<LineasPedido> lineas);
    void onTotalCalculated(double total);
    void onEliminatePedido();
}
