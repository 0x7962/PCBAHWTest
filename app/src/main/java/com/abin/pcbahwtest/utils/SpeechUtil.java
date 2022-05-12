package com.abin.pcbahwtest.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class SpeechUtil implements TextToSpeech.OnInitListener {

    private Context mContext;
    private TextToSpeech mTextToSpeech;

    public SpeechUtil(Context context) {
        mContext = context;
        mTextToSpeech = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int ret = mTextToSpeech.setLanguage(Locale.CHINA);
            if (ret == TextToSpeech.LANG_MISSING_DATA || ret == TextToSpeech.LANG_NOT_SUPPORTED) {
                ALOG.E("device not support chinese tts");
                mTextToSpeech.setLanguage(Locale.US);
            }

            mTextToSpeech.setPitch(1.0f);//音调
            mTextToSpeech.setSpeechRate(1.0f);//语速
        }
    }

    public void shutdown() {
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }
    }

    public void speak(String text) {
        mTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}
