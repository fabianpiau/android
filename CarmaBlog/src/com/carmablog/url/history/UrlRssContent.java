package com.carmablog.url.history;

import java.util.List;


/**
 * Represent a URL content that is coming from an XML RSS feed.
 * @author fpiau
 *
 */
public class UrlRssContent extends UrlContent {

	// List of RSS elements
	private List<UrlRssElement> urlRssElements;

	public List<UrlRssElement> getUrlRssElements() {
		return urlRssElements;
	}

	public void setUrlRssElements(List<UrlRssElement> urlRssElements) {
		this.urlRssElements = urlRssElements;
	}

}
