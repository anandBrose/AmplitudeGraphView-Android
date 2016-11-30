package com.anand.brose.voicerecorder;

/**
 * Created by Anand on 25-03-2016.
 */
import android.media.MediaRecorder;
import android.os.Environment;

import com.anand.brose.graphviewlibrary.GraphView;
import com.anand.brose.graphviewlibrary.WaveSample;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VoiceRecorder {

    private static VoiceRecorder mInstance = null;
    private List<WaveSample> pointList = new ArrayList<>();
    private long startTime = 0;
    private Thread mRecordingThread;
    private volatile Boolean stop = false;
    private MediaRecorder myAudioRecorder;
    private String outputFilePath;
    private File tempFile;
    private GraphView graphView;

    private VoiceRecorder() {

    }

    /**
     * Get singleton instance of voice recorder
     *
     * @return Returns recorder instance
     */
    public static VoiceRecorder getInstance() {
        if (mInstance == null) {
            mInstance = new VoiceRecorder();
        }
        return mInstance;
    }

    /**
     * Start plotting real time wave graph
     *
     * @param graphView to plot
     * @return true if success
     * @see GraphView
     */
    public boolean startPlotting(GraphView graphView) {
        if (graphView != null) {
            this.graphView = graphView;
            graphView.setMasterList(pointList);
            graphView.startPlotting();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the state of recording
     *
     * @return true if currently recording
     */
    public Boolean isRecording() {
        return mRecordingThread != null && mRecordingThread.isAlive();
    }

    /**
     * Returns last recorded audio samples
     *
     * @return {@link WaveSample list}
     */
    public List getSamples() {
        return pointList;
    }

    /**
     * Stop voice recording
     *
     * @return Returns List containing audio samples, can be used to show full graph
     * @see GraphView
     */
    public List stopRecording() {
        this.stop = true;
        mRecordingThread.interrupt();
        if (graphView != null) {
            graphView.stopPlotting();
        }
        myAudioRecorder.stop();
        myAudioRecorder.reset();
        myAudioRecorder.release();
        myAudioRecorder = null;
        return pointList;
    }

    /**
     * Release the resources associated
     */
    public void release() {
        mInstance = null;
        if (isRecording()) {
            stopRecording();
        }
    }

    /**
     * Returns path of the output file
     *
     * @return Output file path
     */
    public String getOutputFilePath() {
        if (outputFilePath != null) {
            return outputFilePath;
        } else {
            return tempFile.getAbsolutePath();
        }
    }

    /**
     * Sets path of the output file, should be called before {@link #startRecording()}.
     * If not set default is sdcard/AudioRecorder/_timestamp.mp3
     *
     * @param file Output file path
     */
    public void setOutputFilePath(String file) {
        this.outputFilePath = file;
    }

    /**
     * Start voice recording, {@link #setOutputFilePath(String)} should be called before this
     */
    public void startRecording() {
        this.stop = false;
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.DEFAULT);
        if (outputFilePath == null) {
            String filepath = Environment.getExternalStorageDirectory().getPath();
            tempFile = new File(filepath, "AudioRecorder");
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }
            myAudioRecorder.setOutputFile((tempFile.getAbsolutePath() + "/" + System.currentTimeMillis() + ".mp3"));
        } else {
            myAudioRecorder.setOutputFile(outputFilePath);
        }
        try {
            myAudioRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        myAudioRecorder.start();
        pointList.clear();
        startTime = System.currentTimeMillis();
        mRecordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!VoiceRecorder.this.stop) {
                    //Add current audio sample amplitude and timestamp
                    pointList.add(new WaveSample(System.currentTimeMillis() - startTime, myAudioRecorder.getMaxAmplitude()));
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mRecordingThread.start();
    }
}
