package com.atsistemas.utils;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import javax.servlet.http.HttpServletRequest;

import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.logging.ADFLogger;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.component.rich.data.RichTreeTable;
import oracle.adf.view.rich.component.rich.input.RichInputListOfValues;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.event.ReturnPopupEvent;
import oracle.adf.view.rich.model.ListOfValuesModel;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

import oracle.jbo.JboException;
import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.uicli.binding.JUCtrlHierBinding;
import oracle.jbo.uicli.binding.JUCtrlHierNodeBinding;

import org.apache.myfaces.trinidad.model.CollectionModel;
import org.apache.myfaces.trinidad.model.RowKeySet;
import org.apache.myfaces.trinidad.model.RowKeySetImpl;
import org.apache.myfaces.trinidad.render.ExtendedRenderKitService;
import org.apache.myfaces.trinidad.util.Service;


/**
 * General useful static utilies for workign with JSF.
 */
public class JSFUtils {

    private static final ADFLogger log = ADFLogger.createADFLogger(JSFUtils.class);
    
    private static final String NO_RESOURCE_FOUND = "Missing resource: ";


    public static void launchNewPage(String url) {
        String newPage = url;
        FacesContext ctx = FacesContext.getCurrentInstance();
        ExtendedRenderKitService erks = Service.getService(ctx.getRenderKit(), ExtendedRenderKitService.class);
        erks.addScript(FacesContext.getCurrentInstance(), "window.open('" + newPage + "','_blank');");
    }


    /**
     * Method for taking a reference to a JSF binding expression and returning
     * the matching object (or creating it).
     * @param expression EL expression
     * @return Managed object
     */
    public static Object resolveExpression(String expression) {
        FacesContext facesContext = getFacesContext();
        Application app = facesContext.getApplication();
        ExpressionFactory elFactory = app.getExpressionFactory();
        ELContext elContext = facesContext.getELContext();
        ValueExpression valueExp = elFactory.createValueExpression(elContext, expression, Object.class);
        return valueExp.getValue(elContext);
    }


    public static String resolveRemoteUser() {
        FacesContext facesContext = getFacesContext();
        ExternalContext ectx = facesContext.getExternalContext();
        return ectx.getRemoteUser();
    }


    public static String resolveUserPrincipal() {
        FacesContext facesContext = getFacesContext();
        ExternalContext ectx = facesContext.getExternalContext();
        HttpServletRequest request = (HttpServletRequest)ectx.getRequest();
        return request.getUserPrincipal().getName();
    }


    public static Object resolveMethodExpression(String expression, Class returnType, Class[] argTypes,
                                                 Object[] argValues) {
        FacesContext facesContext = getFacesContext();
        Application app = facesContext.getApplication();
        ExpressionFactory elFactory = app.getExpressionFactory();
        ELContext elContext = facesContext.getELContext();
        MethodExpression methodExpression = elFactory.createMethodExpression(elContext, expression, returnType, argTypes);
        return methodExpression.invoke(elContext, argValues);
    }

    /**
     * Method for taking a reference to a JSF binding expression and returning
     * the matching Boolean.
     * @param expression EL expression
     * @return Managed object
     */
    public static Boolean resolveExpressionAsBoolean(String expression) {
        return (Boolean)resolveExpression(expression);
    }

    /**
     * Method for taking a reference to a JSF binding expression and returning
     * the matching String (or creating it).
     * @param expression EL expression
     * @return Managed object
     */
    public static String resolveExpressionAsString(String expression) {
        return (String)resolveExpression(expression);
    }

    /**
     * Convenience method for resolving a reference to a managed bean by name
     * rather than by expression.
     * @param beanName name of managed bean
     * @return Managed object
     */
    public static Object getManagedBeanValue(String beanName) {
        StringBuffer buff = new StringBuffer("#{");
        buff.append(beanName);
        buff.append("}");
        return resolveExpression(buff.toString());
    }

