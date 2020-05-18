
/*
 * Copyright (c) 2019. James Bentley, all rights reserved.
 */

package lib.bindable;

import android.os.Build;
import android.support.annotation.VisibleForTesting;
import android.util.AttributeSet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * An AttributeSet may not be continually available - disposed after UI construction as it is
 * typically from an XmlPullParser.
 *
 * It is necessary to capture key data, to insure the data is available for later reference.
 */
public class BindableAttributeSet implements AttributeSet
{
    class Node {
        private String mName;
        private String mNameSpace;
        private String mValue;
        private int mAttributeNameResource = 0;
        private int mAttributeResourceValue = -1;

        public String getName()
        {
            return mName;
        }

        public void setName(String name)
        {
            this.mName = name;
        }

        public String getNameSpace()
        {
            return mNameSpace;
        }

        public void setNameSpace(String nameSpace)
        {
            this.mNameSpace = nameSpace;
        }

        public String getValue()
        {
            return mValue;
        }

        public void setValue(String value)
        {
            this.mValue = value;
        }

        public int getAttributeNameResource()
        {
            return mAttributeNameResource;
        }

        public void setAttributeNameResource(int attributeNameResource)
        {
            this.mAttributeNameResource = attributeNameResource;
        }

        public int getAttributeResourceValue()
        {
            return mAttributeResourceValue;
        }

        public void setAttributeResourceValue(int attributeResourceValue)
        {
            mAttributeResourceValue = attributeResourceValue;
        }
    }
    String mPositionDescription;
    int mStyleAttribute;
    String mIDAttribute;
    String mClassAttribute;

    LinkedList<Node>              mNodeList      = new LinkedList<>();
    LinkedHashMap<String, Map<String,Node>> mNamespaceMap = new LinkedHashMap<>();

    @VisibleForTesting
    BindableAttributeSet() {

    }

    public BindableAttributeSet(AttributeSet other) {
        mPositionDescription = other.getPositionDescription();
        mStyleAttribute = other.getStyleAttribute();
        mIDAttribute = other.getIdAttribute();
        mClassAttribute = other.getClassAttribute();

        int count = other.getAttributeCount();
        for(int i=0; i < count; i++) {
            String name = other.getAttributeName(i);
            String value = other.getAttributeValue(i);
            String namespace = getNameSpace(other, i);
            Node node = new Node();
            node.setName(name);
            node.setValue(value);
            node.setNameSpace(namespace);
            node.setAttributeNameResource(other.getAttributeNameResource(i));
            node.setAttributeResourceValue(other.getAttributeResourceValue(i,-1));

            Map<String,Node> nameMap = mNamespaceMap.get(namespace);
            if(nameMap == null) {
                nameMap = new LinkedHashMap<>();
            }
            nameMap.put(name,node);
            mNamespaceMap.put(namespace,nameMap);

            mNodeList.add(node);
        }
    }

    private String getNameSpace(AttributeSet other, int i)
    {
        try {
            Method method = other.getClass().getMethod("getAttributeNamespace", Integer.TYPE);
            return (String) method.invoke(other, i);
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        return "";
    }


    @Override
    public int getAttributeCount()
    {
        return mNodeList.size();
    }

    @Override
    public String getAttributeName(int index)
    {
        Node node = mNodeList.get(index);
        return node.getName();
    }

    @Override
    public String getAttributeValue(int index)
    {
        Node node = mNodeList.get(index);
        return node.getValue();
    }

    @Override
    public String getAttributeValue(String namespace, String name)
    {
        if(namespace == null) {
            namespace = "";
        }
        Map<String,Node> map = mNamespaceMap.get(namespace);
        if(map == null) {
            return null;
        }
        Node node = map.get(name);
        if(node == null) {
            return null;
        }
        return node.getValue();
    }

    @Override
    public String getPositionDescription()
    {
        return mPositionDescription;
    }

    @Override
    public int getAttributeNameResource(int index)
    {
        Node node = mNodeList.get(index);
        return node.getAttributeNameResource();
    }

    @Override
    public int getAttributeListValue(String namespace, String attribute, String[] options,
            int defaultValue)
    {
        String value = getAttributeValue(namespace,attribute);
        if(value == null || options == null) {
            return defaultValue;
        }
        for(int i=0; i<options.length; i++) {
            if(value.equalsIgnoreCase(options[i])) {
                return i;
            }
        }
        return defaultValue;
    }

    @Override
    public boolean getAttributeBooleanValue(String namespace, String attribute,
            boolean defaultValue)
    {
        String value = getAttributeValue(namespace,attribute);
        if(value != null) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }

    @Override
    public int getAttributeResourceValue(String namespace, String attribute, int defaultValue)
    {
        if(namespace == null) {
            namespace = "";
        }
        Map<String,Node> map = mNamespaceMap.get(namespace);
        if(map == null) {
            return defaultValue;
        }
        Node node = map.get(attribute);
        if(node == null) {
            return defaultValue;
        }
        return (node.getAttributeResourceValue() == -1 ? defaultValue : node.getAttributeResourceValue());
    }

    @Override
    public int getAttributeIntValue(String namespace, String attribute, int defaultValue)
    {
        String value = getAttributeValue(namespace,attribute);
        if(value != null) {
            return Integer.parseInt(value);
        }
        return defaultValue;
    }

    @Override
    public int getAttributeUnsignedIntValue(String namespace, String attribute, int defaultValue)
    {
        String value = getAttributeValue(namespace,attribute);
        if(value != null) {
            try{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    return Integer.parseUnsignedInt(value);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    @Override
    public float getAttributeFloatValue(String namespace, String attribute, float defaultValue)
    {
        String value = getAttributeValue(namespace,attribute);
        if(value != null) {
            try{
                return Float.parseFloat(value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    @Override
    public int getAttributeListValue(int index, String[] options, int defaultValue)
    {
        Node node = mNodeList.get(index);
        return getAttributeListValue(node.getNameSpace(),node.getName(),options,defaultValue);
    }

    @Override
    public boolean getAttributeBooleanValue(int index, boolean defaultValue)
    {
        Node node = mNodeList.get(index);
        return getAttributeBooleanValue(node.getNameSpace(),node.getName(),defaultValue);
    }

    @Override
    public int getAttributeResourceValue(int index, int defaultValue)
    {
        Node node = mNodeList.get(index);
        return getAttributeResourceValue(node.getNameSpace(),node.getName(),defaultValue);
    }

    @Override
    public int getAttributeIntValue(int index, int defaultValue)
    {
        Node node = mNodeList.get(index);
        return getAttributeIntValue(node.getNameSpace(),node.getName(),defaultValue);
    }

    @Override
    public int getAttributeUnsignedIntValue(int index, int defaultValue)
    {
        Node node = mNodeList.get(index);
        return getAttributeUnsignedIntValue(node.getNameSpace(),node.getName(),defaultValue);
    }

    @Override
    public float getAttributeFloatValue(int index, float defaultValue)
    {
        Node node = mNodeList.get(index);
        return getAttributeFloatValue(node.getNameSpace(),node.getName(),defaultValue);
    }

    @Override
    public String getIdAttribute()
    {
        return mIDAttribute;
    }

    @Override
    public String getClassAttribute()
    {
        return mClassAttribute;
    }

    @Override
    public int getIdAttributeResourceValue(int defaultValue)
    {
        if(mIDAttribute == null) {
            return defaultValue;
        }
        try{
            return Integer.parseInt(mIDAttribute);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    @Override
    public int getStyleAttribute()
    {
        return mStyleAttribute;
    }
}
