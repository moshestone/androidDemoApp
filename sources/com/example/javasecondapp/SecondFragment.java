package com.example.javasecondapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import java.util.Random;

public class SecondFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Integer myArg = Integer.valueOf(SecondFragmentArgs.fromBundle(getArguments()).getMyArg());
        ((TextView) view.findViewById(R.id.textview_header)).setText(getString(R.string.randon_heading, myArg));
        Integer count = myArg;
        Random random = new Random();
        Integer randomNumber = Integer.valueOf(0);
        if (count.intValue() > 0) {
            randomNumber = Integer.valueOf(random.nextInt(count.intValue() + 1));
        }
        ((TextView) view.getRootView().findViewById(R.id.textview_random)).setText(randomNumber.toString());
        view.findViewById(R.id.button_second).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this).navigate((int) R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }
}
