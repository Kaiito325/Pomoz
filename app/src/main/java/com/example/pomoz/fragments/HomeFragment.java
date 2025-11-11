package com.example.pomoz.fragments;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.pomoz.R;
import com.example.pomoz.views.SemiCircleProgressView;
public class HomeFragment extends Fragment {
    public HomeFragment() {}

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.home_fragment, container, false);
        SemiCircleProgressView progressView = v.findViewById(R.id.semiProgress);

        int currentStep = 200;
        int maxStep = 500;

        ValueAnimator animation = ValueAnimator.ofInt(0, currentStep);
        animation.setDuration(1200);
        animation.addUpdateListener(animator -> {
            int value = (int) animator.getAnimatedValue();
            progressView.setProgressSteps(value, maxStep);
        });
        animation.start();
        return v;
    }
}
