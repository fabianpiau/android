package com.carmablog.retriever;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.carmablog.R;
import com.carmablog.activity.HtmlActivity;
import com.carmablog.url.model.UrlHtmlContent;
import com.carmablog.util.CarmaBlogUtils;

/**
 * Retrieve the whole HTML page from the Internet then display it in the WebView.
 * Display a progress bar.
 * @author fpiau
 *
 */
public class RetrieveHtmlRemoteTask extends AsyncTask<String, Void, UrlHtmlContent> {

	// HTML activity
    private HtmlActivity activity;
    
    // Progress dialog box
    private ProgressDialog progressDialog;
    
    // Exception
    private Exception exception;
    
    /*
     * Constructor.
     */
	public RetrieveHtmlRemoteTask(final HtmlActivity activity) {
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
			// Manage the reading mode with additional CSS
			if (activity.isNightMode()) {
				doc.head().appendElement("link").attr("rel", "stylesheet").attr("type", "text/css").attr("href", "night_style.css");
			} else {
				doc.head().appendElement("link").attr("rel", "stylesheet").attr("type", "text/css").attr("href", "normal_style.css");
			}
			// Update sources to add missing http
			Elements images = doc.select("img[src^=//]");
			for (Element image : images) {
				String currentSrc = image.attr("src");
				image.attr("src", "http:" + currentSrc);
			}
			// Get all info of the page
			final UrlHtmlContent urlContent = new UrlHtmlContent();
			urlContent.setUrl(url);
			urlContent.setHtmlContent(doc.outerHtml());
			urlContent.setTitle(doc.body().select("h2").first().text());
			urlContent.setNextPostUrl(doc.body().select("[rel=next]").attr("href"));
			urlContent.setPageNumber(doc.body().select(".wp-pagenavi span.current").text());
			urlContent.setPreviousPostUrl(doc.body().select("[rel=prev]").attr("href"));
			// Set the number of pages, done the first time with the homepage
			if (activity.getTotalNumberOfPages() == null) {
				final String lastPageUrl = doc.body().select("a.last").attr("href");
				activity.setTotalNumberOfPages(CarmaBlogUtils.extractPageNumberFromUrl(lastPageUrl));
			}
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
