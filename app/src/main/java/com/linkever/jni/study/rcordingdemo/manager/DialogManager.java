package com.linkever.jni.study.rcordingdemo.manager;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkever.jni.study.rcordingdemo.R;

/**
 * Author:      WW
 * Date:        2018/3/13 18:09
 * Description: This is DialogManager
 */

public class DialogManager {
    private Dialog mDialog;
    private ImageView mIcon, mVoice;
    private TextView mLable;
    private Context mContext;
    private LayoutInflater inflater;

    public DialogManager(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 显示对话框
     */
    public void showRecordingDialog() {
        mDialog = new Dialog(mContext, R.style.Theme_AudioDialog);
        inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_recorder, null);
        mDialog.setContentView(view);
        mIcon = mDialog.findViewById(R.id.iv_icon);
        mVoice = mDialog.findViewById(R.id.iv_voice);
        mLable = mDialog.findViewById(R.id.tv_label);
        //只是提醒作用，没有太多交互，所以没使用DialogFragment
        Log.e("TAG","show");
        mDialog.show();
    }

    /**
     * recording状态
     */
    public void recording() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.recorder);
            mLable.setText(R.string.dialog_recording);
        }
    }

    /**
     * 显示wangtoCancel对话框
     */
    public void wantToCancel() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLable.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.cancel);
            mLable.setText(R.string.dialog_cancel);
        }
    }

    /**
     * 显示时间太短对话框
     */
    public void tooShort() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLable.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.voice_to_short);
            mLable.setText(R.string.dialog_too_short);
        }
    }

    /**
     * 隐藏对话框
     */
    public void dimissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    /**
     * 通过level更新voice上得图片
     *
     * @param level 级别
     */
    public void updateVoiceLevel(int level) {
        if (mDialog != null && mDialog.isShowing()) {
            //通过方法名找资源
            int resId = mContext.getResources().getIdentifier("v" + level,
                    "drawable", mContext.getPackageName());
            mVoice.setImageResource(resId);
        }
    }
}
