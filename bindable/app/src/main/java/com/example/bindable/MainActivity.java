
/*
 * Copyright (c) 2019. James Bentley, all rights reserved.
 */

package com.example.bindable;

import android.os.Bundle;
import android.widget.Button;
import lib.bindable.UIBindable;

import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * We are auto-binding to elements of the view - this activity is the UIScope, but other classes may
 * provide UIScope (like application or component)
 */
public class MainActivity extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        LinkedList<String> example = new LinkedList<>();
        example.add("one");
        example.add("two");
        example.add("three");
        example.add("four");

        LinkedHashMap<String,String> button = new LinkedHashMap<>();
        button.put("text","Hello");

        model.put("example", example);
        model.put("button",button);

        // Since this is the UIScope, we can automatically be resolved by bindable
        // elements defined in the layout
        setContentView(R.layout.activity_main);

        Thread t = new Thread()
        {
            public void run()
            {
                synchronized (this) {
                    try
                    {
                        wait(10000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                LinkedList<String> list = (LinkedList<String>) model.get("example");
                list.add("five");
                list.add("six");
                LinkedHashMap<String,String> button =
                        (LinkedHashMap<String, String>) model.get("button");
                button.put("text","Hello World!");
                model.put("button",button);
                notifyModelHasChanged();
            }
        };
        t.start();
    }

    /**
     * Bound in XML to receive button clicks from the Hello/Hello World button
     * TODO what is sufficient to avoid being stripped by Proguard - add rule?
     * Note that button is a View. We can use getRootView to obtain the root and then
     * find other views by id - to collect data
     * @param button
     */
    public void receiveButtonClick(UIBindable button)
    {
        if(button instanceof Button)
        {
            System.out.println("Eureka!!!!! - the " +
                    ((Button) button).getText()
                    + " button was clicked");
            return;
        }
        System.out.println(button.getClass().getName()+" was clicked!!!");
    }

}
