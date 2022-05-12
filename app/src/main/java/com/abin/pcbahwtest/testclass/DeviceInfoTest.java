package com.abin.pcbahwtest.testclass;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;

import com.abin.pcbahwtest.BaseTest;
import com.abin.pcbahwtest.utils.ALOG;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/*
Created by a_bin on 2018/9/29

email: ybjaychou@gmail.com
*/
public class DeviceInfoTest extends BaseTest {

    public interface CallBack {
        void onInfo(String info, String serial);
    }

    private CallBack mCallBack;

    private static final String CPU_TEMP = "/sys/class/thermal/thermal_zone0/temp";
    private static final String CPU_FREQ = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq";

    private Handler mHandler;
    private HandlerThread mHandlerThread;

    private boolean mIsRunning;
    private Context mContext;

    public void setCallBack(CallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    public DeviceInfoTest(Context mContext) {
        super(mContext);
        this.mContext = mContext;
        mHandlerThread = new HandlerThread("readCpu");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    @Override
    public void startTest() {
        mIsRunning = true;
        mHandler.post(mReadCpuRunnable);
    }

    @Override
    public void stopTest() {
        mIsRunning = false;
        if (mHandlerThread != null) {
            mHandlerThread.quit();
            mHandlerThread = null;
        }
    }

    private Runnable mReadCpuRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                String freq = readCpuFreq();
                String temp = readCpuTemp();
                if (mCallBack != null) {
                    mCallBack.onInfo(temp + ", " + freq, Build.SERIAL);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mIsRunning) {
                mHandler.postDelayed(this, 3000);
            }
        }
    };


    private String readCpuFreq() throws IOException {
        File file = new File(CPU_FREQ);
        if (!file.exists() || !file.canRead()) {
            ALOG.E("cpu temp file is not found or can not read!!!");
            return null;
        }
        String cpuFreq = "";
        FileInputStream inputStream = new FileInputStream(file);
        byte[] data = new byte[16];
        int len = inputStream.read(data);
        if (len > 0) {
            cpuFreq = String.format("频率:%s", new String(data, 0, len));
            ALOG.D("-------->%s", cpuFreq);
        }
        inputStream.close();
        return cpuFreq.trim();
    }

    private String readCpuTemp() throws IOException {
        File file = new File(CPU_TEMP);
        if (!file.exists() || !file.canRead()) {
            ALOG.E("cpu temp file is not found or can not read!!!");
            return null;
        }
        String cpuTemp = "";
        FileInputStream inputStream = new FileInputStream(file);
        byte[] data = new byte[4];
        int len = inputStream.read(data);
        if (len > 0) {
            String str = new String(data, 0, len);
            int temp = Integer.parseInt(str);
            float temp_2 = temp / 100f;
            cpuTemp = String.format("CPU 温度:%.3f°C", temp_2);
            ALOG.D("-------->%s", cpuTemp);
        }
        inputStream.close();
        return cpuTemp;
    }
}
