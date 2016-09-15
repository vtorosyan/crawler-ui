package com.vtor.crawler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class Page {

    private static final Logger  LOGGER  = LoggerFactory.getLogger(Page.class);
    private static final Pattern PATTERN =
            Pattern.compile("<a\\s+href\\s*=\\s*\"?(.*?)[\"|>]", Pattern.CASE_INSENSITIVE);

    private final UrlHolder urlHolder;
    private final boolean   limitToHost;

    Page(UrlHolder urlHolder, boolean limitToHost) {
        this.urlHolder = urlHolder;
        this.limitToHost = limitToHost;
    }

    String download() {
        if (!urlHolder.holdsValidUrl()) {
            return "";
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlHolder.url().get().openStream()))) {
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (IOException iox) {
            LOGGER.error("Unable to download the page", iox);
        }

        return "";
    }

    List<String> getLinks(Set<String> alreadyProcessedUrls) {
        List<String> links = new ArrayList<>();

        String page = download();
        if (StringUtils.isBlank(page)) {
            return links;
        }

        Matcher m = PATTERN.matcher(page);
        while (m.find()) {
            String link = m.group(1).trim();
            if (linkShouldBeIgnored(link)) {
                continue;
            }

            link = normalize(urlHolder.url().get(), link);
            UrlHolder urlHolder = new UrlHolder(link);
            boolean isTheSameHost = this.urlHolder.url().get().getHost().toLowerCase()
                                                  .equals(urlHolder.url().get().getHost().toLowerCase());
            boolean isLimitToHostButTheHostIsNotTheSame = limitToHost && !isTheSameHost;
            boolean urlIsNotValidOrProcessed = !urlHolder.holdsValidUrl() || alreadyProcessedUrls.contains(link);

            if (isLimitToHostButTheHostIsNotTheSame || urlIsNotValidOrProcessed) {
                continue;
            }
            links.add(urlHolder.urlWithoutWww());
        }
        return links;
    }

    private String normalize(URL url, String link) {
        if (!link.contains("://")) {
            return normalizeWithProtocol(url, link);
        }

        int index = link.indexOf('#');
        if (index != -1) {
            return link.substring(0, index);
        }
        return link;
    }

    private String normalizeWithProtocol(URL url, String link) {
        if (link.charAt(0) == '/') {
            return "http://" + url.getHost() + link;
        }

        String file = url.getFile();
        if (file.indexOf('/') == -1) {
            return "http://" + url.getHost() + "/" + link;
        }

        String path = file.substring(0, file.lastIndexOf('/') + 1);
        return "http://" + url.getHost() + path + link;
    }

    private boolean linkShouldBeIgnored(String link) {
        return link.length() < 1 || link.charAt(0) == '#' || link.contains("mailto:");
    }

}
