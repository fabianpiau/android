package com.carmablog.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.MenuItemCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.SearchView;
import android.widget.Toast;

import com.carmablog.R;
import com.carmablog.retriever.RetrieveHtmlRemoteTask;
import com.carmablog.url.common.UrlCallMethod;
import com.carmablog.url.common.UrlConstant;
import com.carmablog.url.manager.UrlContentCacheManager;
import com.carmablog.url.manager.UrlContentHistoryManager;
import com.carmablog.url.model.UrlContent;
import com.carmablog.url.model.UrlHtmlContent;
import com.carmablog.util.CarmaBlogUtils;
import com.carmablog.view.CustomWebView;

/**
 * CarmaBlog HTML Activity.
 * @author fpiau
 *
 */
public class HtmlActivity extends Activity {

	// Application
	private MyApplication myApplication;
	
	// WebView component - Normal Web page
	private WebView myWebView;
	
	// URL history manager
	private UrlContentHistoryManager urlContentHistoryManager;
	
	// Menu
    private Menu menu;

    // Current state and preferences
	private UrlContent currentUrlContent;
	private SharedPreferences preferences;
	private static final String KEY_CURRENT_LANG = "currentLang";
	private boolean nightMode;
	private static final String KEY_NIGHT_MODE_ACTIVE = "nightMode";
	private Integer totalNumberOfPages;
	    
	// Exchange with RSS activity
	private static final int GET_URL_REQUEST_CODE = 1;
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Get a link to the application
		myApplication = (MyApplication) getApplicationContext();
		
		// Initialize display component
		initializeMyWebView();
		
		urlContentHistoryManager = new UrlContentHistoryManager(this);
		
		// Restore settings from preferences
		preferences = getPreferences(MODE_PRIVATE);
		myApplication.setCurrentLang(preferences.getString(KEY_CURRENT_LANG, null));
	    if (getCurrentLang() == null) {
	    	// Nothing found in preferences
	    	// By default, use the language of the device
	    	myApplication.initializeLanguageFromDevice();
	    }
		nightMode = preferences.getBoolean(KEY_NIGHT_MODE_ACTIVE, false);
	    
