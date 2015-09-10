/**
 * The N2NewsArticleFragment class simulates an article view of the N2 News "application".
 * The class is instantiated with an "article ID", which allows the code to be re-used to 
 * show different articles -- one that the user selects and another that the user unlocks.
 */

package com.socialvibe.sampleapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.socialvibe.sampleapp.SocialVibeActivity.InterstitialListener;

public class N2NewsArticleFragment extends Fragment implements InterstitialListener {
    
    private static final String ARGS_KEY_ARTICLE_ID = "ArticleId";
    private int articleId;
    private View articleContainer;
    
    // Reference to the SocialVibeActivity that created this fragment.
    private SocialVibeActivity activity;
    
    // Static instantiation method for the fragment.  The "article ID" specifies what the contents
    // of the article are.  This implementation is for demonstration purposes only.
    public static N2NewsArticleFragment newInstance(int articleId) {
        N2NewsArticleFragment f = new N2NewsArticleFragment();
        
        Bundle args = new Bundle();
        args.putInt(ARGS_KEY_ARTICLE_ID, articleId);
        f.setArguments(args);
        
        return f;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (articleId == 0) {
            articleId = getArguments().getInt(ARGS_KEY_ARTICLE_ID);
        }
        
        activity = (SocialVibeActivity) getActivity();
        activity.setActionBarVisible(true);
        activity.setInterstitialListener(this);
        View ret = inflater.inflate(R.layout.n2_news_article_layout, null);
        
        articleContainer = ret.findViewById(R.id.n2_news_article);
        articleContainer.setBackgroundResource(articleId);
        
        // If the article ID is the user selected article, the onClickListener opens a fullscreen
        // insterstitial ad.  This is to demonstrate the "Interstitials" example.
        if (articleId == R.drawable.n2_news_selected_article) {
            articleContainer.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.showInterstitialAd(null, "sponsor");
                }
            });
        }
        
        return ret;
    }
    
    // InterstitialListener interface method.
    @Override
    public void onClose(boolean credited) {
        // Generally, fullscreen interstitial ads are not "gating".  They just appear periodically as 
        // the user navigates through an application (e.g. every X number of page views); therefore, the
        // user gains access to the next article regardless of whether they were credited for interacting
        // with the engagement to completion.
        activity.newFragment(N2NewsArticleFragment.newInstance(R.drawable.n2_news_unlocked_article));
    }
}