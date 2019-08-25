package com.huari.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huari.client.R;
import com.huari.ui.LinView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentLin extends Fragment {
    LinView linViewTemperature;
    LinView linViewHumidity;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_line,container,false);
        linViewTemperature = view.findViewById(R.id.temperature);
        linViewHumidity = view.findViewById(R.id.humidity);
        return view;
    }
}
