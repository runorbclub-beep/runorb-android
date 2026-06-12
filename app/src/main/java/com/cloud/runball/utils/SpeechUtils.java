package com.cloud.runball.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * @ProjectName: wristball
 * @Package: com.cloud.runball.utils
 * @ClassName: SpeechUtils
 * @Description:
 * @Author: zhd
 * @CreateDate: 2021/7/5 16:34
 * @UpdateUser: zhd
 * @UpdateDate: 2021/7/5 16:34
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class SpeechUtils {
    private Context context;
    private static final String TAG = "SpeechUtils";
    private static SpeechUtils singleton;

    private TextToSpeech textToSpeech;

    public static SpeechUtils getInstance(Context context) {
        if (singleton == null) {
            synchronized (SpeechUtils.class) {
                if (singleton == null) {
                    singleton = new SpeechUtils(context);
                }
            }
        }
        return singleton;
    }

    public SpeechUtils(Context context) {
        this.context = context;
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.CHINA);
                    textToSpeech.setPitch(1.0f);// 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
                    textToSpeech.setSpeechRate(1.0f);
                }
            }
        });
    }

    public void speakLanguage(Locale loc) {
        if (textToSpeech != null) {
            textToSpeech.setLanguage(loc);
        }
    }

    public void speakText(String text) {
        if (textToSpeech != null) {
            textToSpeech.speak(text,
                    TextToSpeech.QUEUE_FLUSH, null);
        }

    }
}
