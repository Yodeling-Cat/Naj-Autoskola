// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.framework.presentation.ui;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import com.spiraclestudios.autoskola.presentation.ui.activities.HomeActivity;

public abstract class PrimaryActivity extends NavigationDrawerActivity {

  /*@Override public void onBackPressed() {
    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
      drawerLayout.closeDrawer(GravityCompat.START);
    } else {
      NavUtils.navigateUpTo(this,
          new Intent(this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME));
      super.onBackPressed();
    }
  }*/
}
