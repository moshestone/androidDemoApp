package com.example.javasecondapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

public class FirstFragment extends Fragment {
    TextView showCountTextView;

    /* access modifiers changed from: private */
    public void countMe(View view) {
        this.showCountTextView.setText(Integer.valueOf(Integer.valueOf(Integer.parseInt(this.showCountTextView.getText().toString())).intValue() + 1).toString());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentFirstLayout = inflater.inflate(R.layout.fragment_first, container, false);
        this.showCountTextView = (TextView) fragmentFirstLayout.findViewById(R.id.textview_first);
        return fragmentFirstLayout;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.random_button).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this).navigate((NavDirections) FirstFragmentDirections.actionFirstFragmentToSecondFragment(Integer.parseInt(((TextView) view.getRootView().findViewById(R.id.textview_first)).getText().toString())));
            }
        });
        view.findViewById(R.id.toast_button).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Toast.makeText(FirstFragment.this.getActivity(), "Hello toast!", 0).show();
            }
        });
        view.findViewById(R.id.count_button).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                FirstFragment.this.countMe(view);
            }
        });
    }
}
