
/*
 * Copyright (c) 2019. James Bentley, all rights reserved.
 */

package lib.bindable;

public interface UIModelObserver
{
    /**
     * Notify the model has changed
     * Note - best to run this on the main ui thread
     * @param model model that has changed
     */
    void modelChanged(UIModel model);
}
