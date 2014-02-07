package com.carmablog.url.history.model;

/**
 * Represent a URL content that is coming from a HTML page.
 * @author fpiau
 *
 */
public class UrlHtmlContent extends UrlContent {

	// Title of the post
	private String title;
	
	// Whole HTML code
	private String htmlContent;

	// Post navigation
	private String previousPostUrl;
	private String nextPostUrl;
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(final String title) {
		this.title = title;
	}
	
	public String getHtmlContent() {
		return htmlContent;
	}
	
	public void setHtmlContent(final String htmlContent) {
		this.htmlContent = htmlContent;
	}

	public String getPreviousPostUrl() {
		return previousPostUrl;
	}

	public void setPreviousPostUrl(String previousPostUrl) {
		this.previousPostUrl = previousPostUrl;
	}

	public String getNextPostUrl() {
		return nextPostUrl;
	}

	public void setNextPostUrl(String nextPostUrl) {
		this.nextPostUrl = nextPostUrl;
	}
	
}
