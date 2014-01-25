package com.carmablog.activity;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.carmablog.R;
import com.carmablog.retreiver.RetrievePageLocalTask;
import com.carmablog.retreiver.RetrievePageRemoteTask;
import com.carmablog.url.URLCallMethod;
import com.carmablog.url.URLContent;
import com.carmablog.url.URLContentHistoryHelper;

/**
 * CarmaBlog Main Activity.
 * @author fpiau
 *
 */
public class MainActivity extends Activity {

	// WebView component
	private WebView myWebView;
	private WebViewClient myWebViewClient;

	// CarmaBlog URL processing
	private static final CharSequence LANG_PATTERN = "[LANG]";
	private static final CharSequence LANG_SUFFIX_FR = "fr";
	private static final CharSequence LANG_SUFFIX_EN = "en";
	public static final String HOME_URL = "http://blog." + URLContentHistoryHelper.CARMABLOG_PATTERN + "/"+ LANG_PATTERN +"/";

	// Menu
	private static final int HOME_ID = Menu.FIRST;
	private static final int MANAGEMENT_ID = Menu.FIRST + 1;
	private static final int AGILE_PROGRAMMING_ID = Menu.FIRST + 2;
	private static final int TECHNOLOGY_ID = Menu.FIRST + 3;
	private static final int LINUX_ID = Menu.FIRST + 4;
	private static final int EVENT_ID = Menu.FIRST + 5;
	
	// URL history helper
	private URLContentHistoryHelper urlContentHistoryHelper;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		myWebView = new WebView(this);
		// Activate main useful features
		myWebView.getSettings().setBuiltInZoomControls(true);
		myWebView.getSettings().setSupportZoom(true);
		myWebView.getSettings().setJavaScriptEnabled(true); // For syntax highlighting
		// Take all the screen space
		setContentView(myWebView);
		
		// Create the URL content history helper
		urlContentHistoryHelper = new URLContentHistoryHelper(this);

		// A client to handle links and navigation history
		myWebViewClient = new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				super.shouldOverrideUrlLoading(view, url);
				if (urlContentHistoryHelper.isUrlMatchingForApp(url)) {
					// Still on CarmaBlog
					// Load in the same WebView
					loadCarmablogUrl(url);
				} else {
					// Not on CarmaBlog anymore or it is about sharing
					// Load in an external browser
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					startActivity(i);
				}
				return true;
			}

		};
		myWebView.setWebViewClient(myWebViewClient);
		
		// Load the homepage
		loadCarmablogUrl(HOME_URL);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (myWebView == null || myWebView.getUrl() == null) {
			return;
		}
		// Not a resume from an external page
		if (!(urlContentHistoryHelper.isUrlMatchingForApp(myWebView.getUrl()))
				// And not the launch of the app
				&& !(myWebView.getUrl().equals("about:blank"))) {
			urlContentHistoryHelper.goBack(URLCallMethod.ON_RESUME);
		}
	}

	/*
	 * Load the URL (link in CarmaBlog).
	 * The content has never been displayed yet.
	 */
	public void loadCarmablogUrl(final String url) {
		if (!urlContentHistoryHelper.isUrlMatchingForApp(url)) {
			Log.e("MainActivity.loadCarmablogUrl", "Not a URL from CarmaBlog, you should open it in an external browser.");
			return;
		}
		String localizedUrl;
		// Localize the URL depending on the device default language
		if (Locale.getDefault().getDisplayLanguage().contains("fran")) {
			localizedUrl = insertCodeLang(url, LANG_SUFFIX_FR);
		} else {
			localizedUrl = insertCodeLang(url, LANG_SUFFIX_EN);
		}
		if (localizedUrl != null) {
			loadUrlInBackground(localizedUrl);
		}
	}
	
	/*
	 * Load the URL (link in CarmaBlog).
	 * The content is coming from the history.
	 */
	public void loadCarmablogUrl(final URLContent urlContent) {
		// Simply display it in the WebView
		// Async call
		new RetrievePageLocalTask(this).execute(urlContent);
	}
	
	/*
	 * Replace the pattern in the URL with the language.
	 */
	private String insertCodeLang(final String url, final CharSequence lang) {
		return url.replace(LANG_PATTERN, lang);
	}
	
	/*
	 * Load URL with Jsoup.
	 * The call is asynchronous.
	 */
	private void loadUrlInBackground(final String url) {
		// Look in the history in case the page is already loaded...
		final URLContent urlContent = urlContentHistoryHelper.getURLContentFromURL(url); 
		if (urlContent != null) {
			loadCarmablogUrl(urlContent);
		} else {
			// First time
			// Async call
			new RetrievePageRemoteTask(this).execute(url);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		super.onCreateOptionsMenu(menu);
		// Inflate the menu
		// This adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		menu.add(0, MANAGEMENT_ID, 0, R.string.menu_management).setShortcut('2', 'm').setIcon(R.drawable.management);
		menu.add(0, AGILE_PROGRAMMING_ID, 0, R.string.menu_agile_programming).setShortcut('3', 'a').setIcon(R.drawable.agile_programming);
		menu.add(0, TECHNOLOGY_ID, 0, R.string.menu_technology).setShortcut('4', 't').setIcon(R.drawable.technology);
		menu.add(0, LINUX_ID, 0, R.string.menu_linux).setShortcut('5', 'l').setIcon(R.drawable.linux);
		menu.add(0, EVENT_ID, 0, R.string.menu_event).setShortcut('6', 'e').setIcon(R.drawable.event);
		
		return true;
	}

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		switch (keyCode) {
			// Check if the key event was the Back button
			case KeyEvent.KEYCODE_BACK:
				return urlContentHistoryHelper.goBack(URLCallMethod.ON_KEY_DOWN);
			default:
				return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		super.onMenuItemSelected(featureId, item);
		switch (item.getItemId()) {
			case HOME_ID:
				loadCarmablogUrl(HOME_URL);
				return true;
			case MANAGEMENT_ID:
				loadCarmablogUrl(HOME_URL + "management/");
				return true;
			case AGILE_PROGRAMMING_ID:
				loadCarmablogUrl(HOME_URL + "agile-programming/");
				return true;
			case TECHNOLOGY_ID:
				loadCarmablogUrl(HOME_URL + "technology/");
				return true;
			case LINUX_ID:
				loadCarmablogUrl(HOME_URL + "linux/");
				return true;
			case EVENT_ID:
				loadCarmablogUrl(HOME_URL + "event/");
				return true;
			default:
				loadCarmablogUrl(HOME_URL);
				return true;
		}
	}
	
	public WebView getMyWebView() {
		return myWebView;
	}

	public URLContentHistoryHelper getUrlContentHistoryHelper() {
		return urlContentHistoryHelper;
	}
		
}
