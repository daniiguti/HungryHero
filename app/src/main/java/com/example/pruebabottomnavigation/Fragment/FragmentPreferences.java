package com.example.pruebabottomnavigation.Fragment;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.pruebabottomnavigation.R;

public class FragmentPreferences extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}