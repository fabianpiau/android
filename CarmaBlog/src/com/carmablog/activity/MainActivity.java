package com.carmablog.activity;

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
import com.carmablog.retriever.RetrievePageLocalTask;
import com.carmablog.retriever.RetrievePageRemoteTask;
import com.carmablog.url.common.URLCallMethod;
import com.carmablog.url.common.URLConstant;
import com.carmablog.url.history.URLContent;
import com.carmablog.url.history.URLContentHistoryHelper;
import com.carmablog.util.LanguageUtils;

/**
 * CarmaBlog Main Activity.
 * @author fpiau
 *
 */
public class MainActivity extends Activity {

	// WebView component
	private WebView myWebView;
	private WebViewClient myWebViewClient;
	
	// Menu
    private Menu menu;
	private static final int SWITCH_LANG_EN_ID = R.id.menu_en;
	private static final int SWITCH_LANG_FR_ID = R.id.menu_fr;
	private static final int HOME_ID = R.id.menu_home;
	private static final int MANAGEMENT_ID = Menu.FIRST;
	private static final int AGILE_PROGRAMMING_ID = Menu.FIRST + 1;
	private static final int TECHNOLOGY_ID = Menu.FIRST + 2;
	private static final int LINUX_ID = Menu.FIRST + 3;
	private static final int EVENT_ID = Menu.FIRST + 4;

	
	// Current state
	private CharSequence currentLang;
	private String currentUrl;
	
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
		initializeLanguageFromDevice();
		loadCarmablogUrl(URLConstant.HOME_CARMABLOG_URL);
	}

	/*
	 * Set the current language
	 * By default, depending on the device settings
	 */
	private void initializeLanguageFromDevice() {
		if (LanguageUtils.isDeviceInFrench()) {
			currentLang = URLConstant.LANG_FR;
		} else {
			currentLang = URLConstant.LANG_EN;
		}
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
		if (currentLang == null) {
			initializeLanguageFromDevice();
		}
		final String localizedUrl = LanguageUtils.localizeUrl(url, currentLang);
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
	 * Load URL with Jsoup.
	 * The call is asynchronous.
	 */
	private void loadUrlInBackground(final String url) {
		// Look in the history in case the page is already loaded...
		final URLContent urlContent = urlContentHistoryHelper.getURLContentFromURL(url); 
		if (urlContent != null) {
			currentUrl = urlContent.getUrl();
			loadCarmablogUrl(urlContent);
		} else {
			// First time
			// Async call
			currentUrl = url;
			new RetrievePageRemoteTask(this).execute(url);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		// Set a reference to the menu for further modifications
		this.menu = menu;
		
		// Inflate the menu
		// This adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		menu.add(0, MANAGEMENT_ID, 0, R.string.menu_management).setShortcut('1', 'm').setIcon(R.drawable.management);
		menu.add(0, AGILE_PROGRAMMING_ID, 0, R.string.menu_agile_programming).setShortcut('2', 'a').setIcon(R.drawable.agile_programming);
		menu.add(0, TECHNOLOGY_ID, 0, R.string.menu_technology).setShortcut('3', 't').setIcon(R.drawable.technology);
		menu.add(0, LINUX_ID, 0, R.string.menu_linux).setShortcut('4', 'l').setIcon(R.drawable.linux);
		menu.add(0, EVENT_ID, 0, R.string.menu_event).setShortcut('5', 'e').setIcon(R.drawable.event);
		
		changeMenuItemLang(currentLang);

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
			case SWITCH_LANG_EN_ID:
				currentLang = URLConstant.LANG_EN;
				changeMenuItemLang(URLConstant.LANG_EN);
				loadCarmablogUrl(currentUrl);
				return true;
			case SWITCH_LANG_FR_ID:
				currentLang = URLConstant.LANG_FR;
				changeMenuItemLang(URLConstant.LANG_FR);
				loadCarmablogUrl(currentUrl);
				return true;
			case HOME_ID:
				loadCarmablogUrl(URLConstant.HOME_CARMABLOG_URL);
				return true;
			case MANAGEMENT_ID:
				loadCarmablogUrl(URLConstant.HOME_CARMABLOG_URL + "management" + URLConstant.SEPARATOR_URL);
				return true;
			case AGILE_PROGRAMMING_ID:
				loadCarmablogUrl(URLConstant.HOME_CARMABLOG_URL + "agile-programming" + URLConstant.SEPARATOR_URL);
				return true;
			case TECHNOLOGY_ID:
				loadCarmablogUrl(URLConstant.HOME_CARMABLOG_URL + "technology" + URLConstant.SEPARATOR_URL);
				return true;
			case LINUX_ID:
				loadCarmablogUrl(URLConstant.HOME_CARMABLOG_URL + "linux" + URLConstant.SEPARATOR_URL);
				return true;
			case EVENT_ID:
				loadCarmablogUrl(URLConstant.HOME_CARMABLOG_URL + "event" + URLConstant.SEPARATOR_URL);
				return true;
			default:
				loadCarmablogUrl(URLConstant.HOME_CARMABLOG_URL);
				return true;
		}
	}

	/*
	 * Change the item to change language.
	 */
	private void changeMenuItemLang(CharSequence lang) {
		final MenuItem langEnMenuItem = menu.findItem(R.id.menu_en);
		final MenuItem langFrMenuItem = menu.findItem(R.id.menu_fr);
		if (currentLang.equals(URLConstant.LANG_FR)) {
			// English switcher
			langEnMenuItem.setVisible(true);
			langFrMenuItem.setVisible(false);
		} else {
			// French switcher	
			langEnMenuItem.setVisible(false);
			langFrMenuItem.setVisible(true);
		}
	}

	public WebView getMyWebView() {
		return myWebView;
	}

	public URLContentHistoryHelper getUrlContentHistoryHelper() {
		return urlContentHistoryHelper;
	}
		
}
