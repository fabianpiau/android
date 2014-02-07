package com.carmablog.url.history;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.carmablog.url.history.model.UrlContent;
import com.carmablog.util.CarmaBlogUtils;

/**
 * Cache manager for URL content.
 * @author fpiau
 *
 */
public class UrlContentCacheManager {
	
	// List of cached URL content
	private List<UrlContent> urlContentCaches = new ArrayList<UrlContent>();
	
	private static final int MAX_CACHED_ITEM = 12;

	/*
	 * Add a new URL content in the cache.
     * We cannot store more than MAX_CACHED_ITEM cached items simultaneously.
	 */
	public void addInCache(final UrlContent urlContent) {
		if (getFromCache(urlContent.getUrl()) == null) {
			// Never added yet
			urlContentCaches.add(0, urlContent);
			Log.d("addInCache", "The URL content with the URL: '" + urlContent.getUrl() + "' has been added in the cache.");
		}
		// Check the size if we need to remove
		final int cacheSize = urlContentCaches.size();
		if (cacheSize > MAX_CACHED_ITEM) {
			removeFromCache(cacheSize);
		}
	}

	/*
	 * Remove the oldest URL HTML content in cache.
	 */
	private void removeFromCache(final int cacheSize) {
		Log.d("removeFromCache", "Cache is full.");
		int lastPosition = cacheSize - 1; // Because index starts at 0
		// RSS URL content always stay in cache
		while (lastPosition > 1 && CarmaBlogUtils.isUrlRssContent(urlContentCaches.get(lastPosition))) {
			// Try the previous element
			lastPosition--;
		}
		final UrlContent urlContentToRemove = urlContentCaches.get(lastPosition);
		if (!CarmaBlogUtils.isUrlRssContent(urlContentToRemove)) {
			urlContentCaches.remove(urlContentToRemove);
			Log.d("removeFromCache", "The URL content with the URL: '" + urlContentToRemove.getUrl() + "' has been removed from the cache.");
		}
	}
	
	/*
	 * Return the URL content from the cache.
	 * If not existing, return null.
	 */
	public UrlContent getFromCache(final String url) {
		for (final UrlContent urlContentCache : urlContentCaches) {
			if (urlContentCache.getUrl().equals(url)) {
				return urlContentCache;
			}
		}
		return null;
	}
	
}
