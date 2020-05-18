package lib.bindable;

import android.util.AttributeSet;

public class MockAttributeSet extends BindableAttributeSet
{
    public MockAttributeSet()
    {
    }

    public MockAttributeSet(AttributeSet other) {
        super(other);
    }

    public String getAttributeNamespace(int i) {
        return "";
    }
}
