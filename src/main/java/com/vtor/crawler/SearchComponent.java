package com.vtor.crawler;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Optional;

final class SearchComponent {

    private static final String[] MAX_URLS = {"10", "50", "100", "500", "1000"};
    private final ActionListener   actionListener;
    private final MatchesComponent matchesComponent;
    private       JPanel           searchPanel;
    private       GridBagLayout    layout;
    private       JTextField       startTextField;
    private       JComboBox        maxComboBox;
    private       JCheckBox        limitCheckBox;
    private       JTextField       logTextField;
    private       JTextField       searchTextField;
    private       JCheckBox        caseCheckBox;
    private       JButton          searchButton;
    private       JLabel           crawlingLabel2;
    private       JLabel           crawledLabel2;
    private       JLabel           toCrawlLabel2;
    private       JProgressBar     progressBar;
    private       JLabel           matchesLabel2;

    SearchComponent(ActionListener actionListener, MatchesComponent matchesComponent) {
        this.actionListener = actionListener;
        this.matchesComponent = matchesComponent;
        buildSearchPanel();
    }

    private JPanel buildSearchPanel() {
        searchPanel = new JPanel();
        layout = new GridBagLayout();
        searchPanel.setLayout(layout);

        buildStartUrlField();
        buildMaxUrlsField();
        buildLimitToHostField();
        buildBlankLabel();
        buildLogFileField();
        buildSearchTextField();
        buildSearchButtonField();
        buildCaseSensitivityField();
        buildSeparatorField();
        buildCrawlingLable();
        buildCrawledLabel();
        buildToCrawlLabel();
        buildProgressBar();
        buildMatchesLabel();

        return searchPanel;
    }

    private void buildStartUrlField() {
        JLabel startLabel = new JLabel("URL to start:");
        GridBagConstraints constraints = new GridBagConstraints();
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
    }

    private void buildMaxUrlsField() {
        JLabel maxLabel = new JLabel("Max URLs:");
        GridBagConstraints constraints = new GridBagConstraints();
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
    }

    private void buildLimitToHostField() {
        limitCheckBox = new JCheckBox("Limit to host");
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 10, 0, 0);
        layout.setConstraints(limitCheckBox, constraints);

        searchPanel.add(limitCheckBox);
    }

    private void buildBlankLabel() {
        JLabel blankLabel = new JLabel();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(blankLabel, constraints);

        searchPanel.add(blankLabel);
    }

    private void buildLogFileField() {
        JLabel logLabel = new JLabel("Log File:");
        GridBagConstraints constraints = new GridBagConstraints();
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
    }

    private void buildSearchTextField() {
        JLabel searchLabel = new JLabel("Search Text:");
        GridBagConstraints constraints = new GridBagConstraints();
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
    }

    private void buildSearchButtonField() {
        searchButton = new JButton("Search");
        searchButton.addActionListener(actionListener);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 5, 5);
        layout.setConstraints(searchButton, constraints);

        searchPanel.add(searchButton);
    }

    private void buildCaseSensitivityField() {
        caseCheckBox = new JCheckBox("Case Sensitive");
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 0, 5);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(caseCheckBox, constraints);

        searchPanel.add(caseCheckBox);
    }

    private void buildSeparatorField() {
        JSeparator separator = new JSeparator();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 5, 5);
        layout.setConstraints(separator, constraints);

        searchPanel.add(separator);
    }

    private void buildCrawlingLable() {
        JLabel crawlingLabel1 = new JLabel("Status:");
        GridBagConstraints constraints = new GridBagConstraints();
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
    }

    private void buildCrawledLabel() {
        JLabel crawledLabel1 = new JLabel("Crawled URLs:");
        GridBagConstraints constraints = new GridBagConstraints();
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
    }

    private void buildToCrawlLabel() {
        JLabel toCrawlLabel1 = new JLabel("URLs to Crawl:");
        GridBagConstraints constraints = new GridBagConstraints();
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
    }

    private void buildProgressBar() {
        JLabel progressLabel = new JLabel("Progress:");
        GridBagConstraints constraints = new GridBagConstraints();
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
    }

    private void buildMatchesLabel() {
        JLabel matchesLabel1 = new JLabel("Matches count:");
        GridBagConstraints constraints = new GridBagConstraints();
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
    }

    JPanel getSearchPanel() {
        return searchPanel;
    }

    String startUrlWithoutWww() {
        String startUrl = startTextField.getText().trim();
        UrlHolder urlHolder = new UrlHolder(startUrl);
        return urlHolder.urlWithoutWww();
    }

    boolean isStartUrlValid() {
        String startUrl = startTextField.getText().trim();
        return startUrl.length() > 1 && new UrlHolder(startUrl).holdsValidUrl();
    }

    boolean isMaxNumberOfUrlsValid() {
        String max = maxNumberOfUrlsAsString();
        return NumberUtils.isNumber(max) && NumberUtils.toInt(max) >= 1;
    }

    int maxNumberOfUrls() {
        return NumberUtils.toInt(maxNumberOfUrlsAsString());
    }

    String maxNumberOfUrlsAsString() {
        return ((String) maxComboBox.getSelectedItem()).trim();
    }

    boolean isSearchLimitedToHost() {
        return limitCheckBox.isSelected();
    }

    String logFile() {
        return logTextField.getText().trim();
    }

    boolean isLogFileValid() {
        return StringUtils.isNotBlank(logFile());
    }

    boolean isSearchTextValid() {
        return StringUtils.isNotBlank(searchText());
    }

    String searchText() {
        return searchTextField.getText().trim();
    }

    boolean isCaseSensitiveSearch() {
        return caseCheckBox.isSelected();
    }

    Optional<String> validateAndReturnErrorMessage() {
        java.util.List<String> errors = new ArrayList<>();

        if (!isStartUrlValid()) {
            errors.add("Invalid start URL.\n");
        }

        if (!isMaxNumberOfUrlsValid()) {
            errors.add("Invalid Max URLs value.\n");
        }

        if (!isLogFileValid()) {
            errors.add("Missing Matches Log File.\n");
        }

        if (!isSearchTextValid()) {
            errors.add("Missing Search String.\n");
        }

        if (errors.isEmpty()) {
            return Optional.empty();
        }

        StringBuilder builder = new StringBuilder();
        errors.stream().forEach(builder::append);

        return Optional.of(builder.toString());
    }

    void switchToSearchMode() {
        startTextField.setEnabled(false);
        maxComboBox.setEnabled(false);
        limitCheckBox.setEnabled(false);
        logTextField.setEnabled(false);
        searchTextField.setEnabled(false);
        caseCheckBox.setEnabled(false);
        searchButton.setText("Stop");
    }

    void reset() {
        startTextField.setEnabled(true);
        maxComboBox.setEnabled(true);
        limitCheckBox.setEnabled(true);
        logTextField.setEnabled(true);
        searchTextField.setEnabled(true);
        caseCheckBox.setEnabled(true);
        searchButton.setText("Search");
        crawlingLabel2.setText("Done");
    }

    void updateStats(String crawling, int crawled, int toCrawl) {
        crawlingLabel2.setText(crawling);
        crawledLabel2.setText("" + crawled);
        toCrawlLabel2.setText("" + toCrawl);
        if (maxNumberOfUrls() == -1) {
            progressBar.setMaximum(crawled + toCrawl);
        } else {
            progressBar.setMaximum(maxNumberOfUrls());
        }
        progressBar.setValue(crawled);
        matchesLabel2.setText("" + matchesComponent.getTable().getRowCount());
    }

}
