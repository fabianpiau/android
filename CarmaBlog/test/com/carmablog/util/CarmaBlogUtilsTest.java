package com.carmablog.util;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;

import org.junit.Test;


public class CarmaBlogUtilsTest {
	
	@Test
	public void testLocalizeInEnUrl() {
		assertThat(CarmaBlogUtils.localizeUrl("http://blog.fabianpiau.com/fr/", "en"), is("http://blog.fabianpiau.com/en/"));
		assertThat(CarmaBlogUtils.localizeUrl("http://blog.fabianpiau.com", "en"), is("http://blog.fabianpiau.com/en/"));
		assertThat(CarmaBlogUtils.localizeUrl("http://blog.fabianpiau.com/", "en"), is("http://blog.fabianpiau.com/en/"));

		assertThat(CarmaBlogUtils.localizeUrl("blog.fabianpiau.com/fr/", "en"), is("blog.fabianpiau.com/en/"));
		assertThat(CarmaBlogUtils.localizeUrl("blog.fabianpiau.com", "en"), is("blog.fabianpiau.com/en/"));
		assertThat(CarmaBlogUtils.localizeUrl("blog.fabianpiau.com/", "en"), is("blog.fabianpiau.com/en/"));

		assertThat(CarmaBlogUtils.localizeUrl("http://fabianpiau.com/fr/", "en"), is("http://fabianpiau.com/en/"));
		assertThat(CarmaBlogUtils.localizeUrl("http://fabianpiau.com", "en"), is("http://fabianpiau.com/en/"));
		assertThat(CarmaBlogUtils.localizeUrl("http://fabianpiau.com/", "en"), is("http://fabianpiau.com/en/"));
		
		assertThat(CarmaBlogUtils.localizeUrl("fabianpiau.com/fr/", "en"), is("fabianpiau.com/en/"));
		assertThat(CarmaBlogUtils.localizeUrl("fabianpiau.com", "en"), is("fabianpiau.com/en/"));
		assertThat(CarmaBlogUtils.localizeUrl("fabianpiau.com/", "en"), is("fabianpiau.com/en/"));
	}
	
	@Test
	public void testLocalizeUrlInEnPattern() {
		assertThat(CarmaBlogUtils.localizeUrl("http://blog.fabianpiau.com/[LANG]/", "en"), is("http://blog.fabianpiau.com/en/"));
		assertThat(CarmaBlogUtils.localizeUrl("blog.fabianpiau.com/[LANG]/", "en"), is("blog.fabianpiau.com/en/"));
		assertThat(CarmaBlogUtils.localizeUrl("http://fabianpiau.com/[LANG]/", "en"), is("http://fabianpiau.com/en/"));
		assertThat(CarmaBlogUtils.localizeUrl("fabianpiau.com/[LANG]/", "en"), is("fabianpiau.com/en/"));
	}
	
	@Test
	public void testLocalizeUrlInEnNothingToDo() {
		assertThat(CarmaBlogUtils.localizeUrl("http://blog.fabianpiau.com/en/", "en"), is("http://blog.fabianpiau.com/en/"));
		assertThat(CarmaBlogUtils.localizeUrl("blog.fabianpiau.com/en/", "en"), is("blog.fabianpiau.com/en/"));
		assertThat(CarmaBlogUtils.localizeUrl("http://fabianpiau.com/en/", "en"), is("http://fabianpiau.com/en/"));
		assertThat(CarmaBlogUtils.localizeUrl("fabianpiau.com/en/", "en"), is("fabianpiau.com/en/"));
	}
	
