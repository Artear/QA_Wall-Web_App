package com.tn.webqawall.util;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * Created by David Tolchinsky on 14/07/2015.
 */
public class WQWInterface
{

    private final Context mContext;

    WQWInterface(Context context)
    {
        mContext = context;
    }

    @JavascriptInterface
    public void showToast()
    {
        Toast.makeText(mContext, "Test WQW", Toast.LENGTH_SHORT).show();
    }
}
