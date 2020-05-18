
/*
 * Copyright (c) 2019. James Bentley, all rights reserved.
 */

package lib.bindable;

import android.view.View;

/**
 * Interface to delegate decoration of elements to. This is typically associated with an adapter and
 * will be called each time a View, within the adapter, is bound for presentation.
 *
 * A class that implements this interface is expected to have the default, no argument, Constructor
 */
public interface UIDecorator
{
    /**
     * Decorate the given view. Parent is provided to allow decoration by XML defined attributes
     * @param v - view to decorate
     * @param parent - parent containing XML attributes that may influence decoration
     * @param scope - scope for Model references
     * @return true if decorator completely handled decoration of the element, false otherwise.
     */
    boolean decorate(View v, UIBindable parent, UIScope scope);

    /**
     * Decorate the given view. Parent is provided to allow decoration by XML defined attributes
     * @param v - view to decorate
     * @param parent - parent containing XML attributes that may influence decoration
     * @param scope - scope for Model references
     * @param position - position in the associated model
     * @return true if decorator completely handled decoration of the element, false otherwise.
     */
    boolean decorate(View v, UIBindable parent, UIScope scope, int position);
}
