package lib.bindable;

/*
 * Copyright (c) 2019. James Bentley, all rights reserved.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Collection;
import java.util.Map;

/**
 * The BindableAdapter is a BaseAdapter that can be used with GridView, Spinner, ListView, etc.
 * (i.e. AdapterView).  It follows XML bindings as defined in BindableRecyclerViewAdapter.
 * Sub-class and override as necessary to customize this adapter's behavior
 *
 * @see BindableRecyclerViewAdapter
 */
public class BindableAdapter extends BaseAdapter implements UIBindable, UIModelObserver
{
    private static final String TAG = BindableAdapter.class.getName();

    // sub-set of TextView XML attributes, understood by this Adapter
    // define in bindable namespace
    private static final String ADAPTER_TEXT_SIZE = "textSize";
    private static final String ADAPTER_TEXT_LINES = "lines";
    private static final String ADAPTER_TEXT_MAX_LINES = "maxLines";
    private static final String ADAPTER_TEXT_MIN_LINES = "minLines";
    private static final String ADAPTER_TEXT_COLOR = "textColor";
    private static final String ADAPTER_TEXT_SINGLE_LINE = "singleLien";
    private static final String ADAPTER_TEXT_IS_SELECTABLE = "textIsSelectable";

    Context context;
    UIBindable parent;
    Object model;
    String modelName;
    int resourceToInflate = -1;
    UIDecorator decorator;
    Object[] modelFromCollection;

    public  BindableAdapter(@NonNull Context context, @NonNull UIBindable parent)
    {
        super();
        this.context = context;
        this.parent = parent;

        AttributeSet attrs = parent.getExportableAttributes();
        //handler = attrs.get(BindableButton.XML_ON_CLICK_LISTENER);

        String decoratorName = BindableUtilities.getValueForNamespaces(attrs,
                UIBindable.NAMESPACES,BindableRecyclerViewAdapter.XML_DECORATOR);
        if(decoratorName != null)
        {
            decorator = (UIDecorator) BindableUtilities.getInstance(decoratorName);
        }

        modelName = BindableUtilities.getValueForNamespaces(attrs,
                UIBindable.NAMESPACES,BindableRecyclerViewAdapter.XML_MODEL);
        if(modelName == null)
        {
            return;
        }
        UIScope scope = getScope();
        if(scope != null)
        {
            model = scope.getModel(modelName);
        }
        if(model != null)
        {
            scope.addModelObserver(this);
        }

        // any view resource should be in the @type/name format
        String viewValue = BindableUtilities.getValueForNamespaces(attrs,
                UIBindable.NAMESPACES,BindableRecyclerViewAdapter.XML_VIEW);
        if(viewValue == null)
        {
            return;
        }
        if(viewValue.startsWith("@"))
        {
            viewValue = viewValue.substring(1);
        }
        try
        {
            resourceToInflate = Integer.parseInt(viewValue);
        }
        catch (NumberFormatException e)
        {
            // ignore resourceToInflate stays -1
        }
    }

    @Override
    public int getCount()
    {
        if(model == null)
        {
            return 0;
        }
        if(model instanceof Collection)
        {
            return ((Collection)model).size();
        }
        if(model instanceof Map)
        {
            return ((Map)model).size();
        }
        return 0;
    }

    private void loadModelFromCollection()
    {
        if(!(model instanceof Collection))
        {
            return;
        }
        if(modelFromCollection == null)
        {
            modelFromCollection = new Object[getCount()];
            modelFromCollection = ((Collection)model).toArray(modelFromCollection);
        }

    }

    @Override
    public Object getItem(int position)
    {
        if(position < 0 || position >= getCount())
        {
            return null;
        }
        loadModelFromCollection();
        if(modelFromCollection != null)
        {
            return modelFromCollection[position];
        }
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        Object o = getItem(position);
        if(o != null)
        {
            return (long)o.hashCode();
        }
        return 0;
    }

