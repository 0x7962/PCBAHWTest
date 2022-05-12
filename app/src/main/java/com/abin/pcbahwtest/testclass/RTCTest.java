package com.abin.pcbahwtest.testclass;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import com.abin.pcbahwtest.BaseTest;
import com.abin.pcbahwtest.utils.ALOG;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by a_Bin on 2018/7/23.
 * Email : ybjaychou@gmail.com
 */
public class RTCTest extends BaseTest {

    public interface Callback {
        void onSuccess(boolean success);

        void onResult(String result);
    }

    private Callback mCallback;

    public void setCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    private static final String RTC_DATE_DRIVER = "sys/class/rtc/rtc0/date";
    private static final String RTC_TIME_DRIVER = "sys/class/rtc/rtc0/time";

    private HandlerThread mHandlerThread;
    private Handler mHandler;

    private char[] mRtcDate = new char[32];
    private char[] mRtcTime = new char[32];

    private String date = "", time = "";

    public RTCTest(Context mContext) {
        super(mContext);

        mHandlerThread = new HandlerThread("rtc");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    @Override
    public void startTest() {
        mHandler.postDelayed(mReadRtcRunnable, 1000);
    }

    @Override
    public void stopTest() {
        mHandler.removeCallbacks(mReadRtcRunnable);
        mHandlerThread.quit();
    }

    private Runnable mReadRtcRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (mCallback != null) mCallback.onSuccess(readRtc());
                mHandler.postDelayed(this, 1000);
            } catch (Exception e) {
                e.printStackTrace();
                if(mCallback != null) mCallback.onSuccess(false);
            }
        }
    };

    /**
     * check rtc date & time not overflow
     *
     * @return true if date & time is ok, otherwise false (eg:2000-13-32 12:89:70)
     * @throws IOException
     */
    private boolean readRtc() throws IOException {
        File fileDate = new File(RTC_DATE_DRIVER);
        File fileTime = new File(RTC_TIME_DRIVER);
        int d1 = 13, d2 = 32, t0 = 24, t1 = 60, t2 = 60;
        if (fileDate.exists() && fileDate.canRead() && fileTime.exists() && fileTime.canRead()) {
            //read data
            FileReader reader = new FileReader(fileDate);
            int len = reader.read(mRtcDate);
            if (len > 0) {
                date = new String(mRtcDate, 0, len);
                ALOG.D("RTC date:%s", date);
                String[] dates = date.split("-");
                if (dates.length > 1) {
                    //if(dates[1].startsWith("0"))
                        d1 = Integer.valueOf(dates[1].trim());
                    //if(dates[2].startsWith("0"))
                        d2 = Integer.valueOf(dates[2].trim());
                }
            }
            //read time
            FileReader reader2 = new FileReader(fileTime);
            int len2 = reader2.read(mRtcTime);
            if (len2 > 0) {
                time = new String(mRtcTime, 0, len2);
                ALOG.D("RTC time:%s", time);
                String[] times = time.split(":");
                if (times.length > 2) {

                    t0 = Integer.valueOf(times[0].trim());
                    t1 = Integer.valueOf(times[1].trim());
                    t2 = Integer.valueOf(times[2].trim());
                }
            }
        }
        if (mCallback != null) mCallback.onResult(date + " " + time);
        return (d1 <= 12 && d2 <= 31 && t0 <= 23 && t1 < 60 && t2 < 60);
    }
}
