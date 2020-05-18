
/*
 * Copyright (c) 2019. James Bentley, all rights reserved.
 */

package com.example.bindable;

import android.view.View;
import lib.bindable.BindableRecyclerViewAdapter;
import lib.bindable.UIBindable;
import lib.bindable.UIDecorator;
import lib.bindable.UIScope;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

public class ExampleDecorator implements UIDecorator
{
    int[] colors = {
            0xffffffff,
            0xffff0000,
            0xff00ff00,
            0xff0000ff,
            0xffafafaf,
            0xffaf00af
    };

    @Override
    public boolean decorate(View v, UIBindable parent, UIScope scope)
    {
        return false;
    }

    @Override
    public boolean decorate(View v, UIBindable parent, UIScope scope, int position)
    {
        if(!(parent instanceof BindableRecyclerViewAdapter))
        {
            return false;
        }
        BindableRecyclerViewAdapter adapter = (BindableRecyclerViewAdapter)parent;
        LinkedList<String> myModel = (LinkedList<String>) adapter.getModel();

        // change the color based on position
        v.setBackgroundColor(colors[position]);

        setText(v, myModel, position);
        return false; //returning false to allow click listener
    }

    private void setText(View v, LinkedList<String> myModel, int position)
    {
        try
        {
            Method m = v.getClass().getMethod("setText", CharSequence.class);
            m.invoke(v,myModel.get(position));
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
    }
}
