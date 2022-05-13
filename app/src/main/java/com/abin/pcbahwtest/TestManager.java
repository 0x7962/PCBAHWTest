package com.abin.pcbahwtest;

import android.content.Context;
import com.abin.pcbahwtest.utils.ALOG;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by a_Bin on 2018/7/21.
 * Email : ybjaychou@gmail.com
 */
public class TestManager {

    private Map<String, BaseTest> tests = new HashMap<String, BaseTest>();

    private Context mContext;

    public TestManager(Context mContext) {
        this.mContext = mContext;
    }

    public <T extends BaseTest> void startTest(Class<T> testClass) {
        final T test;
        String className = testClass.getName();
        ALOG.I("start test -> %s", className);
        try {
            Constructor<T> constructor = testClass.getConstructor(Context.class);
            test = constructor.newInstance(mContext);
            tests.put(className, test);
            test.startTest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopTest() {
        for (Map.Entry<String, BaseTest> entry : tests.entrySet()) {
            String name = entry.getKey();
            BaseTest test = entry.getValue();
            test.stopTest();
            ALOG.I("->%s stop", name);
        }
        tests.clear();
    }

    public BaseTest getTest(String className){
        return tests.get(className);
    }
}
