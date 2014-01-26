package com.carmablog.util;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;


public class LanguageUtilsTest {
	
	@Test
	public void testLocalizeInEnUrl() {
		assertThat(LanguageUtils.localizeUrl("http://blog.fabianpiau.com/fr/", "en"), is("http://blog.fabianpiau.com/en/"));
		assertThat(LanguageUtils.localizeUrl("http://blog.fabianpiau.com", "en"), is("http://blog.fabianpiau.com/en/"));
		assertThat(LanguageUtils.localizeUrl("http://blog.fabianpiau.com/", "en"), is("http://blog.fabianpiau.com/en/"));

		assertThat(LanguageUtils.localizeUrl("blog.fabianpiau.com/fr/", "en"), is("blog.fabianpiau.com/en/"));
		assertThat(LanguageUtils.localizeUrl("blog.fabianpiau.com", "en"), is("blog.fabianpiau.com/en/"));
		assertThat(LanguageUtils.localizeUrl("blog.fabianpiau.com/", "en"), is("blog.fabianpiau.com/en/"));

		assertThat(LanguageUtils.localizeUrl("http://fabianpiau.com/fr/", "en"), is("http://fabianpiau.com/en/"));
		assertThat(LanguageUtils.localizeUrl("http://fabianpiau.com", "en"), is("http://fabianpiau.com/en/"));
		assertThat(LanguageUtils.localizeUrl("http://fabianpiau.com/", "en"), is("http://fabianpiau.com/en/"));
		
		assertThat(LanguageUtils.localizeUrl("fabianpiau.com/fr/", "en"), is("fabianpiau.com/en/"));
		assertThat(LanguageUtils.localizeUrl("fabianpiau.com", "en"), is("fabianpiau.com/en/"));
		assertThat(LanguageUtils.localizeUrl("fabianpiau.com/", "en"), is("fabianpiau.com/en/"));
	}
	
	@Test
	public void testLocalizeUrlInEnPattern() {
		assertThat(LanguageUtils.localizeUrl("http://blog.fabianpiau.com/[LANG]/", "en"), is("http://blog.fabianpiau.com/en/"));
		assertThat(LanguageUtils.localizeUrl("blog.fabianpiau.com/[LANG]/", "en"), is("blog.fabianpiau.com/en/"));
		assertThat(LanguageUtils.localizeUrl("http://fabianpiau.com/[LANG]/", "en"), is("http://fabianpiau.com/en/"));
		assertThat(LanguageUtils.localizeUrl("fabianpiau.com/[LANG]/", "en"), is("fabianpiau.com/en/"));
	}
	
	@Test
	public void testLocalizeUrlInEnNothingToDo() {
		assertThat(LanguageUtils.localizeUrl("http://blog.fabianpiau.com/en/", "en"), is("http://blog.fabianpiau.com/en/"));
		assertThat(LanguageUtils.localizeUrl("blog.fabianpiau.com/en/", "en"), is("blog.fabianpiau.com/en/"));
		assertThat(LanguageUtils.localizeUrl("http://fabianpiau.com/en/", "en"), is("http://fabianpiau.com/en/"));
		assertThat(LanguageUtils.localizeUrl("fabianpiau.com/en/", "en"), is("fabianpiau.com/en/"));
	}
	
