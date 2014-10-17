package com.atsistemas.beans;

import com.atsistemas.utils.JSFUtils;

import javax.faces.event.ActionEvent;

import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.event.DialogEvent;
import oracle.adf.view.rich.event.PopupCanceledEvent;

import oracle.binding.OperationBinding;


public class CompeticionBean extends MainBean{
    private RichPopup popupEditCompeticion;    
    private PopupConfirmDeleteBean popupConfirmDelete;

    public CompeticionBean() {
    }

   public void cancelEdicion(PopupCanceledEvent popupCanceledEvent) {
        OperationBinding operationBinding = this.getBindings().getOperationBinding("Rollback");
        operationBinding.execute();
    }
   
    public void commitCompeticion(DialogEvent dialogEvent) {
        if (DialogEvent.Outcome.ok.equals(dialogEvent.getOutcome())) {
            OperationBinding operationBinding = this.getBindings().getOperationBinding("Commit");
            operationBinding.execute();
        }
    }
    
    public void addCompeticion(ActionEvent actionEvent) {
        OperationBinding operationBinding =
            this.getBindings().getOperationBinding("CreateInsert");
        operationBinding.execute();

        getPopupEditCompeticion().show(new RichPopup.PopupHints());
    }
    
    public void editCompeticion(ActionEvent actionEvent) {
        getPopupEditCompeticion().show(new RichPopup.PopupHints());
    }
    
    public void deleteCompeticion(ActionEvent actionEvent) {
        popupConfirmDelete.getMessageAvisoDelete().setMessage(JSFUtils.getStringMessageFromBundle("message.confirm.delete.competicion"));
        popupConfirmDelete.getPopupAvisoDelete().show(new RichPopup.PopupHints()); 
    }
    
   //GETTERS Y SETTERS
   public void setPopupEditCompeticion(RichPopup popupEditCompeticion) {
       this.popupEditCompeticion = popupEditCompeticion;
   }

   public RichPopup getPopupEditCompeticion() {
       return popupEditCompeticion;
   }


    public void setPopupConfirmDelete(PopupConfirmDeleteBean popupConfirmDelete) {
        this.popupConfirmDelete = popupConfirmDelete;
    }

    public PopupConfirmDeleteBean getPopupConfirmDelete() {
        return popupConfirmDelete;
    }
}
