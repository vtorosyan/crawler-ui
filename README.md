# Crawler UI. Standalone Java application to crawl given URL and search for a text.

Crawler UI is a standalone application written in Java. It uses native JDK libraries and few third-party libraries for utility support. 

### How to use
- As a pre-requisite JDK8 should be installed
- Download/Clone the project
- Go to home directory and run `./gradlew run`

### Supported features
- Simple, easy to use UI
- Search for a text recursively 
- Limit search to the start/given host
- Limit maximum number of URL's to crawl
- Log found URL matches into the given file
- Case sensitive search
- Progress bar during crawling

### GUI

GUI is built using Java Swing with standard cross platform look and feel.

![Start Screen](/static_resources/start_screen.png?raw=true "Start Screen")
![In Progress screen](/static_resources/in_progress_screen.png?raw=true "In Progress screen ")
![Done Screen](/static_resources/finished_screen.png?raw=true "Done Screen")

### Used libraries/tools
- Java Swing
- Apache Commons Validator
- Apache Commons Lang
- Slf4J
- Gradle

