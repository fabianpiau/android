package com.carmablog.url.history;

/**
 * Represent a URL with its content.
 * @author fpiau
 *
 */
public class URLContent {
	
	// URL of the page
	private String url;
		
	// Title of the page
	private String title;
	
	// Whole HTML code
	private String htmlContent;

	public String getUrl() {
		return url;
	}
	public void setUrl(final String url) {
		this.url = url;
	}
	
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

}
