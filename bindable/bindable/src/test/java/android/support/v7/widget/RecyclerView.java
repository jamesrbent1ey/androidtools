package android.support.v7.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class RecyclerView extends ViewGroup
{
    public abstract static class LayoutManager {

        public void assertNotInLayoutOrScroll(String message) {
        }
        public void requestLayout(){
        }
    }

    public abstract static class Adapter {
        public void notifyDataSetChanged() {}
    }

    public abstract static class ViewHolder {

    }

    public RecyclerView(Context context)
    {
        super(context);
    }

    public RecyclerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public RecyclerView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public RecyclerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {

    }

    public void setLayoutManager(LayoutManager layout) {

    }

}
