
/*
 * Copyright (c) 2019. James Bentley, all rights reserved.
 */

package lib.bindable;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.RadioGroup;

/**
 *
 */
public class BindableRadioGroup extends RadioGroup
        implements UIBindable, UIModelObserver, RadioGroup.OnCheckedChangeListener {
    public static final String XML_BUTTON_MODEL = "buttonModel";
    public static final String XML_ON_CHECKED_CHANGE_LISTENER = "onCheckedChangeListener";
    AttributeSet mExportableAttributes;
    String       mModelName;
    Object       mModel;
    Context      mContext;

    public BindableRadioGroup(@NonNull Context context, @NonNull AttributeSet attrs) {
        // the context looks to be the activity that sets the content view!! we have access to the component here
        // it would be good to have an interface for the context so we can bind
        super(context, attrs);
        this.mContext = context;
        mExportableAttributes = new BindableAttributeSet(attrs);
        bind(attrs);
    }

    /**
     * Bind
     *
     * @param attrs
     */
    private void bind(AttributeSet attrs) {
        // bind any model
        mModelName = attrs.getAttributeValue(NAMESPACE, XML_BUTTON_MODEL);
        apply();
        if (mContext instanceof UIScope) {
            ((UIScope) mContext).addModelObserver(this);
            setEnabled(BindableUtilities.isEnabled((UIScope) mContext,
                    attrs.getAttributeValue(NAMESPACE, XML_ENABLED)));
            setVisibility(BindableUtilities.getVisibility((UIScope) mContext,
                    attrs.getAttributeValue(NAMESPACE, XML_VISIBILITY)));
        }

        // click handler bindings
        setOnCheckedChangeListener(this);
    }

    /**
     * Apply bindings
     */
    private void apply() {
        if (mModelName != null && (mContext instanceof UIScope)) {
            UIScope scope = (UIScope) mContext;
            mModel = scope.getModel(mModelName);
            String v = BindableUtilities.getValueForNamespaces(mExportableAttributes,
                    UIBindable.NAMESPACES, XML_ENABLED);
            setEnabled(BindableUtilities.isEnabled(scope,
                    v));
            v = BindableUtilities.getValueForNamespaces(mExportableAttributes,
                    UIBindable.NAMESPACES, XML_ENABLED);
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
        return mExportableAttributes;
    }

    @Override
    public UIScope getScope() {
        if (mContext instanceof UIScope) {
            return (UIScope) mContext;
        }
        return null;
    }

    // from UIModelObserver
    @Override
    public void modelChanged(UIModel model) {
        apply();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        AttributeSet attributes = getExportableAttributes();
        String handler = BindableUtilities.getValueForNamespaces(attributes,
                UIBindable.NAMESPACES,XML_ON_CHECKED_CHANGE_LISTENER);
        if (handler == null) {
            return;
        }
        BindableUtilities.handleClick(mContext, this, this, handler);
    }
}
