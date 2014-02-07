package com.carmablog.retriever;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.carmablog.R;
import com.carmablog.activity.MainActivity;
import com.carmablog.url.history.model.UrlRssContent;
import com.carmablog.url.history.model.UrlRssElement;

/**
 * Retrieve the RSS feed from the Internet then display it in the ListView.
 * Display a progress bar.
 * @author fpiau
 *
 */
public class RetrieveRssRemoteTask extends AsyncTask<String, Void, UrlRssContent> {

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
	public RetrieveRssRemoteTask(final MainActivity activity) {
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
    public UrlRssContent doInBackground(final String... urls) {
		final String url = urls[0];
    	Document doc = null;
        try {
        	// Get the XML code with Jsoup
        	doc = Jsoup.connect(url).parser(Parser.xmlParser()).get();
        } catch (Exception e) {
        	Log.e("Jsoup", "Cannot retrieve document. Check the URL: " + url + " is correct. Exception: " + e.getMessage());
        	exception = e;
        }
		if (doc != null) {
			final SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
			final List<UrlRssElement> urlRssElements = new ArrayList<UrlRssElement>();
		    for (Element e : doc.select("item")) {
		    	UrlRssElement urlRssElement = new UrlRssElement();
		    	urlRssElement.setTitle(e.select("title").text());
		    	urlRssElement.setLink(e.select("link").text());
		    	urlRssElement.setCategory(e.select("category").first().text());
		    	try {
					urlRssElement.setDate(formatter.parse(e.select("pubDate").text()));
				} catch (ParseException e1) {
					Log.e("DateFormat", "Error when parsing the date of this post: " + e.select("pubDate").text());
				}
		    	urlRssElements.add(urlRssElement);
		    }
		    final UrlRssContent urlContent = new UrlRssContent();
		    urlContent.setUrl(url);
		    urlContent.setUrlRssElements(urlRssElements);
		    return urlContent ;
		}
		return null;
    }

	@Override
	protected void onPostExecute(final UrlRssContent result) {
		super.onPostExecute(result);
		if (progressDialog.isShowing()) {
			// Hide progress dialog
			progressDialog.dismiss();
	    }
		if (exception == null) {
			activity.updateHistoryAndCurrentState(result);
			// Refresh the ListView with the urlRssElements
			activity.updateListView(result.getUrlRssElements());
		} else {
			// An error occurred with Jsoup
			// Probably a connection error
			activity.getMyWebView().loadUrl("file:///android_asset/connection_timeout.html");
			Toast.makeText(activity.getApplicationContext(), R.string.message_timeout, Toast.LENGTH_LONG).show();
		}
	}
    
}