    /**
     * Method for setting a new object into a JSF managed bean
     * Note: will fail silently if the supplied object does
     * not match the type of the managed bean.
     * @param expression EL expression
     * @param newValue new value to set
     */
    public static void setExpressionValue(String expression, Object newValue) {
        FacesContext facesContext = getFacesContext();
        Application app = facesContext.getApplication();
        ExpressionFactory elFactory = app.getExpressionFactory();
        ELContext elContext = facesContext.getELContext();
        ValueExpression valueExp = elFactory.createValueExpression(elContext, expression, Object.class);

        //Check that the input newValue can be cast to the property type
        //expected by the managed bean.
        //If the managed Bean expects a primitive we rely on Auto-Unboxing
        Class bindClass = valueExp.getType(elContext);
        if (bindClass.isPrimitive() || bindClass.isInstance(newValue)) {
            valueExp.setValue(elContext, newValue);
        }
    }

    /**
     * Convenience method for setting the value of a managed bean by name
     * rather than by expression.
     * @param beanName name of managed bean
     * @param newValue new value to set
     */
    public static void setManagedBeanValue(String beanName, Object newValue) {
        StringBuffer buff = new StringBuffer("#{");
        buff.append(beanName);
        buff.append("}");
        setExpressionValue(buff.toString(), newValue);
    }


    /**
     * Convenience method for setting Session variables.
     * @param key object key
     * @param object value to store
     */

    public static void storeOnSession(String key, Object object) {
        FacesContext ctx = getFacesContext();
        Map sessionState = ctx.getExternalContext().getSessionMap();
        sessionState.put(key, object);
    }

    /**
     * Convenience method for getting Session variables.
     * @param key object key
     * @return session object for key
     */
    public static Object getFromSession(String key) {
        FacesContext ctx = getFacesContext();
        Map sessionState = ctx.getExternalContext().getSessionMap();
        return sessionState.get(key);
    }


    public static String getFromHeader(String key) {
        FacesContext ctx = getFacesContext();
        ExternalContext ectx = ctx.getExternalContext();
        return ectx.getRequestHeaderMap().get(key);
    }

    /**
     * Convenience method for getting Request variables.
     * @param key object key
     * @return session object for key
     */
    public static Object getFromRequest(String key) {
        FacesContext ctx = getFacesContext();
        Map sessionState = ctx.getExternalContext().getRequestMap();
        return sessionState.get(key);
    }

    /**
     * Pulls a String resource from the property bundle that
     * is defined under the application &lt;message-bundle&gt; element in
     * the faces config. Respects Locale
     * @param key string message key
     * @return Resource value or placeholder error String
     */
    public static String getStringFromBundle(String key) {
        ResourceBundle bundle = getBundle();
        return getStringSafely(bundle, key, null);
    }


    /**
     * Convenience method to construct a <code>FacesMesssage</code>
     * from a defined error key and severity
     * This assumes that the error keys follow the convention of
     * using <b>_detail</b> for the detailed part of the
     * message, otherwise the main message is returned for the
     * detail as well.
     * @param key for the error message in the resource bundle
     * @param severity severity of message
     * @return Faces Message object
     */
    public static FacesMessage getMessageFromBundle(String key, FacesMessage.Severity severity) {
        ResourceBundle bundle = getBundle();
        String summary = getStringSafely(bundle, key, null);
        String detail = getStringSafely(bundle, key + "_detail", summary);
        FacesMessage message = new FacesMessage(summary, detail);
        message.setSeverity(severity);
        return message;
    }
    
    public static String getStringMessageFromBundle(String key) {
             
        FacesContext ctx = getFacesContext();
        UIViewRoot uiRoot = ctx.getViewRoot();
        Locale locale = uiRoot.getLocale();
        ResourceBundle bundle;
        bundle = ResourceBundle.getBundle("com.atsistemas.view.MessageBundle", locale);

        String summary = getStringSafely(bundle, key, null);
        return summary;
    }

