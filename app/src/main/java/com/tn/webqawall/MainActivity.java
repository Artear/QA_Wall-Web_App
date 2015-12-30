package com.tn.webqawall;

import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;
import com.tn.webqawall.socket.event.Page;
import io.palaima.debugdrawer.DebugDrawer;
import io.palaima.debugdrawer.module.BuildModule;
import io.palaima.debugdrawer.module.DeviceModule;
import io.palaima.debugdrawer.module.NetworkModule;
import io.palaima.debugdrawer.module.SettingsModule;
import io.palaima.debugdrawer.scalpel.ScalpelModule;

/**
 * Created by David Tolchinsky on 14/07/2015.
 */
public class MainActivity extends FragmentActivity
{
    private final static String URL_FROM_INTENT = "URL_FROM_INTENT";
    public static final String WAKE_LOCK_TAG = "WAKE_LOCK_TAG";
    private WebViewFragment webViewFragment;
    private PowerManager.WakeLock wakeLock;
    private DebugDrawer debugDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.d("WQWLog", "Action Value: " + getIntent().getAction());
        Log.d("WQWLog", "Has Extras: " + (getIntent().getExtras() != null));

        webViewFragment = new WebViewFragment();

        if (getIntent().getAction().equals("android.intent.action.MAIN") &&
                getIntent().getExtras() != null)
        {
            Log.d("WQWLog", "Extra Value on URL_FROM_INTENT: " + getIntent().getExtras().get(URL_FROM_INTENT));
            String url = getIntent().getExtras().getString(URL_FROM_INTENT);

            webViewFragment.setUrl(url);
        }

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main_activity);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.relative_qa_wall, webViewFragment);
        fragmentTransaction.commit();

        if (BuildConfig.DEBUG)
        {
            debugDrawer = new DebugDrawer.Builder(this).modules(
                    new ScalpelModule(this),
                    new DeviceModule(this),
                    new BuildModule(this),
                    new NetworkModule(this),
                    new SettingsModule(this)
            ).build();
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (debugDrawer != null)
        {
            debugDrawer.onStart();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (debugDrawer != null)
        {
            debugDrawer.onStop();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        App.getSocket().on(Page.EVENT_NAME, new Emitter.Listener()
        {
            @Override
            public void call(final Object... args)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Page pageEvent = new Gson().fromJson(String.valueOf(args[0]), Page.class);

                        webViewFragment.setUrl(pageEvent.getUrl());
                    }
                });
            }
        });

        App.getSocket().connect();

        //Prevent screen sleep
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, WAKE_LOCK_TAG);
        wakeLock.acquire();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        App.getSocket().disconnect();
        App.getSocket().off(Page.EVENT_NAME);

        //Release screen lock
        wakeLock.release();
    }

    @Override
    public void onBackPressed()
    {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.relative_qa_wall);

        if (currentFragment == webViewFragment)
        {
            if (((WebViewFragment) currentFragment).canGoBack())
            {
                ((WebViewFragment) currentFragment).goBack();
            } else
            {
                super.onBackPressed();
            }
        }
    }

}
