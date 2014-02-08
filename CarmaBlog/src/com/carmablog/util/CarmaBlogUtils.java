package com.carmablog.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.carmablog.url.common.UrlConstant;
import com.carmablog.url.history.model.UrlContent;
import com.carmablog.url.history.model.UrlRssContent;

/**
 * Utilities class.
 * @author fpiau
 *
 */
public final class CarmaBlogUtils {

	/*
	 * Return true is the language is French (settings of the device).
	 */
	public static boolean isDeviceInFrench() {
		return Locale.getDefault().getDisplayLanguage().contains("fran");
	}
	
	/*
	 * Localize the URL depending on the selected language or device default settings.
	 */
	public static String localizeUrl(final String url, final CharSequence lang) {
		// Easy way
		if (url.contains(UrlConstant.LANG_PATTERN)) {
			// Replace the pattern
			return url.replace(UrlConstant.LANG_PATTERN, lang); 
		}
		// Easy way 2
		if (url.contains(UrlConstant.CARMABLOG_PATTERN + UrlConstant.SEPARATOR_URL + lang)) {
			// The language is already good
			return url; 
		}
		// Medium way
		if ((!url.contains(UrlConstant.CARMABLOG_PATTERN + UrlConstant.SEPARATOR_URL + UrlConstant.LANG_FR)) &&
				!url.contains(UrlConstant.CARMABLOG_PATTERN + UrlConstant.SEPARATOR_URL + UrlConstant.LANG_EN)) {
			// The language is not set
			// Simply add it
			return formatUrl(url.replace(UrlConstant.CARMABLOG_PATTERN, UrlConstant.CARMABLOG_PATTERN + UrlConstant.SEPARATOR_URL + lang)); 
		}
		// Hard way
		// Language is set and we need to replace it
		final String[] urlParts = url.split(UrlConstant.CARMABLOG_PATTERN);
		if (urlParts.length > 2) {
			// Should be impossible
			return null;
		} else if (urlParts.length == 2) {
			// Rebuild second part
			String secondPart = urlParts[1];
			final String langFrPart = UrlConstant.LANG_FR + UrlConstant.SEPARATOR_URL;
			final String langEnPart = UrlConstant.LANG_EN + UrlConstant.SEPARATOR_URL;
			if (! (secondPart.contains(langFrPart) || secondPart.contains(langEnPart))) {
				return formatUrl(urlParts[0] + UrlConstant.CARMABLOG_PATTERN + secondPart + lang);
			} else {
				secondPart = secondPart.replaceFirst(langFrPart, lang + UrlConstant.SEPARATOR_URL);
				secondPart = secondPart.replaceFirst(langEnPart, lang + UrlConstant.SEPARATOR_URL);
				return formatUrl(urlParts[0] + UrlConstant.CARMABLOG_PATTERN + secondPart);
			}
		} else if (urlParts.length == 1) {
			return urlParts[0] + UrlConstant.CARMABLOG_PATTERN + UrlConstant.SEPARATOR_URL + lang;
		} else if (urlParts.length == 0) {
			return formatUrl(UrlConstant.CARMABLOG_PATTERN + UrlConstant.SEPARATOR_URL + lang);
		}
		return null;
	}

	/*
	 * Just add an ending separator.
	 */
	private static String formatUrl(String url) {
		String formatedUrl = url;
		if (!url.endsWith(UrlConstant.SEPARATOR_URL)) {
			formatedUrl = url + UrlConstant.SEPARATOR_URL;
		}
		return formatedUrl;
	}
	
	/*
	 * Return true is the URL is a link to an article.
	 */
	public static boolean isUrlMatchingSinglePost(final String url) {
		// check if the URL contains a date in the URL with the /YYYY/MM/DD/ format
		return url.matches(".*" + UrlConstant.CARMABLOG_PATTERN + "(/" + UrlConstant.LANG_FR + "|/" + UrlConstant.LANG_EN + ")?/(20[0-9]{2})/([0-1][0-9])/([0-3][0-9])/.*");
	}
	
