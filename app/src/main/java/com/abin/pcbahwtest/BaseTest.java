package com.abin.pcbahwtest;

import android.content.Context;

/**
 * Created by a_Bin on 2018/7/21.
 * Email : ybjaychou@gmail.com
 */
public abstract class BaseTest{

    private Context mContext;

    public BaseTest(Context mContext) {
        this.mContext = mContext;
    }

    public Context getContext() {
        return mContext;
    }

    public abstract void startTest();

    public abstract void stopTest();

}
