package com.vtor.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawler {

    private static final Logger LOGGER    = LoggerFactory.getLogger(Crawler.class);
    private static final int    FIND_URLS = 1000;
    private static final String LOG_FILE  = "crawler.log";

    private Map<String, List<String>> robotLinks = new HashMap();
    private boolean     isCrawl;
    private PrintWriter log;

    private final String url;

    public Crawler(String url) {
        this.url = url;
    }

    public void start() {
        UrlHolder urlHolder = new UrlHolder(url);
        if (urlHolder.holdsValidUrl()) {
            process(LOG_FILE, urlHolder.getWithoutWww());
        }
        LOGGER.error("Provided URL is invalid. Unable to process.");
    }


    private void process(final String fileToLog, final String urlToCrawl) {
        try {
            log = new PrintWriter(new FileWriter(fileToLog));
        } catch (IOException iox) {
            iox.printStackTrace();
            return;
        }
        isCrawl = true;
        crawlTheUrl(urlToCrawl);
        isCrawl = false;

        try {
            log.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Check if crawl link allowed from robots
     *
     * @param urlToCheck URL
     * @return boolean
     */
    @SuppressWarnings("unchecked")
    private boolean allowedRobots(URL urlToCheck) {
        String host = urlToCheck.getHost().toLowerCase();
        List<String> urls = (ArrayList<String>) robotLinks.get(host);
        if (urls == null) {
            urls = new ArrayList<String>();
            try {
                URL robotsUrl = new URL("http://" + host + "/robots.txt");
                BufferedReader r = new BufferedReader(new InputStreamReader(robotsUrl.openStream()));
                String eachLine;
                while ((eachLine = r.readLine()) != null) {
                    if (eachLine.indexOf("Disallow:") == 0) {
                        String path = eachLine.substring("Disallow:".length());
                        int index = path.indexOf("#");
                        if (index != -1) {
                            path = path.substring(0, index);
                        }
                        path = path.trim();
                        urls.add(path);
                    }
                }
                robotLinks.put(host, urls);
            } catch (MalformedURLException max) {
                max.printStackTrace();
                return true;
            } catch (IOException iox) {
                //iox.printStackTrace();
                return true;
            }
        }
        String f = urlToCheck.getFile();
        for (String url : urls) {
            if (f.startsWith(url)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Download bage by given url
     *
     * @param pageUrl URL
     * @return String
     */
    private String downloadPage(URL pageUrl) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(pageUrl.openStream()));
            String eachLine;
            StringBuilder builder = new StringBuilder();
            while ((eachLine = reader.readLine()) != null) {
                builder.append(eachLine);
            }
            return builder.toString();
        } catch (IOException iox) {
            iox.printStackTrace();
        }
        return null;
    }

    // Parse given page and get the urls
    private List<String> getLinks(URL pageUrl, String pageContents, Set<String> crawledList) {
        Pattern p = Pattern.compile("<a\\s+href\\s*=\\s*\"?(.*?)[\"|>]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(pageContents);
        List<String> linkList = new ArrayList<String>();
        while (m.find()) {
            String link = m.group(1).trim();
            if (link.length() < 1) {
                continue;
            }
            if (link.charAt(0) == '#') {
                continue;
            }
            if (link.contains("mailto:")) {
                continue;
            }
            if (link.toLowerCase().contains("javascript")) {
                continue;
            }
            if (!link.contains("://")) {
                if (link.charAt(0) == '/') {
                    link = "http://" + pageUrl.getHost() + link;
                } else {
                    String file = pageUrl.getFile();
                    if (file.indexOf('/') == -1) {
                        link = "http://" + pageUrl.getHost() + "/" + link;
                    } else {
                        String path = file.substring(0, file.lastIndexOf('/') + 1);
                        link = "http://" + pageUrl.getHost() + path + link;
                    }
                }
            }
            int index = link.indexOf('#');
            if (index != -1) {
                link = link.substring(0, index);
            }
            UrlHolder urlHolder = new UrlHolder(link);
            if (!urlHolder.holdsValidUrl()) {
                continue;
            }
            if (crawledList.contains(link)) {
                continue;
            }
            linkList.add(urlHolder.getWithoutWww());
        }
        return (linkList);
    }

    // Perform the actual crawling, searching for the search string.
    public void crawlTheUrl(String startUrl) {
        Set<String> crawledList = new HashSet<String>();
        Set<String> toCrawlList = new LinkedHashSet<String>();
        toCrawlList.add(startUrl);
        while (isCrawl && toCrawlList.size() > 0) {
            if (crawledList.size() == FIND_URLS) {
                break;
            }

            String url = toCrawlList.iterator().next();
            toCrawlList.remove(url);
            Optional<URL> verifiedLink = new UrlHolder(url).get();
            // Skip URL if robots are not allowed to access it.
            if (!verifiedLink.isPresent() || !allowedRobots(verifiedLink.get())) {
                continue;
            }

            // Add page to the crawled list.
            crawledList.add(url);
            log.append("URL Matched - " + url + "\n");

            // Download the page at the given URL.
            String pageContents = downloadPage(verifiedLink.get());
            /* If the page was downloaded successfully, retrieve all its links  */
            if (pageContents != null && pageContents.length() > 0) {
                // Retrieve list of valid links from page.
                List<String> links = getLinks(verifiedLink.get(), pageContents, crawledList);
                // Add links to the To Crawl list.
                toCrawlList.addAll(links);
            }
        }
    }


    public static void main(String[] args) {
        Crawler ct = new Crawler("http://www.vtorosyan.com");
        ct.start();
    }
}
