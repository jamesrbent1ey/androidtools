
/*
 * Copyright (c) 2019. James Bentley, all rights reserved.
 */

package lib.bindable;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SpinnerAdapter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * The BindableSpinner allows for data and functional bindings as expressed in XML attributes.
 * This class follows the pattern and attributes for BindableRecyclerView. A BindableAdapter
 * can be specified.
 *
 * @see BindableRecyclerView
 * @see BindableAdapter
 */
public class BindableSpinner extends android.support.v7.widget.AppCompatSpinner
        implements UIBindable, AdapterView.OnItemSelectedListener
{
    public static final String XML_ON_ITEM_SELECTED_LISTENER = "onItemSelectedListener";
    Context context;
    AttributeSet attributes;

    public BindableSpinner(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        bind(context, attrs);
    }

    public BindableSpinner(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        bind(context, attrs);
    }

    public BindableSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode)
    {
        super(context, attrs, defStyleAttr, mode);
        bind(context, attrs);
    }

    public BindableSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode,
            Resources.Theme popupTheme)
    {
        super(context, attrs, defStyleAttr, mode, popupTheme);
        bind(context, attrs);
    }

    /**
     * Evaluate XML defined bindings
     * @param context
     * @param attrs
     */
    private void bind(Context context, AttributeSet attrs)
    {
        this.context = context;
        attributes = new BindableAttributeSet(attrs);

        String handler =  BindableUtilities.getValueForNamespaces(attributes, UIBindable.NAMESPACES,
                XML_ON_ITEM_SELECTED_LISTENER);
        if(handler != null)
        {
            setOnItemSelectedListener(this);
        }

        if(context instanceof UIScope)
        {
            UIScope scope = (UIScope) context;
            setEnabled(BindableUtilities.isEnabled(scope,
                    BindableUtilities.getValueForNamespaces(attributes, UIBindable.NAMESPACES,
                            XML_ENABLED)));
            setVisibility(BindableUtilities.getVisibility(scope,
                    BindableUtilities.getValueForNamespaces(attributes, UIBindable.NAMESPACES,
                            XML_VISIBILITY)));
        }

        // instantiate adapter
        // We are looking only for a constructor that takes Context and UIBindable - all other adapters
        // have varying Constructor arguments which make it difficult to instantiate - and thus will
        // not be applicable for binding.
        String adapterName = attrs.getAttributeValue(NAMESPACE, BindableRecyclerView.XML_ADAPTER);
        if(adapterName != null)
        {
            try
            {
                Class c = Class.forName(adapterName);
                SpinnerAdapter adapter = null;
                Constructor constructor = null;
                try
                {
                    // adapter taking context and bindable
                    constructor = c.getConstructor(Context.class,UIBindable.class);
                    adapter = (SpinnerAdapter) constructor.newInstance(context, this);
                }
                catch (NoSuchMethodException e)
                {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
                catch (InstantiationException e)
                {
                    e.printStackTrace();
                }
                catch (InvocationTargetException e)
                {
                    e.printStackTrace();
                }

                if(constructor != null)
                {
                    super.setAdapter(adapter);
                }
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public AttributeSet getExportableAttributes()
    {
        return attributes;
    }

    @Override
    public UIScope getScope()
    {
        if(context instanceof UIScope)
        {
            return (UIScope)context;
        }
        return null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        String handler = BindableUtilities.getValueForNamespaces(attributes, UIBindable.NAMESPACES,
                XML_ON_ITEM_SELECTED_LISTENER);
        if(handler == null)
        {
            return; // can't delegate
        }
        BindableUtilities.handleItemSelected(context, this,handler, parent, position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {
        String handler = BindableUtilities.getValueForNamespaces(attributes, UIBindable.NAMESPACES,
                XML_ON_ITEM_SELECTED_LISTENER);
        if(handler == null)
        {
            return; // can't delegate
        }
        BindableUtilities.handleItemSelected(context, this,handler, parent, -1);
    }
}
