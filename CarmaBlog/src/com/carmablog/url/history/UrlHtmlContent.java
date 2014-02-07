package com.carmablog.url.history;

/**
 * Represent a URL content that is coming from a HTML page.
 * @author fpiau
 *
 */
public class UrlHtmlContent extends UrlContent {

	// Title of the page
	private String title;
	
	// Whole HTML code
	private String htmlContent;

	
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
