
/*
 * Copyright (c) 2019. James Bentley, all rights reserved.
 */

package com.example.bindable;

import android.content.Context;
import android.support.annotation.NonNull;
import lib.bindable.BindableRecyclerViewAdapter;
import lib.bindable.UIBindable;

import java.util.LinkedList;

/**
 * Example of how to extend a BindableRecyclerViewAdapter in order to decorate views onBindViewHolder
 */
public class ExampleBindableRecyclerViewAdapter extends BindableRecyclerViewAdapter
{
    // Model could be a Collection or a Map - we'll assume collection here
    LinkedList<String> myModel;

    public ExampleBindableRecyclerViewAdapter(@NonNull Context context,
            @NonNull UIBindable parent)
    {
        super(context, parent);

        myModel = (LinkedList<String>) getModel();
    }

    @Override
    public int getItemCount()
    {
        // there is nothing to display if there is no model
        if(myModel == null)
        {
            return 0;
        }
        return myModel.size();
    }
}
