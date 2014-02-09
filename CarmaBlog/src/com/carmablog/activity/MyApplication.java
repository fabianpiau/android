package com.carmablog.activity;

import android.app.Application;
import android.util.Log;

import com.carmablog.url.common.UrlConstant;
import com.carmablog.url.manager.UrlContentCacheManager;
import com.carmablog.util.CarmaBlogUtils;

/**
 * CarmaBlog application.
 * @author fpiau
 *
 */
public class MyApplication extends Application {

	// Current language
	private String currentLang;
	
	// URL cache manager
	private UrlContentCacheManager urlContentCacheManager;
	
	@Override
	public void onCreate() {
		super.onCreate();
		urlContentCacheManager = new UrlContentCacheManager();
		initializeLanguageFromDevice();
	}
	
	/*
	 * Set the current language
	 * By default, depending on the device settings
	 */
	protected void initializeLanguageFromDevice() {
		if (CarmaBlogUtils.isDeviceInFrench()) {
			currentLang = UrlConstant.LANG_FR;
		} else {
			currentLang = UrlConstant.LANG_EN;
		}
	}
	
	/*
	 * Do some basic checks then set the language in the URL.
	 */
	protected String transformCarmaBlogUrl(final String url) {
		if (!CarmaBlogUtils.isUrlMatchingForApp(url)) {
			Log.e("MyApplication.transformCarmaBlogUrl", "Not a URL from CarmaBlog, you should open it in an external browser.");
			return null;
		}
		return CarmaBlogUtils.localizeUrl(url, currentLang);
	}
	
	public UrlContentCacheManager getUrlContentCacheManager() {
		return urlContentCacheManager;
	}

	public void setUrlContentCacheManager(final UrlContentCacheManager urlContentCacheManager) {
		this.urlContentCacheManager = urlContentCacheManager;
	}

	public String getCurrentLang() {
		return currentLang;
	}

	public void setCurrentLang(final String currentLang) {
		this.currentLang = currentLang;
	}
	
}
