/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.gms.analytics.HitBuilders;
import com.spiraclestudios.autoskola.BuildConfig;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.dialogs.AboutDialog;
import com.spiraclestudios.autoskola.interfaces.IBaseActivity;

import timber.log.Timber;

/**
 * Added by benji on 08/11/2015.
 */
public class BaseActivity extends AppCompatActivity
        implements IBaseActivity, NavigationView.OnNavigationItemSelectedListener {

    public String activityName;

    public BaseActivity() {
        activityName = getActivityName();
    }

    public String getActivityName() {
        return activityName;
    }

    @Override
    public void onResume() {
        super.onResume();

        Timber.i("Setting analytics tracker screen name: %s", getActivityName());
        Helper.getTracker().setScreenName(getActivityName());
        Helper.getTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.nav_drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;

        switch (id) {
            case R.id.nav_tests:
                if (getActivityName().equals("MainActivity")) return true;
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_history:
                if (getActivityName().equals("HistoryActivity")) return true;
                intent = new Intent(this, HistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_road_signs:
                if (BuildConfig.PREMIUM) {
                    if (getActivityName().equals("RoadSignsCategoriesActivity")) return true;
                    intent = new Intent(this, RoadSignsCategoriesActivity.class);
                    startActivity(intent);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.dialog_premium_feature_title)
                            .setMessage(R.string.dialog_premium_feature_message)
                            .setPositiveButton(R.string.dialog_premium_feature_positive, new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Helper.googlePlayPremiumMarketURL)));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Helper.googlePlayPremiumURL)));
                                    }
                                }
                            })
                            .setNegativeButton(R.string.dialog_premium_feature_negative, new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialog, int which) {
                                    // Simply do nothing
                                }
                            });
                    builder.create().show();
                    return true;
                }
                break;
      /*case R.id.nav_laws:
        if (getActivityName().equals("LawsActivity")) return true;
        intent = new Intent(this, LawsActivity.class);
        startActivity(intent);
        break;*/
      /*case R.id.nav_news:
        if (getActivityName().equals("NewsActivity")) return true;
        intent = new Intent(this, NewsActivity.class);
        startActivity(intent);
        break;*/
            case R.id.nav_settings:
                if (getActivityName().equals("SettingsActivity")) return true;
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_help_and_feedback:
                if (getActivityName().equals("FeedbackActivity")) return true;
                Helper.getTracker().send(new HitBuilders.EventBuilder()
                        .setCategory("Navigation")
                        .setAction("Help and Feedback")
                        .build());

                intent = new Intent(this, FeedbackActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_about:
                Helper.getTracker().send(new HitBuilders.EventBuilder()
                        .setCategory("Navigation")
                        .setAction("About")
                        .build());

                DialogFragment dialog = new AboutDialog();
                dialog.show(getSupportFragmentManager(), "About");
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.nav_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
