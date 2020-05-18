
/*
 * Copyright (c) 2019. James Bentley, all rights reserved.
 */

package lib.bindable;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utilities common for the Bindable environment
 */
public class BindableUtilities
{
    /**
     * Convert an AttributeSet into a Map - namespace aware
     * The AttributeSet may not be continually available - disposed after UI construction.
     * It is necessary to capture key data, to insure it is available for later reference.
     * @param set
     * @return a mapping of Bindable namespace attributes
     * @see UIBindable
     */
    static Map<String,String> toMap(@NonNull AttributeSet set) {
        Map<String,String> map = new LinkedHashMap<>();
        for(int i=0; i<set.getAttributeCount(); i++)
        {
            String name = set.getAttributeName(i);
            String value = set.getAttributeValue(UIBindable.NAMESPACE,name);
            String androidValue = set.getAttributeValue(UIBindable.ANDROID_NAMESPACE,name);
            // allow android value to override bindable - bindable contains elements from both namespaces
            value = androidValue != null ? androidValue : value;
            if(value == null)
            {
                continue;
            }
            map.put(name,value);
        }
        return map;
    }

    static String getValueForNamespaces(@NonNull AttributeSet set,
            @NonNull String[] namespaces, @NonNull String name) {
        for(int i = 0; i < namespaces.length; i++) {
            String value = set.getAttributeValue(namespaces[i], name);
            if(value != null && value.length() > 0) {
                return value;
            }
        }
        return null;
    }

    /**
     * Get a method that takes a UIBindable as its only parameter
     * @param scope - object to interrogate
     * @param routine - name of the method
     * @return null if not found
     */
    private static Method getScopeMethod(@NonNull UIScope scope, @NonNull String routine)
    {
        Method m = null;

        try
        {
            m = scope.getClass().getMethod(routine,UIBindable.class);
            return m;
        }
        catch (NoSuchMethodException e)
        {
            //ignore
        }
        return null;
    }

    /**
     * Get a method that takes a UIBindable and String parameters
     * @param scope - object to interrogate for the method
     * @param routine - name of the method
     * @return null if not found
     */
    private static Method getScopeMethodWithParameters(@NonNull UIScope scope, @NonNull String routine)
    {
        Method m = null;

        try
        {
            m = scope.getClass().getMethod(routine,UIBindable.class,String.class);
            return m;
        }
        catch (NoSuchMethodException e)
        {
            // ignore
        }
        return null;
    }

    /**
     * Get a method that expects a view parameter
     * @param scope - scope to interrogate for method
     * @param routine - name of the method
     * @return null if not found
     */
    private static Method getScopeMethodWithView(@NonNull UIScope scope, @NonNull String routine)
    {
        Method m = null;

        try
        {
            m = scope.getClass().getMethod(routine, View.class);
            return m;
        }
        catch (NoSuchMethodException e)
        {
            // ignore
        }
        return null;
    }

    /**
     * Execute a method on the given scope. The method must take UIBindable or
     * View. If the method takes View, bindable must be a View
     * @param scope scope to execute on
     * @param routine name of the method
     * @param bindable parameter given to the method
     * @return false if unsuccessful
     */
    static boolean executeOnScope(@NonNull UIScope scope, @NonNull String routine, @NonNull UIBindable bindable)
    {

        if(!routine.startsWith("$scope."))
        {
            return false;
        }
        routine = routine.substring("$scope.".length());
        int idx = routine.indexOf('(');
        String parameters = null;
        if(idx > 0)
        {
            //this has parameters
            String pstart = routine.substring(idx+1);
            routine = routine.substring(0,idx);
            idx = pstart.lastIndexOf(')');
            if(idx > 0)
            {
                parameters = pstart.substring(0,idx);
            }
        }

        // try to get the method in a form that takes parameters
        Method m = getScopeMethodWithParameters(scope, routine);
        if(m != null)
        {
            return invokeOnScope(m, scope,bindable,null, parameters);
        }

        // try to get the method in a form that takes UIBindable, no parameters
        m = getScopeMethod(scope, routine);
        if(m != null)
        {
            return invokeOnScope(m, scope, bindable, null, null);
        }

        // finally, try to get the method that takes View, no parameters
        if(!(bindable instanceof View))
        {
            return false;
        }
        m= getScopeMethodWithView(scope,routine);

        return invokeOnScope(m, scope, null, (View) bindable, parameters);
    }

