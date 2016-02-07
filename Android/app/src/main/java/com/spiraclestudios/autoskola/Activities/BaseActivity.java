/*
 * Copyright 2015-2016 Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.activities;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.gms.analytics.HitBuilders;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.dialogs.AboutDialog;
import com.spiraclestudios.autoskola.interfaces.IBaseActivity;

import timber.log.Timber;

/**
 * Original created by benji on 08/11/2015.
 */
public class BaseActivity extends AppCompatActivity
        implements IBaseActivity, NavigationView.OnNavigationItemSelectedListener {

    public String mActivityName;

    public BaseActivity() {
        mActivityName = getActivityName();
    }

    public String getActivityName() {
        return mActivityName;
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

        if (id == R.id.nav_testy) {
            if (getActivityName().equals("MainActivity")) {
                return true;
            }
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        /*} else if (id == R.id.nav_novinky) {
            if (getActivityName().equals("NewsActivity")) {
                return true;
            }
            Intent intent = new Intent(this, NewsActivity.class);
            startActivity(intent);*/
        } else if (id == R.id.nav_dopravne_znacky) {
            if (getActivityName().equals("RoadSignsCategoryListActivity")) {
                return true;
            }
            Intent intent = new Intent(this, RoadSignsCategoryListActivity.class);
            startActivity(intent);
        } /*else if (id == R.id.nav_vyhlaska) {
            return true;
        } else if (id == R.id.nav_najst_autoskolu) {
            return true;
        } */else if (id == R.id.nav_nastavenia) {
            if (getActivityName().equals("SettingsActivity")) {
                return true;
            }
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_pomoc_a_pripomienky) {
            if (getActivityName().equals("FeedbackActivity")) {
                return true;
            }
            Helper.getTracker().send(new HitBuilders.EventBuilder()
                    .setCategory("Navigation")
                    .setAction("Pomoc a Pripomienky")
                    .build());

            Intent intent = new Intent(this, FeedbackActivity_.class);
            startActivity(intent);
        } else if (id == R.id.nav_o_aplikacii) {
            Helper.getTracker().send(new HitBuilders.EventBuilder()
                    .setCategory("Navigation")
                    .setAction("O Aplikácii")
                    .build());

            DialogFragment dialog = new AboutDialog();
            dialog.show(getSupportFragmentManager(), "About");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.nav_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
