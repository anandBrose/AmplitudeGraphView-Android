package com.anand.brose.voicerecorder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.anand.brose.graphviewlibrary.GraphView;

import java.io.File;
import java.util.List;



public class MainActivity extends AppCompatActivity {

    public static final String SCALE = "scale";
    public static final String OUTPUT_DIRECTORY = "VoiceRecorder";
    public static final String OUTPUT_FILENAME = "recorder.mp3";
    private static final int MY_PERMISSIONS_REQUEST_CODE = 0;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    int scale = 8;
    private GraphView graphView;
    private VoiceRecorder recorder;
    private List samples;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        graphView = (GraphView) findViewById(R.id.graphView);
        graphView.setGraphColor(Color.rgb(255,255,255));
        graphView.setCanvasColor(Color.rgb(20,20,20));
        graphView.setTimeColor(Color.rgb(255, 255, 255));
        recorder = VoiceRecorder.getInstance();
        if (recorder.isRecording()) {
            ((Button) findViewById(R.id.control)).setText(getResources().getString(R.string.stop));
            recorder.startPlotting(graphView);
        }
        if (savedInstanceState != null) {
            scale = savedInstanceState.getInt(SCALE);
            graphView.setWaveLengthPX(scale);
            if (!recorder.isRecording()) {
                samples = recorder.getSamples();
                graphView.showFullGraph(samples);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SCALE, scale);
        super.onSaveInstanceState(outState);
    }

    public void zoomIn(View v) {
        scale = scale + 1;
        if (scale > 15) {
            scale = 15;
        }
        graphView.setWaveLengthPX(scale);
        if (!recorder.isRecording()) {
            graphView.showFullGraph(samples);
        }
    }

    public void zoomOut(View v) {
        scale = scale - 1;
        if (scale < 2) {
            scale = 2;
        }
        graphView.setWaveLengthPX(scale);
        if (!recorder.isRecording()) {
            graphView.showFullGraph(samples);
        }
    }

    public void controlClick(View v) {
        if (recorder.isRecording()) {
            ((Button) findViewById(R.id.control)).setText(this.getResources().getString(R.string.record));
            graphView.stopPlotting();
            samples = recorder.stopRecording();
            graphView.showFullGraph(samples);
        } else if(checkRecordPermission()&&checkStoragePermission()){
            graphView.reset();
            String filepath = Environment.getExternalStorageDirectory().getPath();
            File file = new File(filepath, OUTPUT_DIRECTORY);
            if (!file.exists()) {
                file.mkdirs();
            }
            recorder.setOutputFilePath(file.getAbsoluteFile() + "/" + OUTPUT_FILENAME);
            recorder.startRecording();
            recorder.startPlotting(graphView);
            ((Button) findViewById(R.id.control)).setText(this.getResources().getString(R.string.stop));
        }else{
            requestPermissions();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        graphView.stopPlotting();
        if (recorder.isRecording()) {
            recorder.stopRecording();
        }
        if (recorder != null) {
            recorder.release();
        }
    }
    public void requestPermissions(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_CODE);

        } else {
            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_CODE);
            // MY_PERMISSIONS_REQUEST_CODE is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    private boolean checkRecordPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

}