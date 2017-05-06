package org.dnacronym.hygene.ui.controller;


/**
 * Controller for both storing and retrieving recently opened files.
 */
public class RecentFilesController {
    private static final String APPLICATION_FOLDER_NAME = "hygene";


    /**
     * Returns the base directory for placing application data.
     *
     * @return the full path to a folder in which to store application data.
     */
    private String getAppDataFolderPath() {
        final String operatingSystemName = getOperatingSystemName();

        String baseDirectory;
        if (operatingSystemName.contains("WIN")) {
            baseDirectory = System.getenv("AppData");
        } else {
            // Assume macOS or Linux
            baseDirectory = System.getProperty("user.home");
        }

        return baseDirectory + "/" + APPLICATION_FOLDER_NAME;
    }

    private String getOperatingSystemName() {
        return (System.getProperty("os.name")).toUpperCase();
    }
}
