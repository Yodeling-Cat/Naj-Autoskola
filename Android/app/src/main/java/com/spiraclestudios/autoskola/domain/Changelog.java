// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.domain;

import android.support.annotation.StringRes;

public class Changelog {

  private final ChangelogType type;
  private final @StringRes int description;

  public Changelog(ChangelogType type, @StringRes int description) {
    this.type = type;
    this.description = description;
  }

  public ChangelogType getType() {
    return type;
  }

  public @StringRes int getDescription() {
    return description;
  }
}
