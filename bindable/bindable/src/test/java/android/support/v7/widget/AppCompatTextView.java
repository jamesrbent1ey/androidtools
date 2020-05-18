package android.support.v7.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Necessary Stub for Test: This class is necessary to allow
 * instantiation of derived classes under test.
 */
public class AppCompatTextView extends TextView
{
    public AppCompatTextView(Context context)
    {
        super(context);
    }

    public AppCompatTextView(Context context,
            @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AppCompatTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public AppCompatTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
            int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
