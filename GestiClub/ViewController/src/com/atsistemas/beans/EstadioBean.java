package com.atsistemas.beans;

import javax.faces.event.ActionEvent;

import oracle.adf.view.rich.component.rich.RichPopup;

import oracle.adf.view.rich.event.PopupCanceledEvent;

import oracle.adf.view.rich.render.ClientEvent;

import oracle.binding.OperationBinding;

public class EstadioBean extends MainBean {

    private RichPopup popupEstadio;

    public EstadioBean() {
    }

    public void saveEstadio(ActionEvent actionEvent) {
        OperationBinding operationBinding = super.getBindings().getOperationBinding("Commit");
        operationBinding.execute();
        super.showInfoMessage("Cambios guardados con éxito");
    }
    
    public void rollbackEstadio(ActionEvent actionEvent) {
        OperationBinding operationBinding = super.getBindings().getOperationBinding("Rollback");
        operationBinding.execute();
        super.showInfoMessage("Cambios no guardados");
    }

    public void editEstadio(ActionEvent actionEvent) {
       this.getPopupEstadio().show(new RichPopup.PopupHints());
    }
    
    public void addEstadio(ActionEvent actionEvent) {        
        OperationBinding operationBinding = super.getBindings().getOperationBinding("CreateInsert");
        operationBinding.execute();
        this.getPopupEstadio().show(new RichPopup.PopupHints());
    }

    public void deleteEstadio(ActionEvent actionEvent) {
        OperationBinding operationBinding = super.getBindings().getOperationBinding("Delete");
        operationBinding.execute();
    }
    
    public void closePopupEstadio(PopupCanceledEvent popupCanceledEvent) {
        OperationBinding operationBinding = super.getBindings().getOperationBinding("Rollback");
        operationBinding.execute();
        this.getPopupEstadio().hide();
    }

    public void handleTableDoubleClick(ClientEvent clientEvent) {
        this.getPopupEstadio().show(new RichPopup.PopupHints());
    }
    
    public void setPopupEstadio(RichPopup popupEstadio) {
        this.popupEstadio = popupEstadio;
    }

    public RichPopup getPopupEstadio() {
        return popupEstadio;
    }
   
}
