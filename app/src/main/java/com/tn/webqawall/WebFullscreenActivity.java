package com.tn.webqawall;

import com.tn.webqawall.util.WQWWebView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by David Tolchinsky on 14/07/2015.
 */
public class WebFullscreenActivity extends Activity {

    private final static String URL_FROM_INTENT = "URL_FROM_INTENT";
    private WQWWebView webView;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("WQWLog", "Action Value: " + getIntent().getAction());
        Log.d("WQWLog", "Has Extras: " + (getIntent().getExtras() != null));

        if(getIntent().getAction().equals("android.intent.action.MAIN") &&
            getIntent().getExtras() != null){

            Log.d("WQWLog", "Extra Value on URL_FROM_INTENT: " + getIntent().getExtras().get(URL_FROM_INTENT));
            url = getIntent().getExtras().getString(URL_FROM_INTENT);
        }

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_web_fullscreen);
        webView = (WQWWebView) findViewById(R.id.qa_wall_web_view);
        webView.loadUrl(url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch(keyCode)
            {
                case KeyEvent.KEYCODE_BACK:
                    if(webView.canGoBack()){
                        webView.goBack();
                    }else{
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

}
