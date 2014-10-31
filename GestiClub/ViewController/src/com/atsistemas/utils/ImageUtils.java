package com.atsistemas.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.faces.context.FacesContext;

import javax.servlet.http.HttpSession;

import oracle.jbo.Row;

import org.apache.commons.io.FilenameUtils;
import org.apache.myfaces.trinidad.model.UploadedFile;

public class ImageUtils {
    
    public final static String REPOSITORIO_IMAGES = "C:/catalogoEquipos/";
    public final static String DEFAULT_AVATAR = "default_avatar.png";
    
    public ImageUtils() {
        super();
    }
    
    public static String saveFoto(Row row, String fotoTemp) {
        String rutaDir;
        String rutaFile;
        
        rutaDir = REPOSITORIO_IMAGES + row.getAttribute("Equipo");
        rutaFile = rutaDir + "/" + row.getAttribute("Id") + "." + FilenameUtils.getExtension(fotoTemp);
        //guardamos la foto en disco
        OutputStream fileFoto = null;
        InputStream isFoto = null;
        File dirEquipo;
        File fichero;
        
        try {
            
            isFoto = new FileInputStream(fotoTemp);

            if(isFoto != null){
                dirEquipo = new File(rutaDir);
                fichero = new File(rutaFile);
                
                //Se crea el directorio
                if(!dirEquipo.isDirectory()){
                    dirEquipo.mkdir();                
                }
                
                //creamos el fichero
                if(!fichero.exists()){
                    fichero.createNewFile();   
                }
                
                fileFoto = new FileOutputStream(fichero);                
                copy(isFoto, fileFoto);
            }            

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(fileFoto != null){
                    fileFoto.close();   
                }
                if(isFoto != null){
                    isFoto.close();   
                }
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return rutaFile;
    }
    
    public static File getImage(String path){

        File image = new File(path);
        
        if(image.exists()){
            return image;   
        }else{
            image = new File(REPOSITORIO_IMAGES + DEFAULT_AVATAR);
            return image;
        }        
    }
    
    public static void copy(InputStream in, OutputStream out) {
        try {

            int bytes = -1;
            while ((bytes = in.read()) > -1) {
                out.write(bytes);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
}
