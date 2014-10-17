package com.atsistemas.beans;

import javax.faces.event.ActionEvent;

import oracle.binding.OperationBinding;

public class EstadioBean extends MainBean {
    
    public EstadioBean() {
    }

    public void saveEstadio(ActionEvent actionEvent) {
        OperationBinding operationBinding = getBindings().getOperationBinding("Commit");
        operationBinding.execute();
        showInfoMessage("Cambios guardados con éxito");
    }
    
    public void rollbackEstadio(ActionEvent actionEvent) {
        OperationBinding operationBinding = getBindings().getOperationBinding("Rollback");
        operationBinding.execute();
        showInfoMessage("Cambios no guardados");
    }
}
