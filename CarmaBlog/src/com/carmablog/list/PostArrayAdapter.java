package com.carmablog.list;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.carmablog.R;
import com.carmablog.activity.MainActivity;
import com.carmablog.url.history.model.UrlRssElement;
import com.carmablog.util.CarmaBlogUtils;

public class PostArrayAdapter extends ArrayAdapter<UrlRssElement> {
	
	private final MainActivity activity;
	private final List<UrlRssElement> urlRssElements;

	public PostArrayAdapter(final MainActivity activity, final List<UrlRssElement> urlRssElements) {
		super(activity, R.layout.row_post, urlRssElements);
		this.activity = activity;
		this.urlRssElements = urlRssElements;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		final LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		final View rowPostView = inflater.inflate(R.layout.row_post, parent, false);

		final ImageView categoryImage = (ImageView) rowPostView.findViewById(R.id.row_post_category);
		final TextView titleText = (TextView) rowPostView.findViewById(R.id.row_post_title);
		final TextView dateText = (TextView) rowPostView.findViewById(R.id.row_post_date);

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
