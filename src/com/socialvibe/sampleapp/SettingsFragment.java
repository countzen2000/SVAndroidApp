package com.socialvibe.sampleapp;

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
//import android.widget.Switch;

import com.socialvibe.mobilesdk.SocialVibeConfig;


public class SettingsFragment extends Fragment {
	  
	
	private static final String ARGS_KEY_EXAMPLE_MODE = "ExampleMode";
	
	  @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
		  View ret = inflater.inflate(R.layout.settings_layout, null);
		  
		  Activity myActivity = this.getActivity();
		  
		  final SharedPreferences settings = myActivity.getPreferences(0);
		  
	     if (!settings.contains("UseQA")) {
	    	  SharedPreferences.Editor editor = settings.edit();
	    	  editor.putBoolean("UseQA", false);

	          // Commit the edits!
	          editor.commit();
	     }
	     
	     Switch theSwitch = (Switch) ret.findViewById(R.id.switch1);
		 theSwitch.setChecked(settings.getBoolean("UseQA", false));
		 if (settings.getBoolean("UseQA", false)) {
			 Log.d("Test Server", "true");
		 } else {
			 Log.d("Test Server", "false");
		 }
		 theSwitch.setChecked(settings.getBoolean("UseQA", false));
		 
	     //Switch theSwitch = (Switch) ret.findViewById(R.id.switch1);
	     theSwitch.setOnClickListener(new OnClickListener() {
	          @Override
	          public void onClick(View v) {
	        	  Log.d("Test Server", "clicked");
	        	  //Switch theSwitch = (Switch) v.findViewById(R.id.switch1);
	        	  Switch inHereSwitch = (Switch) v;
	        	  if (inHereSwitch.isChecked()) {
	        		  Log.d("Test Server", "true");
	        		  SocialVibeConfig.enableTestServer(true);  
	        	  } else {
	        		  Log.d("Test Server", "false");
	        		  SocialVibeConfig.enableTestServer(false);
	        	  }
	        	  
	        	  SharedPreferences.Editor editor = settings.edit();
		    	  editor.putBoolean("UseQA", inHereSwitch.isChecked());

		          // Commit the edits!
		          editor.commit();
	              
	          }
	      });
	       
		  return ret;
	    }
	  
	  public static SettingsFragment newInstance(String exampleMode) {
		  SettingsFragment f = new SettingsFragment();
	        
	        Bundle args = new Bundle();
	        args.putString(ARGS_KEY_EXAMPLE_MODE, exampleMode);
	        f.setArguments(args);
	        
	        return f;
	    }
}
