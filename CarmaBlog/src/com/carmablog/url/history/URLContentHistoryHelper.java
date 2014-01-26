package com.carmablog.url.history;

import java.util.ArrayList;
import java.util.List;

import com.carmablog.activity.MainActivity;
import com.carmablog.url.common.URLCallMethod;
import com.carmablog.url.common.URLConstant;

/**
 * Helper to manage URL content history.
 * @author fpiau
 *
 */
public class URLContentHistoryHelper {

	// Parent main activity
    private MainActivity activity;
    
	// List of visited URL with their content
	private List<URLContent> urlContentHistorys = new ArrayList<URLContent>();
    
    /*
     * Constructor.
     */
	public URLContentHistoryHelper(final MainActivity activity) {
		this.activity = activity;
	}

	/*
	 * Add a URL and its content in the navigation history.
	 */
	public void addUrlContentInHistory(final String url, final String content) {
		final URLContent urlContent = new URLContent();
		urlContent.setUrl(url);
		urlContent.setContent(content);
		addUrlContentInHistory(urlContent);		
	}
	
	/*
	 * Add a URLContent in the navigation history.
	 */
	public void addUrlContentInHistory(final URLContent urlContent) {
		// Add only if it is a URL from CarmaBlog
		final String url = urlContent.getUrl();
		if (isUrlMatchingForApp(url) &&
				// and if it has not been added yet (just before)
				(urlContentHistorys.isEmpty() || ((!urlContentHistorys.isEmpty() && !urlContentHistorys.get(0).getUrl().equals(url))))) {
			urlContentHistorys.add(0, urlContent);
		}
	}

	/*
	 * Check if the URL is 100% a CarmaBlog one.
	 */
	public boolean isUrlMatchingForApp(final String url) {
		return url.contains(URLConstant.CARMABLOG_PATTERN) 
				&& !(url.contains(URLConstant.COMMON_SHARING_PATTERN))
				  && !(url.contains(URLConstant.FACEBOOK_SHARING_PATTERN))
				    && !(url.contains(URLConstant.TWITTER_SHARING_PATTERN))
				      && !(url.contains(URLConstant.GOOGLE_SHARING_PATTERN))
				        && !(url.contains(URLConstant.LINKEDIN_SHARING_PATTERN));
	}

	/*
	 * Navigate through the history to know which URL to load.
	 * Remove the displayed page from the history.
	 */
	public boolean goBack(final URLCallMethod urlCallMethod) {
		if (urlContentHistorys.isEmpty()) {
			activity.loadCarmablogUrl(URLConstant.HOME_CARMABLOG_URL);
		}
		switch (urlCallMethod) {
			case ON_RESUME:
				if (urlContentHistorys.size() >= 1) {
					activity.loadCarmablogUrl(urlContentHistorys.get(0));
				}
				return true;
			case ON_KEY_DOWN:
				if (urlContentHistorys.size() == 1) {
					activity.finish();
					return true;
				} else if (urlContentHistorys.size() > 1) {
					urlContentHistorys.remove(0);
					// load up the previous URL
					activity.loadCarmablogUrl(urlContentHistorys.get(0));
					return true;
				}
		}
		return false;
	}

	/*
	 * Return the URLContent based on a URL.
	 */
	public URLContent getURLContentFromURL(final String url) {
		for (final URLContent urlContent : urlContentHistorys) {
			if (urlContent.getUrl().equals(url)) {
				return urlContent;
			}
		}
		return null;
	}
		
}
