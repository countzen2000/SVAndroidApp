/**
 * The N2NewsIndexFragment class simulates the home screen of the N2 News "application".
 * The class is instantiated with an "example mode", which allows the code to be re-used 
 * to demonstrate various different use-cases.
 */

package com.socialvibe.sampleapp;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.socialvibe.mobilesdk.Engagement;
import com.socialvibe.mobilesdk.Publisher;
import com.socialvibe.mobilesdk.Trigger;
import com.socialvibe.mobilesdk.TriggerListener;
import com.socialvibe.sampleapp.SocialVibeActivity.InterstitialListener;

public class N2NewsIndexFragment extends Fragment implements InterstitialListener {
    
    private static final String ARGS_KEY_EXAMPLE_MODE = "ExampleMode";
    private String exampleMode;
    
    // Reference to the SocialVibeActivity that created this fragment.
    private SocialVibeActivity activity;
    
    // Only used for Expandable Banner example.
    private ImageView bannerAd;
    
    // Static instantiation method for the fragment.  The "example mode" specifies which use-case to
    // run as.  This implementation is for demonstration purposes only.  In your own app, you will 
    // likely have preset ad handling for a given activity or fragment.
    public static N2NewsIndexFragment newInstance(String exampleMode) {
        N2NewsIndexFragment f = new N2NewsIndexFragment();
        
        Bundle args = new Bundle();
        args.putString(ARGS_KEY_EXAMPLE_MODE, exampleMode);
        f.setArguments(args);
        
        return f;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (exampleMode == null) {
            exampleMode = getArguments().getString(ARGS_KEY_EXAMPLE_MODE);
        }
        
        activity = (SocialVibeActivity) getActivity();
        activity.setActionBarVisible(true);
        activity.setInterstitialListener(this);
        
        View ret = inflater.inflate(R.layout.n2_news_index_layout, null);
        View index = ret.findViewById(R.id.n2_news_index);
        
        // Content Gate example.
        if (exampleMode.equalsIgnoreCase("contentGate")) {
            index.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    new ContentGateFragment().show(activity.getSupportFragmentManager(), null);
                }
            });
            
        // Interstitials example.
        } else if (exampleMode.equalsIgnoreCase("interstitials")) {
            index.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.newFragment(N2NewsArticleFragment.newInstance(R.drawable.n2_news_selected_article));
                }
            });
        }
        
        // Expandable Banner example.
        if (exampleMode.equalsIgnoreCase("expandableBanners")) {
            bannerAd = (ImageView) ret.findViewById(R.id.n2_news_banner_ad);
            
            Publisher publisher = activity.getSponsorPublisherInstance();
            int width = activity.getScreenWidth();
            int height = activity.getScreenHeight();
            Trigger trigger = publisher.createTrigger(width, height, 1);
            
            // Create a TriggerListener that asynchronously downloads and creates the banner image for engagements.
            // The onClickListener for the banner image can be set immediately because the view is not visible until
            // DownloadImageAsyncTask completes.
            trigger.setTriggerListener(new TriggerListener() {
                @Override
                public void downloadComplete(Trigger trigger, ArrayList<Engagement> engagementsArrayList) {
                    // Display the first ad that was returned (if available).
                    if (engagementsArrayList != null && engagementsArrayList.size() > 0) {
                        final Engagement engagement = engagementsArrayList.get(0);
                        
                        // Set banner ad's on click so that it opens a full-screen interstitial of the _same_ engagement.
                        bannerAd.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                activity.showInterstitialAd(engagement, "sponsor");
                            }
                        });
                        
                        // Download the banner image and display it.
                        new DownloadImageAsyncTask(activity, bannerAd, engagement.getImageUrl()).execute();
                    } else {
                        // Inform the user that no ads are available at this time (optional).
                        Toast.makeText(activity, "No relevant engagements are available at this time. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                }
                
                // Create a Toast with the error message if the download fails.
                @Override
                public void downloadError(Trigger trigger, String message) {
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                }
            });
            // Start download for engagements.
            trigger.downloadEngagements();
        
        // Local Notifications example.
        } else if (exampleMode.equalsIgnoreCase("localNotifications")) {
            new SponsoredAccessFragment().show(activity.getSupportFragmentManager(), null);
        }
        
        return ret;
    }
    
    // InterstitialListener interface method.
    @Override
    public void onClose(boolean credited) {
        // Load fragment with unlocked content if the user was credited for interacting with the engagement.
        if (credited) {
            activity.newFragment(N2NewsArticleFragment.newInstance(R.drawable.n2_news_unlocked_article));
         }
    }
}