    /**
     * Invoke a method defined in UIScope object
     * @param method - method to invoke
     * @param scope - scope to invoke method on
     * @param bindable - optional bindable, if null, view should be non-null
     * @param view - optional view, if null, bindable should be non-null
     * @param parameters - optional string containing parameters
     * @return true on success, false otherwise
     */
    private static boolean invokeOnScope(@NonNull Method method, @NonNull UIScope scope,
            UIBindable bindable, View view, String parameters)
    {
        // if no method, nothing to invoke
        if(method == null)
        {
            return false;
        }
        try
        {
            // if the view is present, assume invoking view
            if(view != null)
            {
                method.invoke(scope, view);
                return true;
            }
            //if no bindable, then can't invoke
            if(bindable == null)
            {
                return false;
            }
            // if no parameters, assume no param form of method
            if(parameters == null)
            {
                method.invoke(scope, bindable);
                return true;
            }
            // try to invoke with bindable and parameters
            method.invoke(scope, bindable, parameters);
            return true;
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get an instance of a class - default no argument constructor
     * @param className
     * @return null on error
     */
    static Object getInstance(@NonNull String className)
    {
        try
        {
            Class c = Class.forName(className);
            return c.newInstance();
        }
        catch (ClassNotFoundException e)
        {
            // ignore
        }
        catch (IllegalAccessException e)
        {
            // ignore
        }
        catch (InstantiationException e)
        {
            // ignore
        }
        return null;
    }

    /**
     * Handle a click press
     * @param context
     * @param bindable
     * @param view
     * @param handler
     * @return
     */
    public static boolean handleClick(@NonNull Context context,
            @NonNull UIBindable bindable, View view, @NonNull String handler)
    {
        if((context instanceof UIScope) &&
                BindableUtilities.executeOnScope((UIScope)context, handler,bindable))
        {
            return true;
        }

        Object clickHandler = getInstance(handler);
        if(clickHandler == null || !(clickHandler instanceof View.OnClickListener))
        {
            return false;
        }
        ((View.OnClickListener)clickHandler).onClick(view);
        return true;
    }

    /**
     * Handle a click press
     * @param context
     * @param bindable
     * @param handler
     * @return
     */
    public static boolean handleItemSelected(@NonNull Context context,
            @NonNull UIBindable bindable, @NonNull String handler,
            @NonNull AdapterView<?> parent, int position)
    {
        Class[] types = new Class[] {
                UIBindable.class,
                AdapterView.class,
                Integer.TYPE
        };
        Object[] values = new Object[] {
                bindable,
                parent,
                position
        };

        if((context instanceof UIScope) &&
                BindableUtilities.executeOnScope((UIScope)context, handler, types, values))
        {
            return true;
        }

        return false;
    }

    /**
     * Execute on scope
     * @param context scope
     * @param routine name of method to execute
     * @param types list of parameter types, in order
     * @param values list of parameter values, in order
     * @return true if executed, false otherwise
     */
    private static boolean executeOnScope(UIScope context, String routine, Class[] types, Object[] values)
    {
        if(!routine.startsWith("$scope."))
        {
            return false;
        }
        routine = routine.substring("$scope.".length());
        int idx = routine.indexOf('(');
        if(idx > 0)
        {
            //strip parameters as we are passing based on types and values
            routine = routine.substring(0,idx);
        }

        try
        {
            Method m = context.getClass().getMethod(routine,types);
            m.invoke(context, values);
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Inflate the resource if set
     * @return null on error
     */
    public static View inflateResource(@NonNull Context context, int resourceToInflate)
    {
        if(resourceToInflate == -1)
        {
            return null;
        }

        // dummy group for inflation
        ViewGroup vg = new ViewGroup(context)
        {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b)
            {

            }
        };
        // do not attach to root on inflation
        return LayoutInflater.from(context).inflate(resourceToInflate,vg,false);
    }

    /**
     * Determine the value of the model element, representing enabled state.
     * @param scope - scope holding the model
     * @param modelId - reference to model element containing enable state
     * @return true if state is true or cannot be determined. False otherwise
     */
    public static boolean isEnabled(@NonNull UIScope scope, String modelId)
    {
        if(modelId == null)
        {
            return true; // no model
        }
        Object o = scope.getModel(modelId);
        if(o == null)
        {
            return true; // no model
        }
        if(o instanceof Boolean)
        {
            return ((Boolean)o).booleanValue();
        }
        return true;
    }

    /**
     * Determine the value of the model element, representing visibility state.
     * @param scope - scope holding the model
     * @param modelId - reference to model element containing enable state
     * @return true if state is true or cannot be determined. False otherwise
     */
    public static int getVisibility(@NonNull UIScope scope, String modelId)
    {
        if(modelId == null)
        {
            return View.VISIBLE;
        }
        Object o = scope.getModel(modelId);
        if(o == null)
        {
            return View.VISIBLE;
        }
        if(o instanceof Integer)
        {
            return ((Integer)o).intValue();
        }
        return View.VISIBLE;
    }
}
