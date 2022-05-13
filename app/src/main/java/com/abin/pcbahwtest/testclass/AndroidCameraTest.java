package com.abin.pcbahwtest.testclass;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.view.Surface;
import android.widget.Toast;

import com.abin.pcbahwtest.BaseTest;
import com.abin.pcbahwtest.utils.ALOG;
import com.abin.pcbahwtest.utils.AutoFitTextureView;

import java.io.IOException;
import java.util.List;

/**
 * Created by a_Bin on 2018/7/23.
 * Email : ybjaychou@gmail.com
 */
public class AndroidCameraTest extends BaseTest {

    public interface Callback {
        void onCameraResult(boolean success);
    }

    private Callback mCallback;

    public void setCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    private Handler mHandler = new Handler();
    private Context mContext;

    private static final int[] PREVIEW_RES = {640, 480};

    private static final float[] BANDWIDTH_FACTORS = {0.5f, 0.5f};
    // for accessing USB and USB camera
    //private USBMonitor mUSBMonitor;
    //private UVCCameraHandler mHandlerR, mHandlerL;
    private AutoFitTextureView mUVCCameraViewR, mUVCCameraViewL;
    //private List<UsbDevice> mUsbDevices;

    private Camera mCamera0, mCamera1;

    private boolean mIsPortrait = false;

    private boolean mIsPreviewing;

    public void setupCamera(AutoFitTextureView uvcCameraViewL, AutoFitTextureView uvcCameraViewR) {
        mUVCCameraViewL = uvcCameraViewL;
        mUVCCameraViewR = uvcCameraViewR;

        mUVCCameraViewL.setAspectRatio(PREVIEW_RES[1], PREVIEW_RES[0]);
        /*mHandlerL = UVCCameraHandler.createHandler((Activity) mContext, mUVCCameraViewL,
                PREVIEW_RES[0], PREVIEW_RES[1], BANDWIDTH_FACTORS[0]);*/

        mUVCCameraViewR.setAspectRatio(PREVIEW_RES[1], PREVIEW_RES[0]);
        /*mHandlerR = UVCCameraHandler.createHandler((Activity) mContext, mUVCCameraViewR,
                PREVIEW_RES[0], PREVIEW_RES[1], BANDWIDTH_FACTORS[0]);*/
    }

    public AndroidCameraTest(Context mContext) {
        super(mContext);
        this.mContext = mContext;
        //mUSBMonitor = new USBMonitor(mContext, this);
    }

    public void onStart() {
        /*mUSBMonitor.register();
        if (mUVCCameraViewR != null)
            mUVCCameraViewR.onResume();
        if (mUVCCameraViewL != null)
            mUVCCameraViewL.onResume();*/
    }

    public void onResume() {
        //ready to open camera
//        if (Camera.getNumberOfCameras() > 0)
//            mHandler.postDelayed(openCamera0, 1000);
//        else
//            ALOG.E("Camera not found!!!");
    }

    public void onStop() {
        /*mHandlerR.close();
        if (mUVCCameraViewR != null)
            mUVCCameraViewR.onPause();

        mHandlerL.close();
        if (mUVCCameraViewL != null)
            mUVCCameraViewL.onPause();

        mUSBMonitor.unregister();*/

//        mHandler.removeCallbacks(openCamera0);
//        mHandler.removeCallbacks(openCamera1);
//        stopCameras();
    }

    @Override
    public void startTest() {
        //nothing to do
        if (Camera.getNumberOfCameras() > 0)
            mHandler.postDelayed(openCamera0, 2000);
        else
            ALOG.E("Camera not found!!!");
    }

    @Override
    public void stopTest() {

        /*if (mHandlerR != null) {
            mHandlerR = null;
        }
        if (mHandlerL != null) {
            mHandlerL = null;
        }
        if (mUSBMonitor != null) {
            mUSBMonitor.destroy();
            mUSBMonitor = null;
        }*/

        mHandler.removeCallbacks(openCamera0);
        mHandler.removeCallbacks(openCamera1);
        stopCameras();
    }

