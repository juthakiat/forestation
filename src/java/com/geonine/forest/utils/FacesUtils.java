/*
 * Copyright 2004-2013 ICEsoft Technologies Canada Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.geonine.forest.utils;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JSF utilities.
 *
 * @author ICEsoft Technologies Inc.
 * @since 2.0
 */
public class FacesUtils implements Serializable{

    private final static Logger log = Logger.getLogger(FacesUtils.class.getName());

    private static Properties buildProperties = null;

    /**
     * Get servlet context.
     *
     * @return the servlet context
     */
    public static ServletContext getServletContext() {
        return (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
    }

    public static ExternalContext getExternalContext() {
        FacesContext fc = FacesContext.getCurrentInstance();
        return fc.getExternalContext();
    }

    public static HttpSession getHttpSession(boolean create) {
        return (HttpSession) FacesContext.getCurrentInstance().
                getExternalContext().getSession(create);
    }

    /**
     * Get managed bean based on the bean name.
     *
     * @param beanName the bean name
     * @return the managed bean associated with the bean name
     */
    public static Object getManagedBean(String beanName) {
        FacesContext fc = FacesContext.getCurrentInstance();
        ELContext elc = fc.getELContext();
        ExpressionFactory ef = fc.getApplication().getExpressionFactory();
        ValueExpression ve = ef.createValueExpression(elc, getJsfEl(beanName), Object.class);
        try {
            return ve.getValue(elc);
        } catch (Exception e) {
            log.log(Level.SEVERE,"could not get value for bean " + beanName, e);
        }
        return null;
    }


    /**
     * Remove the managed bean based on the bean name.
     *
     * @param beanName the bean name of the managed bean to be removed
     */
    public static void resetManagedBean(String beanName) {
        FacesContext fc = FacesContext.getCurrentInstance();
        ELContext elc = fc.getELContext();
        ExpressionFactory ef = fc.getApplication().getExpressionFactory();
        ef.createValueExpression(elc, getJsfEl(beanName),
                Object.class).setValue(elc, null);
    }

    /**
     * Store the managed bean inside the session scope.
     *
     * @param beanName    the name of the managed bean to be stored
     * @param managedBean the managed bean to be stored
     */
    public static void setManagedBeanInSession(String beanName, Object managedBean) {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(beanName, managedBean);
    }

    /**
     * Store the managed bean inside the request scope.
     *
     * @param beanName    the name of the managed bean to be stored
     * @param managedBean the managed bean to be stored
     */
    public static void setManagedBeanInRequest(String beanName, Object managedBean) {
        FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put(beanName, managedBean);
    }

    /**
     * Get parameter value from request scope.
     *
     * @param name the name of the parameter
     * @return the parameter value
     */
    public static String getRequestParameter(String name) {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(name);
    }

    /**
     * Get named request map object value from request map.
     *
     * @param name the name of the key in map
     * @return the key value if any, null otherwise.
     */
    public static Object getRequestMapValue(String name) {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(name);
    }

    /**
     * Gets the action attribute value from the specified event for the given
     * name.  Action attributes are specified by &lt;f:attribute /&gt;.
     *
     * @param event
     * @param name
     * @return
     */
    public static String getActionAttribute(ActionEvent event, String name) {
        return (String) event.getComponent().getAttributes().get(name);
    }

    public static String getBuildAttribute(String name) {
        if (buildProperties != null)
            return buildProperties.getProperty(name, "unknown");
        InputStream is = null;
        try {
            is = getServletContext().getResourceAsStream("/WEB-INF/buildversion.properties");
            buildProperties = new Properties();
            buildProperties.load(is);
        } catch (Throwable e) {
            is = null;
            buildProperties = null;
            return "unknown";
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Throwable t) {
                }
            }
        }
        return buildProperties.getProperty(name, "unknown");
    }


    /**
     * Gest parameter value from the the session scope.
     *
     * @param name name of the parameter
     * @return the parameter value if any.
     */
    public static String getSessionParameter(String name) {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest myRequest = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession mySession = myRequest.getSession();
        return myRequest.getParameter(name);
    }
    
    public static String getFacesParameter(String parameter) {
        return getFacesParameter(parameter, null);
    }

    /**
     * Get parameter value from the web.xml file
     *
     * @param parameter name to look up
     * @return the value of the parameter
     */
    public static String getFacesParameter(String parameter, String defaultVal) {
        try{
            // Get the servlet context based on the faces context
            ServletContext sc = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
    
            // Return the value read from the parameter
            String toReturn = sc.getInitParameter(parameter);
            
            return toReturn != null ? toReturn : defaultVal;
        }catch (Exception failedGet) { }
        
        return defaultVal;
    }

    /**
     * Add information message.
     *
     * @param msg the information message
     */
    public static void addInfoMessage(String msg) {
        addInfoMessage(null, msg);
    }

    /**
     * Add information message to a specific client.
     *
     * @param clientId the client id
     * @param msg      the information message
     */
    public static void addInfoMessage(String clientId, String msg) {
        FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg));
    }

    /**
     * Add information message to a specific client.
     *
     * @param clientId the client id
     * @param msg      the information message
     */
    public static void addWarnMessage(String clientId, String msg) {
        FacesContext.getCurrentInstance().addMessage(clientId,
                new FacesMessage(FacesMessage.SEVERITY_WARN, msg, msg));
    }

    /**
     * Add error message.
     *
     * @param msg the error message
     */
    public static void addErrorMessage(String msg) {
        addErrorMessage(null, msg);
    }
    
    /**
      * Method to return the value specified via an f:attribute component
      *
      *@param event of the parent that had the f:attribute
      *@param name of the param
      *@return the Object value, which may be null if the attribute wasn't found
      */
    public static Object getFAttribute(ActionEvent event, String name) {
        return event.getComponent().getAttributes().get(name);
    }

    /**
     * Finds component with the given id
     *
     * @param c  component check children of.
     * @param id id of component to search for.
     * @return found component if any.
     */
    public static UIComponent findComponent(UIComponent c, String id) {
        if (id.equals(c.getId())) {
            return c;
        }
        Iterator<UIComponent> kids = c.getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent found = findComponent(kids.next(), id);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    /**
     * Finds all component with the given id.  Component id's are formed from
     * the concatination of parent component ids.  This search will find
     * all componet in the component tree with the specified id as it is possible
     * to have the same id used more then once in the component tree
     *
     * @param root            component check children of.
     * @param id              id of component to search for.
     * @param foundComponents list of found component with the specified id.
     * @return found component if any.
     */
    public static void findAllComponents(UIComponent root, String id,
                                         List<UIComponent> foundComponents) {
        if (id.equals(root.getId())) {
            foundComponents.add(root);
        }
        Iterator<UIComponent> kids = root.getFacetsAndChildren();
        while (kids.hasNext()) {
            findAllComponents(kids.next(), id, foundComponents);
        }
    }

    /**
     * Add error message to a specific client.
     *
     * @param clientId the client id
     * @param msg      the error message
     */
    public static void addErrorMessage(String clientId, String msg) {
        FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
    }

    private static String getJsfEl(String value) {
        return "#{" + value + "}";
    }
    
    public static boolean isBlank(String check) {
        return ((check == null) || (check.trim().length() == 0));
    }
    
    public static String join(String[] base) {
        return join(base, ",");
    }
    
    public static String join(String[] base, String delimiter) {
        // Check if we have enough items to warrant the list logic
        if (base.length > 1) {
            StringBuilder toReturn = new StringBuilder(base.length*5);
            
            for (int i = 0; i < base.length; i++) {
                toReturn.append(base[i]);
                
                // Add a delimiter unless this is the last item
                if ((i+1) <= (base.length-1)) {
                    toReturn.append(delimiter);
                }
            }
            return toReturn.toString();
        }
        //return a single item without any delimiters
        else if(base.length == 1)
            return  base[0];
        //if base is empty - return empty string
        else
        {
            return "";
        }
    }
    
	/**
	 * Method to refresh the browser to the specified url by adding a META-REFRESH tag to the response
	 */
    public static void refreshBrowser(String url) {
        try{
            HttpServletResponse response =
                (HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();
            
            if (response != null) {
                response.setHeader("Refresh", "0; URL=" + response.encodeRedirectURL(url));
            }
        }catch (Exception ignored) { }
    }
    
    /**
     * Method to redirect the browser to the specified url via the ExternalContext redirect method
     */
    public static void redirectBrowser(String url) {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            if ((context != null) &&
                (context.getExternalContext() != null)) {
                context.getExternalContext().redirect(url);
            }
        }catch (Exception failedRedirect) {
        	failedRedirect.printStackTrace();
        }
    }
}