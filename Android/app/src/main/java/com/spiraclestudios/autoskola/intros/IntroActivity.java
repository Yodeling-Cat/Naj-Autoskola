/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.intros;

import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;

/**
 * Added by benji on 04/11/2015.
 */
public class IntroActivity extends AppIntro2 {

    // Note: DO NOT override onCreate, use init()
    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(new AboutYouSlide());
        addSlide(new SubscribeSlide());

        // Vibration
        setVibrate(true);
        setVibrateIntensity(30);
    }

    @Override
    public void onDonePressed() {
        finish();
    }
}