	/*
	 * Return true is the URL is a page with several posts.
	 */
	public static boolean isUrlMatchingPageMultiplePost(final String url) {
		// check if the URL contains the page pattern
		return url.matches(".*" + UrlConstant.CARMABLOG_PATTERN + "(/" + UrlConstant.LANG_FR + "|/" + UrlConstant.LANG_EN + ")?/" + UrlConstant.PAGE +"[0-9]*/");
	}
	
	/*
	 * Return the page with the good language and page number.
	 */
	public static String buildUrlPageMultiplePost(final Integer pageNumber, final String currentLang) {
		final String localizedUrl = localizeUrl(UrlConstant.PAGE_CARMABLOG_URL, currentLang);
		// Replace the pattern
		return localizedUrl.replace(UrlConstant.PAGE_NUMBER_PATTERN, pageNumber.toString()); 
	}

	/*
	 * Check if the app is one we want to use for sharing a post.
	 * Should be a compatible client or the official app among:
	 * Twitter, LinkedIn, Facebook, Google Plus, Viadeo & Email.
	 */
	public static boolean isPackageMatchingAppForSharing(final String packageNameApp) {
		return (packageNameApp.contains("twitter") 
				|| packageNameApp.contains("linkedin") 
				 || packageNameApp.contains("facebook") 
				  || packageNameApp.contains("google.android.apps.plus")
				   || packageNameApp.contains("twidroid")
				    || packageNameApp.contains("tweetcaster")
					 || packageNameApp.contains("thedeck")
					  || packageNameApp.contains("email")
					   || packageNameApp.contains("viadeo"));
	}
	
	/*
	 * Check if the URL is 100% from CarmaBlog.
	 */
	public static boolean isUrlMatchingForApp(final String url) {
		return url.contains(UrlConstant.CARMABLOG_PATTERN) 
				&& !(url.contains(UrlConstant.COMMON_SHARING_PATTERN))
				  && !(url.contains(UrlConstant.FACEBOOK_SHARING_PATTERN))
				    && !(url.contains(UrlConstant.TWITTER_SHARING_PATTERN))
				      && !(url.contains(UrlConstant.GOOGLE_SHARING_PATTERN))
				        && !(url.contains(UrlConstant.LINKEDIN_SHARING_PATTERN));
	}
	
	/*
	 * Return true if the URL content represents a RSS feed.
	 */
	public static boolean isUrlRssContent(final UrlContent urlContent) {
		return urlContent instanceof UrlRssContent;
	}
	
	/*
	 * Format a date depending on the language.
	 */
	public static CharSequence formatDate(final Date date, final String currentLang) {
		SimpleDateFormat simpleDateFormat;
		if (currentLang.equals(UrlConstant.LANG_FR)) {
			 simpleDateFormat = new SimpleDateFormat("EEEE d MMMM yyyy - HH:mm", Locale.FRENCH);
		} else {
			 simpleDateFormat = new SimpleDateFormat("EEEE MMMM d, yyyy - K:mm a", Locale.ENGLISH);
		}
		return simpleDateFormat.format(date);
	}

	/*
	 * Extract the page number in the URL. 
	 */
	public static Integer extractPageNumberFromUrl(final String url) {
		if (!isUrlMatchingPageMultiplePost(url)) {
			return null;
		}
		final String[] parts = url.split(UrlConstant.PAGE);
		if (parts.length < 1) {
			return null;
		}
		final String pageNumber = parts[1].split(UrlConstant.SEPARATOR_URL)[0];
		return convertStringToInteger(pageNumber);
	}
	
	/*
	 * Convert a String to Integer.
	 */
	public static Integer convertStringToInteger(final String integerAsString) {
		if (integerAsString == null || integerAsString.length() == 0) {
			return null;
		}
		try {
			return Integer.parseInt(integerAsString);
		} catch (Exception e) {
			return null;
		}
	}

}
