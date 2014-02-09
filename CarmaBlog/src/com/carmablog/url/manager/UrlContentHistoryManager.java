package com.carmablog.url.manager;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.carmablog.activity.HtmlActivity;
import com.carmablog.url.common.UrlCallMethod;
import com.carmablog.url.common.UrlConstant;
import com.carmablog.url.model.UrlContent;
import com.carmablog.url.model.UrlHtmlContent;
import com.carmablog.util.CarmaBlogUtils;

/**
 * History manager for URL content (HTML only).
 * @author fpiau
 *
 */
public class UrlContentHistoryManager {

	// Parent main activity
    private HtmlActivity activity;
    
	// List of visited URL without their content (the cache is here for that)
	private List<String> urlHistorys = new ArrayList<String>();
    
    /*
     * Constructor.
     */
	public UrlContentHistoryManager(final HtmlActivity activity) {
		this.activity = activity;
	}

	/*
	 * Add a URL in the navigation history.
	 */
	public void addUrlInHistory(final UrlHtmlContent urlContent) {
		final String url = urlContent.getUrl();
		// Add only if it is a URL from CarmaBlog
		if (CarmaBlogUtils.isUrlMatchingForApp(url) &&
				// and if it has not been added yet (just before)
				(urlHistorys.isEmpty() || ((!urlHistorys.isEmpty() && !urlHistorys.get(0).equals(url))))) {
			urlHistorys.add(0, url);
		}
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
	 * Take care of the URL loading depending on the cache availability.
	 */
	private void loadUrl(final String url) {
		// Get the content from the cache
		final UrlContent urlContent = activity.getUrlContentCacheManager().getFromCache(url);
		if (urlContent == null) {
			Log.d("UrlContentHistoryManager", "URL '" + url + "' is in history but its content has not been found in the cache. Need to load the page from internet again.");
			activity.loadCarmablogHtmlUrl(url);
		} else {
			// Load from the cache
			activity.loadCarmablogHtmlUrlFromCache((UrlHtmlContent)urlContent);
		}
	}
		
}
