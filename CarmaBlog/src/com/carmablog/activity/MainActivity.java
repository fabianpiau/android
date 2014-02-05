package com.carmablog.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.protocol.HTTP;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.SearchView;

import com.carmablog.R;
import com.carmablog.retriever.RetrievePageLocalTask;
import com.carmablog.retriever.RetrievePageRemoteTask;
import com.carmablog.url.common.URLCallMethod;
import com.carmablog.url.common.URLConstant;
import com.carmablog.url.history.URLContent;
import com.carmablog.url.history.URLContentHistoryHelper;
import com.carmablog.util.CarmaBlogUtils;

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

    // Current state
	private String currentLang;
	private URLContent currentUrlContent;
	
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
		
		// Restore language from preferences
		final SharedPreferences settings = getPreferences(MODE_PRIVATE);
	    currentLang = settings.getString("currentLang", null);
	    if (currentLang == null) {
	    	// Nothing found in preference
	    	// By default use the language of the device
	    	initializeLanguageFromDevice();
	    }
	    
		// Load the homepage
		loadCarmablogUrl(URLConstant.HOME_CARMABLOG_URL);
	}
	
    @Override
    protected void onStop(){
		super.onStop();
		
		// Save last language used in preferences for the next time
		final SharedPreferences settings = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("currentLang", currentLang);
		editor.commit();
    }

	/*
	 * Set the current language
	 * By default, depending on the device settings
	 */
	private void initializeLanguageFromDevice() {
		if (CarmaBlogUtils.isDeviceInFrench()) {
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
		final String localizedUrl = CarmaBlogUtils.localizeUrl(url, currentLang);
		if (localizedUrl != null) {
			// Load the page
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
		// Look in the history in case the page has been loaded before...
		final URLContent urlContent = urlContentHistoryHelper.getURLContentFromURL(url); 
		if (urlContent != null) {
			// Yeah, it will be faster
			loadCarmablogUrl(urlContent);
		} else {
			// First time
			// Do an async call
			new RetrievePageRemoteTask(this).execute(url);
		}
		if (menu != null) {
			// Show the share button only if we are displaying a single post
			MenuItem shareMenuItem = menu.findItem(R.id.menu_share);
			if (CarmaBlogUtils.isUrlMatchingSinglePost(url)) {
				shareMenuItem.setVisible(true);
			} else {
				shareMenuItem.setVisible(false);
			}
		}
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		// Set a reference to the menu for further modifications
		this.menu = menu;
		
		// Inflate the menu
		// This adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		// Make the share button invisible
		final MenuItem shareMenuItem = menu.findItem(R.id.menu_share);
		shareMenuItem.setVisible(false);

		// Search menu (only if device is Honeycomb or newer)
		MenuItem searchItem = menu.findItem(R.id.menu_search);
		if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) >= 11) {
		    
		    SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		    searchView.setSubmitButtonEnabled(true);
		    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
		    	  	@Override
		    	    public boolean onQueryTextChange(String newText) {
			    	    return false;
		    	    }
			        @Override
			        public boolean onQueryTextSubmit(String query) {
			        	search(query);
			            return false;
			        }
		    });
		} else {
			searchItem.setVisible(false);
		}
	    
		changeMenuItemLang(currentLang);

		return true;
	}
	
	/*
	 * Launch query on the WordPress search feature.
	 */
	private void search(final String query) {
		final String searchUrl = URLConstant.HOME_CARMABLOG_URL + 
								   URLConstant.QUERY_SEARCH_TERM + query.replace(" ", "+") + 
									 URLConstant.QUERY_SEARCH_ACTION;
		loadCarmablogUrl(searchUrl);
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
			case R.id.menu_search:
				return true;
			case R.id.menu_share:
			    startActivity(defineSharingIntent());
				return true;
			case R.id.menu_categories:
				return true;
			case R.id.menu_home:
				loadCarmablogUrl(URLConstant.HOME_CARMABLOG_URL);
				return true;
			case R.id.menu_en:
				currentLang = URLConstant.LANG_EN;
				changeMenuItemLang(URLConstant.LANG_EN);
				loadCarmablogUrl(currentUrlContent.getUrl());
				return true;
			case R.id.menu_fr:
				currentLang = URLConstant.LANG_FR;
				changeMenuItemLang(URLConstant.LANG_FR);
				loadCarmablogUrl(currentUrlContent.getUrl());
				return true;
			case R.id.menu_management:
				loadCarmablogUrl(URLConstant.HOME_CARMABLOG_URL + "management" + URLConstant.SEPARATOR_URL);
				return true;
			case R.id.menu_agile_programming:
				loadCarmablogUrl(URLConstant.HOME_CARMABLOG_URL + "agile-programming" + URLConstant.SEPARATOR_URL);
				return true;
			case R.id.menu_technology:
				loadCarmablogUrl(URLConstant.HOME_CARMABLOG_URL + "technology" + URLConstant.SEPARATOR_URL);
				return true;
			case R.id.menu_linux:
				loadCarmablogUrl(URLConstant.HOME_CARMABLOG_URL + "linux" + URLConstant.SEPARATOR_URL);
				return true;
			case R.id.menu_event:
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
	
	/*
	 * Define sharing intent for the current post.
	 */
	private Intent defineSharingIntent() {
		final Intent globalSharingIntent = new Intent();
		globalSharingIntent.setType(HTTP.PLAIN_TEXT_TYPE);
		globalSharingIntent.setAction(Intent.ACTION_SEND);
		
		final PackageManager packageManager = myWebView.getContext().getPackageManager();
	    final List<ResolveInfo> activities = packageManager.queryIntentActivities(globalSharingIntent, 0);
	    
		final List<Intent> sharingIntents = new ArrayList<Intent>();
		final String shareBody = currentUrlContent.getTitle() + " - " + currentUrlContent.getUrl();
		
	    for (final ResolveInfo app : activities) {
			final Intent sharingIntent = new Intent();
			sharingIntent.setType(HTTP.PLAIN_TEXT_TYPE);
			sharingIntent.setAction(Intent.ACTION_SEND);
	    	sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.id.menu_share));
	    	final String packageName = app.activityInfo.packageName;
	    	if (CarmaBlogUtils.isPackageMatchingAppForSharing(packageName)) {
		        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
		        sharingIntent.setPackage(packageName);
		        sharingIntents.add(sharingIntent);
	    	}
	    }

		final Intent chooserIntent = Intent.createChooser(sharingIntents.remove(0), getString(R.string.share_title));
		chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, sharingIntents.toArray(new Parcelable[]{}));
		return chooserIntent;
	}

	public WebView getMyWebView() {
		return myWebView;
	}

	public URLContentHistoryHelper getUrlContentHistoryHelper() {
		return urlContentHistoryHelper;
	}

	public void setCurrentUrlContent(final URLContent currentUrlContent) {
		this.currentUrlContent = currentUrlContent;
	}
	
}
