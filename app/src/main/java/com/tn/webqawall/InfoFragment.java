package com.tn.webqawall;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by David Tolchinsky on 17/07/2015.
 */
public class InfoFragment extends Fragment
{

    private TextView deviceNameTextView;
    private TextView deviceModelTextView;
    private TextView sdkTextView;
    private TextView resolutionTextView;

    public static InfoFragment newInstance()
    {
        return new InfoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.info_fragment, container, false);

        deviceNameTextView = (TextView) view.findViewById(R.id.device_name_info_fragment);
        deviceModelTextView = (TextView) view.findViewById(R.id.device_model_info_fragment);
        sdkTextView = (TextView) view.findViewById(R.id.sdk_info_fragment);
        resolutionTextView = (TextView) view.findViewById(R.id.resolution_info_fragment);

        BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
        deviceNameTextView.setText(myDevice.getName());
        deviceModelTextView.setText(Build.MODEL);
        sdkTextView.setText(Build.VERSION.RELEASE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getActivity().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        String text = screenHeight + " x " + screenWidth;
        resolutionTextView.setText(text);
        return view;
    }


}
