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
import android.view.MotionEvent;

import java.util.LinkedList;

/**
 * TODO
 * - orientation: default is vertical, allow for horizontal
 * - allow setting of value axis set of labels/values, scale the bars appropriately for the value axis
 * - animation?
 * - decorators/builders instead?
 */

/**
 * The BarChartDrawable displays a Bar Chart on a canvas. The Drawable is inserted into a component
 * such as ImageView. Additional styling can be applied around, or over, the hosting ImageView. This
 * allows for greater flexibility in labeling and formatting.
 * For consistency, BarChartDrawable displays grid (optional), axis and axis labels
 */
public class BarChartDrawable extends AbstractChartDrawable {
    private static final int TICK_LENGTH = 20;

    int backgroundColor;
    boolean showGrid = true;
    int axisColor = 0xff000000;
    int margin = 30;
    int pad = 10;
    int gridColor = 0xffafafaf;
    LinkedList<DataPoint> dataPoints = new LinkedList<>();

    @Override
    public void draw(Canvas canvas) {
        Rect r = getBounds();
        r = new Rect(r.left, r.top + margin, r.right - margin, r.bottom);

        // for clarity
        int left = r.left + margin;
        int top = r.top;
        int right = r.right;
        int bottom = r.bottom - margin;
        int width = right - left;
        int height = bottom - top;

        // TODO these can change for horizontal vs vertical
        int xstep = width/(dataPoints.isEmpty()?10:dataPoints.size());

        Paint axisPaint = PaintUtilities.getPaint(axisColor);
        // draw y axis
        canvas.drawLine(left,top,left,bottom,axisPaint);
        // draw x axis
        canvas.drawLine(left, bottom, right,bottom,axisPaint);
        showTickMarks(canvas, left, top, width, height, axisPaint);

        int xpos = left;
        for(DataPoint point: dataPoints) {
            Paint p = PaintUtilities.getPaint(point.getColorARGB());
            Rect rect = getBarBounds(point.getValue(), getMaxValue(),
                    xpos+pad, top, xstep-(2*pad), height);
            fillRect(canvas, rect, p);
            drawLabel(canvas, point, xpos, bottom, xpos+xstep, bottom+margin);
            xpos += xstep;
        }
    }

    private Rect getBarBounds(double pointValue, double maxValue, int x, int y, int width, int height) {
        int top = height;
        double percentage = pointValue / maxValue;
        top = (int)(top * percentage);
        Rect rect =
                new Rect(
                        x,
                        y+ (height - top),
                        x+width,
                        y+ height
                );
        return rect;
    }

    private void drawLabel(Canvas canvas, DataPoint point, int left, int top, int right, int bottom) {
        if(point.getLabel() == null || point.getLabel().isEmpty()) {
            return;
        }
        String label = point.getLabel();
        Paint labelPaint = new Paint();

        labelPaint.setTextSize((float) (1.375 * 24));

        float[] widths = new float[label.length()];
        labelPaint.getTextWidths(label,widths);
        float width = 0f;
        for(int i = 0; i<widths.length; i++)
            width += widths[i];

        int x = (int) (left + (((right-left)-(width))/2));
        int y = (int) (bottom - (((bottom-top)-labelPaint.getTextSize())/2));

        canvas.drawText(label,x,y,labelPaint);
    }

    private double getMaxValue() {
        if(dataPoints.isEmpty())
            return 10.0;
        double max = 0;
        for(DataPoint dataPoint: dataPoints) {
            if(dataPoint.getValue() > max) {
                max = dataPoint.getValue();
            }
        }
        return Math.ceil(max);
    }

    private void fillRect(Canvas canvas, Rect r, Paint p) {
        canvas.drawRoundRect(r.left,r.top,r.right,r.bottom,0,0,p);
    }

    private void showTickMarks(Canvas canvas, int x, int y, int width, int height, Paint color) {
        int numDataPoints = dataPoints.isEmpty()?10:dataPoints.size();
        int stepx = width/numDataPoints;
        int stepy = height/10;
        Paint gridColor = PaintUtilities.getPaint(this.gridColor);

        // TODO this should be conditional if and only if axis labels/values not provided
        double tickMaxValue = getMaxValue();
        double tickIncrement = tickMaxValue/10;
        double tickValue = tickIncrement;
        String tickLabel;

        // TODO would be better to get the font size from paint fontmetrics
        float tickOffset = (float) (1.375 * 24)/2f;

        for(int i = x + stepx; i < width; i += stepx) {
            canvas.drawLine(i, y+height,
                    i, y+height+TICK_LENGTH, color);
            if(showGrid) {
                canvas.drawLine(i,y, i, y+height, gridColor);
            }
        }

        color.setTextSize(tickOffset*2);
        for(int i = (y+height) - stepy; i > y; i -= stepy) {
            canvas.drawLine(x - TICK_LENGTH, i,
                    x, i, color);
            if(showGrid) {
                canvas.drawLine(x,i, x+width, i, gridColor);
            }
            tickLabel = String.format("%.1f", (float)tickValue);
            tickValue += tickIncrement;
            // TODO this is problematic as x would be margin - may be better to add tick labels separately
            canvas.drawText(tickLabel,0, i+tickOffset,color);
        }
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public boolean isShowGrid() {
        return showGrid;
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }

    public int getAxisColor() {
        return axisColor;
    }

    public void setAxisColor(int axisColor) {
        this.axisColor = axisColor;
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    public LinkedList<DataPoint> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(LinkedList<DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }

    public int getPad() {
        return pad;
    }

    public void setPad(int pad) {
        this.pad = pad;
    }

    public int getGridColor() {
        return gridColor;
    }

    public void setGridColor(int gridColor) {
        this.gridColor = gridColor;
    }

    @Override
    public DataPoint getPointFor(MotionEvent event) {
        // TODO much of this is redundant with draw - refactor to one or more methods
        Rect r = getBounds();
        int xstep = (r.width()-(margin))/(dataPoints.isEmpty()?10:dataPoints.size());
        int xpos = margin;
        for(DataPoint point: dataPoints) {
            Rect rect = getBarBounds(point.getValue(), getMaxValue(),
                    xpos+pad, 0, xstep-(2*pad), r.height()-margin);
            if(rect.contains((int)event.getX(), (int)event.getY())) {
                System.out.println("Bar: "+point.getLabel()+" = "+point.getValue());
                return point;
            }
            xpos += xstep;
        }
        return null;
    }
}
