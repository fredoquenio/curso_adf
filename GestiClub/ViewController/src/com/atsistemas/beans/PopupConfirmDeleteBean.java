package com.atsistemas.beans;

import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.component.rich.output.RichMessage;
import oracle.adf.view.rich.event.DialogEvent;
import oracle.adf.view.rich.event.PopupCanceledEvent;

import oracle.binding.OperationBinding;

public class PopupConfirmDeleteBean extends MainBean{
    
    private RichPopup popupAvisoDelete;
    private RichMessage messageAvisoDelete;
    
    public PopupConfirmDeleteBean() {
    }
    
    public void cancelDelete(PopupCanceledEvent popupCanceledEvent) {
        getPopupAvisoDelete().hide();
        OperationBinding operationBinding = this.getBindings().getOperationBinding("Rollback");
        operationBinding.execute(); 
    }

    public void confirmDelete(DialogEvent dialogEvent) {
        if(DialogEvent.Outcome.yes.equals(dialogEvent.getOutcome())){
            OperationBinding operationBinding = this.getBindings().getOperationBinding("Delete");
            operationBinding.execute(); 
            if(operationBinding.getErrors() == null || operationBinding.getErrors().isEmpty()){
                operationBinding = this.getBindings().getOperationBinding("Commit");
                operationBinding.execute();
            }    
        }
    }
          
    public void setPopupAvisoDelete(RichPopup popupAvisoDelete) {
        this.popupAvisoDelete = popupAvisoDelete;
    }

    public RichPopup getPopupAvisoDelete() {
        return popupAvisoDelete;
    }

    public void setMessageAvisoDelete(RichMessage messageAvisoDelete) {
        this.messageAvisoDelete = messageAvisoDelete;
    }

    public RichMessage getMessageAvisoDelete() {
        return messageAvisoDelete;
    }
    
    
}
