package com.carmablog.url.manager;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.carmablog.url.model.UrlContent;
import com.carmablog.url.model.UrlHtmlContent;
import com.carmablog.url.model.UrlRssContent;
import com.carmablog.util.CarmaBlogUtils;

/**
 * Cache manager for URL contents (HTML and RSS).
 * @author fpiau
 *
 */
public class UrlContentCacheManager {
	
	// List of cached HTML URL content
	private List<UrlHtmlContent> urlContentHtmlCaches = new ArrayList<UrlHtmlContent>();
	private static final int MAX_CACHED_HTML_ITEM = 10;
	
	// List of cached RSS URL content
	private List<UrlRssContent> urlContentRssCaches = new ArrayList<UrlRssContent>();

	/*
	 * Add a new RSS URL content in the RSS cache.
	 */
	public void addRssInCache(final UrlRssContent urlContent) {
		if (getFromCache(urlContent.getUrl()) == null) {
			// Never added yet
			urlContentRssCaches.add(0, urlContent);
			Log.d("UrlContentCacheManager", "The URL content with the URL: '" + urlContent.getUrl() + "' has been added in the RSS cache.");
		}
	}
	
	/*
	 * Add a new HTML URL content in the HTML cache.
     * We cannot store more than MAX_CACHED_ITEM cached items simultaneously.
	 */
	public void addHtmlInCache(final UrlHtmlContent urlContent) {
		if (getFromCache(urlContent.getUrl()) == null) {
			// Never added yet
			urlContentHtmlCaches.add(0, urlContent);
			Log.d("UrlContentCacheManager", "The URL content with the URL: '" + urlContent.getUrl() + "' has been added in the HTML cache.");
		}
		// Check the size if we need to do some cleaning
		final int cacheSize = urlContentHtmlCaches.size();
		if (cacheSize > MAX_CACHED_HTML_ITEM) {
			removeFromHtmlCache(cacheSize);
		}
	}

	/*
	 * Remove the oldest HTML URL content in cache.
	 */
	private void removeFromHtmlCache(final int cacheSize) {
		Log.d("UrlContentCacheManager", "HTML cache is full.");
		int lastPosition = cacheSize - 1; // Because index starts at 0
		final UrlContent urlHtmlContentToRemove = urlContentHtmlCaches.get(lastPosition);
		urlContentHtmlCaches.remove(urlHtmlContentToRemove);
		Log.d("UrlContentCacheManager", "The URL content with the URL: '" + urlHtmlContentToRemove.getUrl() + "' has been removed from the HTML cache.");
	}
	
	/*
	 * Return the URL content from the matching cache.
	 * If not existing, return null.
	 */
	public UrlContent getFromCache(final String url) {
		if (CarmaBlogUtils.isUrlMatchingRssFeed(url)) {
			// Search in the RSS cache
			for (final UrlContent urlContentCache : urlContentRssCaches) {
				if (urlContentCache.getUrl().equals(url)) {
					return urlContentCache;
				}
			}
		} else {
			// Search in HTML
			for (final UrlContent urlContentCache : urlContentHtmlCaches) {
				if (urlContentCache.getUrl().equals(url)) {
					return urlContentCache;
				}
			}
		}
		return null;
	}
	
}