    /*private void getAllUvcCamera() {
        List<DeviceFilter> filter = DeviceFilter.getDeviceFilters(mContext, com.serenegiant.uvccamera.R.xml.device_filter);
        if (mUSBMonitor != null) {
            mUsbDevices = mUSBMonitor.getDeviceList(filter.get(0));
            if (mUsbDevices != null)
                Toast.makeText(mContext, "num of camera:" + mUsbDevices.size(), Toast.LENGTH_LONG).show();
        }
    }*/

    /*private void openCamera(UsbDevice usbDevice) {
        try {
            mUSBMonitor.requestPermission(usbDevice);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/


    private Runnable openCamera0 = new Runnable() {
        @Override
        public void run() {
            /*getAllUvcCamera();
            if(mUsbDevices != null && mUsbDevices.size() > 0){
                openCamera(mUsbDevices.get(0));
            }*/
            try {
                mCamera0 = Camera.open(0);
                Camera.Parameters parameters = mCamera0.getParameters();
                for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
                    ALOG.D("size>>>>>>>>>>%dx%d", size.width, size.height);
                    ALOG.D("========================================");
                }
                parameters.setPreviewSize(PREVIEW_RES[0], PREVIEW_RES[1]);
                if (mIsPortrait)
                    parameters.setRotation(90);
                mCamera0.setParameters(parameters);
                mCamera0.setPreviewTexture(mUVCCameraViewL.getSurfaceTexture());
                if (mIsPortrait)
                    mCamera0.setDisplayOrientation(90);
                mCamera0.startPreview();
                if (mIsPortrait)
                    mUVCCameraViewL.setAspectRatio(PREVIEW_RES[1], PREVIEW_RES[0]);
                else
                    mUVCCameraViewL.setAspectRatio(PREVIEW_RES[0], PREVIEW_RES[1]);
                if (Camera.getNumberOfCameras() > 1)
                    mHandler.postDelayed(openCamera1, 2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private Runnable openCamera1 = new Runnable() {
        @Override
        public void run() {
            /*if(mUsbDevices != null && mUsbDevices.size() > 1){
                openCamera(mUsbDevices.get(1));
            }*/
            try {
                mCamera1 = Camera.open(1);
                Camera.Parameters parameters = mCamera1.getParameters();
                parameters.setPreviewSize(PREVIEW_RES[0], PREVIEW_RES[1]);
                if (mIsPortrait)
                    parameters.setRotation(90);
                mCamera1.setParameters(parameters);

                mCamera1.setPreviewTexture(mUVCCameraViewR.getSurfaceTexture());
                if (mIsPortrait)
                    mCamera1.setDisplayOrientation(90);
                mCamera1.startPreview();
                if (mIsPortrait)
                    mUVCCameraViewR.setAspectRatio(PREVIEW_RES[1], PREVIEW_RES[0]);
                else
                    mUVCCameraViewR.setAspectRatio(PREVIEW_RES[0], PREVIEW_RES[1]);
                mIsPreviewing = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void stopCameras() {
        mIsPreviewing = false;
        if (mCamera0 != null) {
            mCamera0.stopPreview();
            mCamera0.release();
            mCamera0 = null;
        }

        if (mCamera1 != null) {
            mCamera1.stopPreview();
            mCamera1.release();
            mCamera1 = null;
        }
    }

    public void changeOri() {
        if (!mIsPreviewing) {
            ALOG.E("camera is not previewing!!!");
            return;
        }
        mIsPortrait = !mIsPortrait;
        stopCameras();
        mHandler.postDelayed(openCamera0, 1000);
    }

    //usb camera callbacks
    /*@Override
    public void onAttach(UsbDevice device) {

    }

    @Override
    public void onDettach(UsbDevice device) {

    }

    @Override
    public void onConnect(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock, boolean createNew) {
        if (!mHandlerL.isOpened()) {
            mHandlerL.open(ctrlBlock);
            final SurfaceTexture st = mUVCCameraViewL.getSurfaceTexture();
            mHandlerL.startPreview(new Surface(st));
            mHandler.postDelayed(openCamera1,1000);
        } else if (!mHandlerR.isOpened()) {
            mHandlerR.open(ctrlBlock);
            final SurfaceTexture st = mUVCCameraViewR.getSurfaceTexture();
            mHandlerR.startPreview(new Surface(st));
        }
    }

    @Override
    public void onDisconnect(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock) {

    }

    @Override
    public void onCancel(UsbDevice device) {

    }*/
}
