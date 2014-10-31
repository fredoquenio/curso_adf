package com.atsistemas.beans;


import com.atsistemas.utils.ADFUtils;
import com.atsistemas.utils.ImageUtils;
import com.atsistemas.utils.JSFUtils;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.InputStreamReader;

import java.io.OutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import javax.faces.event.ValueChangeEvent;

import oracle.adf.model.binding.DCIteratorBinding;

import oracle.adf.view.rich.component.rich.output.RichImage;

import org.apache.myfaces.trinidad.model.UploadedFile;

import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.event.DialogEvent;
import oracle.adf.view.rich.event.PopupCanceledEvent;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

import oracle.jbo.Row;

import org.apache.myfaces.trinidad.context.RequestContext;


public class JugadorBean extends MainBean {
    private RichPopup popupEditJugador;
    private PopupConfirmDeleteBean popupConfirmDelete;
    private UploadedFile _foto;
    private RichImage imagePreview;

    public UploadedFile getFoto() {
        return _foto;
    }

    public void setFoto(UploadedFile foto) {
        _foto = foto;
    }

    public void cancelEditJugador(PopupCanceledEvent popupCanceledEvent) {
        OperationBinding operationBinding =
            this.getBindings().getOperationBinding("Rollback");
        operationBinding.execute();
    }

    public void commitJugador(DialogEvent dialogEvent) {
        if (DialogEvent.Outcome.ok.equals(dialogEvent.getOutcome())) {
           Row row = getCurrentRow();

            String pathFoto = (String)row.getAttribute("Foto");
            //Sólo guardamos la foto si es de la temporal.
            if(pathFoto != null && !"".equals(pathFoto) && pathFoto.contains("\\tmp\\")){
               String ruta = ImageUtils.saveFoto(row, pathFoto);
               row.setAttribute("Foto", ruta);
           }
            OperationBinding operationBinding =
                this.getBindings().getOperationBinding("Commit");
            operationBinding.execute();
        }
    }

    public void addJugador(ActionEvent actionEvent) {
        OperationBinding operationBinding =
            this.getBindings().getOperationBinding("CreateInsert");
        operationBinding.execute();

        getPopupEditJugador().show(new RichPopup.PopupHints());
    }

    public void editJugador(ActionEvent actionEvent) {
        getPopupEditJugador().show(new RichPopup.PopupHints());
    }

    public void deleteJugador(ActionEvent actionEvent) {
        popupConfirmDelete.getMessageAvisoDelete().setMessage(JSFUtils.getStringMessageFromBundle("message.confirm.delete.jugador"));
        popupConfirmDelete.getPopupAvisoDelete().show(new RichPopup.PopupHints());
    }
    
    public void changeImageHandled(ValueChangeEvent valueChangeEvent) {
        
        File file = handleUpload((UploadedFile)valueChangeEvent.getNewValue());
        String path = file.getPath();        
        
        Row row = getCurrentRow();
        row.setAttribute("Foto", path);
        
        //renderizamos la imagen
        getImagePreview().setRendered(true);
        RequestContext.getCurrentInstance().addPartialTarget(getImagePreview().getParent()); 
    }
    
    public File handleUpload(UploadedFile myImageFile) {
        //get the temporary upload directory
        String fileUploadLoc =
            FacesContext.getCurrentInstance().getExternalContext().getInitParameter("org.apache.myfaces.trinidad.UPLOAD_TEMP_DIR");

        File destinationDir = new File(fileUploadLoc);
        boolean exists = destinationDir.exists();
        // Create upload directory if it does not exist.
        if (!exists) {
            destinationDir.mkdirs();
        }
        
        //create the file for your image file
        File destinationFile = new File(fileUploadLoc, myImageFile.getFilename());
        
        try {
            //copy contents to the file
            ImageUtils.copy(myImageFile.getInputStream(),
                      new FileOutputStream(destinationFile)); 

        } catch (IOException e) {
            FacesMessage message = new FacesMessage(e.getMessage());
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        return destinationFile;
    }

    /**********************************************
     *      GETTERS Y SETTERS                     *
     *********************************************/

    public void setPopupEditJugador(RichPopup popupEditJugador) {
        this.popupEditJugador = popupEditJugador;
    }

    public RichPopup getPopupEditJugador() {
        return popupEditJugador;
    }

    public void setPopupConfirmDelete(PopupConfirmDeleteBean popupConfirmDelete) {
        this.popupConfirmDelete = popupConfirmDelete;
    }

    public PopupConfirmDeleteBean getPopupConfirmDelete() {
        return popupConfirmDelete;
    }

    private Row getCurrentRow(){
        DCIteratorBinding dci = ADFUtils.findIterator("JugadorVOIterator");
        
        return dci.getCurrentRow();
    }

    public void setImagePreview(RichImage imagePreview) {
        this.imagePreview = imagePreview;
    }

    public RichImage getImagePreview() {
        return imagePreview;
    }
    
    public long getTime(){
        return System.currentTimeMillis();
    }
}
