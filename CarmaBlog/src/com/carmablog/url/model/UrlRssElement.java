package com.carmablog.url.model;

import java.util.Date;

/**
 * A RSS element (actually a single post).
 * @author fpiau
 * 
 */
public class UrlRssElement {
	
	private String title;
	
	private String link;
	
	private Date date;
	
	private String category;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
