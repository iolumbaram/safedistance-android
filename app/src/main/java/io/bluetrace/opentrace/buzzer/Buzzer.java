package io.bluetrace.opentrace.buzzer;

import android.media.AudioManager;
import android.media.ToneGenerator;

public class Buzzer {
    public void OnBuzz(){
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        int tone = 0;
        int duration = 1000;
    }
    //toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
}
