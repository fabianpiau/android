package com.carmablog.retriever;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.carmablog.R;
import com.carmablog.activity.MainActivity;
import com.carmablog.url.history.model.UrlHtmlContent;

/**
 * Retrieve the whole HTML page from the Internet then display it in the WebView.
 * Display a progress bar.
 * @author fpiau
 *
 */
public class RetrieveHtmlRemoteTask extends AsyncTask<String, Void, UrlHtmlContent> {

	// Parent main activity
    private MainActivity activity;
    
    // Progress dialog box
    private ProgressDialog progressDialog;
    
    // Exception
    private Exception exception;
    
    /*
     * Constructor.
     */
    @SuppressLint("NewApi")
	public RetrieveHtmlRemoteTask(final MainActivity activity) {
		this.activity = activity;
		if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) >= Build.VERSION_CODES.HONEYCOMB) {
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
    public UrlHtmlContent doInBackground(final String... urls) {
		final String url = urls[0];
    	Document doc = null;
        try {
        	// Get the HTML code with Jsoup
        	doc = Jsoup.connect(url).get();
        } catch (Exception e) {
        	Log.e("Jsoup", "Cannot retrieve document. Check the URL: " + url + " is correct. Exception: " + e.getMessage());
        	exception = e;
        }
		if (doc != null) {
			// Add CSS to remove useless things
			doc.head().appendElement("link").attr("rel", "stylesheet").attr("type", "text/css").attr("href", "android_style.css");
			// Get all info of the page
			final UrlHtmlContent urlContent = new UrlHtmlContent();
			urlContent.setUrl(url);
			urlContent.setHtmlContent(doc.outerHtml());
			urlContent.setTitle(doc.body().select("h2").first().text());
			return urlContent;
		}
		return null;
    }

	@Override
	protected void onPostExecute(final UrlHtmlContent result) {
		super.onPostExecute(result);
		if (progressDialog.isShowing()) {
			// Hide progress dialog
			progressDialog.dismiss();
	    }
		if (exception == null) {
			activity.updateHistoryAndCurrentState(result);
			// Update the WebView content
			activity.getMyWebView().loadDataWithBaseURL("file:///android_asset/", result.getHtmlContent(), "text/html", "UTF-8", null);
		} else {
			// An error occurred with Jsoup
			// Probably a connection error
			activity.getMyWebView().loadUrl("file:///android_asset/connection_timeout.html");
			Toast.makeText(activity.getApplicationContext(), R.string.message_timeout, Toast.LENGTH_LONG).show();
		}
	}
    
}
