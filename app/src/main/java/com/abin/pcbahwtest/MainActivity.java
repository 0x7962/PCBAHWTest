package com.abin.pcbahwtest;


import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.abin.pcbahwtest.testclass.AndroidCameraTest;
import com.abin.pcbahwtest.testclass.AudioTest;
import com.abin.pcbahwtest.testclass.DeviceInfoTest;
import com.abin.pcbahwtest.testclass.EthTest;
import com.abin.pcbahwtest.testclass.IDCardTest;
import com.abin.pcbahwtest.testclass.RTCTest;
import com.abin.pcbahwtest.testclass.USBTest;
import com.abin.pcbahwtest.utils.ALOG;
import com.abin.pcbahwtest.utils.AutoFitTextureView;
import com.abin.pcbahwtest.utils.Recorder;
import com.abin.pcbahwtest.utils.SpeechUtil;

public class MainActivity extends Activity {

    private HwManager mHwManager;
    private SpeechUtil mSpeechUtil;
    private TestManager mTestManager;

    private TextView mEthView, mCpuView, mTempHumView, mRtcView, mUsbView, mIdCardView;

    private Button mBtnRecord, mBtnPlay;
    private AudioTest mAudioTest;

    private AutoFitTextureView mCamView1, mCamView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        TextView tvVersion = (TextView) findViewById(R.id.tv_version);
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo info = packageManager.getPackageInfo(this.getPackageName(), 0);
            String versionName = info.versionName;
            ALOG.D("versionName:%s", versionName);
            tvVersion.setText(getString(R.string.app_version, versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mEthView = (TextView) findViewById(R.id.tv_eth);
        mCpuView = (TextView) findViewById(R.id.tv_cpu);
        mTempHumView = (TextView) findViewById(R.id.tv_temp_hum);
        mRtcView = (TextView)findViewById(R.id.tv_rtc);
        mUsbView = (TextView)findViewById(R.id.tv_usb);
        mIdCardView = (TextView)findViewById(R.id.tv_idcard);

        mBtnRecord = (Button) findViewById(R.id.btn_record);
        mBtnPlay = (Button) findViewById(R.id.btn_play);

        mCamView1 = (AutoFitTextureView) findViewById(R.id.cam_view1);
        mCamView2 = (AutoFitTextureView) findViewById(R.id.cam_view2);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSpeechUtil = new SpeechUtil(this);
        mTestManager = new TestManager(this);

        mHwManager = HwManager.getInstance(this);
        mHwManager.setCallback(mHwCallback);
        mHwManager.setSpeechUtil(mSpeechUtil);

        mTestManager.startTest(EthTest.class);
        EthTest ethTest = (EthTest) mTestManager.getTest(EthTest.class.getName());
        if (ethTest != null) ethTest.setCallback(mEthCallback);

        mTestManager.startTest(DeviceInfoTest.class);
        DeviceInfoTest deviceInfoTest = (DeviceInfoTest) mTestManager.getTest(DeviceInfoTest.class.getName());
        if (deviceInfoTest != null) deviceInfoTest.setCallBack(mDevInfoCallback);

        mTestManager.startTest(RTCTest.class);
        RTCTest rtcTest = (RTCTest) mTestManager.getTest(RTCTest.class.getName());
        if (rtcTest != null) rtcTest.setCallback(mRtcCallback);

        mTestManager.startTest(USBTest.class);
        USBTest usbTest = (USBTest) mTestManager.getTest(USBTest.class.getName());
        if (usbTest != null) usbTest.setCallback(mUsbCallback);

        mTestManager.startTest(AudioTest.class);
        mAudioTest = (AudioTest) mTestManager.getTest(AudioTest.class.getName());
        if(mAudioTest != null) mAudioTest.setCallback(mAudioCallback);

        mTestManager.startTest(IDCardTest.class);
        IDCardTest mIdCardTest = (IDCardTest) mTestManager.getTest(IDCardTest.class.getName());
        if(mIdCardTest != null) mIdCardTest.setCallback(mIdCardCallback);

        mTestManager.startTest(AndroidCameraTest.class);
        AndroidCameraTest cameraTest = (AndroidCameraTest) mTestManager.getTest(AndroidCameraTest.class.getName());
        if(cameraTest != null) cameraTest.setupCamera(mCamView1, mCamView2);
    }

    @Override
    protected void onPause() {
        mHwManager.closeHwCtl();
        if (mTestManager != null)
            mTestManager.stopTest();

        if (mSpeechUtil != null) mSpeechUtil.shutdown();
        super.onPause();
    }

    private final EthTest.Callback mEthCallback = new EthTest.Callback() {
        @Override
        public void onStatus(boolean isConnected) {

        }

        @Override
        public void onGetInfo(final String ip) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mEthView.setText(ip);
                }
            });

        }

        @Override
        public void onMobileStateChange(boolean isConnected, String ip) {
            mTempHumView.setText("isConnected:" + isConnected);
        }
    };

    private final DeviceInfoTest.CallBack mDevInfoCallback = new DeviceInfoTest.CallBack() {
        @Override
        public void onInfo(final String info, final String serial) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCpuView.setText(info + " 序列号:" + serial);
                }
            });
        }
    };

    private final HwManager.Callback mHwCallback = new HwManager.Callback() {
        @Override
        public void onTempHum(final String info) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTempHumView.setText(info);
                }
            });
        }
    };

    private final RTCTest.Callback mRtcCallback = new RTCTest.Callback() {
        @Override
        public void onSuccess(boolean success) {
            if (!success) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRtcView.setTextColor(Color.RED);
                    }
                });

            }
        }

        @Override
        public void onResult(final String result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRtcView.setText(result);
                }
            });
        }
    };

    private final USBTest.Callback mUsbCallback = new USBTest.Callback() {
        @Override
        public void onResult(String result, boolean pass) {
            ALOG.D("usb pass-->%b", pass);
            mUsbView.setText(result);
        }
    };

    private final AudioTest.Callback mAudioCallback = new AudioTest.Callback() {
        @Override
        public void onState(int state) {
            switch (state) {
                case Recorder.RECORDING_STATE:
                    mBtnRecord.setEnabled(false);
                    mBtnPlay.setEnabled(false);
                    //mAudioToast.setText(getString(R.string.audio_toast_recording));
                    break;
                case Recorder.IDLE_STATE:
                    mBtnRecord.setEnabled(true);
                    mBtnPlay.setEnabled(true);
                    //mAudioToast.setText("");
                    break;
                case Recorder.PLAYING_STATE:
                    mBtnRecord.setEnabled(false);
                    mBtnPlay.setEnabled(false);
                    //mAudioToast.setText(getString(R.string.audio_toast_playing));
                    break;
                case Recorder.PLAY_COMPLETED:
                    mBtnRecord.setEnabled(true);
                    mBtnPlay.setEnabled(true);
                    //mAudioToast.setText("");
                    break;
            }
        }

        @Override
        public void onDb(double db) {

        }
    };

    public void recordAudio(View view){
        if (mAudioTest != null) mAudioTest.startRecord();
    }

    public void playAudio(View view){
        if (mAudioTest != null) mAudioTest.startPlayback();
    }

    private final IDCardTest.Callback mIdCardCallback = new IDCardTest.Callback() {
        @Override
        public void onInfo(final String info) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mIdCardView.setText(info);
                }
            });
        }
    };
}
