/**
 * The ContentGateFragment class creates a dialog that blocks the user from further access to the
 * application.  The user is given the option to watch a video ad or engage with a sponsor ad to 
 * continue using the app.
 */

package com.socialvibe.sampleapp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.socialvibe.sampleapp.SocialVibeActivity.InterstitialListener;

public class ContentGateFragment extends DialogFragment implements InterstitialListener {
    
    // Reference to the SocialVibeActivity that created this fragment.
    private SocialVibeActivity activity;
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        activity = (SocialVibeActivity) getActivity();
        activity.setInterstitialListener(ContentGateFragment.this);
        View contentGate = activity.getLayoutInflater().inflate(R.layout.content_gate_layout, null);
        
        // Set onClickListener for "Watch a Video" option.
        View videoButton = contentGate.findViewById(R.id.content_gate_video_button);
        videoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss dialog and show fullscreen interstitial ad of type "video".
                dismiss();
                activity.showInterstitialAd(null, "video");
            }
        });
        
        // Set onClickListener for "Engage with Sponsor" option.
        View engageButton = contentGate.findViewById(R.id.content_gate_engage_button);
        engageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss dialog and show fullscreen interstitial ad of type "sponsor".
                dismiss();
                activity.showInterstitialAd(null, "sponsor");
            }
        });
        
        // Create a transparent dialog that blocks further access to all content except the action bar.
        Dialog dialog = new Dialog(activity, R.style.TransparentDialog);
        dialog.setContentView(contentGate);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int statusBarHeight = activity.getStatusBarHeight();
        int titleBarHeight = activity.getTitleBarHeight();
        
        WindowManager.LayoutParams windowLayoutParams = window.getAttributes();
        windowLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        windowLayoutParams.x = 0;
        windowLayoutParams.y = statusBarHeight + titleBarHeight;
        windowLayoutParams.width = metrics.widthPixels;
        windowLayoutParams.height = metrics.heightPixels - statusBarHeight - titleBarHeight;
        windowLayoutParams.dimAmount = 0;
        window.setAttributes(windowLayoutParams);
        
        return dialog;
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