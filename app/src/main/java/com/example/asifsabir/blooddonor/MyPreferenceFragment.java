package com.example.asifsabir.blooddonor;

/**
 * Created by asifsabir on 11/12/17.
 */

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.widget.Toast;

/**
 * Created by asifsabir on 11/12/17.
 */

public class MyPreferenceFragment extends PreferenceFragment {
    SwitchPreference notificationSwitch;
    public static ListPreference notificationList;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        notificationSwitch = (SwitchPreference) findPreference("enable_notification");
        notificationList = (ListPreference) findPreference("notification_range");

        //initial loading for the settings rangeView
        boolean xx = getPreferenceScreen().findPreference("enable_notification").getSharedPreferences().getBoolean("enable_notification", true);
        if (xx) {
            getPreferenceScreen().findPreference("notification_range").setEnabled(true);
        } else {
            getPreferenceScreen().findPreference("notification_range").setEnabled(false);

        }

        notificationSwitch.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                //open browser or intent here
                boolean xx = preference.getSharedPreferences().getBoolean("enable_notification", true);

                if (xx == false) {
                    //        notificationList.setEnabled(false);
                    getPreferenceScreen().findPreference("notification_range").setEnabled(false);

                } else {
                    //     notificationList.setEnabled(true);
                    getPreferenceScreen().findPreference("notification_range").setEnabled(true);
                }
                return true;
            }
        });

    }
}