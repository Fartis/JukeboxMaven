/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.manuelvillalbaescamilla.applifyingjukebox;

import com.manuelvillalbaescamilla.Class.ReproductorMusica;
import com.manuelvillalbaescamilla.Class.Track;
import com.manuelvillalbaescamilla.GUI.VistaReproductor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Header;

/**
 *
 * @author Manuel
 */
public class ControladorPlaylist {
    
    
    
    private static ArrayList<File> actualPlaylist = new ArrayList<>();
    private static ArrayList<String> urlActualPlaylist = new ArrayList<>();
    private static ControladorPlaylist singleton;
    private static Track actualTrack;
    private static int indice=-1;
    private static ReproductorMusica player = new ReproductorMusica();
    private static Timer chrono;
    private static VistaReproductor gui;
    private static int segundoChrono = 0;
    
    
    private static TimerTask taskChrono = new TimerTask() {
        @Override
        public void run() {
            int minuto = 0, segundo = 0;
            minuto = getDuration()/60;
            segundo = getDuration()-(minuto*60);
            int minutoActual = 0, segundoActual = 0;
            try{
                segundoChrono = (int)player.getActualTime();
            }catch (Exception e){
                segundoChrono = 0;
            }
            minutoActual = segundoChrono/60;
            segundoActual = segundoChrono - (minutoActual*60);
            gui.notificarProceso(String.format("%02d", minutoActual) + ":" + String.format("%02d",segundoActual) + " / " + String.format("%02d",minuto) + ":" + String.format("%02d",segundo), (int)player.getActualTime(), player.getInfo());
        }
    };
    
    
    public static void main(String[] args){
        gui = new VistaReproductor();
        gui.setVisible(true);
    }
    
    private ControladorPlaylist(){}
    
    
    public static ControladorPlaylist getSingleton(){
        if(singleton==null){
            singleton = new ControladorPlaylist();
        }
        return singleton;
    }
    
    public static DefaultListModel addTrack(File newTrack){
        DefaultListModel model = new DefaultListModel();
        
        urlActualPlaylist.add(newTrack.getPath());
        actualPlaylist.add(newTrack);
        
        for (File track : actualPlaylist) {
            model.addElement(track.getName());
        }
            
        return model;
    }
    
    public DefaultListModel addTracks(File[] files) {
        
        for (File file : files){
            urlActualPlaylist.add(file.getPath());
            actualPlaylist.add(file);
        }
        
        DefaultListModel model = new DefaultListModel();
        
        for (File track : actualPlaylist) {
            model.addElement(track.getName());
        }
            
        return model;
        
    }
    
    public static void playTrack(int position){
        try{
            player.pararMusica();
        }catch (Exception e){}
        File fichero = actualPlaylist.get(position);
        actualTrack = new Track(fichero, fichero.getName(), "", getDuration(fichero.getPath()));
        indice = position;
        player.playMusica(actualTrack.getFichero().getPath(), position);
        if(chrono == null) chrono = new Timer();
        chrono.scheduleAtFixedRate(taskChrono, 0, 1000);
    }
    
    public static void stopTrack(){
        player.pararMusica();
    }
    
    public static int getDuration(String filename){
        Header h= null;
        FileInputStream file = null;
        try {
            file = new FileInputStream(filename);
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
        }
        Bitstream bitstream = new  Bitstream(file);
        try {
            h = bitstream.readFrame();

        } catch (BitstreamException ex) {
            System.err.println(ex);
        }
        int size = h.calculate_framesize();
        float ms_per_frame = h.ms_per_frame();
        int maxSize = h.max_number_of_frames(10000);
        float t = h.total_ms(size);
        long tn = 0;
        try {
            tn = file.getChannel().size();
        } catch (IOException ex) {
            System.err.println(ex);
        }
        //System.out.println("Chanel: " + file.getChannel().size());
        int min = h.min_number_of_frames(500);
        return (int) (h.total_ms((int) tn)/1000);
    }
    
    
    public static int getDuration(){
        Header h= null;
        FileInputStream file = null;
        try {
            file = new FileInputStream(actualTrack.getFichero());
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
        }
        Bitstream bitstream = new  Bitstream(file);
        try {
            h = bitstream.readFrame();

        } catch (BitstreamException ex) {
            System.err.println(ex);
        }
        int size = h.calculate_framesize();
        float ms_per_frame = h.ms_per_frame();
        int maxSize = h.max_number_of_frames(10000);
        float t = h.total_ms(size);
        long tn = 0;
        try {
            tn = file.getChannel().size();
        } catch (IOException ex) {
            System.err.println(ex);
        }
        //System.out.println("Chanel: " + file.getChannel().size());
        int min = h.min_number_of_frames(500);
        return (int) (h.total_ms((int) tn)/1000);
    }


    public void pauseTrack() {
        player.pauseMusica();
    }

    public void clearTrackList() {
        actualPlaylist.clear();
        urlActualPlaylist.clear();
    }
    
    public void reproducirSiguiente(){
        stopTrack();
        try{
            playTrack(indice+1);
        }catch(ArrayIndexOutOfBoundsException e){
            playTrack(0);
        }
    }

    
    
}
