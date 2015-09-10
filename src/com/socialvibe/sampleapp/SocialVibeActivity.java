/**
 * The SocialVibeActivity class is the sole activity of the SocialVibe sample application.  The class
 * owns an EngagementView that is used to display ad engagements throughout the application. 
 * To demonstrate various use-cases of the SocialVibe Mobile SDK, other fragments are loaded into view.
 */

package com.socialvibe.sampleapp;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.socialvibe.mobilesdk.Engagement;
import com.socialvibe.mobilesdk.EngagementView;
import com.socialvibe.mobilesdk.EngagementViewListener;
import com.socialvibe.mobilesdk.Publisher;
import com.socialvibe.mobilesdk.SocialVibeConfig;
import com.socialvibe.mobilesdk.Trigger;
import com.socialvibe.mobilesdk.TriggerListener;

public class SocialVibeActivity extends FragmentActivity implements EngagementViewListener, TriggerListener {
    
    // Publisher instances to demonstrate different use-cases in the sample app.  Generally, you
    // will only have one Publisher instance in your application.
    private Publisher sponsorPublisher;
    private Publisher videoPublisher;
    private Publisher notificationPublisher;
    
    private int screenWidth;
    private int screenHeight;
    private View actionBar;
    
    // Flag to track whether the fullscreen EngagementView is being displayed to the user.
    private boolean interstitialShowing = false;
    
    // Flag to track whether credit was received for viewing the most recently loaded engagement.
    private boolean creditReceived = false;
    
    // String to track the interstitial mode (i.e. what Publisher instance is being used).
    // This is generally not necessary since most apps will only have a single Publisher instance.
    private String interstitialMode;
    
    private FrameLayout interstitialFrame;
    private ProgressBar progressBar;
    private EngagementView engagementView;
    private Animation scrollUpAnimation;
    
    private InterstitialListener listener;
    
    // Listener for fragments to determine when the interstitial frame layout has been
    // closed by the user and whether credit was given for viewing the ad.
    public interface InterstitialListener {
        public abstract void onClose(boolean credited);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize SDK.
        SocialVibeConfig.init(this);
        
        // Configure and enable local notifications.
        SocialVibeConfig.configureNotifications(SocialVibeActivity.class);
        SocialVibeConfig.enableNotifications(true);
        
        // Retrieve and send Android device ID with ad requests (optional).
        // NOTE: Requires android.permission.READ_PHONE_STATE.
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        SocialVibeConfig.setDeviceId(tm.getDeviceId());
        
        // Create singleton publisher instances for each example.
        createSponsorPublisherInstance();
        createVideoPublisherInstance();
        createNotificationPublisherInstance();
        
        setContentView(R.layout.main_layout);
        
