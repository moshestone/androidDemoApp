package com.example.javasecondapp;

import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;

public class SecondFragmentDirections {
    private SecondFragmentDirections() {
    }

    public static NavDirections actionSecondFragmentToFirstFragment() {
        return new ActionOnlyNavDirections(R.id.action_SecondFragment_to_FirstFragment);
    }
}
