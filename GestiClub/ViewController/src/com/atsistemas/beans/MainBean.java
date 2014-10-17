package com.atsistemas.beans;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import oracle.adf.model.BindingContext;

import oracle.binding.BindingContainer;

public class MainBean {
         
    private String breadcrumbNivel1Text = "";
    private String breadcrumbNivel1NavigationText = "";
    
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
       
    public void setBreadcrumbNivel1Text(String breadcrumbNivel1Text) {
        this.breadcrumbNivel1Text = breadcrumbNivel1Text;
    }

    public String getBreadcrumbNivel1Text() {
        return breadcrumbNivel1Text;
    }

    public void setBreadcrumbNivel1NavigationText(String breadcrumbNivel1NavigationText) {
        this.breadcrumbNivel1NavigationText = breadcrumbNivel1NavigationText;
    }

    public String getBreadcrumbNivel1NavigationText() {
        return breadcrumbNivel1NavigationText;
    }
   
}
