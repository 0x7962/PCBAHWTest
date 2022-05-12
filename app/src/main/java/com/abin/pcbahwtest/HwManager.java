package com.abin.pcbahwtest;

import android.content.Context;

import com.abin.pcbahwtest.utils.ALOG;
import com.abin.pcbahwtest.utils.SpeechUtil;
import com.example.ax_face.HWCtl;

import java.util.concurrent.atomic.AtomicBoolean;

/*
Created by a_bin on 5/10/22

email: ybjaychou@gmail.com
*/
public class HwManager {

    public interface Callback{
        void onTempHum(String info);
    }

    private Callback mCallback;

    public void setCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    private static final int RELAY_INDEX_1 = 0;
    private static final int RELAY_INDEX_2 = 1;
    private static final int RELAY_INDEX_3 = 2;
    private static final int RELAY_INDEX_4 = 3;

    private static final int[] BUZZER_OFF = {0x35, 0x31, 0x00, 0x00};
    private static final int[] BUZZER_ON = {0x35, 0x32, 0x00, 0x00};
    private static final int[][] BUZZER_HZ = {
            {0x35, 0x33, 0x00, 0x00},
            {0x35, 0x34, 0x00, 0x00},
            {0x35, 0x35, 0x00, 0x00},
            {0x35, 0x36, 0x00, 0x00},
            {0x35, 0x37, 0x00, 0x00},
            {0x35, 0x38, 0x00, 0x00},
    };

    private static final int[] LED_VALUE = {0x21, 0x22, 0x23, 0x24};
    private static final long LED_CTL_INTERVAL = 600;

    private static final long GET_STAT_INTERVAL = 200;
    private int[] mISTAT = {0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01};

    private Context mContext;
    private static HwManager mInstance = null;

    private SpeechUtil mSpeechUtil;

    //jni操作
    private HWCtl mHwCtl;

    private AtomicBoolean mIsCanCtl = new AtomicBoolean();

    public static HwManager getInstance(Context context) {
        if (mInstance == null) mInstance = new HwManager(context);
        return mInstance;
    }

    public HwManager(Context context) {
        mContext = context;
        mHwCtl = new HWCtl();
        mIsCanCtl.set(mHwCtl.HW_StartControl() == 0);
        new StatThread().start();
        new LEDThread().start();
        ALOG.I("HwManager started... mIsCanCtl is:%b", mIsCanCtl.get());
    }

    public void setSpeechUtil(SpeechUtil mSpeechUtil) {
        this.mSpeechUtil = mSpeechUtil;
    }

    /**
     * 退出app，释放资源
     */
    public void closeHwCtl() {
        if (mIsCanCtl.get()) {
            mIsCanCtl.set(false);
            mHwCtl.HW_StopControl();
            mHwCtl = null;
            mInstance = null;
            ALOG.I("HwManager stopped...");
        }
    }


    public void buzzerSndOut() {
        if (mIsCanCtl.get()) {
            mHwCtl.HW_SetAlarm(BUZZER_ON);
        }
    }

    public void buzzerSndOff() {
        if (mIsCanCtl.get()) {
            mHwCtl.HW_SetAlarm(BUZZER_OFF);
        }
    }

    public void buzzerSndOutIndex(int index) {
        if (index > 5 || index < 0) {
            ALOG.E("buzzerSndOutIndex: index is 0 ~ 5");
            return;
        }
        if (mIsCanCtl.get()) {
            mHwCtl.HW_SetAlarm(BUZZER_HZ[index]);
        }
    }


    private class StatThread extends Thread {

        @Override
        public void run() {
            while (mIsCanCtl.get()) {
                int ret = mHwCtl.HW_GetISTAT(mISTAT);
                ALOG.D("HW_GetISTAT ret is:%d", ret);
                if (ret > 0) {
                    checkAndCtl(mISTAT);
                }
                try {
                    Thread.sleep(GET_STAT_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ALOG.I("StatThread is finished...");
        }

        private void checkAndCtl(int[] stats) {
            if (stats[0] == 0) {//gpio 1
                buzzerSndOut();
                mHwCtl.HW_relayEnable(RELAY_INDEX_1);
                if(mSpeechUtil != null) mSpeechUtil.speak(mContext.getString(R.string.gpio_1_speech_text));
            } else {
                buzzerSndOff();
                mHwCtl.HW_relayDisable(RELAY_INDEX_1);
            }

            if (stats[1] == 0) {//gpio 2
                buzzerSndOut();
                mHwCtl.HW_relayEnable(RELAY_INDEX_2);
                if(mSpeechUtil != null) mSpeechUtil.speak(mContext.getString(R.string.gpio_2_speech_text));
            } else {
                buzzerSndOff();
                mHwCtl.HW_relayDisable(RELAY_INDEX_2);
            }

            if (stats[2] == 0) {//gpio 3
                buzzerSndOut();
                mHwCtl.HW_relayEnable(RELAY_INDEX_3);
                if(mSpeechUtil != null) mSpeechUtil.speak(mContext.getString(R.string.gpio_3_speech_text));
            } else {
                buzzerSndOff();
                mHwCtl.HW_relayDisable(RELAY_INDEX_3);
            }

            if (stats[3] == 0) {//gpio 4
                buzzerSndOut();
                mHwCtl.HW_relayEnable(RELAY_INDEX_4);
                if(mSpeechUtil != null) mSpeechUtil.speak(mContext.getString(R.string.gpio_4_speech_text));
            } else {
                buzzerSndOff();
                mHwCtl.HW_relayDisable(RELAY_INDEX_4);
            }
        }
    }

    private class LEDThread extends Thread {
        int i = 0;

        @Override
        public void run() {
            while (mIsCanCtl.get()) {
                mHwCtl.HW_LedEnable(LED_VALUE[i]);

                try {
                    Thread.sleep(LED_CTL_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mHwCtl.HW_LedDisable(LED_VALUE[i]);

                i++;
                if (i >= LED_VALUE.length) {
                    i = 0;
                }
            }
            ALOG.I("LEDThread is finished...");
        }
    }

    private class TempHumThread extends Thread {

        @Override
        public void run() {

        }
    }
}
