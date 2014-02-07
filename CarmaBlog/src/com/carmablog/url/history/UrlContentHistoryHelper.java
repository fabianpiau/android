package com.carmablog.url.history;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.carmablog.activity.MainActivity;
import com.carmablog.url.common.UrlCallMethod;
import com.carmablog.url.common.UrlConstant;
import com.carmablog.url.history.model.UrlContent;
import com.carmablog.url.history.model.UrlHtmlContent;
import com.carmablog.url.history.model.UrlRssContent;
import com.carmablog.util.CarmaBlogUtils;

/**
 * Helper to manage URL content history.
 * @author fpiau
 *
 */
public class UrlContentHistoryHelper {

	// Parent main activity
    private MainActivity activity;
    
    // Cache manager
    private UrlContentCacheManager cacheManager;
    
	// List of visited URL without their content
	private List<String> urlHistorys = new ArrayList<String>();
    
    /*
     * Constructor.
     */
	public UrlContentHistoryHelper(final MainActivity activity, final UrlContentCacheManager cacheManager) {
		this.activity = activity;
		this.cacheManager = cacheManager;
	}

	/*
	 * Add a URL in the navigation history.
	 */
	public void addUrlInHistory(final UrlContent urlContent) {
		// Add only if it is a URL from CarmaBlog
		final String url = urlContent.getUrl();
		if (CarmaBlogUtils.isUrlMatchingForApp(url) &&
				// and if it has not been added yet (just before)
				(urlHistorys.isEmpty() || ((!urlHistorys.isEmpty() && !urlHistorys.get(0).equals(url))))) {
			urlHistorys.add(0, url);
		}
		// Add in cache
		cacheManager.addInCache(urlContent);
	}

	/*
	 * Return the URL content from the cache when a match is found.
	 */
	public UrlContent getURLContentFromURL(final String url) {
		return cacheManager.getFromCache(url);
	}
	
	/*
	 * Navigate through the history to know which URL to load.
	 * Remove the displayed page URL from the history.
	 */
	public boolean goBack(final UrlCallMethod urlCallMethod) {
		if (urlHistorys.isEmpty()) {
			activity.loadCarmablogHtmlUrl(UrlConstant.HOME_CARMABLOG_URL);
		}
		switch (urlCallMethod) {
			case ON_RESUME:
				if (urlHistorys.size() >= 1) {
					loadUrl(urlHistorys.get(0));
				}
				return true;
			case ON_KEY_DOWN:
				if (urlHistorys.size() == 1) {
					activity.finish();
					return true;
				} else if (urlHistorys.size() > 1) {
					urlHistorys.remove(0);
					// load up the previous URL
					loadUrl(urlHistorys.get(0));
					return true;
				}
		}
		return false;
	}

	/*
	 * Take care of loading the URL depending on the content.
	 */
	private void loadUrl(final String url) {
		// Get the content from the cache
		final UrlContent urlContent = cacheManager.getFromCache(url);
		if (urlContent == null) {
			Log.d("History", "URL '" + url + "' is in history but its content has not been found in the cache. Need to load the page from internet.");
			// This is a single post HTML content (RSS always stay in cache)
			activity.loadCarmablogHtmlUrl(url);
		} else {
			// Load from the cache
			if (CarmaBlogUtils.isUrlRssContent(urlContent)) {
				// This is about RSS
				activity.loadCarmablogRssUrl((UrlRssContent)urlContent);
	
			} else {
				// This is a single post HTML content
				activity.loadCarmablogHtmlUrl((UrlHtmlContent)urlContent);
			}
		}
	}
		
}
