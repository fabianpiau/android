package com.carmablog.retriever;

import com.carmablog.activity.MainActivity;
import com.carmablog.url.history.URLContent;

import android.os.AsyncTask;

/**
 * Display the provided content (coming from the history) in the WebView.
 * No request to the Internet is needed.
 * @author fpiau
 *
 */
public class RetrievePageLocalTask extends AsyncTask<URLContent, Void, URLContent> {

	// Parent main activity
    private MainActivity activity;
    
    /*
     * Constructor.
     */
	public RetrievePageLocalTask(final MainActivity activity) {
		this.activity = activity;
	}

	@Override
    public URLContent doInBackground(final URLContent... contents) {
		final URLContent urlContent = contents[0];
		// Add the URLContent in the navigation history
		activity.getUrlContentHistoryHelper().addUrlContentInHistory(urlContent);
		// Update the WebView content on the UI thread
		activity.runOnUiThread(new Runnable() {
			public void run() {
				activity.getMyWebView().loadDataWithBaseURL("file:///android_asset/", urlContent.getHtmlContent(), "text/html", "UTF-8", null);
			}
		});
		return urlContent;
    }
	
	@Override
	protected void onPostExecute(final URLContent result) {
		super.onPostExecute(result);
		// Set the result
		activity.setCurrentUrlContent(result);
	}
    
}
