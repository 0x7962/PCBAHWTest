package com.abin.pcbahwtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.abin.pcbahwtest.utils.ALOG;

public class MainActivity extends AppCompatActivity {

    private HwManager mHwManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvVersion = findViewById(R.id.tv_version);
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo info = packageManager.getPackageInfo(this.getPackageName(), 0);
            String versionName = info.versionName;
            ALOG.D("versionName:%s", versionName);
            tvVersion.setText(getString(R.string.app_version, versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHwManager = HwManager.getInstance();
    }

    @Override
    protected void onPause() {
        mHwManager.closeHwCtl();
        super.onPause();
    }
}
