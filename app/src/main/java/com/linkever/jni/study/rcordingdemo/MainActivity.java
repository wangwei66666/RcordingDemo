package com.linkever.jni.study.rcordingdemo;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.linkever.jni.study.rcordingdemo.adapter.ListViewAdapter;
import com.linkever.jni.study.rcordingdemo.bean.Recorder;
import com.linkever.jni.study.rcordingdemo.manager.MediaManager;
import com.linkever.jni.study.rcordingdemo.widget.AudioRecorderButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView mlistview;
    private AudioRecorderButton recorderBtn;
    private List<Recorder> list = new ArrayList<>();
    private ArrayAdapter adapter;
    private View mAnimView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        recorderBtn.setAudioFinishRecorderListener(new AudioRecorderButton.onAudioFinishRecorderListener() {
            @Override
            public void onFinish(float seconds, String filePath) {
                Recorder mRecorder = new Recorder(seconds, filePath);
                list.add(mRecorder);
                adapter.notifyDataSetChanged();
                //每次更新完listview指向最后的item
                mlistview.setSelection(list.size() - 1);
            }
        });

        adapter = new ListViewAdapter(this, list);
        mlistview.setAdapter(adapter);
        mlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //点击第二个音频时先把第一个停止，动画还原
                if (mAnimView != null) {
                    mAnimView.setBackgroundResource(R.drawable.adj);
                    mAnimView = null;
                }
                //播放动画
                mAnimView = view.findViewById(R.id.v_anime);
                mAnimView.setBackgroundResource(R.drawable.voice_animation);
                AnimationDrawable anim = (AnimationDrawable) mAnimView.getBackground();
                anim.start();
                //播放音频,取消动画
                MediaManager.playAudio(list.get(position).getFilePath(),
                        new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                //播放完成，停止动画效果
                                mAnimView.setBackgroundResource(R.drawable.adj);
                            }
                        });
            }
        });
    }

    private void initView() {
        mlistview = (ListView) findViewById(R.id.im_recyclerview);
        recorderBtn = (AudioRecorderButton) findViewById(R.id.recorder_btn);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //继续播放
        MediaManager.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //停止播放
        MediaManager.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        MediaManager.release();
    }
}
