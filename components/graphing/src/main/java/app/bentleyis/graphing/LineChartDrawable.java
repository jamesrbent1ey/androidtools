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

public class LineChartDrawable extends AbstractChartDrawable {
    int axisColor = 0xff000000;
    int gridColor = 0xffAFAFAF;
    boolean centerOrigin = false;
    LinkedList<DataPoint> dataPoints = new LinkedList<>();
    int margin = 30;
    boolean showGrid = true;

    @Override
    public void draw(Canvas canvas) {
        Rect r = getBounds();
        r = new Rect(
                r.left+margin,
                r.top+margin,
                r.right-margin,
                r.bottom-margin
        );

        // push in the bounds to allow for additional margins
        displayGraph(r, canvas);
    }

    private void displayGraph(Rect r, Canvas canvas) {
        Paint axisPaint = PaintUtilities.getPaint(axisColor);

        if(centerOrigin) {
            // draw y axis
            canvas.drawLine(r.centerX(),r.top,r.centerX(),r.bottom,axisPaint);
            // draw x axis
            canvas.drawLine(r.right, r.centerY(),r.left,r.centerY(),axisPaint);
            showCrosshairs(canvas,r);
        } else {
            // draw y axis
            canvas.drawLine(r.left,r.top,r.left,r.bottom,axisPaint);
            // draw x axis
            canvas.drawLine(r.right, r.bottom,r.left,r.bottom,axisPaint);
            showGrid(canvas,r);
        }

        plotPoints(r,canvas);
    }

    private void plotPoints(Rect r, Canvas canvas) {
        double max = findMax();

        // plot the points where the point position is on the x axis and value is on the y axis
        // use point's position if not NaN
        int minx = findMinX();
        int maxx = findMaxX();
        for(int index = 0; index < dataPoints.size(); index++) {
            DataPoint dataPoint = dataPoints.get(index);
            Paint paint = PaintUtilities.getPaint(dataPoint.getColorARGB());

            // first get the correct value - normalized
            double x = ((dataPoint.getPosition() != Double.MIN_VALUE?dataPoint.getPosition():index+1) - minx);
            double percentagex = x/(maxx-minx);
            // now adjust to x coordinate
            x = r.width() * percentagex;

            double y = dataPoint.getValue();
            double percentagey = y/max;
            y = r.height() * percentagey;
            // now flip it so it's bottom relative
            y = r.bottom - y;

            // now take away half the radius. circle appears to draw x,y = bottom of circle not center
            y += dataPoint.getPointRadius()/2;

            canvas.drawCircle((float)x+r.left, (float)y, dataPoint.getPointRadius(), paint);
        }
    }

    private int findMaxX() {
        double max = Double.MIN_VALUE;
        for(DataPoint point: dataPoints) {
            if(point.getPosition() > max) {
                max = point.getPosition();
            }
        }
        if(max == Double.MIN_VALUE)
            return dataPoints.size()+1;
        return (int)(max+1.0);
    }

    private int findMinX() {
        double min = Double.MAX_VALUE;
        for(DataPoint point: dataPoints) {
            if(point.getPosition() < min) {
                min = point.getPosition();
            }
        }
        if(min == Double.MAX_VALUE || min == Double.MIN_VALUE)
            return 0;
        return (int)Math.min(min-1.0,0);
    }

    private double findMax() {
        double max = Double.MIN_VALUE;
        for(DataPoint point: dataPoints) {
            if(point.getValue() > max) {
                max = point.getValue();
            }
        }
        return max;
    }

    private void showCrosshairs(Canvas canvas, Rect r) {
        int stepx = r.width()/10;
        int stepy = r.height()/10;
        int lenx = showGrid?r.width():30;
        int leny = showGrid?r.height():30;
        Paint color = PaintUtilities.getPaint(gridColor);

        for(int i = stepx; i < r.width()/2; i += stepx) {
            canvas.drawLine(r.centerX() + i, r.centerY() - (leny/2),
                    r.centerX() + i, r.centerY() + (leny/2), color);
            canvas.drawLine( r.centerX() - i, r.centerY() - (leny/2),
                    r.centerX() - i, r.centerY() + (leny/2), color);
        }

        for(int i = stepy; i < r.height()/2; i += stepy) {
            canvas.drawLine(r.centerX() - (lenx/2), r.centerY() + i,
                    r.centerX() + (lenx/2), r.centerY() + i, color);
            canvas.drawLine( r.centerX() - (lenx/2), r.centerY() - i,
                    r.centerX() + (lenx/2), r.centerY() - i, color);
        }
    }

