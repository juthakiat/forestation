package com.geonine.forest.utils;

import java.io.Serializable;
import java.util.Map;
import javax.faces.application.ViewHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

/**
 *
 * @author Juthakiat Tipchai
 */
@ManagedBean(name = "navigation")
@SessionScoped
public class NavigationController implements Serializable {
    
    private String includePage;
    
    /**
     * Creates a new instance of PageNavigationBean
     */
    public NavigationController() {
        this.includePage = "events";
    }

    /**
     * @return
     */
    public String getIncludePage() {
        return includePage;
    }

    /**
     * @param includePage
     */
    public void setIncludePage(String includePage) {
        this.includePage = includePage;
    }

    public void navigate() {
        // Approach to navigation that will grab our params via the request map
        Map<String,String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        this.includePage = map.get("includePage");
    }
    
    public static void refreshPage() {
        FacesContext context = FacesContext.getCurrentInstance();
        ViewHandler handler = context.getApplication().getViewHandler();
        String viewId = context.getViewRoot().getViewId();
        UIViewRoot root = handler.createView(context, viewId);
        
        root.setViewId(viewId);
        context.setViewRoot(root);
    }
    
    public static void loadPage(String page) {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(page);
        }catch (Throwable e) {
        }
    }
    
}
