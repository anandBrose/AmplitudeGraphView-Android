# AmplitudeGraphView-Android
A simple Graph view library to plot audio signals amplitudes in real time
Can also be used to plot any values over a time



<b>DEMO</b>

[![Demo Graphview alpha](https://j.gifs.com/El1VRg.gif)](https://www.youtube.com/watch?v=0N9uHncqmVc)

<b>Steps</b>

1.Add graphview library folder as library to you project

2.Add view to your layout
```
<com.anand.brose.graphviewlibrary.GraphView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/graphView"
        android:fillViewport="true"
        android:layout_alignParentLeft="true"
        android:layout_alignParentStart="true"
        android:layout_alignParentTop="true"
        android:layout_alignParentBottom="true"></com.anand.brose.graphviewlibrary.GraphView>
```

3.Set max amplitude and WaveSample list to graphview
  
```
graphView.setMaxAmplitude(100);
private List<WaveSample> pointList = new ArrayList<>();
graphView.setMasterList(pointList);
graphView.startPlotting();
```


3.start plotting
  
```
graphView.startPlotting();
....
....
/*add samples to the list during runtime
pointList.add(new WaveSample(timeStampInMillisecond, amplitude));
....
....
graphView.stopPlotting();
```



# Documentation

## `public class GraphView extends HorizontalScrollView`

Created by Anand on 25-03-2016. Graph view extends Horizontal scrollview containing a surface view. This view can be used for plotting values over time both in real time and post processed values The graph is plotted using a list containing {@link WaveSample} values
## `public void stopPlotting()`

Stop plotting the wave graph

## `public void startPlotting()`

Start plotting the wave graph
## `public void setCanvasColor(int canvasColor)`

Set plain background canvas color

 * **Parameters:** `canvasColor` — default is Color.rgb(101, 76, 104)

## `public void setMarkerColor(int markerColor)`

Set marker background color

 * **Parameters:** `markerColor` — default is Color.argb(160, 30, 30, 30)

## `public void setGraphColor(int graphColor)`

Set color for waves

 * **Parameters:** `graphColor` — default is Color.rgb(255, 255, 255)

## `public void setTimeColor(int timeColor)`

Set text color for time markers

 * **Parameters:** `timeColor` — default is Color.rgb(250, 250, 250)

## `public void setNeedleColor(int needleColor)`

Set color for needle that displays current amplitude

 * **Parameters:** `needleColor` — default is Color.rgb(250, 0, 0)

## `public int getMaxAmplitude()`

Get maximum value in y axis

## `public void setMaxAmplitude(int maxAmplitude)`

Set maximum value in y axis

 * **Parameters:** `maxAmplitude` — default is 35000

## `public int getTimeScale()`

Get time scale for time markers y axis

## `public void setTimeScale(int timeScale)`

Set time scale in milliseconds for time markers in y axis

 * **Parameters:** `timeScale` — -  default time is marked every 5 sec

## `public double getGraphXOffset()`

Get starting point from where plot starts for realtime plotting

## `public void setGraphXOffset(double graphXOffset)`

Set starting point from where plot starts for realtime plotting

 * **Parameters:** `graphXOffset` — - default is 0.75


## `public boolean isPaused()`

Returns state of plotting, use {@link #resume()} and {@link #pause()} for resume/pause

 * **Returns:** true if paused

## `public void pause()`

Pause the wave graph plotting, use {@link #resume()} for resume

## `public void resume()`

Resume the wave graph plotting, use {@link #pause()} for pause

## `public void showFullGraph(List<WaveSample> waveSamples)`

Show full graph of recorded wave, stopPlotting should be called if plotting in progress

 * **Parameters:** `waveSamples` — {@link WaveSample} list

## `public void setMasterList(List<WaveSample> list)`

Assign list that holds samples

 * **Parameters:** `list` — 

## `public void reset()`

reset the graph before each start plotting and show full graph

## `public void setWaveLengthPX(int scale)`

Set wave length in px

 * **Parameters:** `scale` — can be any value from 2 to 15
