
/*
 * Copyright (c) 2019. James Bentley, all rights reserved.
 */

package com.example.bindable;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.AdapterView;
import lib.bindable.UIBindable;

import java.util.Arrays;
import java.util.List;

public class ActivityTwo extends BaseActivity
{
    String[] a1 = new String[]{
        "",
        "one",
        "two",
        "three"
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        List<String> list = Arrays.asList(a1);
        model.put("spinner1", list);
        list = Arrays.asList(a1);
        model.put("spinner2", list);
        model.put("spinner2Enable",new Boolean(false));

        setContentView(R.layout.activity_two);
    }

    public void spinnerItemSelected(UIBindable bindable, AdapterView parent, int position)
    {
        if(position<0)
        {
            return;
        }
        String s = parent.getItemAtPosition(position).toString();
        System.out.println("Selected: " + s);

        if(s.length() > 0)
        {
            model.put("spinner2Enable", new Boolean(true));
        }
        else
        {
            model.put("spinner2Enable", new Boolean(false));
        }
        notifyModelHasChanged();
    }

    public void spinner2ItemSelected(UIBindable bindable, AdapterView parent, int position)
    {
        if(position < 0)
        {
            return;
        }
        Object o = parent.getItemAtPosition(position);
        System.out.println("Spinner 2 selected: "+o.toString());
    }

}
