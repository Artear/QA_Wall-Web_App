package com.tn.webqawall;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tn.webqawall.util.WQWWebView;

/**
 * Created by David Tolchinsky on 17/07/2015.
 */
public class WebViewFragment extends Fragment
{

    private static final String URL_LOAD = "url_load";

    private WQWWebView webView;
    private String url;

    public static WebViewFragment newInstance(String url)
    {

        WebViewFragment fragment = new WebViewFragment();

        Bundle args = new Bundle();
        args.putString(URL_LOAD, url);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        Bundle args = getArguments();
        url = args.getString(URL_LOAD);

        View view = inflater.inflate(R.layout.web_view_fragment, container, false);
        webView = (WQWWebView) view.findViewById(R.id.qa_wall_web_view);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(URL_LOAD, url);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState)
    {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null)
        {
            // Restore last state for checked position.
            url = savedInstanceState.getString(URL_LOAD);
        }
        webView.loadUrl(url);
    }

    /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        getActivity().finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }*/

    public boolean canGoBack()
    {
        return webView.canGoBack();
    }

    public void goBack()
    {
        webView.goBack();
    }
}
