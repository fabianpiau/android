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
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;

import com.carmablog.R;
import com.carmablog.retriever.RetrieveHtmlRemoteTask;
import com.carmablog.retriever.RetrieveRssRemoteTask;
import com.carmablog.url.common.UrlCallMethod;
import com.carmablog.url.common.UrlConstant;
import com.carmablog.url.history.UrlContentCacheManager;
import com.carmablog.url.history.UrlContentHistoryHelper;
import com.carmablog.url.history.model.UrlContent;
import com.carmablog.url.history.model.UrlHtmlContent;
import com.carmablog.url.history.model.UrlRssContent;
import com.carmablog.url.history.model.UrlRssElement;
import com.carmablog.util.CarmaBlogUtils;
import com.carmablog.view.CustomWebView;
import com.carmablog.view.PostArrayAdapter;

/**
 * CarmaBlog Main Activity.
 * @author fpiau
 *
 */
public class MainActivity extends Activity {

	// WebView component - Normal Web page
	private WebView myWebView;
	// ListView component - RSS Posts page
	private ListView myListView;
	
	// Menu
    private Menu menu;

    // Current state and preferences
	private String currentLang;
	private UrlContent currentUrlContent;
	private SharedPreferences preferences;
	private static final String KEY_CURRENT_LANG = "currentLang";
	private Integer totalNumberOfPages;
    
	// URL history helper
	private UrlContentHistoryHelper urlContentHistoryHelper;
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Initialize display components
		initializeMyWebView();
		initializeMyListView();
		
		// Create the URL content history helper
		urlContentHistoryHelper = new UrlContentHistoryHelper(this, new UrlContentCacheManager());
		
		// Restore language from preferences
		preferences = getPreferences(MODE_PRIVATE);
		currentLang = preferences.getString(KEY_CURRENT_LANG, null);
	    if (currentLang == null) {
	    	// Nothing found in preferences
	    	// By default, use the language of the device
	    	initializeLanguageFromDevice();
	    }
	    
