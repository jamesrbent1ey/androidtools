
/*
 * Copyright (c) 2019. James Bentley, all rights reserved.
 */

package lib.bindable;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import java.util.Map;

/**
 * A BindableButton can be bound to data (for title) and event handling
 * UIBindable holds the namespace definition for bindable objects
 * @see UIBindable
 *
 * XML attributes:
 * model - string identifying a String, through UIScope, to be used as the button text
 * onclick - name of the class to handle click events.
 *
 *
 * XML attributes:
 * model="somePropertyOfThisView=toSomeData"
 * onclick="Someclass"
 *
 * Then, like in Angular, we would need to apply to make changes show up - done.
 */
public class BindableButton extends android.support.v7.widget.AppCompatButton
        implements UIBindable, UIModelObserver, View.OnClickListener, View.OnKeyListener, View.OnLongClickListener
{
    public static final String XML_BUTTON_MODEL = "buttonModel";
    public static final String XML_ON_CLICK_LISTENER = "onClickListener";
    AttributeSet exportableAttributes;
    String             mModelName;
    Object             mModel;
    Context            mContext;

    public BindableButton(@NonNull Context context, @NonNull AttributeSet attrs)
    {
        // the context looks to be the activity that sets the content view!! we have access to the component here
        // it would be good to have an interface for the context so we can bind
        super(context, attrs);
        this.mContext = context;
        exportableAttributes = new BindableAttributeSet(attrs);
        bind(attrs);
    }

    /**
     * Bind
     * @param attrs
     */
    private void bind(AttributeSet attrs) {
        // bind any model
        mModelName = attrs.getAttributeValue(NAMESPACE, XML_BUTTON_MODEL);
        apply();
        if(mContext instanceof UIScope)
        {
            ((UIScope)mContext).addModelObserver(this);
            setEnabled(BindableUtilities.isEnabled((UIScope)mContext,
                    attrs.getAttributeValue(NAMESPACE, XML_ENABLED)));
            setVisibility(BindableUtilities.getVisibility((UIScope)mContext,
                    attrs.getAttributeValue(NAMESPACE, XML_VISIBILITY)));
        }

        // click handler bindings
        setOnClickListener(this);
        setOnLongClickListener(this);
        setOnKeyListener(this);

    }

    /**
     * Apply bindings
     */
    private void apply()
    {
        if(mModelName != null && (mContext instanceof UIScope))
        {
            UIScope scope = (UIScope)mContext;
            mModel = scope.getModel(mModelName);
            if(mModel instanceof String)
            {
                setText((String)mModel);
            }
            String v = BindableUtilities.getValueForNamespaces(exportableAttributes,
                    UIBindable.NAMESPACES,XML_ENABLED);
            setEnabled(BindableUtilities.isEnabled(scope,
                    v));
            v = BindableUtilities.getValueForNamespaces(exportableAttributes,
                    UIBindable.NAMESPACES,XML_VISIBILITY);
            setVisibility(BindableUtilities.getVisibility(scope,
                    v));
        }
    }

    /**
     * This allows click handlers to get attributes for this button
     * @return
     */
    public AttributeSet getExportableAttributes() {
        return exportableAttributes;
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

    // from UIModelObserver
    @Override
    public void modelChanged(UIModel model)
    {
        apply();
    }


    @Override
    public void onClick(View v)
    {
        AttributeSet attributeSet= getExportableAttributes();
        String handler = BindableUtilities.getValueForNamespaces(attributeSet,
                UIBindable.NAMESPACES,XML_ON_CLICK_LISTENER);
        if(handler == null)
        {
            return;
        }
        BindableUtilities.handleClick(mContext,this,this, handler);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event)
    {
        return false;
    }

    @Override
    public boolean onLongClick(View v)
    {
        return false;
    }
}
