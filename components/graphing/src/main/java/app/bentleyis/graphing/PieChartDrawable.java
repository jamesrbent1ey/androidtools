/*
 * Copyright (c) 2020  James Bentley
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package app.bentleyis.graphing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

import java.util.LinkedList;

/**
 * The PieChartDrawable displays a Pie Chart on a canvas. The Drawable is inserted into a component
 * such as ImageView. Additional styling can be applied around, or over, the hosting ImageView. This
 * allows for greater flexibility in labeling and formatting
 */
public class PieChartDrawable extends AbstractChartDrawable
{
    // the chart is drawn from the right center, clockwise
    float m_startAngle = 0f;
    // the chart's endpoint - 360 is the right, center, same as zero
    float m_sweepAngle = 360f;
    // the inner radius defines a hole in the center of the chart, set to zer
    // for no hole.
    int   innerRadius  = 0;
    // defines the color of the hole in the chart and background of the drawable
    // in colorARGB
    int   backgroundColor = 0xffffffff;
    boolean showLabels = true;
    int textColor = 0xff000000;
    // list of data points
    LinkedList<DataPoint> data;

    // calculated segments for display
    private class Segment
    {
        float     start;
        float     sweep;
        Paint     color;
        DataPoint reference;
    }

    // calculations for display
    private double max;
    // segments to display
    LinkedList<Segment> segments = new LinkedList<>();

    @Override
    public void draw(Canvas canvas)
    {
        // calculate the bounds
        Rect r = getBounds();
        RectF rect = new RectF();

        // calculate center and radius
        int centerx = r.width()/2;
        int centery = r.height()/2;
        int rad = Math.min(centerx,centery);

        // create the bounding box
        rect.bottom = centery+rad;
        rect.top = centery-rad;
        rect.left= centerx-rad;
        rect.right = centerx+rad;

        // draw segments
        for(Segment segment: segments) {
            canvas.drawArc(rect,segment.start,segment.sweep, true,
                    segment.color);
            if(showLabels) {
                drawLabel(canvas, centerx, centery,
                        rad - ((rad-innerRadius)/2),
                        segment);
            }
        }

        // draw the hole
        if(innerRadius > 0) {
            drawCenter(canvas, rect);
        }
    }

    private void drawCenter(Canvas canvas, RectF rect) {
        Paint p = new Paint();
        p.setARGB(255,255,255,255);
        float offsetX = (rect.width()/2) - innerRadius;
        float offsetY = (rect.height()/2) - innerRadius;
        rect.top += offsetY;
        rect.bottom -= offsetY;
        rect.left += offsetX;
        rect.right -= offsetX;
        canvas.drawArc(rect, m_startAngle, m_sweepAngle, true,
                p);
    }

    private void drawLabel(Canvas canvas, int centerx, int centery, double rad, Segment segment) {
        Paint labelColor = PaintUtilities.getPaint(textColor);
        // 1.375 * pt size = pixels roughly
        labelColor.setTextSize((float) (1.375 * 24));

        // calculate the label position halfway through the segment
        double angle = (segment.sweep/2.0) + segment.start;
        // convert to radians
        angle = (angle/180.0) * Math.PI;

        double posy = Math.sin(angle) * rad;
        double posx = Math.cos(angle) * rad;

        // adjust x relative to the center
        posx = centerx + posx;

        // adjust y relative to the center
        posy = centery + posy;

        // draw the label
        canvas.drawText(segment.reference.label,
                (float)posx, (float)posy, labelColor);
    }

    public float getStartAngle()
    {
        return m_startAngle;
    }

    public void setStartAngle(float m_startAngle)
    {
        this.m_startAngle = m_startAngle;
    }

    public float getSweepAngle()
    {
        return m_sweepAngle;
    }

    public void setSweepAngle(float m_sweepAngle)
    {
        this.m_sweepAngle = m_sweepAngle;
    }

    public int getInnerRadius()
    {
        return innerRadius;
    }

    public void setInnerRadius(int innerRadius)
    {
        this.innerRadius = innerRadius;
    }

    public int getBackgroundColor()
    {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }

    public LinkedList<DataPoint> getData()
    {
        return data;
    }

    public void setData(LinkedList<DataPoint> data)
    {
        this.data = data;
        this.calculatePoints();
    }

    private void calculatePoints()
    {
        if(data == null) {
            return;
        }
        max = 0.0;
        for(DataPoint point: data) {
            max += point.value;
        }
        Segment segment;
        segments.clear();
        float totalAngle = Math.abs(m_sweepAngle) - Math.abs(m_startAngle);
        float startAngle = m_startAngle;
        for(DataPoint point: data) {
            segment = new Segment();
            segment.start = startAngle;
            segment.sweep = (float)(totalAngle * (point.value/max));
            startAngle += segment.sweep;
            segment.reference = point;
            segment.color = PaintUtilities.getPaint(point.getColorARGB());
            segments.add(segment);
        }
    }

    @Override
    public DataPoint getPointFor(MotionEvent event) {
        Rect r = getBounds();
        int centerx = r.width()/2;
        int centery = r.height()/2;
        int rad = Math.min(centerx,centery);
        double leg1 = event.getX()-centerx;
        double leg2 = event.getY()-centery;
        double hyp = Math.sqrt((leg1*leg1)+(leg2*leg2));
        if(hyp > rad || hyp < innerRadius) {
            return null;
        }
        double angle = Math.toDegrees(Math.acos(leg1/hyp));
        if(event.getY() < centery) {
            angle = 360.0 - angle;
        }
        for(Segment segment: segments) {
            if(angle >= segment.start &&
               angle < (segment.start+segment.sweep)) {
                // HAVE THE SEGMENT THIS IS IN
                System.out.println("Segment: "+segment.reference.getLabel()+" = "+segment.reference.getValue());
                return segment.reference;
            }
        }
        return null;
    }

    public boolean isShowLabels() {
        return showLabels;
    }

    public void setShowLabels(boolean showLabels) {
        this.showLabels = showLabels;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}
