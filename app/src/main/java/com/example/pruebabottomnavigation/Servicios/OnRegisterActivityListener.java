package com.example.pruebabottomnavigation.Servicios;

public interface OnRegisterActivityListener {
    void onComprobarUsuario(boolean usuarioExiste);
    void onUsuarioRegistrado();
    void onUsuarioRegistradoConTelefono();
}
