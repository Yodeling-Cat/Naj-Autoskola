// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.framework.presentation.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.spiraclestudios.autoskola.G;
import com.spiraclestudios.autoskola.R;

public abstract class BaseActivity extends AppCompatActivity {

  protected FirebaseAnalytics firebaseAnalytics;

  @Bind(R.id.toolbar) protected Toolbar toolbar;
  @StyleRes public static int activeThemeResId = R.style.AppTheme_Light;

  protected abstract BaseActivity getThis();

  public abstract void setActivityContentView();

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    // Show intro screen if needed.
        /*if (WelcomeActivity.shouldDisplay(this)) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            finish();
            return;
        }*/

    applyAppTheme(this);
    super.onCreate(savedInstanceState);

    // TODO: Set Fabric user identifiers
    //BaseApplication.setFabricUserIdentifiers(email, fullName);

    firebaseAnalytics = FirebaseAnalytics.getInstance(this);
    setActivityContentView();
    ButterKnife.bind(getThis());
    setUpToolbar();
  }

  protected void setUpToolbar() {
    setSupportActionBar(toolbar);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.global, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    /*int id = item.getItemId();
    if (id == R.id.action_settings) {
        return true;
    }*/
    return super.onOptionsItemSelected(item);
  }

  public static void applyAppTheme(Context context) {
    SharedPreferences prefsSettings =
        context.getSharedPreferences(G.PREFS_SETTINGS, Context.MODE_PRIVATE);
    boolean nightMode = prefsSettings.getBoolean("night_mode", false);

    if (nightMode) {
      context.setTheme(R.style.AppTheme_Dark);
      activeThemeResId = R.style.AppTheme_Dark;
    } else {
      context.setTheme(R.style.AppTheme_Light);
      activeThemeResId = R.style.AppTheme_Light;
    }
  }
}