    /**
     * Safely parse an integer value
     * @param value value to parse as an integer
     * @return -1 on failure
     */
    private int safeParseInt(String value) {
        if(value == null) {
            return -1;
        }

        // grab only the starting digits
        int idx = 0;
        for(;idx<value.length();idx++) {
            if(!Character.isDigit(value.charAt(idx))) {
                break;
            }
        }
        if(idx >0 && idx < value.length())
        {
            value = value.substring(0,idx);
        }

        // now parse
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            Log.i(TAG, e.getMessage(), e);
        }
        return -1;
    }

    /**
     * Safely parse a Color value
     * @param value value to parse as Color
     * @return Color.Black on error
     */
    private int safeParseColor(String value) {
        if(value == null) {
            return Color.BLACK;
        }

        // if the value is a resource id, it'll have @ prefix
        if(value.startsWith("@")) {
            value = value.substring(1);
            return context.getResources().getColor(safeParseInt(value));
        }

        // try a color value
        try {
            return Color.parseColor(value);
        } catch (IllegalArgumentException e) {
            Log.i(TAG, e.getMessage(), e);
        }
        return Color.BLACK;
    }

    /**
     * Format the given TextView based on current XML attribute settings
     * Adding android namespace defined attributes causes AAPT errors when the attributes are not
     * defined for the element. Need to use attributes defined in the bindable namespace.
     *
     * @param tv TextView to format
     * @return formatted TextView (tv)
     */
    private TextView formatTextView(@NonNull TextView tv) {
        AttributeSet attrs = getExportableAttributes();

        String value = BindableUtilities.getValueForNamespaces(attrs,
                UIBindable.NAMESPACES,ADAPTER_TEXT_SIZE);
        int size = safeParseInt(value); // parse the integer part
        if(value != null && size >= 0) {
            int unit = TypedValue.COMPLEX_UNIT_DIP;
            String unitStr = value.length()>=2?value.substring(value.length()-2):"";
            if(unitStr.equalsIgnoreCase("px")) {
                unit = TypedValue.COMPLEX_UNIT_PX;
            }
            if(unitStr.equalsIgnoreCase("sp")) {
                unit = TypedValue.COMPLEX_UNIT_SP;
            }
            if(unitStr.equalsIgnoreCase("pt")) {
                unit = TypedValue.COMPLEX_UNIT_PT;
            }
            tv.setTextSize(unit,(float)size);
        }
        value = BindableUtilities.getValueForNamespaces(attrs,
                UIBindable.NAMESPACES,ADAPTER_TEXT_LINES);
        int i = safeParseInt(value);
        if(value != null && i >= 0) {
            tv.setLines(i);
        }
        value = BindableUtilities.getValueForNamespaces(attrs,
                UIBindable.NAMESPACES,ADAPTER_TEXT_MAX_LINES);
        i = safeParseInt(value);
        if(value != null && i >= 0) {
            tv.setMaxLines(i);
        }
        value = BindableUtilities.getValueForNamespaces(attrs,
                UIBindable.NAMESPACES,ADAPTER_TEXT_MIN_LINES);
        i = safeParseInt(value);
        if(value != null && i >= 0) {
            tv.setMinLines(i);
        }
        value = BindableUtilities.getValueForNamespaces(attrs,
                UIBindable.NAMESPACES,ADAPTER_TEXT_COLOR);
        if(value != null) {
            tv.setTextColor(safeParseColor(value));
        }
        value = BindableUtilities.getValueForNamespaces(attrs,
                UIBindable.NAMESPACES,ADAPTER_TEXT_SINGLE_LINE);
        if(value != null) {
            tv.setSingleLine(Boolean.parseBoolean(value));
        }
        value = BindableUtilities.getValueForNamespaces(attrs,
                UIBindable.NAMESPACES,ADAPTER_TEXT_IS_SELECTABLE);
        if(value != null) {
            tv.setTextIsSelectable(Boolean.parseBoolean(value));
        }
        return tv;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            convertView = BindableUtilities.inflateResource(context, resourceToInflate);
            if(convertView == null)
            {
                convertView = formatTextView(new TextView(context));
            }
        }

        Object o = getItem(position);

        if((context instanceof UIScope) && decorator != null)
        {
            decorator.decorate(convertView,this, getScope(), position);
        }
        else if(o != null && (convertView instanceof TextView))
        {
            ((TextView)convertView).setText(o.toString());
        }
        return convertView;
    }

    @Override
    public AttributeSet getExportableAttributes()
    {
        return parent.getExportableAttributes();
    }

    @Override
    public UIScope getScope()
    {
        if(context instanceof UIScope)
        {
            return (UIScope)context;
        }
        return null;
    }

    @Override
    public void modelChanged(UIModel model)
    {
        if(modelName == null)
        {
            return;
        }
        try
        {
            UIScope scope = getScope();
            if(scope != null)
            {
                this.model = scope.getModel(modelName);

                if(parent instanceof View)
                {
                    AttributeSet attrs = parent.getExportableAttributes();
                    String v = BindableUtilities.getValueForNamespaces(attrs,
                            UIBindable.NAMESPACES, XML_ENABLED);
                    ((View)parent).setEnabled(BindableUtilities.isEnabled(scope,
                            v));
                    v = BindableUtilities.getValueForNamespaces(attrs,
                            UIBindable.NAMESPACES,XML_VISIBILITY);
                    ((View)parent).setVisibility(BindableUtilities.getVisibility(scope,
                            v));
                }
            }
            // update
            notifyDataSetChanged();
        }
        catch (ClassCastException e)
        {
            // ignore
        }
    }
}
