# Graphing Module 
The Graphing Module provides visual graphs and charts for the Android environment.

Each Chart is represented as a Drawable that may be inserted into an ImageView. The Drawables
are light to allow for styling in the encapsulating View. For instance, chart titles and legends
can be added better outside of Canvas.

Each Drawable will return the DataPoint associated with an area that has been touched/clicked. The
call to resolve the DataPoint is made from a View.OnTouchListener

PieChartDrawable: Allows for rendering of Pie charts, Donut charts, and Gauges
BarChartDrawable: Allows for rendering Bar charts
LineChartDrawable: Allows for rendering line charts and scatter plots.
