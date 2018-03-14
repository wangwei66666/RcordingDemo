package com.linkever.jni.study.rcordingdemo.bean;

/**
 * Author:      WW
 * Date:        2018/3/14 12:53
 * Description: This is Recorder
 */

public class Recorder {
    float time;
    String filePath;

    public Recorder(float time, String filePath) {
        this.time = time;
        this.filePath = filePath;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
