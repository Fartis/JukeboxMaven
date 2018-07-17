/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.manuelvillalbaescamilla.applifyingjukebox;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;


import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Clase para gestionar las canciones del juego
 *
 * @author Manuel David Villalba Escamilla
 * @author Victor Manuel Gonzalez Rodriguez
 * @author Alberto Jose Gonzalez Rodriguez
 */
public class ReproductorMusica {

    private static ReproductorMusica singleton = null;
    private FileInputStream fis;
    private BufferedInputStream bis;
    private Player player;
    private boolean loop;
    private Thread musica;
    private int actualFrame = 0;
    private boolean pause = false;
    private File actualTrack;

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

    /**
     * Metodo playMusica, le mandas una url o dirección de un archivo de musica
     * mp3 y lo reproduce.
     *
     * @param urlMusica String de la direccion de la musica
     */
    public void playMusica(final String urlMusica) {
        loop = true;
        pause = false;
          
        musica = new Thread() {
            public void run() {
                try {
                    do {
                        actualTrack = new File(urlMusica);
                        fis = new FileInputStream(urlMusica);
                        bis = new BufferedInputStream(fis);
                        player = new Player(bis);
                        player.play();
                    } while (loop);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(ReproductorMusica.class.getName()).log(Level.SEVERE, null, ex);
                } catch (JavaLayerException ex) {
                    System.err.println(ex);
                    return;
                }
            }
        };

        musica.start();

    }
    
    /**
     * Metodo pararMusica, si esta reproduciendo un archivo de musica lo
     * interrumpe.
     */
    public void pararMusica() {
        if (player != null) {
            player.close();
            player = null;
            musica.stop(); //Utilizamos un metodo deprecado porque no hay alternativas.
        }
        loop = false;
        pause = false;
    }
    
    public synchronized int getActualTime(){
        try{
            return player.getPosition();
        } catch (NullPointerException e){
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


}