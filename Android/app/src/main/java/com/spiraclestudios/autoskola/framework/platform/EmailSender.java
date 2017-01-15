/*
 * Copyright 2016 Spiracle Software. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.framework.platform;

import android.content.Context;
import android.content.Intent;

public class EmailSender {

  private Context context;

  public EmailSender(Context context) {
    this.context = context;
  }

  public void openMailingClient(String subject, String message, String emailAddress,
      String chooserTitle) {
    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("text/email");
    intent.putExtra(Intent.EXTRA_EMAIL, new String[] { emailAddress });
    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
    intent.putExtra(Intent.EXTRA_TEXT, message);
    context.startActivity(Intent.createChooser(intent, chooserTitle));
  }
}
