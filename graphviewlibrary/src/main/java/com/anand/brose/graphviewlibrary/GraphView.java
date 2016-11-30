package com.anand.brose.graphviewlibrary;

        import android.content.Context;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Paint;
        import android.graphics.Path;
        import android.graphics.RectF;
        import android.util.AttributeSet;
        import android.util.Log;
        import android.view.Gravity;
        import android.view.MotionEvent;
        import android.view.SurfaceHolder;
        import android.view.SurfaceView;
        import android.view.ViewGroup;
        import android.widget.FrameLayout;
        import android.widget.HorizontalScrollView;

        import java.util.HashMap;
        import java.util.List;
        import java.util.Set;

/**
 * Created by Anand on 25-03-2016.
 * Graph view extends Horizontal scrollview containing a surface view.
 * This view can be used for plotting values over time both in real time and post processed values
 * The graph is plotted using a list containing {@link WaveSample} values
 */
public class GraphView extends HorizontalScrollView {

    private static final String TAG = "GraphViewLibrary";
    private double graphXOffset = 0.75;//X position to start plotting
    private int timeScale = 5 * 1000;//Put time marker for every 5 sec
    private int maxAmplitude = 35000;//Maximum possible amplitude
    private double defaultWaveLength = 2.6;//default sine wave length
    private int timeMarkerSize = 50;
    private boolean drawFullGraph = false;
    private GraphSurfaceView graphSurfaceView;
    private List<WaveSample> pointList;
    private Paint paint;
    private Paint markerPaint;
    private Paint timePaint;
    private Paint needlePaint;
    private int canvasColor = Color.rgb(0, 0, 0);
    private int markerColor = Color.argb(160, 30, 30, 30);
    private int graphColor = Color.rgb(255, 255, 255);
    private int timeColor = Color.rgb(250, 250, 250);
    private int needleColor = Color.rgb(250, 0, 0);
    private boolean pausePlotting = false;
    private FrameLayout frame;
    private Context context;
    private volatile float move = 0;

    public GraphView(Context context) {
        super(context);
        init(context);
    }

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GraphView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.context = context;

