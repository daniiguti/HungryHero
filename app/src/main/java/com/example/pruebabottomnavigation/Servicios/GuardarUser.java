package com.example.pruebabottomnavigation.Servicios;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class GuardarUser {
    //instancia de la propia clase (patron singleton)
    private static GuardarUser gu;
    //Variables necesarias
    private String user;
    private String pass;

    private File f;

    //Constructor
    private GuardarUser(File ruta){
        f = new File(ruta.getAbsolutePath(), "usuarios.txt");
        this.user = "";
        this.pass = "";
    }
    //patron singleton, se crea una clase sino la habia creado antes, y si ya estaba creada, devuelve esta
    public static GuardarUser getGuardarUser(File ruta) {
        if (gu == null) {
            gu = new GuardarUser(ruta);
        }
        return gu;
    }

    //Para leer el fichero
    public void leerFichero() {
        if(f.length() > 0) {
            this.user = "";
            this.pass = "";
            try {
                char charLeido = ' ';
                int intLeido = 0;
                InputStreamReader ir = new InputStreamReader(new FileInputStream(f));
                do {
                    intLeido = ir.read();
                    charLeido = (char) intLeido;
                    if (charLeido != ',') {
                        this.user = this.user + charLeido;
                    }
                } while (charLeido != ',');

                do {
                    intLeido = ir.read();
                    charLeido = (char) intLeido;
                    if (intLeido != -1) {
                        this.pass = this.pass + charLeido;
                    }
                } while (intLeido != -1);
                ir.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    //Para escribir en el fichero
    public void guardarFichero(String user, String pass){
        borrarFichero();
        try {
            OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(f));
            ow.write(user+",");
            ow.write(pass);
            ow.close();
        }catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    //Para limpiar el fichero
    public void borrarFichero(){
        try {
            OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(f));
            ow.write("");
            ow.close();
        }catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    //Para obtener el usuario y la contraseña
    public String devolverUser(){
        return this.user;
    }
    public String devolverPass(){
        return this.pass;
    }
}
