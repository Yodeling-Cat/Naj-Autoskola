package com.spiraclestudios.autoskola.Activities;

import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.spiraclestudios.autoskola.R;

/**
 * Created by benji on 04/11/2015.
 */
public class IntroActivity extends AppIntro {

    // Please DO NOT override onCreate. Use init
    @Override
    public void init(Bundle savedInstanceState) {

        // Add your slides fragments here
        // AppIntro will automatically generate the dots indicator and buttons.
        //addSlide(first_fragment);
        //addSlide(second_fragment);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest
        addSlide(AppIntroFragment.newInstance("Intro 1", "This app is awesome!",
                R.drawable.app_icon, R.color.colorPrimary));
        addSlide(AppIntroFragment.newInstance("Intro 2", "Get started using it!",
                R.drawable.app_icon, Color.parseColor("#C62828")));

        // OPTIONAL METHODS
        // Override bar/separator color
        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));

        // Turn vibration on and set intensity
        setVibrate(true);
        setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed() {
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed() {
        // Do something when users tap on Done button.
    }

    @Override
    public void onDotSelected(int index) {
        switch (index) {
            case 1:
                showSkipButton(false);
                showDoneButton(false);
                break;
            case 2:
                showSkipButton(true);
                showDoneButton(true);
                break;
            default:
                break;
        }
    }
}