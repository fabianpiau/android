package com.carmablog.url.history;

import java.util.ArrayList;
import java.util.List;

import com.carmablog.activity.MainActivity;
import com.carmablog.url.common.UrlCallMethod;
import com.carmablog.url.common.UrlConstant;
import com.carmablog.util.CarmaBlogUtils;

/**
 * Helper to manage URL content history.
 * @author fpiau
 *
 */
public class UrlContentHistoryHelper {

	// Parent main activity
    private MainActivity activity;
    
	// List of visited URL with their content
	private List<UrlContent> urlContentHistorys = new ArrayList<UrlContent>();
    
    /*
     * Constructor.
     */
	public UrlContentHistoryHelper(final MainActivity activity) {
		this.activity = activity;
	}

	/*
	 * Add a UrlContent in the navigation history.
	 */
	public void addUrlContentInHistory(final UrlContent urlContent) {
		// Add only if it is a URL from CarmaBlog
		final String url = urlContent.getUrl();
		if (isUrlMatchingForApp(url) &&
				// and if it has not been added yet (just before)
				(urlContentHistorys.isEmpty() || ((!urlContentHistorys.isEmpty() && !urlContentHistorys.get(0).getUrl().equals(url))))) {
			urlContentHistorys.add(0, urlContent);
		}
	}

	/*
	 * Check if the URL is 100% from CarmaBlog.
	 */
	public boolean isUrlMatchingForApp(final String url) {
		return url.contains(UrlConstant.CARMABLOG_PATTERN) 
				&& !(url.contains(UrlConstant.COMMON_SHARING_PATTERN))
				  && !(url.contains(UrlConstant.FACEBOOK_SHARING_PATTERN))
				    && !(url.contains(UrlConstant.TWITTER_SHARING_PATTERN))
				      && !(url.contains(UrlConstant.GOOGLE_SHARING_PATTERN))
				        && !(url.contains(UrlConstant.LINKEDIN_SHARING_PATTERN));
	}

	/*
	 * Navigate through the history to know which URL to load.
	 * Remove the displayed page from the history.
	 */
	public boolean goBack(final UrlCallMethod urlCallMethod) {
		if (urlContentHistorys.isEmpty()) {
			activity.loadCarmablogHtmlUrl(UrlConstant.HOME_CARMABLOG_URL);
		}
		switch (urlCallMethod) {
			case ON_RESUME:
				if (urlContentHistorys.size() >= 1) {
					loadUrl(urlContentHistorys.get(0));
				}
				return true;
			case ON_KEY_DOWN:
				if (urlContentHistorys.size() == 1) {
					activity.finish();
					return true;
				} else if (urlContentHistorys.size() > 1) {
					urlContentHistorys.remove(0);
					// load up the previous URL
					loadUrl(urlContentHistorys.get(0));
					return true;
				}
		}
		return false;
	}

	/*
	 * Take care of loading the URL depending on the content.
	 */
	private void loadUrl(UrlContent urlContent) {
		if (CarmaBlogUtils.isUrlRssContent(urlContent)) {
			// This is about RSS
			activity.loadCarmablogRssUrl((UrlRssContent)urlContent);
		} else {
			// This is a single post HTML content
			activity.loadCarmablogHtmlUrl((UrlHtmlContent)urlContent);
		}
	}

	/*
	 * Return the UrlContent based on a URL.
	 */
	public UrlContent getURLContentFromURL(final String url) {
		for (final UrlContent urlContent : urlContentHistorys) {
			if (urlContent.getUrl().equals(url)) {
				return urlContent;
			}
		}
		return null;
	}
		
}
