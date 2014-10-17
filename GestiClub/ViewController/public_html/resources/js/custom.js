 function handleTableDoubleClick(evt){             
    var table = evt.getSource();    
    if (table.getSelectedRowKeys() != null) {
        AdfCustomEvent.queue(table, "TableDoubleClickEvent",{}, true); 
    }
    evt.cancel();
 }