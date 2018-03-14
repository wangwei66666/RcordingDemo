package com.linkever.jni.study.rcordingdemo.manager;


import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Author:      WW
 * Date:        2018/3/14 21:09
 * Description: This is MediaManager
 * TODO 可设置MediaManager为单例
 */
public class MediaManager {
    private static MediaPlayer mp;
    private static boolean isPause;

    public static void playAudio(String path, MediaPlayer.OnCompletionListener listener) {
        if (mp == null) {
            mp = new MediaPlayer();
            //通过对MediaPlayer设置onErrorListener来监听错误
            mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mp.reset();
                    return false;
                }
            });
        } else {
            mp.reset();
        }

        try {
            //设置音频流的类型
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //添加OnCompletionListener,为Media Player的播放完成事件绑定事件监听器
            mp.setOnCompletionListener(listener);
            //设置路径
            mp.setDataSource(path);
            //准备播放（装载音频），调用此方法会使MediaPlayer进入Prepared状态。
            mp.prepare();
            //开始或恢复播放
            mp.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stop() {
        if (mp != null) {
            //停止播放
            mp.stop();
        }
    }


    public static void pause() {
        //不为空且正在播放
        if (mp != null && mp.isPlaying()) {
            //暂停播放
            mp.pause();
            isPause = true;
        }
    }

    public static void resume() {
        //不为空且停止了
        if (mp != null && isPause) {
            //开始或恢复播放
            mp.start();
            isPause = false;
        }
    }

    public static void release() {
        if (mp != null) {
            //释放媒体资源
            mp.release();
            mp = null;
        }
    }
}
