package com.vtor.crawler;

import org.apache.commons.validator.routines.UrlValidator;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

final class UrlHolder {

    private static final UrlValidator VALIDATOR = new UrlValidator();
    private final String url;

    UrlHolder(String url) {
        if (!VALIDATOR.isValid(url)) {
            throw new IllegalArgumentException("Given URL is not valid " + url);
        }
        this.url = url;
    }

    boolean holdsValidUrl() {
        return get().isPresent();
    }

    Optional<URL> get() {
        try {
            return Optional.of(new URL(url));
        } catch (MalformedURLException mex) {
            return Optional.empty();
        }
    }

    String getWithoutWww() {
        int index = url.indexOf("://www.");
        if (index != -1) {
            return url.substring(0, index + 3) + url.substring(index + 7);
        }
        return url;
    }

}
