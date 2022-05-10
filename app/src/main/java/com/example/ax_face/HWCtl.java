package com.example.ax_face;

import android.util.Log;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class HWCtl {
    static {
        System.loadLibrary("AndroidHW");
    }

    private static final String TAG = "jniAndroidHW";

    private  native int OpenHWDevice(); // start so to hardware control
    private  native int CloseHWDevice(); // stop so to hardware control
    private  native int RelayEnable(int relay_index);//relay
    private  native int RelayDisable(int relay_index);
    private  native int LEDEnable(int led_index);// LED
    private  native int LEDDisable(int led_index);
    private  native int SetAlarm(int[] arr);
    private  native int SetTemprature(int grade, int positive_or_negtive, int temprature);
    private  native int SetHeat(int[] arr);
    private  native int SetFan(int[] arr);
    private  native int SetOut(int[] arr,int len);
    private  native int THSet(int[] arr,int len);
    private  native int GetTemprature(int[] arr);
    private  native int GetISTAT(int[] arr);
    private  native int GetOSTAT(int[] arr);
    private  native int GetPollSTAT(int[] arr);
	
    /*
    * Return : success:0 , failed : -1
     */
    public int HW_StartControl(){
        int ret = -1;

        Log.e("dxx","HW_StartControl");

        ret  = OpenHWDevice();

        Log.e("dxx","HW_StartControl : ret :" +  ret);

        return ret;
    }

    /*
     *Return : success:0 , failed : -1
     */
    public int HW_StopControl(){
        int ret = -1;

        Log.e("dxx","HW_StopControl");

        ret = CloseHWDevice();

        Log.e("dxx","HW_StopControl : ret :" +  ret);

        return ret;
    }

    /*
     * Return : success:0 , failed : -1
     */
    public int HW_relayEnable(int index)
    {
        int ret = -1;

        Log.e("dxx","HW_relayEnable");

        ret = RelayEnable(index);

        Log.e("dxx","HW_relayEnable : ret :" +  ret);

        return ret;
    }

    /*
     * Return : success:0 , failed : -1
     */
    public int HW_relayDisable(int index)
    {
        int ret = -1;

        Log.e("dxx","HW_relayDisable");

        ret = RelayDisable(index);

        Log.e("dxx","HW_relayDisable : ret :" +  ret);

        return ret;
    }

    /*
     *Return : success:0 , failed : -1
     */
    public int HW_LedEnable(int index)
    {
        int ret = -1;

        Log.e("dxx","HW_LedEnable");

        ret = LEDEnable(index);

        Log.e("dxx","HW_LedEnable : ret :" +  ret);

        return ret;
    }

    /*
     *Return : success:0 , failed : -1
     */
    public int HW_LedDisable(int index)
    {
        int ret = -1;

        Log.e("dxx","HW_LedDisable");

        ret = LEDDisable(index);

        Log.e("dxx","HW_LedDisable : ret :" +  ret);

        return ret;
    }

    /*
     *Return : success:0 , failed : -1
     */
    public int HW_SetAlarm(int[] arr)
    {
        int ret = -1;

        Log.e("dxx","HW_SetAlarm");

        ret = SetAlarm(arr);

        Log.e("dxx","HW_SetAlarm : ret :" +  ret);

        return ret;
    }

    /*
     *Return : success:0 , failed : -1
     */
    public int HW_SetOut(int[] arr,int len)
    {
        int ret = -1;

        Log.e("dxx","HW_SetOut");

        ret = SetOut(arr,len);

        Log.e("dxx","HW_SetOut : ret :" +  ret);

        return ret;
    }

    /*
    *  Set temprature level
     *Return : success:0 , failed : -1
     */
    public int HW_THSet(int[] arr,int len)
    {
        int ret = -1;

        Log.e("dxx","HW_THSet");

        ret = THSet(arr,len);

        Log.e("dxx","HW_THSet : ret :" +  ret);

        return ret;
    }

    /*
    * param :  grade --  temprature grade : 0~6
    *          positive_or_negative -- 0x0 :positive ,0x1:negative
    *          temprature --  the temperature to be set to different grade
    * Return : success:0 , failed : -1
     */
    public int HW_SetTemprature(int grade, int positive_or_negative, int temprature)
    {
        int ret = -1;
        Log.e("dxx","HW_SetTemprature");

        // Not implement !!!
        //ret = SetTemprature(grade,positive_or_negative,temprature);

        return ret;
    }

    /*
     *   Return : success :the actual successful read byte count!
     *            		fail : 0 or -1 (0/-1 -- indicate have not read data)
     */
    public int HW_GetTemprature(int[] arr)
    {
        int ret = -1;

        Log.e("dxx","HW_GetTemprature");

        ret = GetTemprature(arr); //  the actual success read byte count from uart

        Log.e("dxx","HW_GetTemprature : ret :" +  ret);

        return ret;
    }

    /*
     *   Return : success : the actual successful read byte count!
     *             		fail: 0 or -1	(0/-1 -- indicate have not read data)
     */
    public int HW_GetISTAT(int[] arr)
    {
        int ret = -1;

        Log.e("dxx","HW_GetISTAT");

        ret = GetISTAT(arr); //  the actual success read count from uart

        Log.e("dxx","HW_GetISTAT : ret :" +  ret);

        return ret;
    }

    /*
     *   Return : success : the actual successful read byte count!
     *            		fail: 0 or -1  	(0/-1 -- indicate have not read data)
     */
    public int HW_GetOSTAT(int[] arr)
    {
        int ret = -1;

        Log.e("dxx","HW_GetOSTAT");

        ret = GetOSTAT(arr); //  the actual success read count from uart

        Log.e("dxx","HW_GetOSTAT : ret :" +  ret);

        return ret;
    }

    /*
     *   Return : success : the actual successful read byte count!
     *            	fail: 0 or -1	(0/-1 -- indicate have not read data)
     *          return value []arr : 1byte CMD  + (ret - 1)byte DATA
     */
    public int HW_GetPollSTAT(int[] arr)
    {
        int ret = -1;

        Log.e("dxx","HW_GetPollSTAT");

		ret = GetPollSTAT(arr); //  the actual success read count from uart
        
	    Log.e("dxx","HW_GetPollSTAT : ret :" +  ret);

		return ret;
    }
}