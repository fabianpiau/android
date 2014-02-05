package com.carmablog.retriever;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.carmablog.R;
import com.carmablog.activity.MainActivity;
import com.carmablog.url.history.URLContent;

/**
 * Retrieve the whole page from the Internet then display it in the WebView.
 * Display a progress bar.
 * @author fpiau
 *
 */
public class RetrievePageRemoteTask extends AsyncTask<String, Void, URLContent> {

	// Parent main activity
    private MainActivity activity;
    
    // Progress dialog box
    private ProgressDialog progressDialog;
    
    /*
     * Constructor.
     */
    @SuppressLint("NewApi")
	public RetrievePageRemoteTask(final MainActivity activity) {
		this.activity = activity;
		if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) >= 11) {
			progressDialog = new ProgressDialog(activity, ProgressDialog.THEME_HOLO_DARK);
		} else {
			progressDialog = new ProgressDialog(activity);
		}
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// Show progress dialog
		progressDialog.setMessage(activity.getResources().getString(R.string.message_loading));
		progressDialog.show();
	}

	@Override
    public URLContent doInBackground(final String... urls) {
		final String url = urls[0];
    	Document doc = null;
        try {
        	// Get the HTML code with Jsoup
        	doc = Jsoup.connect(url).get();
        } catch (Exception e) {
        	Log.e("Jsoup", "Cannot retrieve document. Check the URL: " + url + " is correct. Exception: " + e.getMessage());
        }
		if (doc != null) {
			// Add CSS to remove useless things
			doc.head().appendElement("link").attr("rel", "stylesheet").attr("type", "text/css").attr("href", "android_style.css");
			// Get all info of the page
			final URLContent urlContent = new URLContent();
			urlContent.setUrl(url);
			urlContent.setHtmlContent(doc.outerHtml());
			urlContent.setTitle(doc.body().select("h2").first().text());
			// Add the URL and its content in the navigation history
			activity.getUrlContentHistoryHelper().addUrlContentInHistory(urlContent);
			// Update the WebView content on the UI thread
			activity.runOnUiThread(new Runnable() {
				public void run() {
					activity.getMyWebView().loadDataWithBaseURL("file:///android_asset/", urlContent.getHtmlContent(), "text/html", "UTF-8", null);
				}
			});
			return urlContent;
		} else {
			// An error occurred with Jsoup
			// Probably a connection error
			activity.runOnUiThread(new Runnable() {
				public void run() {
					activity.getMyWebView().loadUrl("file:///android_asset/connection_timeout.html");
					Toast.makeText(activity.getApplicationContext(), R.string.message_timeout, Toast.LENGTH_LONG).show();
				}
			});
				
		}
		return null;
    }

	@Override
	protected void onPostExecute(final URLContent result) {
		super.onPostExecute(result);
		if (progressDialog.isShowing()) {
			// Hide progress dialog
			progressDialog.dismiss();
	    }
		// Set the result
		activity.setCurrentUrlContent(result);
	}
    
}
