
/*
 * Copyright (c) 2019. James Bentley, all rights reserved.
 */

package com.example.bindable;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import lib.bindable.UIBindable;
import lib.bindable.UIModelObserver;
import lib.bindable.UIScope;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class BaseActivity extends AppCompatActivity implements UIScope
{
    LinkedList<UIModelObserver>  observers = new LinkedList<>();
    LinkedHashMap<String,Object> model     = new LinkedHashMap<>();

    /**
     * This is an example of the Model that can be resolved to bound elements
     * Typically, we would look up the model from the base application, shared preferences, etc.
     * @param name
     * @return null if not found
     */
    @Override
    public Object getModel(String name)
    {
        if(name.indexOf('.') > 0)
        {
            int s = name.lastIndexOf('.');
            String selector = name.substring(s+1);
            Object o = getModel(name.substring(0,s));
            if(o != null && (o instanceof Map))
            {
                return ((Map)o).get(selector);
            }
            return null;
        }
        return model.get(name);
    }

    @Override
    public synchronized void addModelObserver(UIModelObserver observer)
    {
        if(observers.contains(observer))
        {
            return;
        }
        observers.add(observer);
    }

    @Override
    public synchronized void removeModelObserver(UIModelObserver observer)
    {
        observers.remove(observer);
    }

    /**
     * Notify Model listeners that a change has occurred
     */
    protected void notifyModelHasChanged()
    {
        runOnUiThread(new Runnable(){

            @Override
            public void run()
            {
                synchronized (BaseActivity.this)
                {
                    if (observers == null)
                    {
                        return;
                    }
                    for (UIModelObserver observer : observers)
                    {
                        observer.modelChanged(BaseActivity.this);
                    }
                }
            }
        });
    }

    /**
     * Form that takes parameters
     * @param bindable - object making the call
     * @param parameters - parameters
     */
    public void gotoActivity(UIBindable bindable, String parameters)
    {
        try
        {
            Class c = Class.forName(parameters);
            Intent i = new Intent(this,c);
            startActivity(i);
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}
