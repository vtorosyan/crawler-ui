package com.vtor.crawler;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

final class Robots {

    private static final Logger LOGGER = LoggerFactory.getLogger(Robots.class);

    private final UrlHolder urlHolder;
    private Map<String, List<String>> robots = new HashMap<>();

    Robots(UrlHolder urlHolder) {
        this.urlHolder = urlHolder;
    }

    @SuppressWarnings("unchecked")
    boolean isAllowed() {
        if (!urlHolder.robotsUrl().isPresent()) {
            return true;
        }

        List<String> disallowedUrls = robots.get(getHost());
        if (CollectionUtils.isEmpty(disallowedUrls)) {
            disallowedUrls = disallowedUrls();
            robots.put(getHost(), disallowedUrls);
        }

        return !isCrawlingDisallowed(disallowedUrls);
    }

    private List<String> disallowedUrls() {
        List<String> disallowedUrls = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(urlHolder.robotsUrl().get().openStream()))) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.indexOf("Disallow:") == 0) {
                    String path = line.substring("Disallow:".length());

                    int index = path.indexOf("#");
                    if (index != -1) {
                        path = path.substring(0, index);
                    }

                    path = path.trim();
                    disallowedUrls.add(path);
                }
            }
        } catch (IOException iox) {
            LOGGER.warn("Unable to read the robots.txt file", iox);
        }
        return disallowedUrls;
    }


    private boolean isCrawlingDisallowed(List<String> urls) {
        Optional<String> match = urls.stream().filter(getFile()::startsWith).findAny();

        return match.isPresent();
    }

    private String getFile() {
        return urlHolder.url().get().getFile();
    }

    private String getHost() {
        return urlHolder.url().get().getHost().toLowerCase();
    }
}
