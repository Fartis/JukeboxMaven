/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.manuelvillalbaescamilla.Class;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Manuel
 */
public class Track {
    
    File fichero;
    String nombre, artista;
    int duracion;
    
    public Track(File fichero, String nombre, String artista, int duracion){
        this.fichero = fichero;
        this.nombre = nombre;
        this.artista = artista;
        this.duracion = duracion;
    }

    public Track(String url, String nombre, String artista, int duracion) throws IOException {
        this.fichero = new File(url);
        if(!this.fichero.exists()){
            throw new IOException("El fichero indicado no existe");
        }
        this.nombre = nombre;
        this.artista = artista;
        this.duracion = duracion;
    }   

    public File getFichero() {
        return fichero;
    }

    public void setFichero(File fichero) {
        this.fichero = fichero;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }
    
    
    
}