	@Test
	public void testLocalizeUrlInEnWithSecondPart() {
		assertThat(LanguageUtils.localizeUrl("http://blog.fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/", "en"), is("http://blog.fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));
		assertThat(LanguageUtils.localizeUrl("http://blog.fabianpiau.com/this/is/a/test/url/with/fr/inside/", "en"), is("http://blog.fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));

		assertThat(LanguageUtils.localizeUrl("blog.fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/", "en"), is("blog.fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));
		assertThat(LanguageUtils.localizeUrl("blog.fabianpiau.com/this/is/a/test/url/with/fr/inside/", "en"), is("blog.fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));

		assertThat(LanguageUtils.localizeUrl("http://fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/", "en"), is("http://fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));
		assertThat(LanguageUtils.localizeUrl("http://fabianpiau.com/this/is/a/test/url/with/fr/inside/", "en"), is("http://fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));
		
		assertThat(LanguageUtils.localizeUrl("fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/", "en"), is("fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));
		assertThat(LanguageUtils.localizeUrl("fabianpiau.com/this/is/a/test/url/with/fr/inside/", "en"), is("fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));
	}
	
	@Test
	public void testLocalizeUrlInEnPatternWithSecondPart() {
		assertThat(LanguageUtils.localizeUrl("http://blog.fabianpiau.com/[LANG]/this/is/a/test/url/with/fr/inside/", "en"), is("http://blog.fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));
		assertThat(LanguageUtils.localizeUrl("blog.fabianpiau.com/[LANG]/this/is/a/test/url/with/fr/inside/", "en"), is("blog.fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));
		assertThat(LanguageUtils.localizeUrl("http://fabianpiau.com/[LANG]/this/is/a/test/url/with/fr/inside/", "en"), is("http://fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));
		assertThat(LanguageUtils.localizeUrl("fabianpiau.com/[LANG]/this/is/a/test/url/with/fr/inside/", "en"), is("fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));
	}
	
	@Test
	public void testLocalizeUrlInEnNothingToDoWithSecondPart() {
		assertThat(LanguageUtils.localizeUrl("http://blog.fabianpiau.com/en/this/is/a/test/url/with/fr/inside/", "en"), is("http://blog.fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));
		assertThat(LanguageUtils.localizeUrl("blog.fabianpiau.com/en/this/is/a/test/url/with/fr/inside/", "en"), is("blog.fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));
		assertThat(LanguageUtils.localizeUrl("http://fabianpiau.com/en/this/is/a/test/url/with/fr/inside/", "en"), is("http://fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));
		assertThat(LanguageUtils.localizeUrl("fabianpiau.com/en/this/is/a/test/url/with/fr/inside/", "en"), is("fabianpiau.com/en/this/is/a/test/url/with/fr/inside/"));
		
		assertThat(LanguageUtils.localizeUrl("http://blog.fabianpiau.com/en/this/is/a/test/url/with/en/inside/", "en"), is("http://blog.fabianpiau.com/en/this/is/a/test/url/with/en/inside/"));
		assertThat(LanguageUtils.localizeUrl("blog.fabianpiau.com/en/this/is/a/test/url/with/en/inside/", "en"), is("blog.fabianpiau.com/en/this/is/a/test/url/with/en/inside/"));
		assertThat(LanguageUtils.localizeUrl("http://fabianpiau.com/en/this/is/a/test/url/with/en/inside/", "en"), is("http://fabianpiau.com/en/this/is/a/test/url/with/en/inside/"));
		assertThat(LanguageUtils.localizeUrl("fabianpiau.com/en/this/is/a/test/url/with/en/inside/", "en"), is("fabianpiau.com/en/this/is/a/test/url/with/en/inside/"));
	}
	
	@Test
	public void testLocalizeInFrUrl() {
		assertThat(LanguageUtils.localizeUrl("http://blog.fabianpiau.com/en/", "fr"), is("http://blog.fabianpiau.com/fr/"));
		assertThat(LanguageUtils.localizeUrl("http://blog.fabianpiau.com", "fr"), is("http://blog.fabianpiau.com/fr/"));
		assertThat(LanguageUtils.localizeUrl("http://blog.fabianpiau.com/", "fr"), is("http://blog.fabianpiau.com/fr/"));

		assertThat(LanguageUtils.localizeUrl("blog.fabianpiau.com/en/", "fr"), is("blog.fabianpiau.com/fr/"));
		assertThat(LanguageUtils.localizeUrl("blog.fabianpiau.com", "fr"), is("blog.fabianpiau.com/fr/"));
		assertThat(LanguageUtils.localizeUrl("blog.fabianpiau.com/", "fr"), is("blog.fabianpiau.com/fr/"));

		assertThat(LanguageUtils.localizeUrl("http://fabianpiau.com/en/", "fr"), is("http://fabianpiau.com/fr/"));
		assertThat(LanguageUtils.localizeUrl("http://fabianpiau.com", "fr"), is("http://fabianpiau.com/fr/"));
		assertThat(LanguageUtils.localizeUrl("http://fabianpiau.com/", "fr"), is("http://fabianpiau.com/fr/"));
		
		assertThat(LanguageUtils.localizeUrl("fabianpiau.com/en/", "fr"), is("fabianpiau.com/fr/"));
		assertThat(LanguageUtils.localizeUrl("fabianpiau.com", "fr"), is("fabianpiau.com/fr/"));
		assertThat(LanguageUtils.localizeUrl("fabianpiau.com/", "fr"), is("fabianpiau.com/fr/"));
	}
	
	@Test
	public void testLocalizeUrlInFrPattern() {
		assertThat(LanguageUtils.localizeUrl("http://blog.fabianpiau.com/[LANG]/", "fr"), is("http://blog.fabianpiau.com/fr/"));
		assertThat(LanguageUtils.localizeUrl("blog.fabianpiau.com/[LANG]/", "fr"), is("blog.fabianpiau.com/fr/"));
		assertThat(LanguageUtils.localizeUrl("http://fabianpiau.com/[LANG]/", "fr"), is("http://fabianpiau.com/fr/"));
		assertThat(LanguageUtils.localizeUrl("fabianpiau.com/[LANG]/", "fr"), is("fabianpiau.com/fr/"));
	}

	@Test
	public void testLocalizeUrlInFrNothingToDo() {
		assertThat(LanguageUtils.localizeUrl("http://blog.fabianpiau.com/fr/", "fr"), is("http://blog.fabianpiau.com/fr/"));
		assertThat(LanguageUtils.localizeUrl("blog.fabianpiau.com/fr/", "fr"), is("blog.fabianpiau.com/fr/"));
		assertThat(LanguageUtils.localizeUrl("http://fabianpiau.com/fr/", "fr"), is("http://fabianpiau.com/fr/"));
		assertThat(LanguageUtils.localizeUrl("fabianpiau.com/fr/", "fr"), is("fabianpiau.com/fr/"));
	}
	
	@Test
	public void testLocalizeUrlInFrWithSecondPart() {
		assertThat(LanguageUtils.localizeUrl("http://blog.fabianpiau.com/en/this/is/a/test/url/with/fr/inside/", "fr"), is("http://blog.fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));
		assertThat(LanguageUtils.localizeUrl("http://blog.fabianpiau.com/this/is/a/test/url/with/fr/inside/", "fr"), is("http://blog.fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));

		assertThat(LanguageUtils.localizeUrl("blog.fabianpiau.com/en/this/is/a/test/url/with/fr/inside/", "fr"), is("blog.fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));
		assertThat(LanguageUtils.localizeUrl("blog.fabianpiau.com/this/is/a/test/url/with/fr/inside/", "fr"), is("blog.fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));

		assertThat(LanguageUtils.localizeUrl("http://fabianpiau.com/en/this/is/a/test/url/with/fr/inside/", "fr"), is("http://fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));
		assertThat(LanguageUtils.localizeUrl("http://fabianpiau.com/this/is/a/test/url/with/fr/inside/", "fr"), is("http://fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));
		
		assertThat(LanguageUtils.localizeUrl("fabianpiau.com/en/this/is/a/test/url/with/fr/inside/", "fr"), is("fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));
		assertThat(LanguageUtils.localizeUrl("fabianpiau.com/this/is/a/test/url/with/fr/inside/", "fr"), is("fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));
	}
	
	@Test
	public void testLocalizeUrlInFrPatternWithSecondPart() {
		assertThat(LanguageUtils.localizeUrl("http://blog.fabianpiau.com/[LANG]/this/is/a/test/url/with/fr/inside/", "fr"), is("http://blog.fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));
		assertThat(LanguageUtils.localizeUrl("blog.fabianpiau.com/[LANG]/this/is/a/test/url/with/fr/inside/", "fr"), is("blog.fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));
		assertThat(LanguageUtils.localizeUrl("http://fabianpiau.com/[LANG]/this/is/a/test/url/with/fr/inside/", "fr"), is("http://fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));
		assertThat(LanguageUtils.localizeUrl("fabianpiau.com/[LANG]/this/is/a/test/url/with/fr/inside/", "fr"), is("fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));
	}
	
	@Test
	public void testLocalizeUrlInFrNothingToDoWithSecondPart() {
		assertThat(LanguageUtils.localizeUrl("http://blog.fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/", "fr"), is("http://blog.fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));
		assertThat(LanguageUtils.localizeUrl("blog.fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/", "fr"), is("blog.fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));
		assertThat(LanguageUtils.localizeUrl("http://fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/", "fr"), is("http://fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));
		assertThat(LanguageUtils.localizeUrl("fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/", "fr"), is("fabianpiau.com/fr/this/is/a/test/url/with/fr/inside/"));
		
		assertThat(LanguageUtils.localizeUrl("http://blog.fabianpiau.com/fr/this/is/a/test/url/with/en/inside/", "fr"), is("http://blog.fabianpiau.com/fr/this/is/a/test/url/with/en/inside/"));
		assertThat(LanguageUtils.localizeUrl("blog.fabianpiau.com/fr/this/is/a/test/url/with/en/inside/", "fr"), is("blog.fabianpiau.com/fr/this/is/a/test/url/with/en/inside/"));
		assertThat(LanguageUtils.localizeUrl("http://fabianpiau.com/fr/this/is/a/test/url/with/en/inside/", "fr"), is("http://fabianpiau.com/fr/this/is/a/test/url/with/en/inside/"));
		assertThat(LanguageUtils.localizeUrl("fabianpiau.com/fr/this/is/a/test/url/with/en/inside/", "fr"), is("fabianpiau.com/fr/this/is/a/test/url/with/en/inside/"));
	}

}
