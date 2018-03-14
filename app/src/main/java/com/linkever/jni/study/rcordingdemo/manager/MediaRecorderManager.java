package com.linkever.jni.study.rcordingdemo.manager;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Author:      WW
 * Date:        2018/3/13 22:17
 * Description: This is MediaRecorderManager
 */

public class MediaRecorderManager {

    private MediaRecorder mMediaRecorder;
    private static String mDir;
    private String mCurrentFilePath;
    private IRecorderStateListener mIRecorderStateListener;
    /**
     * 用来判断MediaRecorder的准备情况，true 准备结束。
     */
    private boolean isPrepared;

    private MediaRecorderManager() {
    }

    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }

    private static class Holder {
        private static MediaRecorderManager instatnce = new MediaRecorderManager();
    }

    public static MediaRecorderManager getInstance(String dir) {
        mDir = dir;
        return Holder.instatnce;
    }

    /**
     * @param mIRecorderStateListener
     */
    public void setIRecorderStateListener(IRecorderStateListener mIRecorderStateListener) {
        this.mIRecorderStateListener = mIRecorderStateListener;
    }

    /**
     * 准备
     */
    public void prepareRecorder() {
        isPrepared = false;
        try {

            File dir = new File(mDir);
            if (!dir.exists()) {
                //不存在，创建
                dir.mkdirs();
            }
            String fileName = generateFileName();
            File file = new File(dir, fileName);
            mCurrentFilePath = file.getAbsolutePath();

            mMediaRecorder = new MediaRecorder();
            //设置输出文件的路径
            mMediaRecorder.setOutputFile(mCurrentFilePath);
            //设置MediaRecoreser的录制音源为麦克风
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置音频格式为AMR_NB
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            //设置音频的编码为amr
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            //准备录制
            mMediaRecorder.prepare();
            //开始录制
            mMediaRecorder.start();
            isPrepared = true;
            if (mIRecorderStateListener != null) {
                mIRecorderStateListener.wellPrepared();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 随机生成文件的名称
     *
     * @return
     */
    private String generateFileName() {
//        return UUID.randomUUID().toString() + ".amr";
        return UUID.randomUUID().toString() + ".amr";
    }

    /**
     * 获取音量等级
     *
     * @return
     */
    public int getVoiceLevel(int maxLevel) {
        int level = 1;
        if (isPrepared&&mMediaRecorder!=null) {
            //try-catch 防止音量等级计算出错
            try {
                //mMediaRecorder.getMaxAmplitude()获取在前一次调用此方法之后录音中出现的最大振幅 值在1-32768之间
                // level值在1-7之间
                level = maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
                return level;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        return level;
    }

    /**
     * 释放MediaRecorder资源
     */
    public void release() {
        if(mMediaRecorder!=null){
            //停止录制
            mMediaRecorder.stop();
            //释放资源
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    /**
     * 释放资源
     */
    public void cancel() {
        release();
        if (mCurrentFilePath != null) {
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }

    }

    /**
     * 回调准备完毕
     */
    public interface IRecorderStateListener {
        void wellPrepared();
    }

}
