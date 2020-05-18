
/*
 * Copyright (c) 2019. James Bentley, all rights reserved.
 */

package lib.bindable;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import java.util.Map;

/**
 *
 */
public class BindableEditText extends android.support.v7.widget.AppCompatEditText
        implements UIBindable, UIModelObserver, View.OnClickListener, View.OnKeyListener, View.OnLongClickListener {
    public static final String XML_BUTTON_MODEL = "buttonModel";
    public static final String XML_ON_CLICK_LISTENER = "onClickListener";
    AttributeSet exportableAttributes;
    String modelName;
    Object model;
    Context context;

    public BindableEditText(@NonNull Context context, @NonNull AttributeSet attrs) {
        // the context looks to be the activity that sets the content view!! we have access to the component here
        // it would be good to have an interface for the context so we can bind
        super(context, attrs);
        this.context = context;
        exportableAttributes = new BindableAttributeSet(attrs);
        bind(attrs);
    }

    /**
     * Bind
     *
     * @param attrs
     */
    private void bind(AttributeSet attrs) {
        // bind any model
        modelName = attrs.getAttributeValue(NAMESPACE, XML_BUTTON_MODEL);
        apply();
        if (context instanceof UIScope) {
            ((UIScope) context).addModelObserver(this);
            setEnabled(BindableUtilities.isEnabled((UIScope) context,
                    attrs.getAttributeValue(NAMESPACE, XML_ENABLED)));
            setVisibility(BindableUtilities.getVisibility((UIScope) context,
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
    private void apply() {
        if (modelName != null && (context instanceof UIScope)) {
            UIScope scope = (UIScope) context;
            model = scope.getModel(modelName);
            if (model instanceof String) {
                setText((String) model);
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
     *
     * @return
     */
    public AttributeSet getExportableAttributes() {
        return exportableAttributes;
    }

    @Override
    public UIScope getScope() {
        if (context instanceof UIScope) {
            return (UIScope) context;
        }
        return null;
    }

    // from UIModelObserver
    @Override
    public void modelChanged(UIModel model) {
        apply();
    }


    @Override
    public void onClick(View v) {
        AttributeSet attributes = getExportableAttributes();
        String handler = BindableUtilities.getValueForNamespaces(attributes,
                UIBindable.NAMESPACES,XML_ON_CLICK_LISTENER);
        if (handler == null) {
            return;
        }
        BindableUtilities.handleClick(context, this, this, handler);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }
}
