package com.carmablog.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.carmablog.R;
import com.carmablog.activity.MainActivity;
import com.carmablog.url.history.model.UrlContent;
import com.carmablog.url.history.model.UrlHtmlContent;
import com.carmablog.util.CarmaBlogUtils;

/**
 * Web view (HTML part).
 * 
 * @author fpiau
 * 
 */
public class CustomWebView extends WebView {

	private MainActivity activity;
	private GestureDetector gestureDetector;

	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	public CustomWebView(final MainActivity activity) {
		super(activity);
		this.activity = activity;

		// Activate main useful features
		getSettings().setBuiltInZoomControls(true);
		getSettings().setSupportZoom(true);
		getSettings().setJavaScriptEnabled(true); // For syntax highlighting

		// To avoid flickering
		if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) >= Build.VERSION_CODES.HONEYCOMB) {
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		
		// A client to handle links and navigation history
		final WebViewClient webViewClient = new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				super.shouldOverrideUrlLoading(view, url);
				if (CarmaBlogUtils.isUrlMatchingForApp(url)) {
					// Still on CarmaBlog
					// Load in the same WebView
					activity.loadCarmablogHtmlUrl(url);
				} else {
					// Not on CarmaBlog anymore or it is about sharing
					// Load in an external browser
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					activity.startActivity(i);
				}
				return true;
			}
		};
		setWebViewClient(webViewClient);
		gestureDetector = new GestureDetector(activity, sogl);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
	}

	final GestureDetector.SimpleOnGestureListener sogl = new GestureDetector.SimpleOnGestureListener() {
		
		private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }
        
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    	final UrlContent urlContent = activity.getCurrentUrlContent();
                        if (diffX > 0) {
                            onSwipeRight(urlContent);
                            return true;
                        } else {
                            onSwipeLeft(urlContent);
                            return true;
                        }
                    }
                } else {
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            // onSwipeBottom() - WebView normal behavior
                            return false;
                        } else {
                            // onSwipeTop() - WebView normal behavior
                            return false;
                        }
                    }
                }
            } catch (Exception exception) {
                Log.e("Touch error", exception.getStackTrace().toString());
            }
            return false;
        }
 
	};

    private void onSwipeRight(final UrlContent urlContent) {
    	
    	if (CarmaBlogUtils.isUrlMatchingSinglePost(urlContent.getUrl())) {
    		final String previousPostUrl = ((UrlHtmlContent) urlContent).getPreviousPostUrl();
    		if (previousPostUrl != null && previousPostUrl.length() > 0) {
        		// Display the previous article
        		show_toast(R.string.message_previous_post);
        		activity.loadCarmablogHtmlUrl(previousPostUrl);
    		} else {
    			show_toast(R.string.message_first_post);
    		}        		
    	}
    }

    private void onSwipeLeft(final UrlContent urlContent) {
    	if (CarmaBlogUtils.isUrlMatchingSinglePost(urlContent.getUrl())) {
    		final String nextPostUrl = ((UrlHtmlContent) urlContent).getNextPostUrl();
    		if (nextPostUrl != null && nextPostUrl.length() > 0) {
        		// Display the next article
        		show_toast(R.string.message_next_post);
        		activity.loadCarmablogHtmlUrl(nextPostUrl);
    		} else {
    			show_toast(R.string.message_last_post);
    		}
    	}
    }
    
	private void show_toast(final int messageId) {
		Toast.makeText(activity, messageId, Toast.LENGTH_LONG).show();
	}

}
