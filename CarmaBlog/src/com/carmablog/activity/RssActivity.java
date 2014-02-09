package com.carmablog.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.carmablog.retriever.RetrieveRssRemoteTask;
import com.carmablog.url.common.UrlConstant;
import com.carmablog.url.manager.UrlContentCacheManager;
import com.carmablog.url.model.UrlContent;
import com.carmablog.url.model.UrlRssContent;
import com.carmablog.url.model.UrlRssElement;
import com.carmablog.view.RssArrayAdapter;

/**
 * CarmaBlog RSS Activity.
 * @author fpiau
 *
 */
public class RssActivity extends Activity {

	// Application
	private MyApplication myApplication;
	
	// ListView component - RSS Posts page
	private ListView myListView;
	
	// Exchange with HTML activity
	public final static String URL = "com.carmablog.URL";

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Get a link to the application
		myApplication = (MyApplication) getApplicationContext();
		
		// Initialize display component
		initializeMyListView();
		
		// Load the RSS page
		loadCarmablogRssUrl(UrlConstant.RSS_CARMABLOG_URL);
	}

	private void initializeMyListView() {
		myListView = new ListView(this);
		setContentView(myListView);
		// Prepare list of posts
		final RssArrayAdapter rssArrayAdapter = new RssArrayAdapter(this, new ArrayList<UrlRssElement>());
		myListView.setAdapter(rssArrayAdapter);
		// Each item is a link to a post
		myListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Display the post
				// Start HTML activity with the selected URL
				final UrlRssElement urlRssElement = (UrlRssElement) myListView.getItemAtPosition(position);
				Intent i = new Intent();
				i.setType("text/plain");
				i.putExtra(URL, urlRssElement.getLink());
				setResult(RESULT_OK, i);
				// Finish the RSS activity
				finish();
			}
		});
	}
	
	/*
	 * Update the ListView with the list of RSS elements.
	 */
	public void updateListView(final List<UrlRssElement> urlRssElements) {
		final RssArrayAdapter rssArrayAdapter = ((RssArrayAdapter) myListView.getAdapter());
		rssArrayAdapter.clear();	
		for (UrlRssElement urlRssElement : urlRssElements) {
			rssArrayAdapter.add(urlRssElement);
		}
	}
	
	/*
	 * Load the RSS URL.
	 */
	private void loadCarmablogRssUrl(final String url) {
		final String localizedUrl = myApplication.transformCarmaBlogUrl(url);
		if (localizedUrl != null) {
			// Look in the cache in case the RSS has been loaded before...
			final UrlContent urlContent = getUrlContentCacheManager().getFromCache(localizedUrl); 
			if (urlContent != null) {
				// Yeah, it will be faster
				loadCarmablogRssUrlFromCache((UrlRssContent)urlContent);
			} else {
				// First time
				loadCarmablogRssUrlInBackground(localizedUrl);
			}
		}
	}

	/*
	 * Load the RSS URL with Jsoup.
	 * The call is asynchronous.
	 */
	private void loadCarmablogRssUrlInBackground(final String url) {
		// Do an async call
		new RetrieveRssRemoteTask(this).execute(url);
	}

	/*
	 * Load the RSS URL.
	 * The content is coming from the cache.
	 */
	private void loadCarmablogRssUrlFromCache(final UrlRssContent urlContent) {
		// Reload list directly
		updateListView(((UrlRssContent)urlContent).getUrlRssElements());
	}

	public String getCurrentLang() {
		return myApplication.getCurrentLang();
	}
	
	public UrlContentCacheManager getUrlContentCacheManager() {
		return myApplication.getUrlContentCacheManager();
	}
	
}
