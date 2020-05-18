
/*
 * Copyright (c) 2019. James Bentley, all rights reserved.
 */

package lib.bindable;

import android.util.AttributeSet;

/**
 * Specify a Class as Bindable
 */
public interface UIBindable
{
    // namespace for XML attributes
    String NAMESPACE = "http://schemas.bindable.lib/widget";
    String ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android";
    String[] NAMESPACES = {
        ANDROID_NAMESPACE,
        NAMESPACE
    };

    // common namespace attributes
    String XML_VISIBILITY = "visible"; // model element indicating visibility (visible, invisible, gone)
    String XML_ENABLED = "enabled"; // model element indicating enable state (true/false)

    /**
     * Get a set of XML defined attributes exported by this object
     * @return
     */
    AttributeSet getExportableAttributes();

    /**
     * Get the scope this object is bound to
     * @return currently bound scope
     */
    UIScope getScope();
}
