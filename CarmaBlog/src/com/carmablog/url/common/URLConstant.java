package com.carmablog.url.common;

/**
 * Constants about URL processing.
 * @author fpiau
 *
 */
public class URLConstant {
	
	// Patterns to recognize URLs from CarmaBlog
	// Match
	public static final String CARMABLOG_PATTERN = "fabianpiau.com";
	// No match
	public static final CharSequence COMMON_SHARING_PATTERN = "?share=";
	public static final CharSequence LINKEDIN_SHARING_PATTERN = "linkedin.com";
	public static final CharSequence FACEBOOK_SHARING_PATTERN = "facebook.com";
	public static final CharSequence TWITTER_SHARING_PATTERN = "twitter.com";
	public static final CharSequence GOOGLE_SHARING_PATTERN = "plus.google.com";
	
	// Patterns to process URLs
	public static final CharSequence LANG_PATTERN = "[LANG]";
	public static final String LANG_FR = "fr";
	public static final String LANG_EN = "en";
	public static final String SEPARATOR_URL = "/";
	public static final CharSequence BASE_URL = "http:" + SEPARATOR_URL + SEPARATOR_URL;
	public static final CharSequence BASE_CARMABLOG_URL = BASE_URL + "blog." + CARMABLOG_PATTERN;
	public static final String HOME_CARMABLOG_URL = BASE_CARMABLOG_URL + SEPARATOR_URL + LANG_PATTERN + SEPARATOR_URL;
	
	// Search URLs
	public static final CharSequence QUERY_SEARCH_TERM = "?s=";
	public static final CharSequence QUERY_SEARCH_ACTION = "&submit=Rechercher";

}
