package com.huari.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huari.client.R;
import com.huari.ui.CirclePainView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentCircle extends Fragment {
    CirclePainView circlePainViewApere;
    CirclePainView circlePainViewVoltmeter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_circle,container,false);
        circlePainViewApere = view.findViewById(R.id.ampere);
        circlePainViewVoltmeter = view.findViewById(R.id.voltmeter);
        return view;
    }
}
