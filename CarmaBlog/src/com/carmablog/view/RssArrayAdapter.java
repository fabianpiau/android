package com.carmablog.view;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.carmablog.R;
import com.carmablog.activity.RssActivity;
import com.carmablog.url.model.UrlRssElement;
import com.carmablog.util.CarmaBlogUtils;

/**
 * List of posts (RSS part).
 * @author fpiau
 *
 */
public class RssArrayAdapter extends ArrayAdapter<UrlRssElement> {
	
	// RSS activity
	private final RssActivity activity;
	
	// List of RSS elements
	private final List<UrlRssElement> urlRssElements;

    /*
     * Constructor.
     */
	public RssArrayAdapter(final RssActivity activity, final List<UrlRssElement> urlRssElements) {
		super(activity, R.layout.rss_element_row, urlRssElements);
		this.activity = activity;
		this.urlRssElements = urlRssElements;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		final LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		final View rowPostView = inflater.inflate(R.layout.rss_element_row, parent, false);

		final ImageView categoryImage = (ImageView) rowPostView.findViewById(R.id.rss_element_category);
		final TextView titleText = (TextView) rowPostView.findViewById(R.id.rss_element_title);
		final TextView dateText = (TextView) rowPostView.findViewById(R.id.rss_element_date);

		final UrlRssElement post = urlRssElements.get(position);
		
		// Set title and date
		titleText.setText(post.getTitle());
		dateText.setText(CarmaBlogUtils.formatDate(post.getDate(), activity.getCurrentLang()));

		// Change icon based on the category name
		final String categoryText = post.getCategory();
		if (categoryText.contains("gile")) {
			categoryImage.setImageResource(R.drawable.agile_programming);
		} else if (categoryText.contains("Ev")) {
			categoryImage.setImageResource(R.drawable.event);
		} else if (categoryText.contains("Linux")) {
			categoryImage.setImageResource(R.drawable.linux);
		} else if (categoryText.contains("Management")) {
			categoryImage.setImageResource(R.drawable.management);
		} else if (categoryText.contains("Technolog")) {
			categoryImage.setImageResource(R.drawable.technology);
		} 

		return rowPostView;
	}
}
