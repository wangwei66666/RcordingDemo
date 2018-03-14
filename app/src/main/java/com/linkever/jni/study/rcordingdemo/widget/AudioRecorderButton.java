package com.linkever.jni.study.rcordingdemo.widget;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.linkever.jni.study.rcordingdemo.R;
import com.linkever.jni.study.rcordingdemo.manager.DialogManager;
import com.linkever.jni.study.rcordingdemo.manager.MediaRecorderManager;

/**
 * Author:      WW
 * Date:        2018/3/13 16:35
 * Description: This is AudioRecorderButton
 */

public class AudioRecorderButton extends android.support.v7.widget.AppCompatButton implements MediaRecorderManager.IRecorderStateListener {

    /**
     * 三种状态：正常、录音、想结束
     */
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_CANCEL = 3;
    /**
     * 当前状态
     */
    private static int mCurState = STATE_NORMAL;
    /**
     * 判断是否已经开始录音
     */
    private boolean isRecording = false;
    /**
     * 需转dp ---未转
     */
    private int DISTANCE_Y_CAL = 80;

    private DialogManager mDialogManager;

    private MediaRecorderManager mMediaRecorderManager;

    private static final int MSG_RECORDER_PREPARED = 100;
    private static final int MSG_VOICE_CHANGED = 101;
    private static final int MSG_DIALOG_DIMISS = 102;
    /**
     * 语音等级判断
     */
    private static final int MAX_VOICE_LEVEL = 7;
    /**
     * 计时
     */
    private float mTime;
    /**
     * onclickc是否触发
     */
    private boolean mReady;

    private onAudioFinishRecorderListener mAudioFinishRecorderListener;

    public void setAudioFinishRecorderListener(onAudioFinishRecorderListener mAudioFinishRecorderListener) {
        this.mAudioFinishRecorderListener = mAudioFinishRecorderListener;
    }

    /**
     * 获取音量大小和计时功能的的runable
     */
    private Runnable mGetVoiceLevelRunable = new Runnable() {
        @Override
        public void run() {
            while (isRecording) {
                try {
                    Thread.sleep(100);
                    mTime += 0.1f;
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_RECORDER_PREPARED:
                    //真正显示在audio prepared以后
                    mDialogManager.showRecordingDialog();
                    isRecording = true;
                    //获取音量级别的线程
                    new Thread(mGetVoiceLevelRunable).start();
                    break;
                case MSG_VOICE_CHANGED:
                    mDialogManager.updateVoiceLevel(mMediaRecorderManager.getVoiceLevel(MAX_VOICE_LEVEL));
                    break;
                case MSG_DIALOG_DIMISS:
                    mDialogManager.dimissDialog();
                    break;
                default:
                    break;
            }
        }
    };

    public AudioRecorderButton(Context context) {
        this(context, null);
    }

    public AudioRecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 初始化
     */
    private void init(Context context) {
        mDialogManager = new DialogManager(context);
        //TODO 判断外部存储卡是否存在，是否可读写
        String dir = Environment.getExternalStorageDirectory() + "/ww_audio";
        //MediaRecorderManager初始化
        mMediaRecorderManager = MediaRecorderManager.getInstance(dir);
        mMediaRecorderManager.setIRecorderStateListener(this);
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mReady = true;
                mMediaRecorderManager.prepareRecorder();
                return false;
            }
        });
    }

    @Override
    public void wellPrepared() {
        mHandler.sendEmptyMessage(MSG_RECORDER_PREPARED);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isRecording = true;
                changeState(STATE_RECORDING);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isRecording) {
                    if (wantToCancel(x, y)) {
                        changeState(STATE_WANT_CANCEL);
                    } else {
                        changeState(STATE_RECORDING);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!mReady) {
                    //onclick未触发
                    reset();
                    return super.onTouchEvent(event);
                }
                if (!isRecording || mTime < 0.6f) {
                    //recorder的prepared未完成就up了 || 时间太短
                    mDialogManager.tooShort();
                    mMediaRecorderManager.cancel();
                    //延迟关闭dialog
                    mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1300);
                } else if (mCurState == STATE_RECORDING) {
                    //正常录音结束
                    mDialogManager.dimissDialog();
                    mMediaRecorderManager.release();
                    if (mAudioFinishRecorderListener != null) {
                        mAudioFinishRecorderListener.onFinish(mTime,mMediaRecorderManager.getCurrentFilePath());
                    }
                } else if (mCurState == STATE_WANT_CANCEL) {
                    //取消录音
                    mDialogManager.dimissDialog();
                    mMediaRecorderManager.cancel();
                }
                reset();
                Log.e("TAG",isRecording+"");
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 恢复状态及标志位
     */
    private void reset() {
        isRecording = false;
        mTime = 0;
        mReady = false;
        changeState(STATE_NORMAL);
    }

    /**
     * 根据xy坐标判断，是否取消录制
     *
     * @param x
     * @param y
     * @return
     */
    private boolean wantToCancel(int x, int y) {
        if (x < 0 || x > getWidth()) {
            return true;
        } else if (y < -DISTANCE_Y_CAL || y - DISTANCE_Y_CAL > getHeight()) {
            return true;
        }
        return false;
    }

    /**
     * 更改button状态
     *
     * @param state
     */
    private void changeState(int state) {
        if (mCurState != state) {
            //传入状态与当前状态不同，才会changestate
            mCurState = state;
            switch (state) {
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.btn_state_normal_bcg);
                    setText(R.string.recorder_normal);
                    break;
                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.btn_state_recording_bcg);
                    setText(R.string.recorder_recording);
                    if (isRecording) {
                        mDialogManager.recording();
                    }
                    break;
                case STATE_WANT_CANCEL:
                    setBackgroundResource(R.drawable.btn_state_recording_bcg);
                    setText(R.string.recorder_want_cancel);
                    if (isRecording) {
                        mDialogManager.wantToCancel();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 录音完成后的回调
     */
    public interface onAudioFinishRecorderListener {
        void onFinish(float seconds, String filePath);
    }
}
