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
	private static final int HOME_ID = Menu.FIRST;
	private static final int MANAGEMENT_ID = Menu.FIRST + 1;
	private static final int AGILE_PROGRAMMING_ID = Menu.FIRST + 2;
	private static final int TECHNOLOGY_ID = Menu.FIRST + 3;
	private static final int LINUX_ID = Menu.FIRST + 4;
	private static final int EVENT_ID = Menu.FIRST + 5;
	private static final int SWITCH_LANG_EN_ID = Menu.FIRST + 6;
	private static final int SWITCH_LANG_FR_ID = Menu.FIRST + 7;
	
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
		// Inflate the menu
		// This adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		menu.add(0, MANAGEMENT_ID, 0, R.string.menu_management).setShortcut('2', 'm').setIcon(R.drawable.management);
		menu.add(0, AGILE_PROGRAMMING_ID, 0, R.string.menu_agile_programming).setShortcut('3', 'a').setIcon(R.drawable.agile_programming);
		menu.add(0, TECHNOLOGY_ID, 0, R.string.menu_technology).setShortcut('4', 't').setIcon(R.drawable.technology);
		menu.add(0, LINUX_ID, 0, R.string.menu_linux).setShortcut('5', 'l').setIcon(R.drawable.linux);
		menu.add(0, EVENT_ID, 0, R.string.menu_event).setShortcut('6', 'e').setIcon(R.drawable.event);
		
		if (currentLang.equals(URLConstant.LANG_FR)) {
			// English switcher
			menu.add(0, SWITCH_LANG_EN_ID, 0, R.string.menu_en).setShortcut('7', 'n');
		} else {
			// French switcher		
			menu.add(0, SWITCH_LANG_FR_ID, 0, R.string.menu_fr).setShortcut('8', 'f');
		}
		
		// Set a reference to the menu for further modifications
		this.menu = menu;
		
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
			default:
				loadCarmablogUrl(URLConstant.HOME_CARMABLOG_URL);
				return true;
		}
	}

	/*
	 * Change the item to change language.
	 */
	private void changeMenuItemLang(CharSequence lang) {
		if (lang.equals(URLConstant.LANG_EN)) {
			// Display the item to go to the French version
			menu.removeItem(SWITCH_LANG_EN_ID);
			menu.add(0, SWITCH_LANG_FR_ID, 0, R.string.menu_fr).setShortcut('8', 'f');
		}
		if (lang.equals(URLConstant.LANG_FR)) {
			// Display the item to go to the English version
			menu.removeItem(SWITCH_LANG_FR_ID);
			menu.add(0, SWITCH_LANG_EN_ID, 0, R.string.menu_en).setShortcut('7', 'n');
		}
		
	}

	public WebView getMyWebView() {
		return myWebView;
	}

	public URLContentHistoryHelper getUrlContentHistoryHelper() {
		return urlContentHistoryHelper;
	}
		
}
