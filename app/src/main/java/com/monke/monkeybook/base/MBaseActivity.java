//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.monke.monkeybook.base;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.View;

import com.monke.basemvplib.BaseActivity;
import com.monke.basemvplib.impl.IPresenter;
import com.monke.monkeybook.R;
import com.monke.monkeybook.utils.StatusBarUtil;

import java.lang.reflect.Method;

public abstract class MBaseActivity<T extends IPresenter> extends BaseActivity<T> {
    public SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate(savedInstanceState);
        setStatusBar();
        setNightTheme();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.menu_color_default), PorterDuff.Mode.SRC_ATOP);
            }
        }
        return true;
    }

    public void setOrientation() {
        switch (preferences.getString(getString(R.string.pk_screen_direction), "0")) {
            case "0":
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                break;
            case "1":
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case "2":
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                break;
        }
    }

    public void setNightTheme() {
        if (preferences.getBoolean("nightTheme", false)) {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public void setStatusBar() {
        if (preferences.getBoolean("immersionStatusBar", false)) {
            StatusBarUtil.compat(this, 0);
        }
        if (preferences.getBoolean("nightTheme", false) || !preferences.getBoolean("immersionStatusBar", false)){
            StatusBarUtil.setDarkStatusIcon(this,false);
        }else {
            StatusBarUtil.setDarkStatusIcon(this,true);
        }
    }
}