		// Load the homepage
		loadCarmablogHtmlUrl(UrlConstant.HOME_PAGE_CARMABLOG_URL);
	}

	private void initializeMyWebView() {
		myWebView = new CustomWebView(this);
		myWebView.setBackgroundColor(Color.parseColor("#1F2021"));
		setContentView(myWebView);
	}

    @Override
    protected void onStop(){
		super.onStop();
		
		// Save last language used and reading mode in preferences for the next time
		final SharedPreferences.Editor editor = preferences.edit();
		editor.putString(KEY_CURRENT_LANG, getCurrentLang()).commit();
		editor.putBoolean(KEY_NIGHT_MODE_ACTIVE, nightMode).commit();
    }

	@Override
	protected void onResume() {
		super.onResume();
		if (myWebView == null || myWebView.getUrl() == null) {
			return;
		}
		// Not a resume from an external page
		if (!(CarmaBlogUtils.isUrlMatchingForApp(myWebView.getUrl()))
				// And not the launch of the app
				&& !(myWebView.getUrl().equals("about:blank"))) {
			urlContentHistoryManager.goBack(UrlCallMethod.ON_RESUME);
		}
	}

	/*
	 * Load the HTML URL.
	 */
	public void loadCarmablogHtmlUrl(final String url) {
		final String localizedUrl = myApplication.transformCarmaBlogUrl(url);
		defineShareButtonVisibility(url);
		if (localizedUrl != null) {
			// Look in the cache in case the page has been loaded before...
			final UrlContent urlContent = getUrlContentCacheManager().getFromCache(localizedUrl); 
			if (urlContent != null) {
				// Yeah, it will be faster
				loadCarmablogHtmlUrlFromCache((UrlHtmlContent)urlContent);
			} else {
				// First time
				loadCarmablogHtmlUrlInBackground(localizedUrl);
			}
		}
	}

	/*
	 * Load the HTML URL with Jsoup.
	 * The call is asynchronous.
	 */
	private void loadCarmablogHtmlUrlInBackground(final String url) {
		// Do an async call
		new RetrieveHtmlRemoteTask(this).execute(url);
	}
	
	/*
	 * Load the HTML URL.
	 * The content is coming from the cache.
	 */
	public void loadCarmablogHtmlUrlFromCache(final UrlHtmlContent urlContent) {
		updateHistoryAndCurrentState(urlContent);
		defineShareButtonVisibility(null);
		// Refresh the reading mode
		refreshReadingMode(urlContent);
		// Simply display it in the WebView
		myWebView.loadDataWithBaseURL("file:///android_asset/", urlContent.getHtmlContent(), "text/html", "UTF-8", null);
	}

	/*
	 * Update history, cache and current state with the just loaded HTML content.
	 */
	public void updateHistoryAndCurrentState(final UrlHtmlContent urlContent) {
		// Add the URL in the history
		urlContentHistoryManager.addUrlInHistory(urlContent);
		// Add the content in cache
		getUrlContentCacheManager().addHtmlInCache(urlContent);
		// Update the current URL
		currentUrlContent = urlContent;
	}

	/*
	 * Define the visibility of the share button depending on the URL.
	 * @param url The URL to check. If null, try to get the URL from the current state.
	 * @param visibility Force the visibility (no calculation done). Put false to hide the button.
	 */
	private void defineShareButtonVisibility(final String url, final boolean... visibility) {
		if (menu != null) {
			final MenuItem shareMenuItem = menu.findItem(R.id.menu_share);
			if (visibility.length == 1) {
				shareMenuItem.setVisible(visibility[0]);
				return;
			}
			String urlToCheck = null;
			if (url != null) {
				urlToCheck = url;
			} else if (currentUrlContent != null) {
				urlToCheck = currentUrlContent.getUrl();
			} else {
				// Don't know what to do so leave the button in its current state
				return;
			}
			// Show the share button only if we are displaying a single post
			if (CarmaBlogUtils.isUrlMatchingSinglePost(urlToCheck)) {
				shareMenuItem.setVisible(true);
			} else {
				shareMenuItem.setVisible(false);
			}
		}
	}
	
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if (requestCode == GET_URL_REQUEST_CODE && resultCode == RESULT_OK) {
			// Load the selected URL
			loadCarmablogHtmlUrl(data.getStringExtra(RssActivity.URL));
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
		
		// This is the homepage so make the share button invisible
		defineShareButtonVisibility(null, false);

		// Search menu (only if device is Honeycomb or newer)
		MenuItem searchItem = menu.findItem(R.id.menu_search);
		if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) >= Build.VERSION_CODES.HONEYCOMB) {
		    
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
	    
		// Show switchers
		changeMenuItemLang();
		changeReadingMode();
		
		return true;
	}
	
	/*
	 * Launch query on the WordPress search feature.
	 */
	private void search(final String query) {
		final String searchUrl = UrlConstant.HOME_CARMABLOG_URL + 
								   UrlConstant.QUERY_SEARCH_TERM + query.replace(" ", "+") + 
									 UrlConstant.QUERY_SEARCH_ACTION;
		loadCarmablogHtmlUrl(searchUrl);
	}

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		switch (keyCode) {
			// Check if the key event was the Back button
			case KeyEvent.KEYCODE_BACK:
				return urlContentHistoryManager.goBack(UrlCallMethod.ON_KEY_DOWN);
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
			case R.id.menu_home:
				loadCarmablogHtmlUrl(UrlConstant.HOME_PAGE_CARMABLOG_URL);
				return true;
			case R.id.menu_en:
				myApplication.setCurrentLang(UrlConstant.LANG_EN);
				changeMenuItemLang();
				if (currentUrlContent != null) {
					loadCarmablogHtmlUrl(currentUrlContent.getUrl());
				}
				return true;
			case R.id.menu_fr:
				myApplication.setCurrentLang(UrlConstant.LANG_FR);
				changeMenuItemLang();
				if (currentUrlContent != null) {
					loadCarmablogHtmlUrl(currentUrlContent.getUrl());
				}
				return true;
			case R.id.menu_rss:
				// Start the RSS activity
				Intent intent = new Intent(this, RssActivity.class);
				startActivityForResult(intent, GET_URL_REQUEST_CODE);
				return true;
			case R.id.menu_share:
				final Intent sharingIntent = defineSharingIntent();
				if (sharingIntent != null) {
					startActivity(sharingIntent);
			    }
				return true;
			case R.id.menu_categories:
				return true;
			case R.id.menu_management:
				loadCarmablogHtmlUrl(UrlConstant.CATEGORY_MANAGEMENT_URL);
				return true;
			case R.id.menu_agile_programming:
				loadCarmablogHtmlUrl(UrlConstant.CATEGORY_AGILE_PROGRAMMING_URL);
				return true;
			case R.id.menu_technology:
				loadCarmablogHtmlUrl(UrlConstant.CATEGORY_TECHNOLOGY_URL);
				return true;
			case R.id.menu_linux:
				loadCarmablogHtmlUrl(UrlConstant.CATEGORY_LINUX_URL);
				return true;
			case R.id.menu_event:
				loadCarmablogHtmlUrl(UrlConstant.CATEGORY_EVENT_URL);
				return true;
			case R.id.menu_mode_normal:
				nightMode = false;
					changeReadingMode();
				if (currentUrlContent != null) {
					// Go back to the normal mode
					refreshReadingMode((UrlHtmlContent)currentUrlContent);
				}
				return true;
			case R.id.menu_mode_night:
				nightMode = true;
					changeReadingMode();
					// Go to the night mode
					if (currentUrlContent != null) {
					refreshReadingMode((UrlHtmlContent)currentUrlContent);
				}
				return true;
			case R.id.menu_about:
				loadCarmablogHtmlUrl(UrlConstant.CATEGORY_ABOUT_URL);
				return true;
			default:
				loadCarmablogHtmlUrl(UrlConstant.HOME_PAGE_CARMABLOG_URL);
				return true;
		}
	}

	/*
	 * Change the item to change reading mode.
	 */
	private void changeReadingMode() {
		final MenuItem nightModeMenuItem = menu.findItem(R.id.menu_mode_night);
		final MenuItem normalModeMenuItem = menu.findItem(R.id.menu_mode_normal);
		// Modify the current HTML content with jsoup to add or remove the CSS
		if (isNightMode()) {
			nightModeMenuItem.setVisible(false);
			normalModeMenuItem.setVisible(true);
		} else {
			normalModeMenuItem.setVisible(false);
			nightModeMenuItem.setVisible(true);
		}
	}

	/*
	 * Replace the CSS according to the mode selected.
	 */
	private void refreshReadingMode(final UrlHtmlContent currentUrlContent) {
		// Replace the CSS mode in the HTML content
		if (isNightMode()) {
			currentUrlContent.setHtmlContent(currentUrlContent.getHtmlContent().replace("normal_style.css", "night_style.css"));
		} else {
			currentUrlContent.setHtmlContent(currentUrlContent.getHtmlContent().replace("night_style.css", "normal_style.css"));
		}
		// Reload the WebView	
		myWebView.loadDataWithBaseURL("file:///android_asset/", currentUrlContent.getHtmlContent(), "text/html", "UTF-8", null);
	}

	/*
	 * Change the item to change language.
	 */
	private void changeMenuItemLang() {
		final MenuItem langEnMenuItem = menu.findItem(R.id.menu_en);
		final MenuItem langFrMenuItem = menu.findItem(R.id.menu_fr);
		if (getCurrentLang().equals(UrlConstant.LANG_FR)) {
			// English switcher
			langFrMenuItem.setVisible(false);
			langEnMenuItem.setVisible(true);
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
		globalSharingIntent.setType("text/plain");
		globalSharingIntent.setAction(Intent.ACTION_SEND);
		
		final PackageManager packageManager = myWebView.getContext().getPackageManager();
	    final List<ResolveInfo> activities = packageManager.queryIntentActivities(globalSharingIntent, 0);
	    
		final List<Intent> sharingIntents = new ArrayList<Intent>();
		// Cast is possible because we share only on HTML pages
		final String shareBody = ((UrlHtmlContent)currentUrlContent).getTitle() + " - " + currentUrlContent.getUrl();
		
	    for (final ResolveInfo app : activities) {
			final Intent sharingIntent = new Intent();
			sharingIntent.setType("text/plain");
			sharingIntent.setAction(Intent.ACTION_SEND);
	    	sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.id.menu_share));
	    	final String packageName = app.activityInfo.packageName;
	    	if (CarmaBlogUtils.isPackageMatchingAppForSharing(packageName)) {
		        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
		        sharingIntent.setPackage(packageName);
		        sharingIntents.add(sharingIntent);
	    	}
	    }
	    if (sharingIntents.isEmpty()) {
	    	Toast.makeText(getApplicationContext(), R.string.message_share_app_not_found, Toast.LENGTH_LONG).show();
	    	return null;
	    }
		final Intent chooserIntent = Intent.createChooser(sharingIntents.remove(0), getString(R.string.title_share));
		chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, sharingIntents.toArray(new Parcelable[]{}));
		return chooserIntent;
	}

	public WebView getMyWebView() {
		return myWebView;
	}

	public String getCurrentLang() {
		return myApplication.getCurrentLang();
	}
	
	public UrlContent getCurrentUrlContent() {
		return currentUrlContent;
	}

	public Integer getTotalNumberOfPages() {
		return totalNumberOfPages;
	}

	public void setTotalNumberOfPages(final Integer totalNumberOfPages) {
		this.totalNumberOfPages = totalNumberOfPages;
	}
	
	public UrlContentCacheManager getUrlContentCacheManager() {
		return myApplication.getUrlContentCacheManager();
	}

	public boolean isNightMode() {
		return nightMode;
	}

}
