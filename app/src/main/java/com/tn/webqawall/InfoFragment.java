package com.tn.webqawall;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by David Tolchinsky on 17/07/2015.
 */
public class InfoFragment extends Fragment{

    public static InfoFragment newInstance() {
        return new InfoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.info_fragment, container, false);

        return view;
    }


}
