package com.vtor.crawler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Application extends JFrame {

    // Max URLs drop-down values.
    private static final String[] MAX_URLS          = {"50", "100", "500", "1000"};
    // Cache of robot disallow lists.
    private              HashMap  disallowListCache = new HashMap();
    // Search GUI controls.
    private JTextField   startTextField;
    private JComboBox    maxComboBox;
    private JCheckBox    limitCheckBox;
    private JTextField   logTextField;
    private JTextField   searchTextField;
    private JCheckBox    caseCheckBox;
    private JButton      searchButton;
    // Search stats GUI controls.
    private JLabel       crawlingLabel2;
    private JLabel       crawledLabel2;
    private JLabel       toCrawlLabel2;
    private JProgressBar progressBar;
    private JLabel       matchesLabel2;
    // Table listing search matches.
    private JTable       table;
    // Flag for whether or not crawling is underway.
    private boolean      crawling;
    // Matches log file print writer.
    private PrintWriter  logFileWriter;

    // Constructor for Search Web Crawler.
    public Application() {
        // Set application title.
        setTitle("Search Crawler");
        // Set window size.
        setSize(600, 600);
        // Handle window closing events.
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                actionExit();
            }
        });
        // Set up File menu.
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem fileExitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        fileExitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionExit();
            }
        });
        fileMenu.add(fileExitMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
        // Set up search panel.
        JPanel searchPanel = new JPanel();
        GridBagConstraints constraints;
        GridBagLayout layout = new GridBagLayout();
        searchPanel.setLayout(layout);
        JLabel startLabel = new JLabel("Start URL:");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(startLabel, constraints);
        searchPanel.add(startLabel);
        startTextField = new JTextField();
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 0, 5);
        layout.setConstraints(startTextField, constraints);
        searchPanel.add(startTextField);
        JLabel maxLabel = new JLabel("Max URLs to Crawl:");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(maxLabel, constraints);
        searchPanel.add(maxLabel);
        maxComboBox = new JComboBox(MAX_URLS);
        maxComboBox.setEditable(true);
        constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(maxComboBox, constraints);
        searchPanel.add(maxComboBox);
        limitCheckBox = new JCheckBox("Limit crawling to Start URL site");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 10, 0, 0);
        layout.setConstraints(limitCheckBox, constraints);
        searchPanel.add(limitCheckBox);
        JLabel blankLabel = new JLabel();
        constraints = new GridBagConstraints();
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(blankLabel, constraints);
        searchPanel.add(blankLabel);
        JLabel logLabel = new JLabel("Matches Log File:");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(logLabel, constraints);
        searchPanel.add(logLabel);
        String file = System.getProperty("user.dir") +
                      System.getProperty("file.separator") +
                      "crawler.log";
        logTextField = new JTextField(file);
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 0, 5);
        layout.setConstraints(logTextField, constraints);
        searchPanel.add(logTextField);
        JLabel searchLabel = new JLabel("Search String:");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(searchLabel, constraints);
        searchPanel.add(searchLabel);
        searchTextField = new JTextField();
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(5, 5, 0, 0);
        constraints.gridwidth = 2;
        constraints.weightx = 1.0d;
        layout.setConstraints(searchTextField, constraints);
        searchPanel.add(searchTextField);
        caseCheckBox = new JCheckBox("Case Sensitive");
        constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 0, 5);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(caseCheckBox, constraints);
        searchPanel.add(caseCheckBox);
        searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionSearch();
            }
        });
        constraints = new GridBagConstraints();
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 5, 5);
        layout.setConstraints(searchButton, constraints);
        searchPanel.add(searchButton);
        JSeparator separator = new JSeparator();
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 5, 5);
        layout.setConstraints(separator, constraints);
        searchPanel.add(separator);
        JLabel crawlingLabel1 = new JLabel("Crawling:");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(crawlingLabel1, constraints);
        searchPanel.add(crawlingLabel1);
        crawlingLabel2 = new JLabel();
        crawlingLabel2.setFont(crawlingLabel2.getFont().deriveFont(Font.PLAIN));
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 0, 5);
        layout.setConstraints(crawlingLabel2, constraints);
        searchPanel.add(crawlingLabel2);
        JLabel crawledLabel1 = new JLabel("Crawled URLs:");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(crawledLabel1, constraints);
        searchPanel.add(crawledLabel1);
        crawledLabel2 = new JLabel();
        crawledLabel2.setFont(crawledLabel2.getFont().deriveFont(Font.PLAIN));
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 0, 5);
        layout.setConstraints(crawledLabel2, constraints);
        searchPanel.add(crawledLabel2);
        JLabel toCrawlLabel1 = new JLabel("URLs to Crawl:");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(toCrawlLabel1, constraints);
        searchPanel.add(toCrawlLabel1);
        toCrawlLabel2 = new JLabel();
        toCrawlLabel2.setFont(toCrawlLabel2.getFont().deriveFont(Font.PLAIN));
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 0, 5);
        layout.setConstraints(toCrawlLabel2, constraints);
        searchPanel.add(toCrawlLabel2);
        JLabel progressLabel = new JLabel("Crawling Progress:");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(progressLabel, constraints);
        searchPanel.add(progressLabel);
        progressBar = new JProgressBar();
        progressBar.setMinimum(0);
        progressBar.setStringPainted(true);
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 0, 5);
        layout.setConstraints(progressBar, constraints);
        searchPanel.add(progressBar);
        JLabel matchesLabel1 = new JLabel("Search Matches:");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 10, 0);
        layout.setConstraints(matchesLabel1, constraints);
        searchPanel.add(matchesLabel1);
        matchesLabel2 = new JLabel();
        matchesLabel2.setFont(matchesLabel2.getFont().deriveFont(Font.PLAIN));
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 10, 5);
        layout.setConstraints(matchesLabel2, constraints);
        searchPanel.add(matchesLabel2);
        // Set up matches table.
        table = new JTable(new DefaultTableModel(new Object[][]{}, new String[]{"URL"}) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        // Set up Matches panel.
        JPanel matchesPanel = new JPanel();
        matchesPanel.setBorder(BorderFactory.createTitledBorder("Matches"));
        matchesPanel.setLayout(new BorderLayout());
        matchesPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        // Add panels to display.
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(searchPanel, BorderLayout.NORTH);
        getContentPane().add(matchesPanel, BorderLayout.CENTER);
    }

    // Exit this program.
    private void actionExit() {
        System.exit(0);
    }

    // Handle Search/Stop button being clicked.
    private void actionSearch() {
        // If stop button clicked, turn crawling flag off.
        if (crawling) {
            crawling = false;
            return;
        }
        ArrayList errorList = new ArrayList();
        // Validate that start URL has been entered.
        String startUrl = startTextField.getText().trim();
        if (startUrl.length() < 1) {
            errorList.add("Missing Start URL.");
        }
        // Verify start URL.
        else if (verifyUrl(startUrl) == null) {
            errorList.add("Invalid Start URL.");
        }
        // Validate that Max URLs is either empty or is a number.
        int maxUrls = 0;
        String max = ((String) maxComboBox.getSelectedItem()).trim();
        if (max.length() > 0) {
            try {
                maxUrls = Integer.parseInt(max);
            } catch (NumberFormatException e) {
            }
            if (maxUrls < 1) {
                errorList.add("Invalid Max URLs value.");
            }
        }
        // Validate that matches log file has been entered.
        String logFile = logTextField.getText().trim();
        if (logFile.length() < 1) {
            errorList.add("Missing Matches Log File.");
        }
        // Validate that search string has been entered.
        String searchString = searchTextField.getText().trim();
        if (searchString.length() < 1) {
            errorList.add("Missing Search String.");
        }
        // Show errors, if any, and return.
        if (errorList.size() > 0) {
            StringBuffer message = new StringBuffer();
            // Concatenate errors into single message.
            for (int i = 0; i < errorList.size(); i++) {
                message.append(errorList.get(i));
                if (i + 1 < errorList.size()) {
                    message.append("\n");
                }
            }
            showError(message.toString());
            return;
        }
        // Remove "www" from start URL if present.
        startUrl = removeWwwFromUrl(startUrl);
        // Start the Search Crawler.
        search(logFile, startUrl, maxUrls, searchString);
    }

    private void search(
            final String logFile, final String startUrl, final int maxUrls, final String searchString
    ) {
        // Start the search in a new thread.
        Thread thread = new Thread(new Runnable() {
            public void run() {
                // Show hour glass cursor while crawling is under way.
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                // Disable search controls.
                startTextField.setEnabled(false);
                maxComboBox.setEnabled(false);
                limitCheckBox.setEnabled(false);
                logTextField.setEnabled(false);
                searchTextField.setEnabled(false);
                caseCheckBox.setEnabled(false);
                // Switch Search button to "Stop."
                searchButton.setText("Stop");
                // Reset stats.
                table.setModel(new DefaultTableModel(new Object[][]{}, new String[]{"URL"}) {
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                });
                updateStats(startUrl, 0, 0, maxUrls);
                // Open matches log file.
                try {
                    logFileWriter = new PrintWriter(new FileWriter(logFile));
                } catch (Exception e) {
                    showError("Unable to open matches log file.");
                    return;
                }
                // Turn crawling flag on.
                crawling = true;
                // Perform the actual crawling.
                crawl(startUrl, maxUrls, limitCheckBox.isSelected(), searchString, caseCheckBox.isSelected());
                // Turn crawling flag off.
                crawling = false;
                // Close matches log file.
                try {
                    logFileWriter.close();
                } catch (Exception e) {
                    showError("Unable to close matches log file.");
                }
                // Mark search as done.
                crawlingLabel2.setText("Done");
                // Enable search controls.
                startTextField.setEnabled(true);
                maxComboBox.setEnabled(true);
                limitCheckBox.setEnabled(true);
                logTextField.setEnabled(true);
                searchTextField.setEnabled(true);
                caseCheckBox.setEnabled(true);
                // Switch search button back to "Search."
                searchButton.setText("Search");
                // Return to default cursor.
                setCursor(Cursor.getDefaultCursor());
                // Show message if search string not found.
                if (table.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(
                            Application.this,
                            "Your Search String was not found. Please try another.",
                            "Search String Not Found",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            }
        });
        thread.start();
    }

    // Show dialog box with error message.
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Update crawling stats.
    private void updateStats(
            String crawling, int crawled, int toCrawl, int maxUrls
    ) {
        crawlingLabel2.setText(crawling);
        crawledLabel2.setText("" + crawled);
        toCrawlLabel2.setText("" + toCrawl);
        // Update progress bar.
        if (maxUrls == -1) {
            progressBar.setMaximum(crawled + toCrawl);
        } else {
            progressBar.setMaximum(maxUrls);
        }
        progressBar.setValue(crawled);
        matchesLabel2.setText("" + table.getRowCount());
    }

    // Add match to matches table and log file.
    private void addMatch(String url) {
        // Add URL to matches table.
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.addRow(new Object[]{url});
        // Add URL to matches log file.
        try {
            logFileWriter.println(url);
        } catch (Exception e) {
            showError("Unable to log match.");
        }
    }

    // Verify URL format.
    private URL verifyUrl(String url) {
        // Only allow HTTP URLs.
        if (!url.toLowerCase().startsWith("http://")) {
            return null;
        }
        // Verify format of URL.
        URL verifiedUrl = null;
        try {
            verifiedUrl = new URL(url);
        } catch (Exception e) {
            return null;
        }
        return verifiedUrl;
    }

    boolean isRobotAllowed(URL urlToCheck) {
        String host = urlToCheck.getHost().toLowerCase();
        // Retrieve host's disallow list from cache.
        ArrayList disallowList = (ArrayList) disallowListCache.get(host);
        // If list is not in the cache, download and cache it.
        if (disallowList == null) {
            disallowList = new ArrayList();
            try {
                URL robotsFileUrl = new URL("http://" + host + "/robots.txt");
                // Open connection to robot file URL for reading.
                BufferedReader reader = new BufferedReader(new InputStreamReader(robotsFileUrl.openStream()));
                // Read robot file, creating list of disallowed paths.
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.indexOf("Disallow:") == 0) {
                        String disallowPath = line.substring("Disallow:".length());
                        // Check disallow path for comments and remove if present.
                        int commentIndex = disallowPath.indexOf("#");
                        if (commentIndex != -1) {
                            disallowPath = disallowPath.substring(0, commentIndex);
                        }
                        // Remove leading or trailing spaces from disallow path.
                        disallowPath = disallowPath.trim();
                        // Add disallow path to list.
                        disallowList.add(disallowPath);
                    }
                }
                // Add new disallow list to cache.
                disallowListCache.put(host, disallowList);
            } catch (Exception e) {
                /* Assume robot is allowed since an exception
        is thrown if the robot file doesn't exist. */
                return true;
            }
        }
        /* Loop through disallow list to see if
     crawling is allowed for the given URL. */
        String file = urlToCheck.getFile();
        for (int i = 0; i < disallowList.size(); i++) {
            String disallow = (String) disallowList.get(i);
            if (file.startsWith(disallow)) {
                return false;
            }
        }
        return true;
    }

    // Download page at given URL.
    private String downloadPage(URL pageUrl) {
        try {
            // Open connection to URL for reading.
            BufferedReader reader = new BufferedReader(new InputStreamReader(pageUrl.openStream()));
            // Read page into buffer.
            String line;
            StringBuffer pageBuffer = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                pageBuffer.append(line);
            }
            return pageBuffer.toString();
        } catch (Exception e) {
        }
        return null;
    }

    // Remove leading "www" from a URL's host if present.
    private String removeWwwFromUrl(String url) {
        int index = url.indexOf("://www.");
        if (index != -1) {
            return url.substring(0, index + 3) + url.substring(index + 7);
        }
        return (url);
    }

    // Parse through page contents and retrieve links.
    private ArrayList retrieveLinks(
            URL pageUrl, String pageContents, HashSet crawledList, boolean limitHost
    ) {
        // Compile link matching pattern.
        Pattern p = Pattern.compile("<a\\s+href\\s*=\\s*\"?(.*?)[\"|>]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(pageContents);
        // Create list of link matches.
        ArrayList linkList = new ArrayList();
        while (m.find()) {
            String link = m.group(1).trim();
            // Skip empty links.
            if (link.length() < 1) {
                continue;
            }
            // Skip links that are just page anchors.
            if (link.charAt(0) == '#') {
                continue;
            }
            // Skip mailto links.
            if (link.indexOf("mailto:") != -1) {
                continue;
            }
            // Skip JavaScript links.
            if (link.toLowerCase().indexOf("javascript") != -1) {
                continue;
            }
            // Prefix absolute and relative URLs if necessary.
            if (link.indexOf("://") == -1) {
                // Handle absolute URLs.
                if (link.charAt(0) == '/') {
                    link = "http://" + pageUrl.getHost() + link;
                    // Handle relative URLs.
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
            // Remove anchors from link.
            int index = link.indexOf('#');
            if (index != -1) {
                link = link.substring(0, index);
            }
            // Remove leading "www" from URL's host if present.
            link = removeWwwFromUrl(link);
            // Verify link and skip if invalid.
            URL verifiedLink = verifyUrl(link);
            if (verifiedLink == null) {
                continue;
            }
            /* If specified, limit links to those
    having the same host as the start URL. */
            if (limitHost && !pageUrl.getHost().toLowerCase().equals(verifiedLink.getHost().toLowerCase())) {
                continue;
            }
            // Skip link if it has already been crawled.
            if (crawledList.contains(link)) {
                continue;
            }
            // Add link to list.
            linkList.add(link);
        }
        return (linkList);
    }

    /* Determine whether or not search string is
   matched in the given page contents. */
    private boolean searchStringMatches(
            String pageContents, String searchString, boolean caseSensitive
    ) {
        String searchContents = pageContents;
        /* If case-sensitive search, lowercase
    page contents for comparison. */
        if (!caseSensitive) {
            searchContents = pageContents.toLowerCase();
        }
        // Split search string into individual terms.
        Pattern p = Pattern.compile("[\\s]+");
        String[] terms = p.split(searchString);
        // Check to see if each term matches.
        for (int i = 0; i < terms.length; i++) {
            if (caseSensitive) {
                if (searchContents.indexOf(terms[i]) == -1) {
                    return false;
                }
            } else {
                if (searchContents.indexOf(terms[i].toLowerCase()) == -1) {
                    return false;
                }
            }
        }
        return true;
    }

    // Perform the actual crawling, searching for the search string.
    public void crawl(
            String startUrl, int maxUrls, boolean limitHost, String searchString, boolean caseSensitive
    ) {
        // Set up crawl lists.
        HashSet crawledList = new HashSet();
        LinkedHashSet toCrawlList = new LinkedHashSet();
        // Add start URL to the to crawl list.
        toCrawlList.add(startUrl);
        /* Perform actual crawling by looping
   through the To Crawl list. */
        while (crawling && toCrawlList.size() > 0) {
            /* Check to see if the max URL count has
       been reached, if it was specified.*/
            if (maxUrls != -1) {
                if (crawledList.size() == maxUrls) {
                    break;
                }
            }
            // Get URL at bottom of the list.
            String url = (String) toCrawlList.iterator().next();
            // Remove URL from the To Crawl list.
            toCrawlList.remove(url);
            // Convert string url to URL object.
            URL verifiedUrl = verifyUrl(url);
            // Skip URL if robots are not allowed to access it.
            if (!isRobotAllowed(verifiedUrl)) {
                continue;
            }
            // Update crawling stats.
            updateStats(url, crawledList.size(), toCrawlList.size(), maxUrls);
            // Add page to the crawled list.
            crawledList.add(url);
            // Download the page at the given URL.
            String pageContents = downloadPage(verifiedUrl);
            /* If the page was downloaded successfully, retrieve all its
  links and then see if it contains the search string. */
            if (pageContents != null && pageContents.length() > 0) {
                // Retrieve list of valid links from page.
                ArrayList links = retrieveLinks(verifiedUrl, pageContents, crawledList, limitHost);
                // Add links to the To Crawl list.
                toCrawlList.addAll(links);
                /* Check if search string is present in
         page, and if so, record a match. */
                if (searchStringMatches(pageContents, searchString, caseSensitive)) {
                    addMatch(url);
                }
            }
            // Update crawling stats.
            updateStats(url, crawledList.size(), toCrawlList.size(), maxUrls);
        }
    }

    // Run the Search Crawler.
    public static void main(String[] args) {
        Application crawler = new Application();
        crawler.show();
    }
}
