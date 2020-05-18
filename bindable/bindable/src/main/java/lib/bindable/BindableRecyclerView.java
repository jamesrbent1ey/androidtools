
/*
 * Copyright (c) 2019. James Bentley, all rights reserved.
 */

package lib.bindable;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Most Layouts won't need binding. A RecyclerView needs an Adapter and a LayoutManager.
 * BindableRecyclerView allows for RecyclerView Adapter and LayoutManager bindings to be specified
 * in XML attributes.
 *
 * UIBindable holds the namespace definition for bindable objects
 * @see UIBindable
 *
 * XML Attributes:
 * layoutManager  (REQUIRED)
 * grid or linear - grid instantiates GridLayoutManager, linear will instantiate LinearLyoutManager
 *
 * layoutOrientation (required if layoutManager specified)
 * horizontal or vertical - default vertical
 *
 * reverseLayout
 * true or false - default false
 *
 * layoutSpan (required if layoutManager is GridLayoutManager)
 * integer - number of columns - default 1
 *
 * adapter
 * Class name of an RecyclerView.Adapter to instantiate and bind.
 * Two forms of adapter constructors are supported. One taking Context and UIBindable, the other
 * taking Context only. If neither or present, no binding occurs.
 */
public class BindableRecyclerView extends RecyclerView implements UIBindable
{
    public static final String XML_LAYOUT_MANAGER = "layoutManager";
    public static final String XML_ADAPTER = "adapter";
    public static final String XML_VALUE_GRID = "grid";
    public static final String XML_LAYOUT_SPAN = "layoutSpan";
    public static final String XML_REVERSE_LAYOUT = "reverseLayout";
    public static final String TRUE = "true";
    public static final String XML_VALUE_HORIZONTAL = "horizontal";
    public static final String XML_LAYOUT_ORIENTATION = "layoutOrientation";
    AttributeSet exportedAttributes = null;
    Context mContext;

    public BindableRecyclerView(Context context)
    {
        super(context);
        this.mContext = context;
    }

    /**
     * Construct with context and XML defined attributes
     * @param context
     * @param attrs
     */
    public BindableRecyclerView(Context context,
            @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        this.mContext = context;
        exportedAttributes= new BindableAttributeSet(attrs);
        bind(context, attrs);
    }

    /**
     * Construct with context, XML defined attributes and default style
     * @param context
     * @param attrs
     */
    public BindableRecyclerView(Context context,
            @Nullable AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        this.mContext = context;
        exportedAttributes= new BindableAttributeSet(attrs);
        bind(context, attrs);
    }

    /**
     * Bind XML definitions
     * @param context - context for binding
     * @param attrs - xml definitions
     */
    private void bind(@NonNull Context context, @NonNull AttributeSet attrs)
    {
        bindLayoutManager(context, attrs);

        // look for an adapter
        String adapterName = attrs.getAttributeValue(NAMESPACE, XML_ADAPTER);
        if(adapterName != null)
        {
            try
            {
                Class c = Class.forName(adapterName);
                RecyclerView.Adapter adapter = null;
                Constructor constructor = null;
                try
                {
                    // adapter taking context and bindable
                    constructor = c.getConstructor(Context.class,UIBindable.class);
                    adapter = (RecyclerView.Adapter) constructor.newInstance(context, this);
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
                if(constructor == null)
                {
                    try
                    {
                        // adapter taking context only
                        constructor = c.getConstructor(Context.class);
                        adapter = (RecyclerView.Adapter) constructor.newInstance(context);
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

    /**
     * Bind LayoutManager described in XML
     * @param context - context for binding
     * @param attrs - xml definitions
     */
    private void bindLayoutManager(@NonNull Context context,
            @NonNull AttributeSet attrs)
    {
        // look for layout manager
        String lmName = attrs.getAttributeValue(NAMESPACE, XML_LAYOUT_MANAGER);
        if(lmName == null)
        {
            return;
        }

        // defaults
        boolean reverseLayout = false;
        int orientation = LinearLayout.VERTICAL;
        int span = 1;

        String value = attrs.getAttributeValue(NAMESPACE, XML_LAYOUT_ORIENTATION);
        if(value != null&& value.equalsIgnoreCase(XML_VALUE_HORIZONTAL))
        {
            orientation = LinearLayout.HORIZONTAL;
        }
        value = attrs.getAttributeValue(NAMESPACE, XML_REVERSE_LAYOUT);
        if(value != null && value.equalsIgnoreCase(TRUE))
        {
            reverseLayout = true;
        }
        value = attrs.getAttributeValue(NAMESPACE, XML_LAYOUT_SPAN);
        if(value != null)
        {
            try
            {
                span = Integer.parseInt(value);
            }
            catch (NumberFormatException e)
            {
                // do nothing - span remains 1
            }
        }

        if(lmName.equalsIgnoreCase(XML_VALUE_GRID))
        {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context,span,
                    orientation,reverseLayout);
            super.setLayoutManager(gridLayoutManager);
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,
                orientation,reverseLayout);
        super.setLayoutManager(linearLayoutManager);
    }

    // from UIBindable
    @Override
    public AttributeSet getExportableAttributes()
    {
        return exportedAttributes;
    }

    @Override
    public UIScope getScope()
    {
        if(mContext instanceof UIScope)
        {
            return (UIScope)mContext;
        }
        return null;
    }
}
