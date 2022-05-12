package com.abin.pcbahwtest.testclass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.abin.pcbahwtest.BaseTest;
import com.abin.pcbahwtest.HwManager;
import com.abin.pcbahwtest.R;
import com.abin.pcbahwtest.utils.ALOG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * Created by a_Bin on 2018/7/23.
 * Email : ybjaychou@gmail.com
 */
public class EthTest extends BaseTest {

    public interface Callback {
        void onStatus(boolean isConnected);

        void onGetInfo(String info);

        void onMobileStateChange(boolean isConnected, String ip);
    }

    private Callback mCallback;

    public void setCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    private ConnectivityManager mConnectivityManager;

    private Context mContext;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private boolean mIsStopped;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ALOG.D(">>>>>>>>>>>%s", intent.getAction());
            //updateEthState();
            //mHandler.postDelayed(mDelayUpdateState, 500);
        }
    };

    public EthTest(Context mContext) {
        super(mContext);
        this.mContext = mContext;
        mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public void startTest() {

        //register network state change broadcast receiver
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//        mContext.registerReceiver(mReceiver, filter);

        //delay update eth state, wait callback is ready
        //mHandler.postDelayed(mDelayUpdateState, 2000);
        mIsStopped = false;

        new NetWorkRequest().start();
    }

    private Runnable mDelayUpdateState = new Runnable() {
        @Override
        public void run() {
            updateEthState();
        }
    };

    public void updateEthState() {
        if (mIsStopped) {
            ALOG.D("stopped return it");
            return;
        }
        boolean isConnected = false;
        boolean isMobileConnected = false;
        if (mConnectivityManager != null) {
            NetworkInfo mInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mInfo != null) {
                int type = mInfo.getType();
                ALOG.D(">>>>>>>>>>>>type:%d", type);
                switch (type) {
                    case ConnectivityManager.TYPE_ETHERNET:
                        isConnected = true;
                        //mHandler.postDelayed(mGetEthRunnable, 5000);
                        //String ip = getEthIp();
                        //ALOG.D(">>>>>>>>>>>>>>>>>>>IP:%s", ip);
                        String mac = getMac("eth0");
                        ALOG.D("MAC:%s", mac);
                        InetAddress address = getLocalIPAddress();
                        String ip = "";
                        if (address != null) {
                            ip = address.getHostAddress();
                        }
                        ping("192.168.0.1");
                        if (mCallback != null)
                            mCallback.onGetInfo(mContext.getString(R.string.eth_infos, ip, mac));
                        break;
                    case ConnectivityManager.TYPE_MOBILE:
                        isMobileConnected = true;
                        break;
                    default://other network types
                        isConnected = false;
                        break;
                }
            }
        }
        //ALOG.D("updateEthState-->%b", isConnected);
        if (mCallback != null) {
            mCallback.onStatus(isConnected);
            //mCallback.onMobileStateChange(isMobileConnected, getMobileIp());
        }

        ALOG.D("======>>>>mIsStopped:%b", mIsStopped);
        if (!mIsStopped) {
            mHandler.removeCallbacks(mDelayUpdateState);
            mHandler.postDelayed(mDelayUpdateState, 2000);
        }
    }


    @Override
    public void stopTest() {
        mIsStopped = true;
        //mHandler.removeCallbacks(mDelayUpdateState);
        //mContext.unregisterReceiver(mReceiver);
    }

    private Runnable mGetEthRunnable = new Runnable() {
        @Override
        public void run() {
            String ip = getEthIp();
            ALOG.D(">>>>>>>>>>>>>>>>>>>IP:%s", ip);
            if (mCallback != null) mCallback.onGetInfo(ip);
        }
    };

    private String getEthIp() {
        String ip = "0.0.0.0";
        try {
            Process process = Runtime.getRuntime().exec("ip route");
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                ALOG.D(">>>>>>>>>>>>line:%s", line);
                if (!TextUtils.isEmpty(line) && line.contains("eth0")) {
                    String[] strs = line.split(" ");
                    ip = strs[strs.length - 1];
                }
            }
            process.waitFor();
            reader.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ip;
    }

    private String getMobileIp() {
        String ip = "0.0.0.0";
        try {
            Process process = Runtime.getRuntime().exec("ip route");
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                ALOG.D(">>>>>>>>>>>>line:%s", line);
                if (!TextUtils.isEmpty(line) && line.contains("ppp0")) {
                    String[] strs = line.split(" ");
                    ip = strs[strs.length - 1];
                }
            }
            process.waitFor();
            reader.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ip;
    }

    /**
     * Ipv4 address check.
     */
    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^(" + "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}" +
                    "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");

    /**
     * Check if valid IPV4 address.
     *
     * @param input the address string to check for validity.
     * @return True if the input parameter is a valid IPv4 address.
     */
    private boolean isIPv4Address(String input) {
        return IPV4_PATTERN.matcher(input).matches();
    }

    /**
     * Get local Ip address.
     */
    private InetAddress getLocalIPAddress() {
        Enumeration<NetworkInterface> enumeration = null;
        try {
            enumeration = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                NetworkInterface nif = enumeration.nextElement();
                Enumeration<InetAddress> inetAddresses = nif.getInetAddresses();
                if (inetAddresses != null) {
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = inetAddresses.nextElement();
                        ALOG.D("------>>>%s", inetAddress.getHostAddress());
                        if (!inetAddress.isLoopbackAddress() && isIPv4Address(inetAddress.getHostAddress())) {
                            return inetAddress;
                        }
                    }
                }
            }
        }
        ALOG.D("getLocalIPAddress return null ");
        return null;
    }

    private String getMac(String name) {
        String macSerial = "";
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/" + name + "/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
            ir.close();
            input.close();
        } catch (IOException ex) {
            macSerial = "";
            ex.printStackTrace();
        }
        return macSerial;
    }

    private void ping(String ip) {
        Process process;
        boolean isOk = false;
        try {
            process = Runtime.getRuntime().exec("ls");
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            ALOG.D("------>>sasas>%s", reader.readLine());
            while ((line = reader.readLine()) != null) {
                ALOG.D("---1111--->>%s", line);
                if (line.contains("rtt")) {
                    isOk = true;
                    break;
                }
            }

            reader.close();
            inputStream.close();
            ALOG.D("-------->>>isOk:%b", isOk);
            if (mCallback != null) mCallback.onMobileStateChange(isOk, "exit:");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class NetWorkRequest extends Thread {

        @Override
        public void run() {
            while (!mIsStopped) {
                try {
                    String mac = getMac("eth0");
                    ALOG.D("MAC:%s", mac);

                    int requestCode = requestNetwork("https://www.baidu.com/");
                    ALOG.D("===========>>>>requestCode:%d", requestCode);
                    if (mCallback != null)
                        mCallback.onGetInfo(mContext.getString(
                                R.string.eth_infos,
                                mac,
                                (requestCode == 200) ?
                                        mContext.getString(R.string.connected) :
                                        mContext.getString(R.string.disconnected)));
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }

        private int requestNetwork(String urlStr) {
            int code = 0;
            try {
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                code = connection.getResponseCode();
                connection.disconnect();
            }catch (Exception e){
                e.printStackTrace();
            }
            return code;
        }
    }
}
