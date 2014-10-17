package com.atsistemas.beans;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import oracle.adf.model.BindingContext;
import oracle.binding.BindingContainer;

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
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Mensaje de info");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }   
    
}
