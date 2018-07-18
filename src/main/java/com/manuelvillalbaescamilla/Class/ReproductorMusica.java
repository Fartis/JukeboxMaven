/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.manuelvillalbaescamilla.Class;
import com.manuelvillalbaescamilla.applifyingjukebox.ControladorPlaylist;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javazoom.jlgui.basicplayer.BasicController;


import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;


/**
 * Clase para gestionar las canciones del juego
 *
 * @author Manuel David Villalba Escamilla
 * @author Victor Manuel Gonzalez Rodriguez
 * @author Alberto Jose Gonzalez Rodriguez
 */
public class ReproductorMusica implements BasicPlayerListener {

    private static ReproductorMusica singleton = null;
    private FileInputStream fis;
    private BufferedInputStream bis;
    private BasicPlayer player;
    private boolean loop;
    private Thread musica;
    private int actualFrame = 0;
    private boolean pause = false;
    private File actualTrack;
    private long tiempoInicio = 0, tiempoPause = 0;
    private static ControladorPlaylist padre;

    private void ReproductorMusica() {
    }

    /**
     * Metodo getSingleton.
     *
     * @return ReproductorMusica
     */
    public static ReproductorMusica getSingleton() {
        if (singleton == null) {
            singleton = new ReproductorMusica();
        }
        return singleton;
    }
    
    public static void setControl(ControladorPlaylist padre){
        this.padre = padre;
    }
    

    /**
     * Metodo playMusica, le mandas una url o dirección de un archivo de musica
     * mp3 y lo reproduce.
     *
     * @param urlMusica String de la direccion de la musica
     */
    public void playMusica(final String urlMusica, int position) {
        int indice = position;
        try {
            actualTrack = new File(urlMusica);
            player = new BasicPlayer();
            player.addBasicPlayerListener(this);
            player.open(actualTrack);
            player.play();
            tiempoInicio = System.currentTimeMillis();
        } catch (BasicPlayerException ex) {
            Logger.getLogger(ReproductorMusica.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    /**
     * Metodo pararMusica, si esta reproduciendo un archivo de musica lo
     * interrumpe.
     */
    public void pararMusica() {
        if (player != null) {
            try {
                player.stop();
                player = null;
                tiempoInicio = 0;
            } catch (BasicPlayerException ex) {
                Logger.getLogger(ReproductorMusica.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public synchronized long getActualTime(){
        if(pause){
            return tiempoPause / 1000;
        }else if(tiempoInicio!=0){
            return (System.currentTimeMillis() - tiempoInicio) / 1000;
        }else{
            return 0;
        }
    }
    
    public String[] getInfo(){
        String[] info = new String[5];
        
        try {

            InputStream input = new FileInputStream(actualTrack);
            ContentHandler handler = new DefaultHandler();
            Metadata metadata = new Metadata();
            Parser parser = new Mp3Parser();
            ParseContext parseCtx = new ParseContext();
            parser.parse(input, handler, metadata, parseCtx);
            input.close();

            // List all metadata
            String[] metadataNames = metadata.names();

            info[0] = metadata.get("title");
            info[1] = metadata.get("xmpDM:artist");
            info[2] = metadata.get("xmpDM:composer");
            info[3] = metadata.get("xmpDM:genre");
            info[4] = metadata.get("xmpDM:album");


            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            } catch (SAXException | TikaException e) {
                e.printStackTrace();
            }
        return info;
    }

    public void pauseMusica() {
        try {
            if(pause){
                tiempoInicio = System.currentTimeMillis() - tiempoPause;
                pause = false;
                player.resume();
            }else{
                tiempoPause = System.currentTimeMillis() - tiempoInicio;
                pause = true;
                player.pause();
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    @Override
    public void opened(Object stream, Map properties) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void progress(int arg0, long arg1, byte[] arg2, Map arg3) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stateUpdated(BasicPlayerEvent event) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        if(event.getCode()==-1){
            padre.reproducirSiguiente();
        }
        System.out.println(event.toString());
    }

    @Override
    public void setController(BasicController controller) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }



}