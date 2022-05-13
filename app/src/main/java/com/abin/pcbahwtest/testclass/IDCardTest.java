package com.abin.pcbahwtest.testclass;

import android.content.Context;

import com.abin.pcbahwtest.BaseTest;
import com.abin.pcbahwtest.utils.ALOG;
import com.sdtapi.jni.JNISDTReader;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class IDCardTest extends BaseTest {

    public interface Callback {
        void onInfo(String info);
    }

    private Callback mCallback;

    public void setCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    private boolean mIsCanRead;

    public IDCardTest(Context mContext) {
        super(mContext);
    }

    @Override
    public void startTest() {
        String ttyStr = "/dev/ttyS1";
        int ret = JNISDTReader._SDT_OpenComBypath(ttyStr.getBytes(StandardCharsets.UTF_8), 115200);
        ALOG.D("open serial ret:%d", ret);
        if (ret == 0x90) {
            ret = JNISDTReader._SDT_GetSAMStatus();
            ALOG.D("==============>>>_SDT_GetSAMStatus:%d", ret);
            mIsCanRead = (ret == 0x90);
            new IDCardReadThread().start();
        }
    }

    @Override
    public void stopTest() {
        mIsCanRead = false;
        int ret = JNISDTReader._SDT_ClosePort();
        ALOG.D("close serial ret:%d", ret);
    }

    private class IDCardReadThread extends Thread {
        int time = 500;
        byte[] pucManaInfo = new byte[8];
        byte[] pucManaMsg = new byte[8];
        byte[] pucCHMsg = new byte[260];
        byte[] pucPHMsg = new byte[1032];
        byte[] pucFPMsg = new byte[1032];
        int[] puiCHMsgLen = new int[2];
        int[] puiPHMsgLen = new int[2];
        int[] puiFPMsgLen = new int[2];
        int ret = 0;
        int count = 0, success = 0;
        int failFlag = 0;

        @Override
        public void run() {
            while (mIsCanRead) {
                try {
                    count++;
                    //寻卡
                    //选卡
                    //读卡
                    Date curDate = new Date(System.currentTimeMillis());
                    ret = JNISDTReader._SDT_StartFindIDCard(pucManaInfo);//4 个字节 0x00
                    ret = JNISDTReader._SDT_SelectIDCard(pucManaMsg);    //8 个字节 0x00

                    puiFPMsgLen[0] = 0;
                    ret = JNISDTReader._SDT_ReadBaseMsg(pucCHMsg, puiCHMsgLen,//文字信息不得小于 256 字节
                            pucPHMsg, puiPHMsgLen);//相片信息不得小于 1024 字节

                    Date endDate = new Date(System.currentTimeMillis());
                    long diff = endDate.getTime() - curDate.getTime();
                    if (ret != 0x90) {
                        if (failFlag % 5 == 0) {
                            JNISDTReader._SDT_ResetSAM();
                            count--;
                            failFlag++;
                            Thread.sleep(1500);
                            continue;
                        }
                        ALOG.D("读取失败,错误码:%d,总次数:%d,成功次数:%d", ret, count, success);
                        if (mCallback != null)
                            mCallback.onInfo("读取失败,错误码:" + ret + ",总次数:" + count + ",成功次数:" + success);
                        //ShowTips(1, "读取失败,错误码:" + ret + ",总次数:" + count + ",成功次数:" + success);
                    } else {
                        success++;
                        failFlag = 0;
                        ALOG.D("读取成功,总次数:%d,成功次数:%d,读卡时间(ms):%d", count, success, diff);
                        if (mCallback != null)
                            mCallback.onInfo("读取成功:" + ",总次数:" + count + ",成功次数:" + success + ",读卡时间(ms):" + diff);
                        //ShowTips(1, "读取成功:" + ",总次数:" + count + ",成功次数:" + success + ",读卡时间(ms):" + diff);
                    }
                    Thread.sleep(time);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
