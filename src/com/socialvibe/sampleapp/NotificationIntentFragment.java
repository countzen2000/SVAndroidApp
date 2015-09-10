/**
 * The NotificationIntentFragment class loads a SocialVibe local notification bundle into an 
 * EngagementView.  If the bundle is not null and contains a SocialVibe follow-up URL, the URL
 * will be opened within the N2 News "application".
 */

package com.socialvibe.sampleapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.socialvibe.mobilesdk.EngagementView;

public class NotificationIntentFragment extends Fragment {
    
    public static NotificationIntentFragment newInstance(Bundle bundle) {
        NotificationIntentFragment f = new NotificationIntentFragment();
        f.setArguments(bundle);
        return f;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((SocialVibeActivity) getActivity()).setActionBarVisible(true);
        EngagementView ret = (EngagementView) inflater.inflate(R.layout.notification_intent_layout, null);
        Bundle bundle = getArguments();
        ret.loadNotification(bundle);
        
        return ret;
    }
}
