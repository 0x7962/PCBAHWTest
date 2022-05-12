package com.abin.pcbahwtest.testclass;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;


import com.abin.pcbahwtest.BaseTest;
import com.abin.pcbahwtest.R;
import com.abin.pcbahwtest.utils.ALOG;
import com.abin.pcbahwtest.utils.Recorder;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by a_Bin on 2018/7/23.
 * Email : ybjaychou@gmail.com
 */
public class AudioTest extends BaseTest implements Recorder.OnStateChangedListener {

    public interface Callback {
        void onState(int state);

        void onDb(double db);
    }

    private Callback mCallback;

    public void setCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    private Recorder mRecorder;
    private MediaPlayer mMediaPlayer;

    private Context mContext;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private GetAmplitudeThread amplitudeThread;

    private final Object mSync = new Object();

    private boolean mIsRecording;

    public AudioTest(Context mContext) {
        super(mContext);

        this.mContext = mContext;

        mRecorder = new Recorder();
        mRecorder.setOnStateChangedListener(this);

        mMediaPlayer = MediaPlayer.create(mContext, R.raw.audio_test);
        mMediaPlayer.setLooping(true);
    }

    /**
     * called by button
     */
    public void startRecord() {
        mMediaPlayer.start();
        mRecorder.startRecording(MediaRecorder.OutputFormat.AMR_NB, ".amr", mContext);
        mHandler.postDelayed(mStopRecordRunnable, 5000);

        mIsRecording = true;
        synchronized (mSync) {
            mSync.notify();
        }
    }

    /**
     * called by button
     */
    public void startPlayback() {
        mRecorder.startPlayback();
    }

    private Runnable mStopRecordRunnable = new Runnable() {
        @Override
        public void run() {
            mIsRecording = false;
            mRecorder.stop();
            if (mMediaPlayer != null)
                mMediaPlayer.pause();
        }
    };

    @Override
    public void startTest() {
        //mMediaPlayer.start();
        /*amplitudeThread = new GetAmplitudeThread();
        amplitudeThread.start();*/
    }

    @Override
    public void stopTest() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (amplitudeThread != null) {
            amplitudeThread.stopGet();
        }

        mRecorder = null;

    }

    @Override
    public void onStateChanged(int state) {
        if (mCallback != null) mCallback.onState(state);
        /*switch (mRecorder.state()) {
            case Recorder.RECORDING_STATE:
                ALOG.D( "RECORDING_STATE");
                mRecordButton.setEnabled(false);
                mRecordPlay.setEnabled(false);
                mRecordView.setText(getString(R.string.recording_msg));
                break;
            case Recorder.IDLE_STATE:
                mRecordButton.setEnabled(true);
                mRecordPlay.setEnabled(true);
                mRecordView.setText(getString(R.string.record_msg));
                break;
            case Recorder.PLAYING_STATE:
                mRecordButton.setEnabled(false);
                mRecordPlay.setEnabled(false);
                mRecordView.setText(getString(R.string.playing));
                break;
            case Recorder.PLAY_COMPLETED:
                mRecordButton.setEnabled(true);
                mRecordPlay.setEnabled(true);
                mRecordView.setText(getString(R.string.record_msg));
                break;
        }*/
    }

    @Override
    public void onError(int error) {

    }

    private class GetAmplitudeThread extends Thread {

        AtomicBoolean isRunning = new AtomicBoolean(false);

        public GetAmplitudeThread() {
            isRunning.set(true);
        }

        public void stopGet() {
            isRunning.set(false);
            synchronized (mSync) {
                mSync.notify();
            }
        }

        @Override
        public void run() {
            try {
                while (isRunning.get()) {
                    synchronized (mSync) {
                        ALOG.D("audio thread");
                        if (!mIsRecording) {
                            mSync.wait();
                        }

                        if (mRecorder != null) {
                            int amp = mRecorder.getMaxAmplitude();
                            ALOG.D("audio record amplitude:%d", amp);
                            if (mCallback != null) mCallback.onDb(20 * Math.log10(amp));
                            Thread.sleep(100);
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
