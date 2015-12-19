package com.spiraclestudios.autoskola.intros;

import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;

/**
 * Created by benji on 04/11/2015.
 */
public class IntroActivity extends AppIntro2 {

    // Note: DO NOT override onCreate, use init()
    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(new SubscribeSlide());
        addSlide(new AdsSlide());

        // Vibration
        setVibrate(true);
        setVibrateIntensity(30);
    }

    @Override
    public void onDonePressed() {
        finish();
    }
}