package com.vtor.crawler;

import org.junit.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class UrlHolderTest {

    @Test
    public void get_GivenValidUrl_ReturnURL() throws URISyntaxException {
        UrlHolder holder = new UrlHolder("http://www.example.com");

        Optional<URL> url = holder.get();

        assertThat(url.isPresent(), is(true));
        assertThat(url.get().getProtocol(), equalTo("http"));
        assertThat(url.get().getHost(), equalTo("www.example.com"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void get_GivenInvalidUrl_ThrowException() throws URISyntaxException {
        new UrlHolder("invalid.example.com");
    }

    @Test(expected = IllegalArgumentException.class)
    public void get_GivenNullAsParameter_ThrowException() throws URISyntaxException {
        new UrlHolder(null);
    }

    @Test
    public void getWithoutWww_GivenValidUrlWithWww_ReturnUrlAsString() throws URISyntaxException {
        UrlHolder holder = new UrlHolder("http://www.example.com");

        String url = holder.getWithoutWww();

        assertThat(url, equalTo("http://example.com"));
    }

    @Test
    public void getWithoutWww_GivenValidUrlWithoutWww_ReturnUrlAsString() throws URISyntaxException {
        UrlHolder holder = new UrlHolder("http://example.com");

        String url = holder.getWithoutWww();

        assertThat(url, equalTo("http://example.com"));
    }

}
