
/*
 * Copyright (c) 2019. James Bentley, all rights reserved.
 */

package lib.bindable;

/**
 * Represents the Scope from which View related elements can obtain the Model and Controller methods
 * for display and function
 * <p>
 * Each View element, constructed from XML, receives the XML definition and the Context (Activity).
 * The Context/Activity can implement this interface to enable auto-binding elements to data and
 * methods.
 */
public interface UIScope extends UIModel {
    /**
     * Get the model identified by the given name
     *
     * @param name name of the model item
     * @return null if not found
     */
    Object getModel(String name);

    // TODO setModel?
}
