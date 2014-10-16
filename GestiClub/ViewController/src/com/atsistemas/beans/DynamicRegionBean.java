package com.atsistemas.beans;

import oracle.adf.controller.TaskFlowId;

public class DynamicRegionBean {
    private String taskFlowBienvenidaId = "/WEB-INF/pageFlows/task-flow-bienvenida.xml#task-flow-bienvenida";
    private String taskFlowCompeticionId = "/WEB-INF/pageFlows/task-flow-competicion.xml#task-flow-competicion";
    private String taskFlowPartidosId = "/WEB-INF/pageFlows/task-flow-partidos.xml#task-flow-partidos";
    private String taskFlowEstadiosId = "/WEB-INF/pageFlows/task-flow-estadios.xml#task-flow-estadios";
    
    private String currentTF = "bienvenida";
    
    public DynamicRegionBean() {
    }

    public TaskFlowId getDynamicTaskFlowId() {
        
        String returnTF;
        
        if ("competicion".equals(currentTF)) {
            returnTF = taskFlowCompeticionId;
        } else if ("partidos".equals(currentTF)) {
            returnTF = taskFlowPartidosId;
        } else if ("estadios".equals(currentTF)) {
            returnTF = taskFlowEstadiosId;
        } else {
            returnTF = taskFlowBienvenidaId;
        }
        
        return TaskFlowId.parse(returnTF);
    }

    public void setCurrentTF(String currentTF) {
        this.currentTF = currentTF;
    }

    public String getCurrentTF() {
        return currentTF;
    }
}