    public static void addFacesGlobalMessage(String msg, FacesMessage.Severity severity) {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage fm = new FacesMessage(severity, null, msg);
        context.addMessage(null, fm);
    }

    public static void addFacesGlobalListMessage(List msgs, FacesMessage.Severity severity) {
        if (!msgs.isEmpty()) {
            FacesContext context = FacesContext.getCurrentInstance();
            for (int i = 0; i < msgs.size(); i++) {
                FacesMessage fm = new FacesMessage(severity, null, (String)msgs.get(i));
                context.addMessage(null, fm);
            }
        }
    }

    /**
     * Add JSF info message.
     * @param msg info message string
     */
    public static void addFacesInformationMessage(String msg) {
        FacesContext ctx = getFacesContext();
        FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, "");
        ctx.addMessage(getRootViewComponentId(), fm);
    }

    /**
     * Add JSF error message.
     * @param msg error message string
     */
    public static void addFacesErrorMessage(String msg) {
        FacesContext ctx = getFacesContext();
        FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, "");
        ctx.addMessage(getRootViewComponentId(), fm);
    }


    public static void addFacesWarningMessage(String msg) {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_WARN, null, msg);
        context.addMessage(null, fm);
    }

    /**
     * Add JSF error message for a specific attribute.
     * @param attrName name of attribute
     * @param msg error message string
     */
    public static void addFacesErrorMessage(String attrName, String msg) {
        FacesContext ctx = getFacesContext();
        FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, attrName, msg);
        ctx.addMessage(getRootViewComponentId(), fm);
    }

    // Informational getters

    /**
     * Get view tipo of the view root.
     * @return view tipo of the view root
     */
    public static String getRootViewId() {
        return getFacesContext().getViewRoot().getViewId();
    }

    /**
     * Get component tipo of the view root.
     * @return component tipo of the view root
     */
    public static String getRootViewComponentId() {
        return getFacesContext().getViewRoot().getId();
    }

    /**
     * Get FacesContext.
     * @return FacesContext
     */
    public static FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }
    /*
    * Internal method to pull out the correct local
    * message bundle
    */


    private static ResourceBundle getBundle() {
        FacesContext ctx = getFacesContext();
        UIViewRoot uiRoot = ctx.getViewRoot();
        Locale locale = uiRoot.getLocale();
        ClassLoader ldr = Thread.currentThread().getContextClassLoader();
        return ResourceBundle.getBundle(ctx.getApplication().getMessageBundle(), locale, ldr);
    }

    /**
     * Get an HTTP Request attribute.
     * @param name attribute name
     * @return attribute value
     */
    public static Object getRequestAttribute(String name) {
        return getFacesContext().getExternalContext().getRequestMap().get(name);
    }

    /**
     * Set an HTTP Request attribute.
     * @param name attribute name
     * @param value attribute value
     */
    public static void setRequestAttribute(String name, Object value) {
        getFacesContext().getExternalContext().getRequestMap().put(name, value);
    }

    /**
     * Get an HTTP Session attribute.
     * @param name attribute name
     * @return attribute value
     */
    public static Object getSessionAttribute(String name) {
        return getFacesContext().getExternalContext().getSessionMap().get(name);
    }

    /**
     * Set an HTTP Session attribute.
     * @param name attribute name
     * @param value attribute value
     */
    public static void setSessionAttribute(String name, Object value) {
        getFacesContext().getExternalContext().getSessionMap().put(name, value);
    }

    /*
    * Internal method to proxy for resource keys that don't exist
    */


    private static String getStringSafely(ResourceBundle bundle, String key, String defaultValue) {
        String resource = null;
        try {
            resource = bundle.getString(key);
        } catch (MissingResourceException mrex) {
            if (defaultValue != null) {
                resource = defaultValue;
            } else {
                resource = NO_RESOURCE_FOUND + key;
            }
        }
        return resource;
    }

    /**
     * Locate an UIComponent in view root with its component tipo. Use a recursive way to achieve this.
     * @param id UIComponent tipo
     * @return UIComponent object
     */
    public static UIComponent findComponentInRoot(String id) {
        UIComponent component = null;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            UIComponent root = facesContext.getViewRoot();
            component = findComponent(root, id);
        }
        return component;
    }

    /**
     * Locate an UIComponent from its root component.
     * Taken from http://www.jroller.com/page/mert?entry=how_to_find_a_uicomponent
     * @param base root Component (parent)
     * @param id UIComponent tipo
     * @return UIComponent object
     */
    public static UIComponent findComponent(UIComponent base, String id) {
        if (id.equals(base.getId()))
            return base;

        UIComponent children = null;
        UIComponent result = null;
        Iterator childrens = base.getFacetsAndChildren();
        while (childrens.hasNext() && (result == null)) {
            children = (UIComponent)childrens.next();
            if (id.equals(children.getId())) {
                result = children;
                break;
            }
            result = findComponent(children, id);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    /**
     * Method to create a redirect URL. The assumption is that the JSF servlet mapping is
     * "faces", which is the default
     *
     * @param view the JSP or JSPX page to redirect to
     * @return a URL to redirect to
     */
    public static String getPageURL(String view) {
        FacesContext facesContext = getFacesContext();
        ExternalContext externalContext = facesContext.getExternalContext();
        String url = ((HttpServletRequest)externalContext.getRequest()).getRequestURL().toString();
        StringBuffer newUrlBuffer = new StringBuffer();
        newUrlBuffer.append(url.substring(0, url.lastIndexOf("faces/")));
        newUrlBuffer.append("faces");
        String targetPageUrl = view.startsWith("/") ? view : "/" + view;
        newUrlBuffer.append(targetPageUrl);
        return newUrlBuffer.toString();
    }

    /**
     * Create value binding for EL exression
     * @param expression
     * @return Object
     */
    public static Object getExpressionObjectReference(String expression) {
        FacesContext fc = FacesContext.getCurrentInstance();
        ELContext elctx = fc.getELContext();
        ExpressionFactory elFactory = fc.getApplication().getExpressionFactory();
        return elFactory.createValueExpression(elctx, expression, Object.class).getValue(elctx);
    }


    /**
     * Invokes an expression
     * @param expr
     * @param returnType
     * @param argTypes
     * @param args
     * @return
     */
    public static Object invokeMethodExpression(String expr, Class returnType, Class[] argTypes, Object[] args) {
        FacesContext fc = FacesContext.getCurrentInstance();
        ELContext elctx = fc.getELContext();
        ExpressionFactory elFactory = fc.getApplication().getExpressionFactory();
        MethodExpression methodExpr = elFactory.createMethodExpression(elctx, expr, returnType, argTypes);
        return methodExpr.invoke(elctx, args);
    }

    public static void invokeMethodExpression(String expr) {
        FacesContext fc = FacesContext.getCurrentInstance();
        ELContext elctx = fc.getELContext();
        ExpressionFactory elFactory = fc.getApplication().getExpressionFactory();
        Class[] paramsType = new Class[0];
        MethodExpression methodExpr = elFactory.createMethodExpression(elctx, expr, null, paramsType);
        methodExpr.invoke(elctx, null);
    }

    /**
     * Invoke an expression
     * @param expr
     * @param returnType
     * @param argType
     * @param argument
     * @return
     */
    public static Object invokeMethodExpression(String expr, Class returnType, Class argType, Object argument) {
        return invokeMethodExpression(expr, returnType, new Class[] { argType }, new Object[] { argument });
    }
    
    public static void invokeVoidOperationBindingWithParams(BindingContainer bindings, String method, Map params) {
        OperationBinding operationBinding = bindings.getOperationBinding(method);

        for (Object value : params.keySet()) {
            operationBinding.getParamsMap().put(value, params.keySet());
        }

        operationBinding.execute();
        if (!operationBinding.getErrors().isEmpty()) {
            JSFUtils.addFacesGlobalListMessage(operationBinding.getErrors(), FacesMessage.SEVERITY_ERROR);
        }

    }

    public static String invokeActionMethod(BindingContainer bindings, String method) {
        OperationBinding operationBinding = bindings.getOperationBinding(method);
        Object result = operationBinding.execute();
        if (!operationBinding.getErrors().isEmpty()) {
            return null;
        }
        if (result != null) {
            return result.toString();
        }
        return null;
    }

    public static void execute_JScript(String script) {
        ExtendedRenderKitService service =
            Service.getRenderKitService(FacesContext.getCurrentInstance(), ExtendedRenderKitService.class);
        service.addScript(FacesContext.getCurrentInstance(), script);
    }

    public static void addRefreshComponent(UIComponent component) {
        if(component != null){
            AdfFacesContext.getCurrentInstance().addPartialTarget(component);
        }
    }
    
    public static void clearSelectionTable(RichTable table) 
    {
        if (table != null) 
        {   
            table.setActiveRowKey(null);
            if (table.getSelectedRowKeys() != null) {
                table.getSelectedRowKeys().clear();
            }
        }
    }
    
    public static void setSelectionTable(RichTable table, String rowAttribute, Object rowId) {
        if (table != null) {
            CollectionModel model = (CollectionModel)table.getValue();
            if (model != null) {
                try {
                    int rowCount = model.getRowCount();
                    boolean encontrado = false;
                    JUCtrlHierNodeBinding rowdata = null;
                    Row row = null;
                    RowKeySetImpl newSelection = new RowKeySetImpl();
                    for (int i = 0; i < rowCount && !encontrado; i++) {
                        model.setRowIndex(i);
                        rowdata = (JUCtrlHierNodeBinding)model.getRowData(i);
                        if (rowdata != null) {
                            row = rowdata.getRowAtRangeIndex(i);
                            if (row != null && rowId.equals(row.getAttribute(rowAttribute))) {
                                encontrado = true;
                                newSelection.add(model.getRowKey());
                                table.setSelectedRowKeys(newSelection);
                            }
                        }
                    }
                } 
                catch (Exception e) {
                    // NO SE SELECCIONA LA TABLA...
                }
            }
        }
    }
    
    public static void setSelectionTable(RichTable table, List<String> rowAttributes, Object rowId) {
        if (table != null) {
            CollectionModel model = (CollectionModel)table.getValue();
            if (model != null) {
                try {
                    int rowCount = model.getRowCount();
                    boolean seleccionado = false;
                    boolean encontrado = true;
                    JUCtrlHierNodeBinding rowdata = null;
                    Row row = null;
                    RowKeySetImpl newSelection = new RowKeySetImpl();
                    for (int i = 0; i < rowCount && !seleccionado; i++) {
                        model.setRowIndex(i);
                        rowdata = (JUCtrlHierNodeBinding)model.getRowData(i);
                        if (rowdata != null) {
                            row = rowdata.getRowAtRangeIndex(i);
                            for (int j = 0; j < rowAttributes.size() && encontrado; j++){
                                encontrado = row != null && rowId.equals(row.getAttribute(rowAttributes.get(j)));
                            }
                            if(encontrado){
                                newSelection.add(model.getRowKey());
                                table.setSelectedRowKeys(newSelection);
                                seleccionado = true;
                            }
                        }
                    }
                } 
                catch (Exception e) {
                    // NO SE SELECCIONA LA TABLA...
                }
            }
        }
    }
    
    public static void clearSelectionTreeTable(RichTreeTable treeTable) {
        if (treeTable != null && treeTable.getSelectedRowKeys() != null && !treeTable.getSelectedRowKeys().isEmpty()) {
            treeTable.getSelectedRowKeys().clear();
            treeTable.setActiveRowKey(null);
        }
    }

    public static String controlCapitalizeCosteNutrienteObjetivo(String objetivo) {
        String result = null;
        if (objetivo != null) {
            String[] parts = objetivo.split("_");
            for (int i = 0; i<parts.length; i++) {
                if(result == null){
                    result = "";
                }
                result = result.concat(parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1).toLowerCase());
            }
        }
        return result;
    }
    
    public static String controlCapitalizeObjetivoPlato(String objetivo) {
        String result = "Coste";
        if (objetivo != null) {
            result = objetivo.substring(0, 1).toUpperCase() + objetivo.substring(1).toLowerCase();
        }
        return result;
    }
    
     
    
    /**
     * Método para comprobar si se han producido errores despues del "execute" de una operación
     * @param operationBinding operación a realizar
     * @return booleano que indica si hay error
     */
    public static boolean isOperationErrors(OperationBinding operationBinding)
    {
        String error = null;
        if (!operationBinding.getErrors().isEmpty()) {
            JboException e = (JboException)operationBinding.getErrors().get(0);
            log.severe("###### ERROR COMPROBANDO OPERATIONBINDING #######", e);
            if(e.getBaseMessage() != null && !"".equals(e.getBaseMessage())){
                error = e.getBaseMessage();
            }
            if(e.getMessage() != null && !"".equals(e.getMessage())){
                if(error != null){
                    error += ". " + e.getMessage();
                }
                else{
                    error = e.getMessage();
                }
            }
            if(error == null || "".equals(error)){
                error = "Se ha producido un error inesperado. Consulte con el administrador.";
            }
            JSFUtils.addFacesErrorMessage(error);
        }
        return (error != null);
    }
    
    /**
     * Método para comprobar si se han producido errores despues del "execute" de una operación
     * @param operationBinding operación a realizar
     * @return booleano que indica si hay error
     */
    public static String isOperationErrorsPopUp(OperationBinding operationBinding)
    {
        String error = null;
        if (!operationBinding.getErrors().isEmpty()) {
            JboException e = (JboException)operationBinding.getErrors().get(0);
            log.severe("###### ERROR COMPROBANDO OPERATIONBINDING #######", e);
            if(e.getBaseMessage() != null && !"".equals(e.getBaseMessage())){
                error = e.getBaseMessage();
            }
            if(e.getMessage() != null && !"".equals(e.getMessage())){
                if(error != null){
                    error += ". " + e.getMessage();
                }
                else{
                    error = e.getMessage();
                }
            }
            if(error == null || "".equals(error)){
                error = "Se ha producido un error inesperado. Consulte con el administrador.";
            }
        }
        return error;
    }
    
        
    public static Row getSelectedRowFromInputLOV(ReturnPopupEvent returnPopupEvent) 
    {
        //access UI component instance from return event
        RichInputListOfValues lovField = (RichInputListOfValues)returnPopupEvent.getSource();
        
        //The LOVModel gives us access to the Collection Model and 
        //ADF tree binding used to populate the lookup table
        ListOfValuesModel lovModel =  lovField.getModel();
        CollectionModel collectionModel = lovModel.getTableModel().getCollectionModel();   
        
        //The collection model wraps an instance of the ADF 
        //FacesCtrlHierBinding, which is casted to JUCtrlHierBinding
        JUCtrlHierBinding treeBinding = (JUCtrlHierBinding) collectionModel.getWrappedData();
        
        //the selected rows are defined in a RowKeySet.As the LOV table only
        //supports single selections, there is only one entry in the rks
        RowKeySet rks = (RowKeySet) returnPopupEvent.getReturnValue();
        
        // the ADF Faces table row key is a list. The list contains the oracle.jbo.Key
        List tableRowKey = (List) rks.iterator().next();
        
        //get the iterator binding for the LOV lookup table binding  
        DCIteratorBinding dciter = treeBinding.getDCIteratorBinding();
        
        //get the selected row by its JBO key  
        Key key = (Key) tableRowKey.get(0);
        Row rw =  dciter.findRowByKeyString(key.toStringFormat(true));
        
        //work with the row
        return rw;
    }
}
