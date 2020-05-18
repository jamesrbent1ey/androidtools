
/*
 * Copyright (c) 2019. James Bentley, all rights reserved.
 */

package lib.bindable;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.AttributeSet;

import java.util.Map;

/**
 *
 */
public class BindableTextView extends android.support.v7.widget.AppCompatTextView
        implements UIBindable, UIModelObserver {
    public static final String XML_TEXTVIEW_MODEL = "textViewModel";
    AttributeSet exportableAttributes;
    String modelName;
    Object model;
    Context context;

    public BindableTextView(@NonNull Context context, @NonNull AttributeSet attrs) {
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
        modelName = attrs.getAttributeValue(NAMESPACE, XML_TEXTVIEW_MODEL);
        apply();
        if (context instanceof UIScope) {
            ((UIScope) context).addModelObserver(this);
            setEnabled(BindableUtilities.isEnabled((UIScope) context,
                    attrs.getAttributeValue(NAMESPACE, XML_ENABLED)));
            setVisibility(BindableUtilities.getVisibility((UIScope) context,
                    attrs.getAttributeValue(NAMESPACE, XML_VISIBILITY)));
        }
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
            setEnabled(BindableUtilities.isEnabled(scope,
                    BindableUtilities.getValueForNamespaces(exportableAttributes,UIBindable.NAMESPACES,
                            XML_ENABLED)));
            setVisibility(BindableUtilities.getVisibility(scope,
                    BindableUtilities.getValueForNamespaces(exportableAttributes,UIBindable.NAMESPACES,
                            XML_VISIBILITY)));
        }

        AttributeSet attributes= getExportableAttributes();
        String handler = BindableUtilities.getValueForNamespaces(attributes,UIBindable.NAMESPACES,
                XML_TEXTVIEW_MODEL);
        if (handler == null) {
            return;
        }
        BindableUtilities.executeOnScope((UIScope) context, handler, this);
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
}
