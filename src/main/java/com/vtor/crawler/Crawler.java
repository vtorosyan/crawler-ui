package com.vtor.crawler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import static java.util.Collections.singletonList;

class Crawler extends JFrame {
    private static final Pattern SEARCH_TERM_PATTERN = Pattern.compile("[\\s]+");

    private boolean          crawlingIsInProgress;
    private SearchComponent  searchComponent;
    private MatchesComponent matchesComponent;

    Crawler() {
        buildMainWindow();
        buildMenu();
        buildPanels();
    }

    private void buildMainWindow() {
        setTitle("Crawler UI");
        setSize(700, 700);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
    }

    private void buildMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        exitMenuItem.addActionListener(e -> exit());
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    private void buildPanels() {
        getContentPane().setLayout(new BorderLayout());
        matchesComponent = new MatchesComponent();
        searchComponent = new SearchComponent(e -> search(), matchesComponent);

        getContentPane().add(searchComponent.getSearchPanel(), BorderLayout.NORTH);
        getContentPane().add(matchesComponent.getPanel(), BorderLayout.CENTER);
    }

    private void exit() {
        System.exit(0);
    }

    private void search() {

        if (crawlingIsInProgress) {
            crawlingIsInProgress = false;
            return;
        }

        Optional<String> error = searchComponent.validateAndReturnErrorMessage();
        if (error.isPresent()) {
            showError(error.get());
            return;
        }

        start();
    }

    private void start() {
        new Thread(() -> {
            reset();
            process();
        }).start();
    }

    private void process() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(searchComponent.logFile()))) {
            crawlingIsInProgress = true;
            crawl(writer);
            crawlingIsInProgress = false;

            searchComponent.reset();
            setCursor(Cursor.getDefaultCursor());

            showNoSearchResultDialogIfNecessary();
        } catch (IOException e) {
            showError("Unable to read log file.");
        }
    }

    private void reset() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        searchComponent.switchToSearchMode();
        matchesComponent.getTable().setModel(new DefaultTableModel(new Object[][]{}, new String[]{"URL"}) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        searchComponent.updateStats(searchComponent.startUrlWithoutWww(), 0, 0);
    }

    private void showNoSearchResultDialogIfNecessary() {
        if (matchesComponent.getTable().getRowCount() == 0) {
            JOptionPane.showMessageDialog(Crawler.this, "Search text was not found.",
                                          "Search text not found", JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Something went wrong", JOptionPane.ERROR_MESSAGE);
    }

    private void crawl(PrintWriter writer) {
        Set<String> processed = new HashSet<>();
        Set<String> notProcessed = new LinkedHashSet<>(singletonList(searchComponent.startUrlWithoutWww()));

        while (crawlingIsInProgress && !notProcessed.isEmpty()) {
            if (processed.size() == searchComponent.maxNumberOfUrls()) {
                break;
            }

            String url = notProcessed.iterator().next();
            notProcessed.remove(url);

            UrlHolder urlHolder = new UrlHolder(url);
            if (!new Robots(urlHolder).isAllowed()) {
                continue;
            }

            processed.add(url);

            Page page = new Page(urlHolder, searchComponent.isSearchLimitedToHost());
            notProcessed.addAll(page.getLinks(processed));

            addResultIfMatches(page, url, writer);
            searchComponent.updateStats(url, processed.size(), notProcessed.size());
        }
    }

    private boolean searchTextMatches(String pageContents) {

        if (!searchComponent.isCaseSensitiveSearch()) {
            pageContents = pageContents.toLowerCase();
        }

        String[] terms = SEARCH_TERM_PATTERN.split(searchComponent.searchText());

        for (String term : terms) {
            boolean doesNotMatchForCaseSensitive =
                    searchComponent.isCaseSensitiveSearch() && !pageContents.contains(term);
            if (doesNotMatchForCaseSensitive || !pageContents.contains(term.toLowerCase())) {
                return false;
            }
        }
        return true;
    }

    private void addResultIfMatches(Page page, String url, PrintWriter writer) {
        if (!searchTextMatches(page.download())) {
            return;
        }

        try {
            matchesComponent.add(url);
            writer.println(url);
        } catch (Exception e) {
            showError("Unable to log found match.");
        }
    }

    public static void main(String[] args) {
        Crawler crawler = new Crawler();
        crawler.setVisible(true);
    }

}
