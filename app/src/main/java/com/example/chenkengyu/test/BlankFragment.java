package com.example.chenkengyu.test;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class BlankFragment extends Fragment {

    TextView textView;

    public BlankFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        textView = view.findViewById(R.id.tv);
        textView.setText(""+position);
        return view;
    }

    private static final String POSITION_PARAM = "position";
    private int position;
    private static final int TYPE_THEME = 0;
    private static final int TYPE_FONT = 1;

    public static BlankFragment newInstance(int position) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION_PARAM, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(POSITION_PARAM);
        }
    }



}
