package com.tn.webqawall.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by David Tolchinsky on 14/07/2015.
 */
public class WQWWebView extends WebView{

    public WQWWebView(Context context) {
        super(context);
    }

    public WQWWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WQWWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public WQWWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public WQWWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        addJavascriptInterface(new WQWInterface(getContext()),"Android");

        setWebViewClient(new WQWWebViewClient(getContext()));

    }

}
