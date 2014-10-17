 function handleTableDoubleClick(evt){             
    var table = evt.getSource();
    AdfCustomEvent.queue(table, "TableDoubleClickEvent",{}, true); 
    evt.cancel();
 }