        //FrameLayout config to hold SurfaceView
        frame = new FrameLayout(context);
        graphSurfaceView = new GraphSurfaceView(context);
        frame.addView(graphSurfaceView);
        this.setBackgroundColor(canvasColor);
        frame.setBackgroundColor(canvasColor);
        frame.setLayoutParams(new HorizontalScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.LEFT));
        frame.requestLayout();
        this.addView(frame);
    }

    /**
     * Get maximum value in y axis
     */
    public int getMaxAmplitude() {
        return maxAmplitude;
    }

    /**
     * Set maximum value in y axis
     *
     * @param maxAmplitude default is 35000
     */
    public void setMaxAmplitude(int maxAmplitude) {
        this.maxAmplitude = maxAmplitude;
    }

    /**
     * Get time scale for time markers y axis
     */
    public int getTimeScale() {
        return timeScale;
    }

    /**
     * Set time scale in milliseconds for time markers in y axis
     *
     * @param timeScale -  default time is marked every 5 sec
     */
    public void setTimeScale(int timeScale) {
        this.timeScale = timeScale;
    }

    /**
     * Get starting point from where plot starts for realtime plotting
     */
    public double getGraphXOffset() {
        return graphXOffset;
    }

    /**
     * Set starting point from where plot starts for realtime plotting
     *
     * @param graphXOffset - default is 0.75
     */
    public void setGraphXOffset(double graphXOffset) {
        if (graphXOffset > 0 && graphXOffset < 1) {
            this.graphXOffset = graphXOffset;
        }
    }

    /**
     * Set plain background canvas color
     *
     * @param canvasColor default is Color.rgb(101, 76, 104)
     */
    public void setCanvasColor(int canvasColor) {
        this.canvasColor = canvasColor;
        this.setBackgroundColor(canvasColor);
        frame.setBackgroundColor(canvasColor);
    }

    /**
     * Set marker background color
     *
     * @param markerColor default is Color.argb(160, 30, 30, 30)
     */
    public void setMarkerColor(int markerColor) {
        this.markerColor = markerColor;
        markerPaint.setColor(markerColor);
    }

    /**
     * Set color for waves
     *
     * @param graphColor default is Color.rgb(255, 255, 255)
     */
    public void setGraphColor(int graphColor) {
        this.graphColor = graphColor;
        paint.setColor(graphColor);
    }

    /**
     * Set text color for time markers
     *
     * @param timeColor default is Color.rgb(250, 250, 250)
     */
    public void setTimeColor(int timeColor) {
        this.timeColor = timeColor;
        timePaint.setColor(timeColor);
    }

    /**
     * Set color for needle that displays current amplitude
     *
     * @param needleColor default is Color.rgb(250, 0, 0)
     */
    public void setNeedleColor(int needleColor) {
        this.needleColor = needleColor;
        needlePaint.setColor(needleColor);
    }

    /**
     * Returns state of plotting, use {@link #resume()} and {@link #pause()} for resume/pause
     *
     * @return true if paused
     */
    public boolean isPaused() {
        return pausePlotting;
    }

    /**
     * Pause the wave graph plotting, use {@link #resume()} for resume
     */
    public void pause() {
        this.pausePlotting = true;
    }

    /**
     * Resume the wave graph plotting, use {@link #pause()} for pause
     */
    public void resume() {
        this.pausePlotting = false;
    }

    /**
     * Show full graph of recorded wave, stopPlotting should be called if plotting in progress
     *
     * @param waveSamples {@link WaveSample} list
     */
    public void showFullGraph(List<WaveSample> waveSamples) {
        graphSurfaceView.setMasterList(waveSamples);
        graphSurfaceView.showFullGraph();
    }

    /**
     * Assign list that holds samples
     *
     * @param list
     */
    public void setMasterList(List<WaveSample> list) {
        graphSurfaceView.setMasterList(list);
    }


    public void startPlotting() {
        graphSurfaceView.startPlotting();
    }

    /**
     * reset the graph before each start plotting and show full graph
     */
    public void reset() {
        graphSurfaceView.resetDimensions();
    }

    /**
     * Stop plotting the wave graph
     */
    public void stopPlotting() {
        graphSurfaceView.stopPlotting();
    }

    /**
     * Set wave length in px
     *
     * @param scale can be any value from 2 to 15
     */
    public void setWaveLengthPX(int scale) {
        if (scale < 2) {
            scale = 2;
        }
        if (scale > 15) {
            scale = 15;
        }

        graphSurfaceView.setWaveLength(scale);
    }

    private float x1, x2;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!drawFullGraph) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                x2 = event.getX();
                float delta = x2 - x1;
                x1 = x2;
                move = move + delta;
                graphSurfaceView.drawFullGraph();
                break;
        }
        return super.onTouchEvent(event);
    }

    private class GraphSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
        private SurfaceHolder holder;
        private Context context;
        private Thread _plottingThread;
        //Current rendered surface view dimensions
        private int height;
        private int halfHeight;
        private int width;

        private volatile int waveLength;
        private volatile boolean isRunning = false;
        private volatile boolean stop = false;
        private int widthForFullGraph = 50;
        int listMasterSize = 0;
        int redrawCount = 0;
        int freezCount = 0;
        int sleepTime = 5;
        private int deltaWidth;

        public GraphSurfaceView(Context context) {
            super(context);
            init(context);
        }

        public GraphSurfaceView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }

        public GraphSurfaceView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init(context);
        }

        public void setWaveLength(int scale) {
            waveLength = scale;
        }

        public void init(Context context) {
            this.context = context;
            //Setting up surface view with default dimensions
            this.setLayoutParams(new FrameLayout.LayoutParams(100, 100));

            //Set wave length in mm
            waveLength = (int) (context.getResources().getDisplayMetrics().xdpi / (25.4 * defaultWaveLength));

            holder = getHolder();
            holder.addCallback(this);

            //Paint config for waves
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(graphColor);
            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.STROKE);

            //Paint config for amplitude needle
            needlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            needlePaint.setColor(needleColor);
            needlePaint.setStrokeWidth(1);
            needlePaint.setStyle(Paint.Style.STROKE);

            //Paint config for time text
            timePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            timePaint.setColor(timeColor);
            timePaint.setStrokeWidth(1);
            timePaint.setTextSize(timeMarkerSize / this.context.getResources().getDisplayMetrics().scaledDensity);
            timePaint.setStyle(Paint.Style.STROKE);

            //Paint config for right side marker
            markerPaint = new Paint();
            markerPaint.setColor(markerColor);
            markerPaint.setStyle(Paint.Style.STROKE);
        }

        /**
         * Function to calculate time to x and amplitude to y mapping of each sample for current frame
         */
        private void processAmplitude() {
            //calculate sleep time and redraw count for smooth wave movement
            if (pointList.size() != listMasterSize) {//new sample found since last frame
                listMasterSize = pointList.size();
                freezCount = -1;
                redrawCount = 0;
            } else {//same no of samples since last frame so move the wave to left by incrementing redrawCount
                redrawCount++;
                if (redrawCount > waveLength) {//Moved too much left still no new sample
                    freezCount++;
                    if (freezCount > 0) {//Increase sleep time to avoid wave redraw
                        sleepTime = sleepTime + 1;
                    } else if (freezCount < 0) {
                        sleepTime = sleepTime - 1;
                        if (sleepTime < 0) {
                            sleepTime = 0;
                        }
                    }
                    redrawCount = waveLength;
                }
            }
            //Path variable for wave
            Path graphPath = new Path();

            //HashMap to hold time markers position
            HashMap<Integer, String> timeMap = new HashMap<>();

            //Initialize start position for wave path
            int x = (int) (width * graphXOffset);
            int listSize = pointList.size() - 1;

            //Path variable for marker
            Path markerPath = new Path();
            markerPaint.setStrokeWidth((float) (width - (width * graphXOffset)));
            markerPath.moveTo(x + (width / 8), 0);
            markerPath.lineTo(x + (width / 8), height);

            //Path variable for needle
            Path needlePath = new Path();

            /*
            Draw sine waves for last 'n' no of samples.
            'n' is calculated from no x - direction pixels available in surface view from width * 3/4 to 0 - wavelength.
            Each sample will be drawn as a sine wave with waveLength as width
            */
            for (int i = listSize - 1; x >= 0 - waveLength; x = x - waveLength) {
                if (i >= 0) {
                    if (i == 0) {
                        timeMap.put(x - redrawCount, "00:00"); //put time 00.00 for first sample
                    } else {
                        long currentSampleTime = pointList.get(i).getTime();
                        long lastSampleTime = pointList.get(i - 1).getTime();
                        //put a time marker if current sampled time passes timeScale sec
                        if (lastSampleTime % timeScale > currentSampleTime % timeScale) {
                            timeMap.put(x - redrawCount, formatTime(currentSampleTime));
                        }
                    }

                    int amplitude = (int) pointList.get(i).getAmplitude();
                    drawAmplitude(amplitude, x, graphPath, needlePath);

                }
                i--;
            }
            renderAmplitude(timeMap, graphPath, markerPath, needlePath);
        }

        /**
         * Draw sine wave path for current sample at x position with amplitude and needle path to show current amplitude
         */
        private Path drawAmplitude(int amplitude, int x, Path graphPath, Path needlePath) {

            /* Calculate no y pixels for sine wave magnitude from amplitude */
            amplitude = halfHeight * amplitude / maxAmplitude;
            /*  If current sample is the latest then move needle to show current amplitude    */
            if (x == (int) (width * graphXOffset)) {
                needlePath.moveTo((float) (width * graphXOffset), halfHeight - amplitude);
                needlePath.lineTo(width, halfHeight - amplitude);
            }
            if (amplitude > 0) {

                /*  Below code can be customized to support more graph types
                 *  Draw a sine wave from x-redrawCount to x - redrawCount + waveLength with positive magnitude at halfHeight - amplitude and negative at halfHeight + amplitude    */
                RectF oval = new RectF();
                oval.set(x - redrawCount, halfHeight - amplitude, x - redrawCount + (waveLength / 2), halfHeight + amplitude);
                graphPath.addArc(oval, 180, 180);
                oval.set(x - redrawCount + (waveLength / 2), halfHeight - amplitude, x - redrawCount + (waveLength), halfHeight + amplitude);
                graphPath.addArc(oval, 0, 180);
            } else {
                /*  Draw simple line to represent 0 */
                graphPath.moveTo(x - redrawCount, halfHeight);
                graphPath.lineTo(x - redrawCount + waveLength, halfHeight);
            }
            return graphPath;
        }

        /**
         * Draw all the path on SurfaceView canvas
         */
        private void renderAmplitude(HashMap<Integer, String> timeMap, Path tempPath, Path markerPath, Path needlePath) {
            Canvas tempCanvas = null;
            if (holder.getSurface().isValid()) {//SurfaceView available
                try {
                    tempCanvas = holder.lockCanvas();
                    synchronized (holder) {
                        if (tempCanvas != null) {
                            /*  Clean SurfaceView with plain canvas color   */
                            tempCanvas.drawColor(canvasColor);

                            //Draw time texts
                            Set<Integer> keys = timeMap.keySet();
                            for (int key : keys) {
                                tempCanvas.drawText(timeMap.get(key), key, 20, timePaint);
                            }
                            /*  Draw sine waves, marker and needle  */
                            tempCanvas.drawPath(tempPath, paint);
                            if (markerPath != null) {
                                tempCanvas.drawPath(markerPath, markerPaint);
                            }
                            if (needlePath != null) {
                                tempCanvas.drawPath(needlePath, needlePaint);
                            }
                        }
                    }
                } finally {
                    if (tempCanvas != null) {
                        holder.unlockCanvasAndPost(tempCanvas);
                    }
                }
            }
            try {
                /*  Sleep the thread to reduce CPU usage and avoid same wave redrawn    */
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            Log.d(TAG, "Created");

            /*  Configure width for current mode  */
            this.setLayoutParams(new LayoutParams(GraphView.this.getWidth(), GraphView.this.getHeight()));
            /*  Continue plotting on app switches between foreground and background  */
            if (isRunning && !_plottingThread.isAlive()) {
                startPlotting();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            Log.d(TAG, "Changed");
            //Redraw full graph if needed
            if (drawFullGraph) {
                drawFullGraph();
            }
            //Reset will get current rendered dimensions
            reset();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            Log.d(TAG, "Destroyed");
            //Stop the plotting if app goes to background
            this.stop = true;
            if (_plottingThread != null) {
                _plottingThread.interrupt();
            }
        }

        /**
         * Reset the surface view with plain canvas color and get current rendered dimensions
         */
        public void reset() {
            height = getHeight();
            halfHeight = height / (2);
            width = getWidth();
            Canvas tempCanvas = null;
            if (holder.getSurface().isValid()) {
                try {
                    tempCanvas = holder.lockCanvas();
                    synchronized (holder) {
                        if (tempCanvas != null) {
                            tempCanvas.drawColor(canvasColor);
                        }
                    }
                } finally {
                    if (tempCanvas != null) {
                        holder.unlockCanvasAndPost(tempCanvas);
                    }
                }
            }
        }

        /**
         * set master list that holds the samples
         *
         * @param list
         */
        public void setMasterList(List<WaveSample> list) {
            pointList = list;
        }

        /**
         * Calculate no of pixels needed in x direction to display all available samples in the point list and set it as surface view's width
         * Will trigger surface change after new dimensions
         */
        public void showFullGraph() {
            if (pointList == null) {
                return;
            }
            if (pointList.size() == 0) {
                return;
            }
            drawFullGraph = true;
            reset();
            this.stop = true;
            isRunning = false;
            if (_plottingThread != null) {
                _plottingThread.interrupt();
            }
            widthForFullGraph = pointList.size() * waveLength + 50;
            drawFullGraph();
        }


        /**
         * Same as processAmplitude function
         */
        private void drawFullGraph() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    deltaWidth = width - widthForFullGraph;
                    if (move > 0) {
                        move = 0;
                    }
                    if (deltaWidth < 0) {
                        if (move < deltaWidth) {
                            move = deltaWidth;
                        }
                    } else {
                        move = 0;
                    }
                    int sampleNo;
                    int x = 0;
                    if (widthForFullGraph < width) {
                        sampleNo = pointList.size();
                    } else {
                        sampleNo = (int) ((width + waveLength + Math.abs(move)) / waveLength);
                    }
                    if (sampleNo > pointList.size()) {
                        sampleNo = pointList.size();
                    }
                    HashMap<Integer, String> timeMap1 = new HashMap<>();
                    Path tempPath1 = new Path();

                    for (int i = (int) (Math.abs(move) / waveLength); i <= sampleNo - 1; i++) {
                        if (i == 0) {
                            timeMap1.put(x, "00:00");
                        } else {
                            long currentSampleTime = pointList.get(i).getTime();
                            long lastSampleTime = pointList.get(i - 1).getTime();
                            if (lastSampleTime % timeScale > currentSampleTime % timeScale) {
                                timeMap1.put(x, formatTime(currentSampleTime));
                            }
                        }

                        int amplitude = (int) pointList.get(i).getAmplitude();
                        amplitude = halfHeight * amplitude / maxAmplitude;
                        if (amplitude > 0) {
                            RectF oval = new RectF();
                            oval.set(x, halfHeight - amplitude, x + (waveLength / 2), halfHeight + amplitude);
                            tempPath1.addArc(oval, 180, 180);
                            oval.set(x + (waveLength / 2), halfHeight - amplitude, x + (waveLength), halfHeight + amplitude);
                            tempPath1.addArc(oval, 0, 180);
                        } else {
                            tempPath1.moveTo(x, halfHeight);
                            tempPath1.lineTo(x + waveLength, halfHeight);
                        }

                        x = x + waveLength;
                    }
                    renderAmplitude(timeMap1, tempPath1, null, null);
                }
            }).start();
        }

        /**
         * Reset the flags and start drawing thread
         */
        public void startPlotting() {
            drawFullGraph = false;
            reset();
            this.stop = false;
            isRunning = true;
            _plottingThread = new Thread(this);
            _plottingThread.start();
        }

        /**
         * Reset the flags and stop drawing thread
         */
        public void stopPlotting() {
            this.stop = true;
            isRunning = false;
            if (_plottingThread != null) {
                _plottingThread.interrupt();
            }
        }

        @Override
        public void run() {
            while (!this.stop) {
                if (!pausePlotting) {
                    processAmplitude();
                }
            }
        }

        /**
         * Make surface view to fit it's width and height to the rendered horizontal scroll view
         */
        public void resetDimensions() {
            this.setLayoutParams(new LayoutParams(GraphView.this.getWidth(), GraphView.this.getHeight()));
        }
    }

    /**
     * Convert millisecond to mm:ss string
     *
     * @param currentSampleTime in millisecond
     * @return formatted mm:ss string
     */
    private String formatTime(long currentSampleTime) {
        int seconds = (int) (currentSampleTime / 1000) % 60;
        int minutes = (int) (currentSampleTime / 60000);

        return (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds);
    }


}