        actionBar = findViewById(R.id.n2_news_header_layout);
        Button homeButton = (Button) actionBar.findViewById(R.id.home_button);
        homeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new HomeFragment());
            }
        });
        
        interstitialFrame = (FrameLayout) findViewById(R.id.interstitial_layout);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        engagementView = (EngagementView) findViewById(R.id.engagement_view);
        engagementView.setEngagementViewListener(this);
        
        scrollUpAnimation = AnimationUtils.loadAnimation(this, R.anim.scroll_up_animation);
        scrollUpAnimation.setInterpolator(new DecelerateInterpolator(5.0f));
        
        initScreenMetrics();
        replaceFragment(new HomeFragment());
        
        // Check intent for a SocialVibe local notification bundle and handle it if it exists.
        checkForNotification(getIntent());
    }
    
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        
        // Check intent for a SocialVibe local notification bundle and handle it if it exists.
        checkForNotification(intent);
    }
    
    // Helper method for handling local notification intents.
    private void checkForNotification(Intent intent) {
        if (intent != null) {
            String socialVibeBundleName = getPackageName() + ".SocialVibeNotification";
            Bundle bundle = intent.getBundleExtra(socialVibeBundleName);
            if (bundle != null) {
                // Close the interstitial ad if it's showing before creating the
                // notification intent fragment.
                closeInterstitialAd();
                newFragment(NotificationIntentFragment.newInstance(bundle));
            }
        }
    }
    
    // Initialize screen metrics so that fragments can easily retrieve dimensions for ad-requesting purposes.
    private void initScreenMetrics() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int statusBarHeight = getStatusBarHeight();
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels - statusBarHeight;
    }
    
    public int getScreenWidth() {
        return screenWidth;
    }
    
    public int getScreenHeight() {
        return screenHeight;
    }
    
    public int getStatusBarHeight() {
        Window window = getWindow();
        Rect rect = new Rect();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }
    
    // Returns the height of the title bar based on the asset since the N2 News sample app 
    // doesn't implement an actual Android title/action bar.
    public int getTitleBarHeight() {
        return getResources().getDimensionPixelSize(R.dimen.n2_news_title_bar_height);
    }
    
    // Initialization methods for Publisher instances.  This is for demonstration purposes only.
    // Generally, you will only have one Publisher instance in your application.
    private void createSponsorPublisherInstance() {
        String placementKey = "239d5535714d812b2a56d4fa14ba31c0805f72d2";
        String userId = UUID.randomUUID().toString();
        String secretKey = "0d1f90042168148818b0830d9e280de023891a7da3";
        sponsorPublisher = new Publisher(placementKey, userId, secretKey);
    }
    
    private void createVideoPublisherInstance() {
        String placementKey = "eb843cc22eb1cb4274d675671697abfcbf8e9fc2";
        String userId = UUID.randomUUID().toString();
        String secretKey = "b849728c751c91348b34ae7f8440e5f6cd25e8c862";
        videoPublisher = new Publisher(placementKey, userId, secretKey);
    }
    
    private void createNotificationPublisherInstance() {
        String placementKey = "e24273fd23521b696b8aa146dd2fc0cdefe12e03";
        String userId = UUID.randomUUID().toString();
        String secretKey = "eeed37dfad2cf728a899bf623687f17c8d8354246d";
        notificationPublisher = new Publisher(placementKey, userId, secretKey);
    }
    
    public Publisher getSponsorPublisherInstance() {
        return sponsorPublisher;
    }
    
    public Publisher getVideoPublisherInstance() {
        return videoPublisher;
    }
    
    public Publisher getNotificationPublisherInstance() {
        return notificationPublisher;
    }
    
    public void setActionBarVisible(boolean visible) {
        if (visible) {
            actionBar.setVisibility(View.VISIBLE);
        } else {
            actionBar.setVisibility(View.GONE);
        }
    }
    
    // Loads a new fragment into the SocialVibeActivity and adds it to the back stack.
    public void newFragment(Fragment newFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment_container, newFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
    }
    
    // Removes all fragments in the SocialVibeActivity back stack and replaces it with a 
    // single new fragment.
    public void replaceFragment(Fragment newFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment_container, newFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }
    
    
    // Full-screen interstitial ad methods.
    public void setInterstitialListener(InterstitialListener listener) {
        this.listener = listener;
    }
    
    public void showInterstitialAd(Engagement engagement, String mode) {
        creditReceived = false;
        interstitialMode = mode;
        
        if (isOnline()) {
            // If an engagement was provided, display it.
            if (engagement != null) {
                engagementView.loadEngagement(engagement);
                
            // Else, fetch new engagements using the Publisher instance for the given mode.
            } else {
                Publisher publisher;
                if (mode.equalsIgnoreCase("video")) {
                    publisher = getVideoPublisherInstance();
                } else if (mode.equalsIgnoreCase("notification")) {
                    publisher = getNotificationPublisherInstance();
                } else {
                    publisher = getSponsorPublisherInstance();
                }
                
                Trigger trigger = publisher.createTrigger(getScreenWidth(), getScreenHeight(), 1);
                trigger.setTriggerListener(this);
                trigger.downloadEngagements();
            }
        
            interstitialFrame.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            interstitialShowing = true;
        } else {
            closeInterstitialAd();
            Toast.makeText(this, "Network connection unavailable.", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void closeInterstitialAd() {
        // Set the engagement frame to INVISIBLE instead of GONE, because the EngagementView
        // utilizes Android's WebView, which has a bug where the content doesn't scale properly
        // when the view changes from GONE to VISIBLE.
        engagementView.setVisibility(View.INVISIBLE);
        
        // Call clearAnimation() after hiding the engagement view to prevent a potential animation
        // flicker the next time it is set to visible.
        engagementView.clearAnimation();
        
        interstitialFrame.setVisibility(View.GONE);
        interstitialShowing = false;
        
        if (listener != null) {
            listener.onClose(creditReceived);
        }
    }
    
    // Helper method to check network connectivity.
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
    
    // EngagementViewListener interface methods.
    @Override
    public void onReady(EngagementView view) {
        // Check if the interstitial is still showing before handling the ready event.
        if (interstitialShowing) {
            progressBar.setVisibility(View.GONE);
            engagementView.setVisibility(View.VISIBLE);
            interstitialFrame.setVisibility(View.VISIBLE);
            engagementView.startAnimation(scrollUpAnimation);
        }
    }

    @Override
    public void onError(EngagementView view, String message) {
    }
    
    @Override
    public void onOpen(EngagementView view) {
    }

    @Override
    public void onClose(EngagementView view) {
        closeInterstitialAd();
    }
    
    // The SocialVibeActivity creates multiple Publisher instances for demonstration purposes, so the 
    // the onCredit method must validate against the correct instance.  In your own app, you will probably
    // only have a single Publisher instance, so tracking the "interstitial mode" won't be required.
    @Override
    public void onCredit(EngagementView view, String payload, String signature) {
        if (interstitialMode.equalsIgnoreCase("video")) {
            creditReceived = getVideoPublisherInstance().validateCredit(payload, signature);
        } else if (interstitialMode.equalsIgnoreCase("notification")) {
            creditReceived = getNotificationPublisherInstance().validateCredit(payload, signature);
        } else {
            creditReceived = getSponsorPublisherInstance().validateCredit(payload, signature);
        }
    }

    @Override
    public void onFinish(EngagementView view) {
    }
    
    // TriggerListener interface methods.
    @Override
    public void downloadComplete(Trigger trigger, ArrayList<Engagement> engagementsArrayList) {
        Log.d("SocialVibeSampleApp", "Engagements list received in sample app!");
        
        // Display the first ad that was returned (if available).
        if (engagementsArrayList != null && engagementsArrayList.size() > 0) {
            engagementView.loadEngagement(engagementsArrayList.get(0));
            
        // Otherwise, dismiss the fullscreen interstitial.
        } else {
            closeInterstitialAd();
            
            // Inform the user that no ads are available at this time (optional).
            Toast.makeText(this, "No relevant engagements are available at this time. Please try again later.", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void downloadError(Trigger trigger, String message) {
        closeInterstitialAd();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    // Override back button to close the interstitial frame layout when it's showing.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && interstitialShowing) {
            closeInterstitialAd();
            return true;
            
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}