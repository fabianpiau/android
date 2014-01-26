package com.carmablog.retriever;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.carmablog.R;
import com.carmablog.activity.MainActivity;

/**
 * Retrieve the whole page from the Internet then display it in the WebView.
 * Display a progress bar.
 * @author fpiau
 *
 */
public class RetrievePageRemoteTask extends AsyncTask<String, Void, String> {

	// Parent main activity
    private MainActivity activity;
    
    // Progress dialog box
    private ProgressDialog progressDialog;
    
    /*
     * Constructor.
     */
	public RetrievePageRemoteTask(final MainActivity activity) {
		this.activity = activity;
		progressDialog = new ProgressDialog(activity);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// Show progress dialog
		progressDialog.setMessage(activity.getResources().getString(R.string.message_loading));
		progressDialog.show();
	}

	@Override
    public String doInBackground(final String... urls) {
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
			final String htmlData = doc.outerHtml();
			// Add the URL and its content in the navigation history
			activity.getUrlContentHistoryHelper().addUrlContentInHistory(url, htmlData);
			// Update the WebView content on the UI thread
			activity.runOnUiThread(new Runnable() {
				public void run() {
				activity.getMyWebView().loadDataWithBaseURL("file:///android_asset/", htmlData, "text/html", "UTF-8", null);
				}
			});
			
		} else {
			// An error occurred with Jsoup
			// Probably a connection error
			activity.runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(activity.getApplicationContext(), R.string.message_timeout, Toast.LENGTH_LONG).show();
				}
			});
				
		}
		return null;
    }

	@Override
	protected void onPostExecute(final String result) {
		super.onPostExecute(result);
		if (progressDialog.isShowing()) {
			// Hide progress dialog
			progressDialog.dismiss();
	    }
	}
    
}
