package com.abin.pcbahwtest.testclass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Looper;


import com.abin.pcbahwtest.BaseTest;
import com.abin.pcbahwtest.utils.ALOG;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by a_Bin on 2018/7/23.
 * Email : ybjaychou@gmail.com
 */
public class USBTest extends BaseTest {

    /**
     * /dev/bus/usb/004
     * /dev/bus/usb/001
     */


    public interface Callback {
        void onResult(String result, boolean pass);
    }

    private Callback mCallback;

    public void setCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    private UsbManager mUsbManager;

    private Context mContext;

    private StringBuffer mUsbStr = new StringBuffer();

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public USBTest(Context mContext) {
        super(mContext);

        this.mContext = mContext;
        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
    }


    @Override
    public void startTest() {

        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        mContext.registerReceiver(mUsbReceiver, filter);

        mHandler.postDelayed(mDelayUpdateUsb, 2000);
    }

    private Runnable mDelayUpdateUsb = new Runnable() {
        @Override
        public void run() {
            updateUsbDevices();
        }
    };

    private void updateUsbDevices() {
        int usbCnt = 0;
        mUsbStr.delete(0, mUsbStr.length());
        HashMap<String, UsbDevice> devices = mUsbManager.getDeviceList();
        if (devices != null) {
            for (Map.Entry<String, UsbDevice> entry : devices.entrySet()) {
                ALOG.D("ProductName:%s", entry.getValue().getProductName());
                mUsbStr.append(entry.getValue().getProductName());
                mUsbStr.append("\n");
                ++usbCnt;
            }
            ALOG.D(">>>>>>>>>usb device count:%d", usbCnt);
            if (mCallback != null) mCallback.onResult(mUsbStr.toString().trim(), usbCnt >= 3);
        }
    }

    @Override
    public void stopTest() {

        mContext.unregisterReceiver(mUsbReceiver);
    }

    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUsbDevices();
        }
    };
}