		// Load the homepage
		loadCarmablogHtmlUrl(UrlConstant.HOME_PAGE_CARMABLOG_URL);
	}

	private void initializeMyWebView() {
		myWebView = new CustomWebView(this);
	}

	private void initializeMyListView() {
		myListView = new ListView(this);
		// Prepare list of posts
		final PostArrayAdapter postArrayAdapter = new PostArrayAdapter(this, new ArrayList<UrlRssElement>());
		myListView.setAdapter(postArrayAdapter);
		// Each item is a link to a post
		myListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Display the post
				UrlRssElement urlRssElement = (UrlRssElement) myListView.getItemAtPosition(position);
			    loadCarmablogHtmlUrl(urlRssElement.getLink());
			}
		});
	}
	
	/*
	 * Update the ListView with the list of RSS elements.
	 */
	public void updateListView(final List<UrlRssElement> urlRssElements) {
		final PostArrayAdapter postArrayAdapter = ((PostArrayAdapter)myListView.getAdapter());
		postArrayAdapter.clear();	
		for (UrlRssElement urlRssElement : urlRssElements) {
			postArrayAdapter.add(urlRssElement);
		}
	}
	
    @Override
    protected void onStop(){
		super.onStop();
		
		// Save last language used in preferences for the next time
		final SharedPreferences.Editor editor = preferences.edit();
		editor.putString(KEY_CURRENT_LANG, currentLang).commit();
    }

	/*
	 * Set the current language
	 * By default, depending on the device settings
	 */
	private void initializeLanguageFromDevice() {
		if (CarmaBlogUtils.isDeviceInFrench()) {
			currentLang = UrlConstant.LANG_FR;
		} else {
			currentLang = UrlConstant.LANG_EN;
		}
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
			urlContentHistoryHelper.goBack(UrlCallMethod.ON_RESUME);
		}
	}

	/*
	 * Load the HTML URL.
	 */
	public void loadCarmablogHtmlUrl(final String url) {
		final String localizedUrl = transformCarmaBlogUrl(url);
		if (localizedUrl != null) {
			// Load the page
			loadHtmlUrlInBackground(localizedUrl);
		}
	}
	
	/*
	 * Load the RSS URL.
	 */
	public void loadCarmablogRssUrl(final String url) {
		final String localizedUrl = transformCarmaBlogUrl(url);
		if (localizedUrl != null) {
			// Load the RSS page
			loadRssUrlInBackground(localizedUrl);
		}
	}
	
	/*
	 * Load RSS URL with Jsoup.
	 * The call is asynchronous.
	 */
	private void loadRssUrlInBackground(final String url) {
		setFocusOnListView();
		// Look in the history in case the page has been loaded before...
		final UrlContent urlContent = urlContentHistoryHelper.getURLContentFromURL(url); 
		if (urlContent != null) {
			// Yeah, it will be faster
			loadCarmablogRssUrl((UrlRssContent)urlContent);
		} else {
			// First time
			// Do an async call
			new RetrieveRssRemoteTask(this).execute(url);
		}
	}

	public void setFocusOnListView() {
		if (menu != null) {
			// Never show the share button on RSS feed
			MenuItem shareMenuItem = menu.findItem(R.id.menu_share);
			shareMenuItem.setVisible(false);
		}
		// The ListView takes all the screen space
		if (!(getCurrentFocus() instanceof ListView)) {
			setContentView(myListView);
		}
	}
	
	/*
	 * Load the RSS URL.
	 * The content is coming from the history.
	 */
	public void loadCarmablogRssUrl(final UrlRssContent urlContent) {
		updateHistoryAndCurrentState(urlContent);
		// Reload list directly
		updateListView(((UrlRssContent)urlContent).getUrlRssElements());
	}

	/*
	 * Do some basic checks then set the language in the URL.
	 */
	private String transformCarmaBlogUrl(final String url) {
		if (!CarmaBlogUtils.isUrlMatchingForApp(url)) {
			Log.e("MainActivity.loadCarmablogUrl", "Not a URL from CarmaBlog, you should open it in an external browser.");
			return null;
		}
		if (currentLang == null) {
			initializeLanguageFromDevice();
		}
		return CarmaBlogUtils.localizeUrl(url, currentLang);
	}

	/*
	 * Load the URL.
	 * The content is coming from the history.
	 */
	public void loadCarmablogHtmlUrl(final UrlHtmlContent urlContent) {
		updateHistoryAndCurrentState(urlContent);
		// Simply display it in the WebView
		myWebView.loadDataWithBaseURL("file:///android_asset/", urlContent.getHtmlContent(), "text/html", "UTF-8", null);
	}

	public void updateHistoryAndCurrentState(final UrlContent urlContent) {
		// Add the URL in the history and its content in cache
		getUrlContentHistoryHelper().addUrlInHistory(urlContent);
		// Set the result
		setCurrentUrlContent(urlContent);
	}

	/*
	 * Load URL with Jsoup.
	 * The call is asynchronous.
	 */
	private void loadHtmlUrlInBackground(final String url) {
		setFocusOnWebView();
		// Look in the history in case the page has been loaded before...
		final UrlContent urlContent = urlContentHistoryHelper.getURLContentFromURL(url); 
		if (urlContent != null) {
			// Yeah, it will be faster
			loadCarmablogHtmlUrl((UrlHtmlContent)urlContent);
		} else {
			// First time
			// Do an async call
			new RetrieveHtmlRemoteTask(this).execute(url);
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

	public void setFocusOnWebView() {
		// The WebView takes all the screen space
		if (!(getCurrentFocus() instanceof CustomWebView)) {
			setContentView(myWebView);
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
	    
		changeMenuItemLang(currentLang);

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
				return urlContentHistoryHelper.goBack(UrlCallMethod.ON_KEY_DOWN);
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
				currentLang = UrlConstant.LANG_EN;
				changeMenuItemLang(UrlConstant.LANG_EN);
				if (currentUrlContent != null) {
					if (CarmaBlogUtils.isUrlRssContent(currentUrlContent)) {
						loadCarmablogRssUrl(currentUrlContent.getUrl());
					} else {
						loadCarmablogHtmlUrl(currentUrlContent.getUrl());
					}
				}
				return true;
			case R.id.menu_fr:
				currentLang = UrlConstant.LANG_FR;
				changeMenuItemLang(UrlConstant.LANG_FR);
				if (currentUrlContent != null) {
					if (CarmaBlogUtils.isUrlRssContent(currentUrlContent)) {
						loadCarmablogRssUrl(currentUrlContent.getUrl());
					} else {
						loadCarmablogHtmlUrl(currentUrlContent.getUrl());
					}
				}
				return true;
			case R.id.menu_rss:
				loadCarmablogRssUrl(UrlConstant.RSS_CARMABLOG_URL);
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
			default:
				loadCarmablogHtmlUrl(UrlConstant.HOME_PAGE_CARMABLOG_URL);
				return true;
		}
	}

	/*
	 * Change the item to change language.
	 */
	private void changeMenuItemLang(CharSequence lang) {
		final MenuItem langEnMenuItem = menu.findItem(R.id.menu_en);
		final MenuItem langFrMenuItem = menu.findItem(R.id.menu_fr);
		if (currentLang.equals(UrlConstant.LANG_FR)) {
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
		// Cast is possible because we share only on HTML pages
		final String shareBody = ((UrlHtmlContent)currentUrlContent).getTitle() + " - " + currentUrlContent.getUrl();
		
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
	    if (sharingIntents.isEmpty()) {
	    	return null;
	    }
		final Intent chooserIntent = Intent.createChooser(sharingIntents.remove(0), getString(R.string.share_title));
		chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, sharingIntents.toArray(new Parcelable[]{}));
		return chooserIntent;
	}

	public WebView getMyWebView() {
		return myWebView;
	}

	public ListView getMyListView() {
		return myListView;
	}

	public String getCurrentLang() {
		return currentLang;
	}

	public UrlContentHistoryHelper getUrlContentHistoryHelper() {
		return urlContentHistoryHelper;
	}
	
	public UrlContent getCurrentUrlContent() {
		return currentUrlContent;
	}

	public void setCurrentUrlContent(final UrlContent currentUrlContent) {
		this.currentUrlContent = currentUrlContent;
	}

	public Integer getTotalNumberOfPages() {
		return totalNumberOfPages;
	}

	public void setTotalNumberOfPages(Integer totalNumberOfPages) {
		this.totalNumberOfPages = totalNumberOfPages;
	}
	
}
