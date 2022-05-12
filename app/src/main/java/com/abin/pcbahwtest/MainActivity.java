package com.abin.pcbahwtest;


import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.abin.pcbahwtest.testclass.DeviceInfoTest;
import com.abin.pcbahwtest.testclass.EthTest;
import com.abin.pcbahwtest.utils.ALOG;
import com.abin.pcbahwtest.utils.SpeechUtil;

public class MainActivity extends Activity {

    private HwManager mHwManager;
    private SpeechUtil mSpeechUtil;
    private TestManager mTestManager;

    private TextView mEthView, mCpuView, mTempHumView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    @Override
    protected void onPause() {
        mHwManager.closeHwCtl();
        if (mTestManager != null)
            mTestManager.stopTest();

        if(mSpeechUtil != null) mSpeechUtil.shutdown();
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
}
