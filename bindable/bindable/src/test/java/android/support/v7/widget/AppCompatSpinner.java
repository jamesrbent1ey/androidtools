package android.support.v7.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.Spinner;

public class AppCompatSpinner extends Spinner
{
    public AppCompatSpinner(Context context)
    {
        super(context);
    }

    public AppCompatSpinner(Context context, int mode)
    {
        super(context, mode);
    }

    public AppCompatSpinner(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AppCompatSpinner(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public AppCompatSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode)
    {
        super(context, attrs, defStyleAttr, mode);
    }

    public AppCompatSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes,
            int mode)
    {
        super(context, attrs, defStyleAttr, defStyleRes, mode);
    }

    public AppCompatSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes,
            int mode,
            Resources.Theme popupTheme)
    {
        super(context, attrs, defStyleAttr, defStyleRes, mode, popupTheme);
    }
}
