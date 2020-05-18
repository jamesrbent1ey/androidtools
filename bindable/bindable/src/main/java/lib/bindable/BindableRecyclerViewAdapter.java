
/*
 * Copyright (c) 2019. James Bentley, all rights reserved.
 */

package lib.bindable;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collection;
import java.util.Map;


/**
 * The BindableRecyclerViewAdapater expects to be associated with a RecyclerView .
 * Bindings (for data sets to manipulate) are defined in the XML associated with the
 * RecyclerView this object is associated with
 * <p>
 * Implementations must provide RecyclerView.Adapter methods as well as a method to
 * bind the data for adaptation to the view.
 * <p>
 * A view may be inflated from a layout that is also marked up with bindable elements.
 * Use LayoutInflater.from(getContext()) to obtain the appropriate view inflator for
 * constructing views in onCreateViewHolder
 * <p>
 * XML Attributes:
 * adapterModel - name of the data element that can be resolved through UIScope and used to decorate the view
 * adapterView - id of the view to inflate. Must be specified in layout as @type/name
 * <p>
 * This class is abstract for the getCount method. Necessary because the type of model is unknown.
 */
public class BindableRecyclerViewAdapter extends RecyclerView.Adapter
        implements UIBindable, UIModelObserver, View.OnClickListener {
    public static final String XML_MODEL = "adapterModel";
    public static final String XML_VIEW = "adapterView";
    public static final String XML_DECORATOR = "adapterDecorator";

    UIBindable parent;
    Context context;
    Object model;
    String modelName;
    int resourceToInflate = -1;
    String handler = null;
    UIDecorator decorator;

    /**
     * Simple view holder that can be used with Derivatives of this Adapter
     * Default ViewHolder
     */
    protected class ViewHolder extends RecyclerView.ViewHolder {
        View item;

        public ViewHolder(View itemView) {
            super(itemView);
            item = itemView;
        }

        public View getView() {
            return item;
        }
    }

    /**
     * Constructor for the BindableRecyclerViewAdapter
     *
     * @param context - context/activity/UIScope for the call
     * @param parent  - bindable parent
     */
    public BindableRecyclerViewAdapter(@NonNull Context context, @NonNull UIBindable parent) {
        super();
        this.context = context;
        this.parent = parent;

        if (!(context instanceof UIScope)) {
            return;
        }

        AttributeSet attrs = parent.getExportableAttributes();
        handler = BindableUtilities.getValueForNamespaces(attrs, UIBindable.NAMESPACES,
                BindableButton.XML_ON_CLICK_LISTENER);

        String decoratorName =  BindableUtilities.getValueForNamespaces(attrs, UIBindable.NAMESPACES,
                XML_DECORATOR);
        if (decoratorName != null) {
            decorator = (UIDecorator) BindableUtilities.getInstance(decoratorName);
        }

        modelName =  BindableUtilities.getValueForNamespaces(attrs, UIBindable.NAMESPACES,
                XML_MODEL);
        if (modelName == null) {
            return;
        }
        UIScope scope = (UIScope) getContext();
        model = scope.getModel(modelName);
        if (model != null) {
            scope.addModelObserver(this);
        }

        // any view resource should be in the @type/name format
        String viewValue =  BindableUtilities.getValueForNamespaces(attrs, UIBindable.NAMESPACES,
                XML_VIEW);
        if (viewValue.startsWith("@")) {
            viewValue = viewValue.substring(1);
        }
        try {
            resourceToInflate = Integer.parseInt(viewValue);
        } catch (NumberFormatException e) {
            // ignore resourceToInflate stays -1
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //if we don't have a view to inflate, return
        if (resourceToInflate == -1) {
            return null;
        }
        return new ViewHolder(BindableUtilities.inflateResource(context, resourceToInflate));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        View v = holder.itemView;

        if ((context instanceof UIScope) &&
                decorator.decorate(v, this, (UIScope) context, position)) {
            return; //decorator handled the processing
        }

        if (handler == null) {
            return;
        }
        v.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        Object model = getModel();
        if (model == null) {
            return 0;
        }
        if (model instanceof Collection) {
            return ((Collection) model).size();
        }
        if (model instanceof Map) {
            return ((Map) model).size();
        }
        return 0;
    }

    @Override
    public AttributeSet getExportableAttributes() {
        return parent.getExportableAttributes();
    }

    /**
     * Get the Context/Activity/UIScope associated with this adapter
     *
     * @return Context/Activity/UIScope as Context
     */
    protected Context getContext() {
        return context;
    }

    /**
     * Get the parent bound to this adapter
     *
     * @return UIBindable parent
     */
    protected UIBindable getParent() {
        return parent;
    }

    /**
     * Get the model binding
     *
     * @return the bound model, null if not bound
     */
    public Object getModel() {
        return model;
    }

    @Override
    public void modelChanged(UIModel model) {
        if (modelName == null) {
            return;
        }
        try {
            UIScope scope = (UIScope) getContext();
            this.model = scope.getModel(modelName);
            // update
            notifyDataSetChanged();
        } catch (ClassCastException e) {
            // ignore
        }
    }

    @Override
    public void onClick(View v) {
        if (handler == null) {
            return;
        }
        UIBindable bindable = this;
        if (v instanceof UIBindable) {
            bindable = (UIBindable) v;
        }
        // handle delegated click - override for specific function or set new handler on view.
        BindableUtilities.handleClick(context, bindable, v, handler);
    }

    @Override
    public UIScope getScope() {
        if (context instanceof UIScope) {
            return (UIScope) context;
        }
        return null;
    }
}
