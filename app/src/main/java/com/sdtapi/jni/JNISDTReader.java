package com.sdtapi.jni;

import android.util.Log;

public class JNISDTReader{
    public static final String TAG= "JNISDTReader";
    static{
        try{
            System.loadLibrary("JNISDTReader");
        }catch(Throwable ex){
            Log.e(TAG, ex.toString());
        }
    }
    /*错误码说明
    *  0x90 操作成功
       0x91 居民身份证中无此项内容
       0x9F 寻找居民身份证成功
       0x01 端口打开失败/端口尚未打开/端口号不合法
       0x02 PC 接收超时，在规定的时间内未接收到规定长度的数据
       0x03 数据传输错误
       0x05 SAM_A 串口不可用，只有 SDT_GetCOMBaud 函数返回
       0x09 打开文件失败
       0x10 接收业务终端数据的校验和错
       0x11 接收业务终端数据的长度错
       0x21 接收业务终端的命令错误，包括命令中的各种数值或逻辑搭配错误
       0x23 越权操作
       0x24 无法识别的错误
       0x80 寻找居民身份证失败
       0x81 选取居民身份证失败
       0x31 居民身份证认证 SAM_A 失败
       0x32 SAM_A 认证居民身份证失败
       0x33 信息验证失败
       0x37 指纹信息验证错误
       0x3F 信息长度错误
       0x40 无法识别的居民身份证类型
       0x41 读居民身份证操作失败
       0x47 取随机数失败
       0x60 SAM_A 自检失败，不能接收命令
       0x66 SAM_A 没经过授权，无法使用
    */
    //
    public static native int _SDT_OpenUsbByFD(int fd);
    //
    public static native int _SDT_OpenUsbByPath(int fd, byte[] mntDevpath);
    // COM
    public static native int _SDT_OpenComBypath(byte[] mntDevpath, int iBaud);

    public static native int _SDT_ClosePort();

    //SAM卡操作
    public static native int _SDT_ResetSAM();
    public static native int _SDT_GetSAMStatus();
    public static native int _SDT_GetSAMID(byte[] pucSAMID);//返回16字节数据

    //读卡
    public static native int _SDT_StartFindIDCard(byte[] pucManaInfo);//4 个字节 0x00
    public static native int _SDT_SelectIDCard(byte[] pucManaMsg);    //8 个字节 0x00
    public static native int _SDT_ReadBaseMsg(byte[] pucCHMsg,int[] puiCHMsgLen,//文字信息不得小于 256 字节
                                              byte[] pucPHMsg,int[] puiPHMsgLen);//相片信息不得小于 1024 字节

    public static native int _SDT_ReadBaseFPMsg(byte[] pucCHMsg,int[] puiCHMsgLen,
                                                byte[] pucPHMsg,int[] puiPHMsgLen,
                                                byte[] pucFPMsg,int[] puiFPMsgLen);//指纹信息不得小于 1024 字节

    public static native int _SDT_ReadNewAppMsg(byte[] pucAppMsg, int[] puiAppMsgLen);//追加信息不得小于 70 字节
}