package com.anand.brose.graphviewlibrary;

/**
 * Created by Anand on 25-03-2016.
 * Class for holding each sample value time to be rendered in X and amplitude in y
 */
public class WaveSample {
    private long time;
    private long amplitude;

    public WaveSample(long time, int amplitude) {
        this.time = time;
        this.amplitude = amplitude;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(long amplitude) {
        this.amplitude = amplitude;
    }
}

