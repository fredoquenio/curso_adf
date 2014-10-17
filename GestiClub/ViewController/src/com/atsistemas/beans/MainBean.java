package com.atsistemas.beans;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import oracle.adf.model.BindingContext;
import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.component.rich.output.RichMessage;
import oracle.adf.view.rich.event.DialogEvent;
import oracle.adf.view.rich.event.PopupCanceledEvent;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

public class MainBean {

       public MainBean() {
    }
    
    public BindingContainer getBindings() {
        if(BindingContext.getCurrent() != null){
            return BindingContext.getCurrent().getCurrentBindingsEntry();
        }
        else{
            return null;
        }
    }
   
    public void showInfoMessage(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, null, message);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    

   
}
