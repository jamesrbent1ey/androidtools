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
import java.util.List;

/*
 *  TODO line of best fit and curve through points
 */

/**
 * Shows point an line plots on a grid. The grid origin may be centered to show negative range
 * and/or value.
 */
public class LineChartDrawable extends AbstractChartDrawable {
    int axisColor = 0xff000000;
    int gridColor = 0xffAFAFAF;
    boolean centerOrigin = false;
    int margin = 30;
    boolean showGrid = true;

    LinkedList<DataSet> data = new LinkedList<>();

    /**
     * Relate a DataPoint to its graph coordinates
     */
    class Point {
        double x;
        double y;
        DataPoint point;

        public Point(double x, double y, DataPoint point) {
            this.x = x;
            this.y = y;
            this.point = point;
        }
    }

    /**
     * Relate a dataset to the graph coordinates of its DataPoints
     */
    class PointSet {
        LinkedList<Point> points = new LinkedList<>();
        DataSet dataSet;
    }
    LinkedList<PointSet> pointSets = new LinkedList<>();

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

    /**
     * Display a graph in the given bounds
     * @param r
     * @param canvas
     */
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

        plotDataSets(r, canvas);
    }

    /**
     * Plot the data sets in the graph
     * @param r
     * @param canvas
     */
    private void plotDataSets(Rect r, Canvas canvas) {
        pointSets.clear();
        for(DataSet dataSet: data) {
            PointSet pointSet = plotPoints(r,canvas,dataSet.getDataPoints());
            pointSets.add(pointSet);
            pointSet.dataSet = dataSet;
            plotLine(r, canvas, pointSet);
        }
    }

    /**
     * Plot points on the graph, returning the calculated coordinate values for each plotted point
     * @param r
     * @param canvas
     * @param dataPoints
     * @return the graph coordinates for each DataPoint
     */
    private synchronized PointSet plotPoints(Rect r, Canvas canvas, List<DataPoint> dataPoints) {
        PointSet pointSet = new PointSet();
        double max = findMax(dataPoints);

        // plot the points where the point position is on the x axis and value is on the y axis
        // use point's position if not NaN
        int minx = findMinX(dataPoints);
        int maxx = findMaxX(dataPoints);
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
            pointSet.points.add(new Point(x+r.left, y, dataPoint));
        }
        return pointSet;
    }

    /**
     * Plot a line on the graph
     * @param r
     * @param canvas
     * @param pointSet
     */
    private void plotLine(Rect r, Canvas canvas, PointSet pointSet) {
        if(pointSet.dataSet.getLineType() == LineType.NONE) {
            return;
        }

        Paint paint = PaintUtilities.getPaint(pointSet.dataSet.getColorARGB());
        paint.setStrokeWidth(Math.max(1, pointSet.dataSet.getLineWidth()));
        Point previous = null;
        for(Point point: pointSet.points) {
            if(previous == null) {
                previous = point;
                continue;
            }
            // drawLines requires replication of points to draw each segment.
            // drawing individual segments here to reduce memory and not require translation to array
            canvas.drawLine((float)previous.x,(float)previous.y,
                    (float)point.x,(float)point.y, paint);
            previous = point;
        }
    }

    private int findMaxX(List<DataPoint> dataPoints) {
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

    private int findMinX(List<DataPoint> dataPoints) {
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

    private double findMax(List<DataPoint> dataPoints) {
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

    /**
     * Add a set of points to display. Note that if the set includes negative
     * values, center origin will be automatically selected.
     * @param set
     */
    public synchronized void addDataSet(DataSet set) {
        if(data.contains(set)) {
            return;
        }
        data.add(set);
        if(set.min < 0) {
            setCenterOrigin(true);
        }
    }

    public synchronized void removeDataSet(DataSet set) {
        data.remove(set);
        this.invalidateSelf();
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

    /**
     * Returns the first DataPoint found matching the given event coordinates.
     * @param event
     * @return
     */
    @Override
    public DataPoint getPointFor(MotionEvent event) {
        // more than one dataset can contain a given point. Use getPointsFor
        for(PointSet pointSet: pointSets) {
            for(Point point: pointSet.points) {
                float rad = point.point.getPointRadius()/2;
                // have to adjust so that y is center, not top
                float y = (float) (point.y+rad);
                if(event.getX() <= point.x+rad && event.getX() >= point.x-rad  &&
                    event.getY() <= y+rad && event.getY() >= y-rad) {
                    System.out.println("Point: " + point.point.getLabel() + " " + point.point.getValue());
                    return point.point;
                }
            }
        }
        return null;
    }

    public List<DataPoint> getPointsFor(MotionEvent event) {
        LinkedList<DataPoint> points = new LinkedList<>();
        for(PointSet pointSet: pointSets) {
            for(Point point: pointSet.points) {
                float rad = point.point.getPointRadius()/2;
                // have to adjust so that y is center, not top
                float y = (float) (point.y+rad);
                if(event.getX() <= point.x+rad && event.getX() >= point.x-rad  &&
                        event.getY() <= y+rad && event.getY() >= y-rad) {
                    System.out.println("Point: " + point.point.getLabel() + " " + point.point.getValue());
                    points.add(point.point);
                }
            }
        }
        return points;
    }
}