	@Test
	public void testLocalizeUrlInEnWithSecondPart() {
		assertThat(CarmaBlogUtils.localizeUrl("http://blog.fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/", "en"), is("http://blog.fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));
		assertThat(CarmaBlogUtils.localizeUrl("http://blog.fabianpiau.com/this/is/a/test/url/with/fr/inside/", "en"), is("http://blog.fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));

		assertThat(CarmaBlogUtils.localizeUrl("blog.fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/", "en"), is("blog.fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));
		assertThat(CarmaBlogUtils.localizeUrl("blog.fabianpiau.com/this/is/a/test/url/with/fr/inside/", "en"), is("blog.fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));

		assertThat(CarmaBlogUtils.localizeUrl("http://fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/", "en"), is("http://fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));
		assertThat(CarmaBlogUtils.localizeUrl("http://fabianpiau.com/this/is/a/test/url/with/fr/inside/", "en"), is("http://fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));
		
		assertThat(CarmaBlogUtils.localizeUrl("fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/", "en"), is("fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));
		assertThat(CarmaBlogUtils.localizeUrl("fabianpiau.com/this/is/a/test/url/with/fr/inside/", "en"), is("fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));
	}
	
	@Test
	public void testLocalizeUrlInEnPatternWithSecondPart() {
		assertThat(CarmaBlogUtils.localizeUrl("http://blog.fabianpiau.com/[LANG]/this/is/a/test/url/with/fr/inside/", "en"), is("http://blog.fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));
		assertThat(CarmaBlogUtils.localizeUrl("blog.fabianpiau.com/[LANG]/this/is/a/test/url/with/fr/inside/", "en"), is("blog.fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));
		assertThat(CarmaBlogUtils.localizeUrl("http://fabianpiau.com/[LANG]/this/is/a/test/url/with/fr/inside/", "en"), is("http://fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));
		assertThat(CarmaBlogUtils.localizeUrl("fabianpiau.com/[LANG]/this/is/a/test/url/with/fr/inside/", "en"), is("fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));
	}
	
	@Test
	public void testLocalizeUrlInEnNothingToDoWithSecondPart() {
		assertThat(CarmaBlogUtils.localizeUrl("http://blog.fabianpiau.com/en/this/is/a/test/url/with/fr/inside/", "en"), is("http://blog.fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));
		assertThat(CarmaBlogUtils.localizeUrl("blog.fabianpiau.com/en/this/is/a/test/url/with/fr/inside/", "en"), is("blog.fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));
		assertThat(CarmaBlogUtils.localizeUrl("http://fabianpiau.com/en/this/is/a/test/url/with/fr/inside/", "en"), is("http://fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));
		assertThat(CarmaBlogUtils.localizeUrl("fabianpiau.com/en/this/is/a/test/url/with/fr/inside/", "en"), is("fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));
		
		assertThat(CarmaBlogUtils.localizeUrl("http://blog.fabianpiau.com/en/this/is/a/test/url/with/en/inside/", "en"), is("http://blog.fabianpiau.com/en/this/is/a/test/url/with/en/inside/"));
		assertThat(CarmaBlogUtils.localizeUrl("blog.fabianpiau.com/en/this/is/a/test/url/with/en/inside/", "en"), is("blog.fabianpiau.com/en/this/is/a/test/url/with/en/inside/"));
		assertThat(CarmaBlogUtils.localizeUrl("http://fabianpiau.com/en/this/is/a/test/url/with/en/inside/", "en"), is("http://fabianpiau.com/en/this/is/a/test/url/with/en/inside/"));
		assertThat(CarmaBlogUtils.localizeUrl("fabianpiau.com/en/this/is/a/test/url/with/en/inside/", "en"), is("fabianpiau.com/en/this/is/a/test/url/with/en/inside/"));
	}
	
	@Test
	public void testLocalizeInFrUrl() {
		assertThat(CarmaBlogUtils.localizeUrl("http://blog.fabianpiau.com/en/", "fr"), is("http://blog.fabianpiau.com/fr/"));
		assertThat(CarmaBlogUtils.localizeUrl("http://blog.fabianpiau.com", "fr"), is("http://blog.fabianpiau.com/fr/"));
		assertThat(CarmaBlogUtils.localizeUrl("http://blog.fabianpiau.com/", "fr"), is("http://blog.fabianpiau.com/fr/"));

		assertThat(CarmaBlogUtils.localizeUrl("blog.fabianpiau.com/en/", "fr"), is("blog.fabianpiau.com/fr/"));
		assertThat(CarmaBlogUtils.localizeUrl("blog.fabianpiau.com", "fr"), is("blog.fabianpiau.com/fr/"));
		assertThat(CarmaBlogUtils.localizeUrl("blog.fabianpiau.com/", "fr"), is("blog.fabianpiau.com/fr/"));

		assertThat(CarmaBlogUtils.localizeUrl("http://fabianpiau.com/en/", "fr"), is("http://fabianpiau.com/fr/"));
		assertThat(CarmaBlogUtils.localizeUrl("http://fabianpiau.com", "fr"), is("http://fabianpiau.com/fr/"));
		assertThat(CarmaBlogUtils.localizeUrl("http://fabianpiau.com/", "fr"), is("http://fabianpiau.com/fr/"));
		
		assertThat(CarmaBlogUtils.localizeUrl("fabianpiau.com/en/", "fr"), is("fabianpiau.com/fr/"));
		assertThat(CarmaBlogUtils.localizeUrl("fabianpiau.com", "fr"), is("fabianpiau.com/fr/"));
		assertThat(CarmaBlogUtils.localizeUrl("fabianpiau.com/", "fr"), is("fabianpiau.com/fr/"));
	}
	
	@Test
	public void testLocalizeUrlInFrPattern() {
		assertThat(CarmaBlogUtils.localizeUrl("http://blog.fabianpiau.com/[LANG]/", "fr"), is("http://blog.fabianpiau.com/fr/"));
		assertThat(CarmaBlogUtils.localizeUrl("blog.fabianpiau.com/[LANG]/", "fr"), is("blog.fabianpiau.com/fr/"));
		assertThat(CarmaBlogUtils.localizeUrl("http://fabianpiau.com/[LANG]/", "fr"), is("http://fabianpiau.com/fr/"));
		assertThat(CarmaBlogUtils.localizeUrl("fabianpiau.com/[LANG]/", "fr"), is("fabianpiau.com/fr/"));
	}

	@Test
	public void testLocalizeUrlInFrNothingToDo() {
		assertThat(CarmaBlogUtils.localizeUrl("http://blog.fabianpiau.com/fr/", "fr"), is("http://blog.fabianpiau.com/fr/"));
		assertThat(CarmaBlogUtils.localizeUrl("blog.fabianpiau.com/fr/", "fr"), is("blog.fabianpiau.com/fr/"));
		assertThat(CarmaBlogUtils.localizeUrl("http://fabianpiau.com/fr/", "fr"), is("http://fabianpiau.com/fr/"));
		assertThat(CarmaBlogUtils.localizeUrl("fabianpiau.com/fr/", "fr"), is("fabianpiau.com/fr/"));
	}
	
	@Test
	public void testLocalizeUrlInFrWithSecondPart() {
		assertThat(CarmaBlogUtils.localizeUrl("http://blog.fabianpiau.com/en/this/is/a/test/url/with/fr/inside/", "fr"), is("http://blog.fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));
		assertThat(CarmaBlogUtils.localizeUrl("http://blog.fabianpiau.com/this/is/a/test/url/with/fr/inside/", "fr"), is("http://blog.fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));

		assertThat(CarmaBlogUtils.localizeUrl("blog.fabianpiau.com/en/this/is/a/test/url/with/fr/inside/", "fr"), is("blog.fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));
		assertThat(CarmaBlogUtils.localizeUrl("blog.fabianpiau.com/this/is/a/test/url/with/fr/inside/", "fr"), is("blog.fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));

		assertThat(CarmaBlogUtils.localizeUrl("http://fabianpiau.com/en/this/is/a/test/url/with/fr/inside/", "fr"), is("http://fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));
		assertThat(CarmaBlogUtils.localizeUrl("http://fabianpiau.com/this/is/a/test/url/with/fr/inside/", "fr"), is("http://fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));
		
		assertThat(CarmaBlogUtils.localizeUrl("fabianpiau.com/en/this/is/a/test/url/with/fr/inside/", "fr"), is("fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));
		assertThat(CarmaBlogUtils.localizeUrl("fabianpiau.com/this/is/a/test/url/with/fr/inside/", "fr"), is("fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));
	}
	
	@Test
	public void testLocalizeUrlInFrPatternWithSecondPart() {
		assertThat(CarmaBlogUtils.localizeUrl("http://blog.fabianpiau.com/[LANG]/this/is/a/test/url/with/fr/inside/", "fr"), is("http://blog.fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));
		assertThat(CarmaBlogUtils.localizeUrl("blog.fabianpiau.com/[LANG]/this/is/a/test/url/with/fr/inside/", "fr"), is("blog.fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));
		assertThat(CarmaBlogUtils.localizeUrl("http://fabianpiau.com/[LANG]/this/is/a/test/url/with/fr/inside/", "fr"), is("http://fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));
		assertThat(CarmaBlogUtils.localizeUrl("fabianpiau.com/[LANG]/this/is/a/test/url/with/fr/inside/", "fr"), is("fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));
	}
	
	@Test
	public void testLocalizeUrlInFrNothingToDoWithSecondPart() {
		assertThat(CarmaBlogUtils.localizeUrl("http://blog.fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/", "fr"), is("http://blog.fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));
		assertThat(CarmaBlogUtils.localizeUrl("blog.fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/", "fr"), is("blog.fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));
		assertThat(CarmaBlogUtils.localizeUrl("http://fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/", "fr"), is("http://fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));
		assertThat(CarmaBlogUtils.localizeUrl("fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/", "fr"), is("fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));
		
		assertThat(CarmaBlogUtils.localizeUrl("http://blog.fabianpiau.com/fr/this/is/a/test/url/with/en/inside/", "fr"), is("http://blog.fabianpiau.com/fr/this/is/a/test/url/with/en/inside/"));
		assertThat(CarmaBlogUtils.localizeUrl("blog.fabianpiau.com/fr/this/is/a/test/url/with/en/inside/", "fr"), is("blog.fabianpiau.com/fr/this/is/a/test/url/with/en/inside/"));
		assertThat(CarmaBlogUtils.localizeUrl("http://fabianpiau.com/fr/this/is/a/test/url/with/en/inside/", "fr"), is("http://fabianpiau.com/fr/this/is/a/test/url/with/en/inside/"));
		assertThat(CarmaBlogUtils.localizeUrl("fabianpiau.com/fr/this/is/a/test/url/with/en/inside/", "fr"), is("fabianpiau.com/fr/this/is/a/test/url/with/en/inside/"));
	}

	@Test
	public void testIsUrlMatchingSinglePost() {
		assertThat(CarmaBlogUtils.isUrlMatchingSinglePost("http://blog.fabianpiau.com/fr/2012/07/25/open-street-map-better-map-than-google-maps/"), is(true));
		assertThat(CarmaBlogUtils.isUrlMatchingSinglePost("http://blog.fabianpiau.com/en/2012/07/25/open-street-map-better-map-than-google-maps/"), is(true));
		assertThat(CarmaBlogUtils.isUrlMatchingSinglePost("http://blog.fabianpiau.com/2012/07/25/open-street-map-better-map-than-google-maps/"), is(true));
		assertThat(CarmaBlogUtils.isUrlMatchingSinglePost("blog.fabianpiau.com/fr/2012/07/25/open-street-map-better-map-than-google-maps/"), is(true));
		assertThat(CarmaBlogUtils.isUrlMatchingSinglePost("blog.fabianpiau.com/en/2012/07/25/open-street-map-better-map-than-google-maps/"), is(true));
		assertThat(CarmaBlogUtils.isUrlMatchingSinglePost("blog.fabianpiau.com/2012/07/25/open-street-map-better-map-than-google-maps/"), is(true));
		
		assertThat(CarmaBlogUtils.isUrlMatchingSinglePost("http://blog.fabianpiau.com/fr/1912/07/25/open-street-map-better-map-than-google-maps/"), is(false));
		assertThat(CarmaBlogUtils.isUrlMatchingSinglePost("http://blog.fabianpiau.com/technology/"), is(false));
		assertThat(CarmaBlogUtils.isUrlMatchingSinglePost("http://blog.fabianpiau.com/2012/20/10/open-street-map-better-map-than-google-maps/"), is(false));

	}

	@Test
	public void testIsUrlMatchingPageMultiplePost() {
		assertThat(CarmaBlogUtils.isUrlMatchingPageMultiplePost("http://blog.fabianpiau.com/fr/page/2/"), is(true));
		assertThat(CarmaBlogUtils.isUrlMatchingPageMultiplePost("http://blog.fabianpiau.com/en/page/2/"), is(true));
		assertThat(CarmaBlogUtils.isUrlMatchingPageMultiplePost("http://blog.fabianpiau.com/page/2/"), is(true));
		assertThat(CarmaBlogUtils.isUrlMatchingPageMultiplePost("http://blog.fabianpiau.com/fr/page/10/"), is(true));
		assertThat(CarmaBlogUtils.isUrlMatchingPageMultiplePost("http://blog.fabianpiau.com/fr/page/120/"), is(true));
		assertThat(CarmaBlogUtils.isUrlMatchingPageMultiplePost("fabianpiau.com/fr/page/120/"), is(true));
		assertThat(CarmaBlogUtils.isUrlMatchingPageMultiplePost("blog.fabianpiau.com/fr/page/120/"), is(true));
		
		assertThat(CarmaBlogUtils.isUrlMatchingPageMultiplePost("http://blog.fabianpiau.com/fr/page/"), is(false));
		assertThat(CarmaBlogUtils.isUrlMatchingPageMultiplePost("http://blog.fabianpiau.com/fr/page/2/test"), is(false));
		assertThat(CarmaBlogUtils.isUrlMatchingPageMultiplePost("http://blog.fabianpiau.com/technology/"), is(false));
		assertThat(CarmaBlogUtils.isUrlMatchingPageMultiplePost("http://blog.fabianpiau.com/2012/10/10/open-street-map-better-map-than-google-maps/"), is(false));

	}
	
	@Test
	public void testExtractPageNumberFromUrl() {
		assertThat(CarmaBlogUtils.extractPageNumberFromUrl("http://blog.fabianpiau.com/fr/page/2/"), is(2));
		assertThat(CarmaBlogUtils.extractPageNumberFromUrl("http://blog.fabianpiau.com/en/page/2/"), is(2));
		assertThat(CarmaBlogUtils.extractPageNumberFromUrl("http://blog.fabianpiau.com/page/5/"), is(5));
		assertThat(CarmaBlogUtils.extractPageNumberFromUrl("http://blog.fabianpiau.com/fr/page/10/"), is(10));
		assertThat(CarmaBlogUtils.extractPageNumberFromUrl("http://blog.fabianpiau.com/fr/page/120/"), is(120));
		assertThat(CarmaBlogUtils.extractPageNumberFromUrl("fabianpiau.com/fr/page/254/"), is(254));
		assertThat(CarmaBlogUtils.extractPageNumberFromUrl("blog.fabianpiau.com/fr/page/1/"), is(1));
		
		assertThat(CarmaBlogUtils.extractPageNumberFromUrl("http://blog.fabianpiau.com/fr/page/"), nullValue());
		assertThat(CarmaBlogUtils.extractPageNumberFromUrl("http://blog.fabianpiau.com/technology/"), nullValue());
		assertThat(CarmaBlogUtils.extractPageNumberFromUrl("http://blog.fabianpiau.com/2012/10/10/open-street-map-better-map-than-google-maps/"), nullValue());

	}
	
	
	
	
}
