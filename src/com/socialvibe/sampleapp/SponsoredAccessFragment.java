/**
 * The SponsoredAccessFragment class creates a dialog that blocks the user from further access to the
 * application.  The user is given various options to obtain access to the application, of which only 
 * the "Free 3 Day Pass" is demonstrated in the N2 News "application".
 * 
 * This use-case grants the user access to the app for a limited amount of time (e.g. 3 days) if they
 * engaged with a sponsor ad.  After this time has expired, they would be re-prompted to renew their
 * free access by engaging with another ad.
 * 
 * The actual tracking of the "3 days of free usage" is outside the scope of this sample app.
 */

package com.socialvibe.sampleapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.socialvibe.sampleapp.SocialVibeActivity.InterstitialListener;

public class SponsoredAccessFragment extends DialogFragment implements InterstitialListener {
    
    // Reference to the SocialVibeActivity that created this fragment.
    private SocialVibeActivity activity;
    
    // Flag to track whether the user has opened one of the sponsored access options.
    private boolean sponsorAccessOpened = false;
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        activity = (SocialVibeActivity) getActivity();
        activity.setInterstitialListener(this);
        
        View contentGate = activity.getLayoutInflater().inflate(R.layout.sponsored_access_layout, null);
        
        // Set onClickListener for "Free 3 Day Pass" option.
        View freePassButton = contentGate.findViewById(R.id.sponsored_access_3_day_pass_button);
        freePassButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss dialog and show fullscreen interstitial ad of type "notification".
                sponsorAccessOpened = true;
                dismiss();
                activity.showInterstitialAd(null, "notification");
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
    
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        
        // If the user dismissed the dialog without opening a sponsor access ad, simulate
        // exiting the N2 News sample app by returning to the Home screen.
        if (!sponsorAccessOpened) {
            activity.replaceFragment(new HomeFragment());
        }
    }
    
    // InterstitialListener interface method.
    @Override
    public void onClose(boolean credited) {
        // If the user was not credited for a Free 3 Day pass, simulate exiting of the 
        // N2 News sample app by returning to the Home screen.
        if (!credited) {
            activity.replaceFragment(new HomeFragment());
        }
    }
}
