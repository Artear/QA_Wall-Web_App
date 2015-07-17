package com.tn.webqawall;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;
import com.tn.webqawall.socket.event.Page;

/**
 * Created by David Tolchinsky on 14/07/2015.
 */
public class MainActivity extends FragmentActivity
{
    private final static String URL_FROM_INTENT = "URL_FROM_INTENT";
    private ViewPager viewPager;
    private MyPagerAdapter adapterViewPager;
    private WebViewFragment webViewFragment;
    private InfoFragment infoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.d("WQWLog", "Action Value: " + getIntent().getAction());
        Log.d("WQWLog", "Has Extras: " + (getIntent().getExtras() != null));

        webViewFragment = new WebViewFragment();
        infoFragment = new InfoFragment();

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


        viewPager = (ViewPager) findViewById(R.id.view_pager_qa_wall);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);
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
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        App.getSocket().disconnect();
        App.getSocket().off(Page.EVENT_NAME);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter
    {
        private final int NUM_ITEMS = 2;

        public MyPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            switch (position)
            {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return webViewFragment;
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return infoFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount()
        {
            return NUM_ITEMS;
        }
    }

    @Override
    public void onBackPressed()
    {
        Fragment currentFragment = adapterViewPager.getItem(viewPager.getCurrentItem());

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