    private void showGrid(Canvas canvas, Rect r) {
        if(!showGrid) {
            return;
        }
        int stepx = r.width()/10;
        int stepy = r.height()/10;
        int lenx = showGrid?r.width():15;
        int leny = showGrid?r.height():15;
        Paint color = PaintUtilities.getPaint(gridColor);

        for(int i = stepx; i < r.width(); i += stepx) {
            canvas.drawLine(r.left + i, r.bottom - leny,
                    r.left + i, r.bottom, color);
        }

        for(int i = stepy; i < r.height(); i += stepy) {
            canvas.drawLine(r.left, r.bottom - i,
                    r.left + lenx, r.bottom -i, color);
        }
    }

    public int getAxisColor() {
        return axisColor;
    }

    /**
     * Set the color to display the axis in. Grid is always displayed in light gray
     * @param axisColor
     */
    public void setAxisColor(int axisColor) {
        this.axisColor = axisColor;
    }

    public boolean isCenterOrigin() {
        return centerOrigin;
    }

    /**
     * Instruct the engine on how to display the origin. Default is false
     * @param centerOrigin origin in center if true. Otherwise only positive quadrant shown
     */
    public void setCenterOrigin(boolean centerOrigin) {
        this.centerOrigin = centerOrigin;
    }

    public LinkedList<DataPoint> getDataPoints() {
        return dataPoints;
    }

    /**
     * Set the points to display. Note that if the set includes negative
     * values, center origin will be automatically selected.
     * @param dataPoints
     */
    public void setDataPoints(LinkedList<DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
        for(DataPoint point: dataPoints) {
            if(point.getValue() < 0) {
                setCenterOrigin(true);
                break;
            }
        }
    }

    public int getMargin() {
        return margin;
    }

    /**
     * Set the margin, in pixels, surrounding the graph
     * @param margin
     */
    public void setMargin(int margin) {
        this.margin = margin;
    }

    public boolean isShowGrid() {
        return showGrid;
    }

    /**
     * Turn on/off the grid display
     * @param showGrid
     */
    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }

    @Override
    public DataPoint getPointFor(MotionEvent event) {
        Rect r = getBounds();
        r = new Rect(
                r.left+margin,
                r.top+margin,
                r.right-margin,
                r.bottom-margin
        );

        double max = findMax();

        // plot the points where the point position is on the x axis and value is on the y axis
        // use point's position if not NaN
        int minx = findMinX();
        int maxx = findMaxX();

        for(int index = 0; index < dataPoints.size(); index++) {
            DataPoint dataPoint = dataPoints.get(index);

            // first get the correct value - normalized
            double x = ((dataPoint.getPosition() != Double.MIN_VALUE?dataPoint.getPosition():index+1) - minx);
            double percentagex = x/(maxx-minx);
            // now adjust to x coordinate
            x = r.width() * percentagex;

            double y = dataPoint.getValue();
            double percentagey = y/max;
            y = r.height() * percentagey;
            // now flip it so it's bottom relative
            y = r.bottom - y;

            float rad = dataPoint.getPointRadius()/2;

            // now take away half the radius. circle appears to draw x,y = bottom of circle not center
            y += rad;

            x += r.left;
            // for a larger touch point - 34px is std
            rad = Math.max(rad,17f);

            if(event.getX() <= x+rad && event.getX() >= x-rad  &&
                    event.getY() <= y+rad && event.getY() >= y-rad) {
                System.out.println("Point: "+dataPoint.getLabel()+" "+dataPoint.getValue());
                return dataPoint;
            }
        }
        return null;
    }
}
