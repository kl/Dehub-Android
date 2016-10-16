package com.dewire.dehub;

import android.app.Application;
import android.content.Context;
import android.os.Looper;

import com.dewire.dehub.model.AppComponent;
import com.dewire.dehub.model.AppModule;
import com.dewire.dehub.model.DaggerAppComponent;
import com.dewire.dehub.model.NetModule;

/**
 * Created by kl on 14/10/16.
 */

public class DehubApplication extends Application {

  public static boolean isMainThread() {
    return Thread.currentThread() == Looper.getMainLooper().getThread();
  }

  public static AppComponent getAppComponent(Context context) {
    return ((DehubApplication) context.getApplicationContext()).getAppComponent();
  }

  private AppComponent appComponent;

  public AppComponent getAppComponent() {
    return appComponent;
  }

  @Override
  public void onCreate() {
    super.onCreate();

    appComponent = DaggerAppComponent.builder()
        .appModule(new AppModule(this))
        .netModule(new NetModule("https://api.github.com"))
        .build();
  }
}