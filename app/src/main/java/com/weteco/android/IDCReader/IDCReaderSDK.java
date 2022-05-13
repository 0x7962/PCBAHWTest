package com.weteco.android.IDCReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class IDCReaderSDK {
    //getExternalFilesDir
    //public static String  savepath = Environment.getExternalStorageDirectory() + File.separator + "decodelib";
    //private static String  bmpPath = savepath + File.separator + "zp.bmp";
    //public static String  bmpPath = Environment.getExternalStorageDirectory() + File.separator + "zp.bmp";
    public static String bmpPath = "/mnt/sdcard/zp.bmp";

    public static boolean loadflag = false;
    static
    {
        try{
            System.loadLibrary("wlt2bmp");
            System.loadLibrary("unpack");
            Log.e("unpack","Load wlt2bmp success");
            loadflag = true;
        }catch(UnsatisfiedLinkError e){
            Log.e("unpack","Load wlt2bmp err",e);
            loadflag =false;
        }
    }
    /*
     * 解码
		src：从身份证读取的1024字节照片数据byte []
		dst：解出的头像bmp图像数据byte []
		base64：解出的头像bmp图像数据Base64编码
	返回值：
   		-2：加载照片解码库函数失败
    	-1：加载照片解码库失败
    	1：解码成功
    	其它值：一所照片解码库返回的错误值，未知含义
     */
    public static native int wltGetbmp(byte[] src ,byte[] dst,byte[] base64);


    public static Bitmap Wlt2Bmp(byte[] wlt){
        if(loadflag == false){
            Log.e("unpack","load fail");
            return null;
        }
        byte[] temp = new byte[51817]; //35721 51817
        byte[] base64 = new byte[51817];

        int ret = wltGetbmp(wlt,temp,base64);
        if(ret == 1){
            Bitmap bmp = BitmapFactory.decodeByteArray(temp, 0, temp.length).copy(Bitmap.Config.ARGB_8888, true);

            Log.e("unpack","unpack success");

            return bmp;
        }else{
            return null;
        }
    }

    public static int GetBmp(byte[] wlt){
        if(loadflag == false){
            return -102;
        }
        byte[] bmpBuf = new byte[51817]; //35721 51817
        byte[] bas64code = new byte[51817];
        int ret = wltGetbmp(wlt,bmpBuf,bas64code);
        if(ret == 1){
            try {
                //File cacheDir=getCacheDir();

                Log.e("unpack",bmpPath);

                File fs = new File(bmpPath);
                if (fs.isFile() && fs.exists()) {
                    fs.delete();
                    Log.e("unpack","delete");
                }
                FileOutputStream outputStream =new FileOutputStream(fs);
                outputStream.write(bmpBuf);
                outputStream.flush();
                outputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return -101;
            } catch (IOException e) {
                e.printStackTrace();
                return -101;
            }
        }
        return ret;
    }

}
