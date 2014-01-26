package com.carmablog.util;

import java.util.Locale;

import com.carmablog.url.common.URLConstant;

/**
 * Do some operations on languages.
 * @author fpiau
 *
 */
public final class LanguageUtils {

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
		if (url.contains(URLConstant.LANG_PATTERN)) {
			// Replace the pattern
			return url.replace(URLConstant.LANG_PATTERN, lang); 
		}
		// Easy way 2
		if (url.contains(URLConstant.CARMABLOG_PATTERN + URLConstant.SEPARATOR_URL + lang)) {
			// The language is already good
			return url; 
		}
		// Medium way
		if ((!url.contains(URLConstant.CARMABLOG_PATTERN + URLConstant.SEPARATOR_URL + URLConstant.LANG_FR)) &&
				!url.contains(URLConstant.CARMABLOG_PATTERN + URLConstant.SEPARATOR_URL + URLConstant.LANG_EN)) {
			// The language is not set
			// Simply add it
			return formatUrl(url.replace(URLConstant.CARMABLOG_PATTERN, URLConstant.CARMABLOG_PATTERN + URLConstant.SEPARATOR_URL + lang)); 
		}
		// Hard way
		// Language is set and we need to replace it
		final String[] urlParts = url.split(URLConstant.CARMABLOG_PATTERN);
		if (urlParts.length > 2) {
			// Should be impossible
			return null;
		} else if (urlParts.length == 2) {
			// Rebuild second part
			String secondPart = urlParts[1];
			final String langFrPart = URLConstant.LANG_FR + URLConstant.SEPARATOR_URL;
			final String langEnPart = URLConstant.LANG_EN + URLConstant.SEPARATOR_URL;
			if (! (secondPart.contains(langFrPart) || secondPart.contains(langEnPart))) {
				return formatUrl(urlParts[0] + URLConstant.CARMABLOG_PATTERN + secondPart + lang);
			} else {
				secondPart = secondPart.replaceFirst(langFrPart, lang + URLConstant.SEPARATOR_URL);
				secondPart = secondPart.replaceFirst(langEnPart, lang + URLConstant.SEPARATOR_URL);
				return formatUrl(urlParts[0] + URLConstant.CARMABLOG_PATTERN + secondPart);
			}
		} else if (urlParts.length == 1) {
			return urlParts[0] + URLConstant.CARMABLOG_PATTERN + URLConstant.SEPARATOR_URL + lang;
		} else if (urlParts.length == 0) {
			return formatUrl(URLConstant.CARMABLOG_PATTERN + URLConstant.SEPARATOR_URL + lang);
		}
		return null;
	}

	private static String formatUrl(String url) {
		String formatedUrl = url;
		if (!url.endsWith(URLConstant.SEPARATOR_URL)) {
			formatedUrl = url + URLConstant.SEPARATOR_URL;
		}
		return formatedUrl;
	}
	
	
}
