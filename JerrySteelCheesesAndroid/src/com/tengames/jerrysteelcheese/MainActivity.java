/*
The MIT License

Copyright (c) 2014 kong <tengames.inc@gmail.com>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
package com.tengames.jerrysteelcheese;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.tengames.jerrysteelcheese.interfaces.AndroidListener;
import com.tengames.jerrysteelcheese.main.JerrySteelCheese;

public class MainActivity extends AndroidApplication implements AndroidListener {
	private static final String ADMOBID_INTERSTITIAD = "xx-xxx-xx";
	private static final String PROPERTY_ID = "xx-xxx-xx";

	private static InterstitialAd mInterstitialAd = null;

	/**
	 * Enum used to identify the tracker that needs to be used for tracking.
	 *
	 * A single tracker is usually enough for most purposes. In case you do need
	 * multiple trackers, storing them all in Application object helps ensure that
	 * they are created only once per application instance.
	 */
	private enum TrackerName {
		APP_TRACKER, // Tracker used only in this app.
		GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg:
						// roll-up tracking.
		ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a
							// company.
	}

	private static HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

	private ProgressDialog dgProgress = null;
	private JerrySteelCheese coreGame;
	private Context context;
	private SharedPreferences preData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = this.getBaseContext();

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		if (coreGame == null) {
			coreGame = new JerrySteelCheese();
			coreGame.registerAndroidInterface(this);
		}

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useGL20 = true;

		setContentView(R.layout.activity_main);

		final FrameLayout fr = (FrameLayout) findViewById(R.id.id_frame_game);
		if (coreGame != null)
			fr.addView(initializeForView(coreGame, config));

		// initialize preferences
		preData = PreferenceManager.getDefaultSharedPreferences(this);

		// Create the interstitial.
		mInterstitialAd = new InterstitialAd(this);
		mInterstitialAd.setAdUnitId(ADMOBID_INTERSTITIAD);

		// Create ad request.
		AdRequest adRequest = new AdRequest.Builder().build();
		// Begin loading your interstitial.
		mInterstitialAd.loadAd(adRequest);

		// Enable Advertising Features.
		getTracker(TrackerName.APP_TRACKER).enableAdvertisingIdCollection(true);
	}

	private synchronized Tracker getTracker(TrackerName trackerId) {
		if (!mTrackers.containsKey(trackerId)) {

			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			Tracker t = analytics.newTracker(PROPERTY_ID);

			mTrackers.put(trackerId, t);

		}
		return mTrackers.get(trackerId);
	}

	@Override
	public BufferedReader getData() {
		InputStream is = context.getResources().openRawResource(R.raw.jerrysteelcheese);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		return br;
	}

	@Override
	public void setValue(int map, int value) {
		SharedPreferences.Editor editor = preData.edit();
		editor.putInt(("map" + map), value);
		editor.commit();
	}

	@Override
	public int getValue(int map) {
		int value = 0;
		// default map 1
		if (map == 0)
			value = preData.getInt(("map" + map), 1);
		else
			value = preData.getInt(("map" + map), 0);
		return value;
	}

	@Override
	public void setSound(boolean isSound) {
		SharedPreferences.Editor editor = preData.edit();
		editor.putBoolean("sound", isSound);
		editor.commit();
	}

	@Override
	public boolean getSound() {
		boolean isSound = preData.getBoolean("sound", true);
		return isSound;
	}

	@Override
	public void saveHscore(int addScore) {
		int hscore = getHscore();
		hscore += addScore;

		SharedPreferences.Editor editor = preData.edit();
		editor.putInt("hscore", hscore);
		editor.commit();
	}

	@Override
	public int getHscore() {
		int hscore = preData.getInt("hscore", 0);
		return hscore;
	}

	@Override
	public void showDialog(final String text) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				AlertDialog.Builder dgPopup = new AlertDialog.Builder(MainActivity.this);
				dgPopup.setTitle("WARNING");
				dgPopup.setMessage(text);
				dgPopup.setCancelable(false);
				// accept
				dgPopup.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				dgPopup.show();
			}
		});
	}

	@Override
	public void showToast(final String text) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void showLoading(final boolean show) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (show) {
					dgProgress = ProgressDialog.show(MainActivity.this, "Sending request", "Please wait ...");
				} else {
					dgProgress.dismiss();
				}
			}
		});
	}

	@Override
	public void showIntertitial() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (mInterstitialAd.isLoaded()) {
					mInterstitialAd.show();

				}
				// Create ad request.
				AdRequest adRequest = new AdRequest.Builder().build();
				// Begin loading your interstitial.
				mInterstitialAd.loadAd(adRequest);
			}
		});
	}

	@Override
	public void gotoHighscore(final String link) {
		// open webview
		if (isConnected(this)) {
			Intent myIntent = new Intent(this, WebViewActivity.class);
			myIntent.putExtra("link", link); // Optional parameters
			startActivity(myIntent);
		} else {
			showToast("Please check your internet connection and try again !");
		}
	}

	@Override
	public void gotoStore(String link) {
		// open webview
		if (isConnected(this)) {
			Intent myIntent = new Intent(this, WebViewActivity.class);
			myIntent.putExtra("link", link); // Optional parameters
			startActivity(myIntent);
		} else {
			showToast("Please check your internet connection and try again !");
		}
	}

	/**
	 * Device id
	 */
	@Override
	public String getDeviceId() {
		return Secure.getString(getContentResolver(), Secure.ANDROID_ID);
	}

	/**
	 * Google Analytic
	 */
	@Override
	public void traceScene(final String level) {
		// Set screen name.
		getTracker(TrackerName.APP_TRACKER).setScreenName(level);

		// Send a screen view.
		getTracker(TrackerName.APP_TRACKER).send(new HitBuilders.ScreenViewBuilder().build());
	}

	@Override
	protected void onStart() {
		super.onStart();
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}

	/**
	 * check Internet connection
	 * 
	 * @param context
	 * @return
	 */
	public boolean isConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
			return true;
		}

		return false;
	}

}
