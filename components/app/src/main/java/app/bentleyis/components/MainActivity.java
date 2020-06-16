
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

package app.bentleyis.components;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.LinkedList;

import app.bentleyis.graphing.BarChartDrawable;
import app.bentleyis.graphing.DataPoint;
import app.bentleyis.graphing.DataSet;
import app.bentleyis.graphing.LineChartDrawable;
import app.bentleyis.graphing.LineType;
import app.bentleyis.graphing.PieChartDrawable;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    PieChartDrawable m_pieChartDrawable;
    BarChartDrawable m_barChartDrawable;
    LineChartDrawable m_lineChartDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinkedList<DataPoint> points = new LinkedList<>();
        DataPoint dataPoint = new DataPoint();
        dataPoint.setColorARGB(0xffff0000);
        dataPoint.setLabel("one");
        dataPoint.setValue(60.0);
        points.add(dataPoint);
        dataPoint = new DataPoint();
        dataPoint.setColorARGB(0xff007f00);
        dataPoint.setLabel("two");
        dataPoint.setValue(20.0);
        points.add(dataPoint);
        dataPoint = new DataPoint();
        dataPoint.setColorARGB(0xff0000ff);
        dataPoint.setLabel("three");
        dataPoint.setValue(10.0);
        points.add(dataPoint);

        ImageView iv = findViewById(R.id.imageView1);
        m_pieChartDrawable = new PieChartDrawable();
        m_pieChartDrawable.setData(points);
        m_pieChartDrawable.setInnerRadius(100);
        m_pieChartDrawable.setTextColor(0xffffffff);
        iv.setImageDrawable(m_pieChartDrawable);
        iv.setOnTouchListener(this);

        iv = findViewById(R.id.imageView2);
        m_barChartDrawable = new BarChartDrawable();
        m_barChartDrawable.setDataPoints(points);
        m_barChartDrawable.setMargin(100);

        iv.setImageDrawable(m_barChartDrawable);
        iv.setOnTouchListener(this);

        iv = findViewById(R.id.imageView3);
        m_lineChartDrawable = new LineChartDrawable();
        DataSet dataSet = new DataSet();
        dataSet.setColorARGB(0xffd4af37);
        dataSet.setLineWidth(5);
        dataSet.setDataPoints(points);
        dataSet.setLabel("data set");
        dataSet.setLineType(LineType.CURVE);
        m_lineChartDrawable.addDataSet(dataSet);
        m_lineChartDrawable.setMargin(100);
        m_lineChartDrawable.setCenterOrigin(false);

        iv.setImageDrawable(m_lineChartDrawable);
        iv.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getAction() != MotionEvent.ACTION_DOWN)
            return false;
        switch (view.getId()){
            case R.id.imageView1:
                // this gives us back the details of what was clicked so that we can
                // display tool-tip or other information - independent of the drawable.
                m_pieChartDrawable.getPointFor(motionEvent);
                break;
            case R.id.imageView2:
                m_barChartDrawable.getPointFor(motionEvent);
                break;
            case R.id.imageView3:
                m_lineChartDrawable.getPointFor(motionEvent);
                break;
        }
        return true;
    }
}
