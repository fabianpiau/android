package com.carmablog.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.carmablog.R;
import com.carmablog.activity.HtmlActivity;
import com.carmablog.url.model.UrlContent;
import com.carmablog.url.model.UrlHtmlContent;
import com.carmablog.util.CarmaBlogUtils;

/**
 * Web view (HTML part).
 * 
 * @author fpiau
 * 
 */
public class CustomWebView extends WebView {

	// HTML activity
	private HtmlActivity activity;
	
	// A custom gesture detector
	// to detect left <-> right fling and enable double tap zoom
	private GestureDetector gestureDetector;
	
	// A WebView client 
	// to handle links and navigation history
	private WebViewClient webViewClient;
	
	// To know is the WebView is zoomed out
	private boolean isZoomedOut = true;
	private Float originalScale;
	private final static float ERROR_MARGIN = 0.05F;

	@SuppressLint("SetJavaScriptEnabled")
	public CustomWebView(final HtmlActivity activity) {
		super(activity);
		this.activity = activity;

		// Activate main useful features
		getSettings().setBuiltInZoomControls(true);
		getSettings().setSupportZoom(true);
		getSettings().setJavaScriptEnabled(true); // For syntax highlighting

		initializeWebViewClient();
		initializeGestureDetector();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
	}
	
	private void initializeWebViewClient() {
		webViewClient = new WebViewClient() {
			
			@Override
			public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
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
			
			@Override
			public void onScaleChanged(final WebView view, final float oldScale, final float newScale) {
				if (originalScale == null) {
					originalScale = oldScale;
				}
				if (newScale <= originalScale + ERROR_MARGIN) {
					isZoomedOut = true;
				} else {
					isZoomedOut = false;
				}				
				super.onScaleChanged(view, oldScale, newScale);
			}
		};
		setWebViewClient(webViewClient);
	}

	private void initializeGestureDetector() {
		final GestureDetector.SimpleOnGestureListener sogl = new GestureDetector.SimpleOnGestureListener() {
			
			private static final int SWIPE_THRESHOLD = 200;
	        private static final int SWIPE_VELOCITY_THRESHOLD = 200;
	        private boolean lastZoomOutResult = true;
	
	        @Override
	        public boolean onDown(MotionEvent e) {
	            return false;
	        }
	        
	        @Override
	        public boolean onDoubleTap(MotionEvent e) {
	        	if (lastZoomOutResult) {
	        		// We did a zoom out last time
	        		lastZoomOutResult = false;
	        		CustomWebView.this.zoomIn(); // Call 2 times for a bigger zoom
	        		CustomWebView.this.zoomIn();
	        	} else {
	        	    // We did a zoom in last time
	        		lastZoomOutResult = CustomWebView.this.zoomOut();
	        		if (!lastZoomOutResult) {
	        			// Already zoom out (the user has played with the zoom in between...)
	        			lastZoomOutResult = false;
	            		CustomWebView.this.zoomIn();
	            		CustomWebView.this.zoomIn();
	        		} else {
	        			CustomWebView.this.zoomOut();
	        		}
	            }
				return true;
	        }
	        
	        @Override
	        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
	            try {
	                float diffY = e2.getY() - e1.getY();
	                float diffX = e2.getX() - e1.getX();
	                if (Math.abs(diffX) > Math.abs(diffY)) {
	                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
	                    	if (!isZoomActive()) { // We don't want to fling while the zoom is active
		                    	final UrlContent urlContent = activity.getCurrentUrlContent();
		                        if (diffX > 0) {
		                            onSwipeRight(urlContent);
		                            return true;
		                        } else {
		                            onSwipeLeft(urlContent);
		                            return true;
		                        }
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
	
			private boolean isZoomActive() {
				return !isZoomedOut;
			}
		};
		gestureDetector = new GestureDetector(activity, sogl);
	}
	
	private void onSwipeRight(final UrlContent urlContent) {
    	// Single post
    	if (CarmaBlogUtils.isUrlMatchingSinglePost(urlContent.getUrl())) {
    		final String previousPostUrl = ((UrlHtmlContent) urlContent).getPreviousPostUrl();
    		if (previousPostUrl != null && previousPostUrl.length() > 0) {
        		// Display the previous article
    			showToast(R.string.message_previous_post);
        		activity.loadCarmablogHtmlUrl(previousPostUrl);
    		} else {
    			showToast(R.string.message_first_post);
    		}        		
    	}
    	// Page (with multiple posts)
    	else if (CarmaBlogUtils.isUrlMatchingPageMultiplePost(urlContent.getUrl())) {
    		Integer pageNumber = ((UrlHtmlContent) urlContent).getPageNumberAsInteger();
    		if (pageNumber != null && pageNumber != 1) {
        		// Display the previous page
    			pageNumber--;
    			showToast(R.string.message_previous_page, pageNumber, activity.getTotalNumberOfPages());
        		activity.loadCarmablogHtmlUrl(CarmaBlogUtils.buildUrlPageMultiplePost(pageNumber, activity.getCurrentLang()));
    		} else {
    			showToast(R.string.message_first_page);
    		} 
    	}
    }

    private void onSwipeLeft(final UrlContent urlContent) {
    	// Single post
    	if (CarmaBlogUtils.isUrlMatchingSinglePost(urlContent.getUrl())) {
    		final String nextPostUrl = ((UrlHtmlContent) urlContent).getNextPostUrl();
    		if (nextPostUrl != null && nextPostUrl.length() > 0) {
        		// Display the next article
    			showToast(R.string.message_next_post);
        		activity.loadCarmablogHtmlUrl(nextPostUrl);
    		} else {
    			showToast(R.string.message_last_post);
    		}
    	}
    	// Page (with multiple posts)
    	else if (CarmaBlogUtils.isUrlMatchingPageMultiplePost(urlContent.getUrl())) {
    		Integer pageNumber = ((UrlHtmlContent) urlContent).getPageNumberAsInteger();
    		if (pageNumber != null && activity.getTotalNumberOfPages() != null && pageNumber < activity.getTotalNumberOfPages()) {
        		// Display the next page
    			pageNumber++;
        		showToast(R.string.message_next_page, pageNumber, activity.getTotalNumberOfPages());
        		activity.loadCarmablogHtmlUrl(CarmaBlogUtils.buildUrlPageMultiplePost(pageNumber, activity.getCurrentLang()));
    		} else {
    			showToast(R.string.message_last_page);
    		} 
    	}
    }

	private void showToast(final int messageId) {
		Toast.makeText(activity, messageId, Toast.LENGTH_LONG).show();
	}

	private void showToast(final int messageId, final Integer pageNumber, final Integer pageTotal) {
		Toast.makeText(activity, activity.getString(messageId, pageNumber, pageTotal), Toast.LENGTH_LONG).show();
	}

}
