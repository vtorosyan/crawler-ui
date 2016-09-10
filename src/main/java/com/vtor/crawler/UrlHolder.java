package com.vtor.crawler;

import org.apache.commons.validator.routines.UrlValidator;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

final class UrlHolder {

    private static final UrlValidator VALIDATOR = new UrlValidator();
    private final String url;

    UrlHolder(String url) {
        this.url = url;
    }

    boolean holdsValidUrl() {
        return VALIDATOR.isValid(url);
    }

    Optional<URL> url() {
        return get(url);
    }

    Optional<URL> robotsUrl() {
        Optional<URL> url = url();
        if (!url.isPresent()) {
            return Optional.empty();
        }
        return get(url.get().getProtocol() + "://" + url.get().getHost() + "/robots.txt");
    }

    String urlWithoutWww() {
        int index = url.indexOf("://www.");
        if (index != -1) {
            return url.substring(0, index + 3) + url.substring(index + 7);
        }
        return url;
    }

    private Optional<URL> get(String url) {
        try {
            return Optional.of(new URL(url));
        } catch (MalformedURLException mex) {
            return Optional.empty();
        }
    }
    
}
