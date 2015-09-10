/**
 * The HomeFragment class creates the main view for the sample application, which allows the user
 * to select which SocialVibe Mobile SDK use-case to demonstrate.
 * The four use-cases are: Content Gate, Expandable Banners, Local Notifications, and Interstitials.
 */

package com.socialvibe.sampleapp;

import com.socialvibe.mobilesdk.SocialVibeConfig;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Switch;

public class HomeFragment extends Fragment {
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final SocialVibeActivity activity = ((SocialVibeActivity) getActivity());
        activity.setActionBarVisible(false);
        View ret = inflater.inflate(R.layout.home_layout, null);
        
        Activity myActivity = this.getActivity();
		  
		  final SharedPreferences sharedSettings = myActivity.getPreferences(0);
		  
	     if (!sharedSettings.contains("UseQA")) {
	    	  SharedPreferences.Editor editor = sharedSettings.edit();
	    	  editor.putBoolean("UseQA", false);

	          // Commit the edits!
	          editor.commit();
	     }
	     SocialVibeConfig.enableTestServer(sharedSettings.getBoolean("UseQA", false));

        // Set onClickListener for "Content Gate" example.
        View contentGate = ret.findViewById(R.id.content_gate);
        contentGate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.newFragment(N2NewsIndexFragment.newInstance("contentGate"));
            }
        });
        
        // Set onClickListener for "Expandable Banners" example.
        View expandableBanners = ret.findViewById(R.id.expandable_banners);
        expandableBanners.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.newFragment(N2NewsIndexFragment.newInstance("expandableBanners"));
            }
        });
        
        // Set onClickListener for "Local Notifications" example.
        View localNotifications = ret.findViewById(R.id.local_notifications);
        localNotifications.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.newFragment(N2NewsIndexFragment.newInstance("localNotifications"));
            }
        });
        
        // Set onClickListener for "Interstitials" example.
        View interstitials = ret.findViewById(R.id.interstitials);
        interstitials.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.newFragment(N2NewsIndexFragment.newInstance("interstitials"));
            }
        });
        
        View settings = ret.findViewById(R.id.button1);
        settings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	 activity.newFragment(SettingsFragment.newInstance("settings"));
            }
        });
        
        return ret;
    }
    

}
