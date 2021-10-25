package com.hsh.project101;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Window;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

public class CustomProgressDialog {

    private Activity activity;
    private Dialog dialog;

    public CustomProgressDialog(Activity activity) {
        this.activity = activity;
        init();
    }

    private void init() {
        dialog = new Dialog(activity, android.R.style.Theme_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(false);
    }

    public void show() {
        if (dialog == null) return;
        dialog.show();
    }

    public void dismiss() {
        if (dialog == null) return;
        dialog.dismiss();
    }
}
