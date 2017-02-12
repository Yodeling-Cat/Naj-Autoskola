// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.framework.presentation.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.framework.presentation.utils.AppearanceController;

public abstract class BaseActivity extends AppCompatActivity {

  protected FirebaseAnalytics firebaseAnalytics;

  @Bind(R.id.toolbar) protected Toolbar toolbar;

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

    new AppearanceController().setActiveThemeOnContext(this);
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
}
