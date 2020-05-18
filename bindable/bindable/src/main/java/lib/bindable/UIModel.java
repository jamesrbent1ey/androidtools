
/*
 * Copyright (c) 2019. James Bentley, all rights reserved.
 */

package lib.bindable;

/**
 * The UIModel is an observable object. Note that preferred implementations
 * of the UIModel is a Map of Collections, Maps and discrete values.
 *
 * Note - it is best that this is not the Component as it will hold stale references
 * The Activity can listen the the Component and remove itself when the Activity goes away -
 * preventing stale references. The Activity is then a delegate to all bindable views.
 */
public interface UIModel
{
    void addModelObserver(UIModelObserver observer);
    void removeModelObserver(UIModelObserver observer);
}
