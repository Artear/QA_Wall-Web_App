package com.tn.webqawall.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by David Tolchinsky on 14/07/2015.
 */
public class WQWWebViewClient extends WebViewClient
{

    private final Context mContext;

    WQWWebViewClient(Context context)
    {
        mContext = context;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon)
    {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url)
    {
        super.onPageFinished(view, url);

    }
}
