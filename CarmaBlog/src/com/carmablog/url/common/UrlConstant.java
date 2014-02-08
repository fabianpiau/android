package com.carmablog.url.common;

/**
 * Constants about URL processing.
 * @author fpiau
 *
 */
public class UrlConstant {
	
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
	public static final String PAGE = "page" + SEPARATOR_URL;
	public static final CharSequence PAGE_NUMBER_PATTERN = "[PAGE]";
	public static final CharSequence BASE_URL = "http:" + SEPARATOR_URL + SEPARATOR_URL;
	public static final CharSequence BASE_CARMABLOG_URL = BASE_URL + "blog." + CARMABLOG_PATTERN;
	// Full URL
	public static final String HOME_CARMABLOG_URL = BASE_CARMABLOG_URL + SEPARATOR_URL + LANG_PATTERN + SEPARATOR_URL; // Page 1 - implicit
	public static final String PAGE_CARMABLOG_URL = HOME_CARMABLOG_URL + PAGE + PAGE_NUMBER_PATTERN + SEPARATOR_URL;
	public static final String HOME_PAGE_CARMABLOG_URL = HOME_CARMABLOG_URL + PAGE + 1 + SEPARATOR_URL; // Page 1 - explicit
	public static final String CATEGORY_MANAGEMENT_URL = HOME_CARMABLOG_URL + "management" + SEPARATOR_URL;
	public static final String CATEGORY_AGILE_PROGRAMMING_URL = HOME_CARMABLOG_URL + "agile-programming" + SEPARATOR_URL;
	public static final String CATEGORY_TECHNOLOGY_URL = HOME_CARMABLOG_URL + "technology" + SEPARATOR_URL;
	public static final String CATEGORY_LINUX_URL = HOME_CARMABLOG_URL + "linux" + SEPARATOR_URL;
	public static final String CATEGORY_EVENT_URL = HOME_CARMABLOG_URL + "event" + SEPARATOR_URL;
	
	// Search URLs
	public static final CharSequence QUERY_SEARCH_TERM = "?s=";
	public static final CharSequence QUERY_SEARCH_ACTION = "&submit=Rechercher";
	
	// RSS URL
	private static final CharSequence RSS_PATTERN = "feed";
	public static final String RSS_CARMABLOG_URL = BASE_CARMABLOG_URL + SEPARATOR_URL + LANG_PATTERN + SEPARATOR_URL + RSS_PATTERN + SEPARATOR_URL;

}
