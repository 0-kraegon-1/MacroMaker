/**
 * @author Anthony Diorio
 * @version 1.1
 * @since 12/19/2023
 * */
package com.example.macromaker_apicontroller;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class FileHandler {
    private final File rootSaveFolder;
    private final AppData appData = new AppData();

    public FileHandler() {
        File file = new File("");
        rootSaveFolder = new File(file.getAbsolutePath() + "\\" + appData.getSaveFileTitle());
        if (!rootSaveFolder.isDirectory()) {
            System.out.println("*** Cannot locate save-file folder ***");
            try {
                // Create root save-file directory
                Files.createDirectories(Paths.get(rootSaveFolder.getAbsolutePath()));
                if (!rootSaveFolder.canWrite())
                    rootSaveFolder.setWritable(true);
                System.out.println("[Save-File Directory Created]-> " + rootSaveFolder.getAbsolutePath());

                // Create Keyboard-Macro save-file directory
                Files.createDirectories(Paths.get(getKeyMacroSaveFilePath()));
                if (!Paths.get(getKeyMacroSaveFilePath()).toFile().canWrite())
                    Paths.get(getKeyMacroSaveFilePath()).toFile().setWritable(true);
                System.out.println("[Keyboard Macro Save-File Folder Created]-> " + getKeyMacroSaveFilePath());

                // Create Mouse-Macro save-file directory
                Files.createDirectories(Paths.get(getMouseMacroSaveFilePath()));
                if (!Paths.get(getMouseMacroSaveFilePath()).toFile().canWrite())
                    Paths.get(getMouseMacroSaveFilePath()).toFile().setWritable(true);
                System.out.println("[Mouse Macro Save-File Folder Created]-> " + getMouseMacroSaveFilePath());

            } catch (IOException e) {
                System.out.println("* An error occurred trying to create save-file folders in this directory: " + rootSaveFolder.getAbsolutePath());
            }
        }
    }


    public String loadFile(File file) {
        Scanner fileScanner;
        StringBuilder fileData = new StringBuilder();
        try {
            if (!file.exists())
                throw new FileNotFoundException();
            else {
                fileScanner = new Scanner(file);
                while (fileScanner.hasNextLine()) {
                    fileData.append(fileScanner.nextLine());
                    if (fileScanner.hasNextLine())
                        fileData.append('\n');
                }
                fileScanner.close();
                System.out.println("*** FILE LOADED ***");
            }
        } catch (FileNotFoundException e) {
            return "*** File-Not-Found ***\n";
        }
        return fileData.toString();
    }

    /**               NEW-CODE-ABOVE
                   DEPRECATED-CODE-BELOW         */
    public String getFileData(String filename) {
        Scanner fileScanner;
        File file;
        switch (appData.getActiveApp().name()) {
            case "KEYBOARD_MACRO_EDITOR" -> {
                file = createKeyboardRepeaterSaveFilePath(filename);
            }
            case "MOUSE_MACRO_EDITOR" -> {
                file = createMouseSaveFilePath(filename);
            }
            default -> {
                return "*** ERROR: CANNOT-DISCERN-ACTIVE-APP ***\n";
            }
        }
        StringBuilder fileData = new StringBuilder();
        try {
            if (!file.exists())
                throw new FileNotFoundException();
            else {
                fileScanner = new Scanner(file);
                while (fileScanner.hasNextLine()) {
                    fileData.append(fileScanner.nextLine());
                    fileData.append('\n');
                }
                fileScanner.close();
                System.out.println("*** FILE LOADED ***");
            }
        } catch (FileNotFoundException e) {
            return "*** File-Not-Found ***\n";
        }
        return fileData.toString();
    }

    public void createNewFile(File file) {
        try {
            if (file.createNewFile())
                System.out.printf("*** [File created: %s] ***%n", file.getName());
            else
                System.out.println("*** File already exists ***\n");
        } catch (IOException e) {
            System.out.println("*** A file creation error occurred ***\n");
        }
    }
    /**               NEW-CODE-ABOVE
                  DEPRECATED-CODE-BELOW         */
    public void createNewFile(String filename) {
        try {
            File newFile;
            switch (appData.getActiveApp().name()) {
                case "KEYBOARD_MACRO_EDITOR" -> {
                    newFile = createKeyboardRepeaterSaveFilePath(filename);
                }
                case "MOUSE_MACRO_EDITOR" -> {
                    newFile = createMouseSaveFilePath(filename);
                }
                default -> {
                    return;
                }
            }
            if (newFile.createNewFile())
                System.out.printf("*** [File created: %s] ***%n", newFile.getName());
            else
                System.out.println("*** File already exists ***\n");
        } catch (IOException e) {
            System.out.println("*** A file creation error occurred ***\n");
        }
    }


    public boolean writeToFile(String filename, String fileData) {
        // Create file pathway
        File file;
        switch (AppData.activeApp.name()) {
            case "KEYBOARD_MACRO_EDITOR" -> {
                file = createKeyboardRepeaterSaveFilePath(filename);
            }
            case "MOUSE_MACRO_EDITOR" -> {
                file = createMouseSaveFilePath(filename);
            }
            default -> {
                System.out.println("*** [FATAL-ERROR] NO-ACTIVE-APP-IS-SET ***");       // debug-print
                return false;
            }
        }
        // if the file doesn't exist, create it
        if (!file.exists())
            createNewFile(filename);
        else
            System.out.println("file already exists");
        // Write the text to the file
        try {
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file, false));
            fileWriter.write(fileData);
            fileWriter.close();
            System.out.println("*** Successfully wrote to the file ***");    // notification-print
            return true;
        } catch (IOException e) {
            System.out.println("*** A file write error occurred ***");       // notification-print
            return false;
        }
    }

    public void deleteFile(File file) {
        if (file.delete())
            System.out.println("*** File deleted successfully ***");
        else
            System.out.println("*** Failed to delete the file ***");
    }

    /**               NEW-CODE-ABOVE
                  DEPRECATED-CODE-BELOW         */
    public void deleteFile(String filename) {
        File file;
        switch (appData.getActiveApp().name()) {
            case "KEYBOARD_MACRO_EDITOR" -> {
                file = createKeyboardRepeaterSaveFilePath(filename);
            }
            case "MOUSE_MACRO_EDITOR" -> {
                file = createMouseSaveFilePath(filename);
            }
            default -> {
                return;
            }
        }
        if (file.delete())
            System.out.println("*** File deleted successfully ***");
        else
            System.out.println("*** Failed to delete the file ***");
    }


    public List<File> listOfAllMacroFiles(String macroFolder) {
        String filePath = rootSaveFolder.getAbsolutePath() + "\\" + macroFolder;
        File openedFolder = new File(filePath);
        List<File> listOfFiles = Arrays.stream(Objects.requireNonNull(openedFolder.listFiles())).toList();
        System.out.println("\n*** SAVE FILES GRABBED ***");
        return listOfFiles;
    }
    /**               NEW-CODE-ABOVE
                   DEPRECATED-CODE-BELOW         */
    public String listOfAllStorageFiles(String macroFolder) {
        String filePath = rootSaveFolder.getAbsolutePath() + "\\" + macroFolder;
        File openedFolder = new File(filePath);
        File[] listOfFiles = openedFolder.listFiles();
        System.out.println("\n*** SAVE FILES GRABBED ***");

        assert listOfFiles != null;
        int fileCount = listOfFiles.length;


        StringBuilder files = new StringBuilder();

        for (int i = 0; i < fileCount; i++) {
            if (listOfFiles[i].isDirectory())
                continue;
            int filenameLength = listOfFiles[i].getName().length();
            files.append(listOfFiles[i].getName(), 0, filenameLength - 4);
            if (i < fileCount - 1)
                files.append('\n');
        }

        System.out.println(String.valueOf(files));
        return String.valueOf(files);
    }


    public String getKeyMacroSaveFilePath() {
        return rootSaveFolder.getAbsolutePath() + "\\" + appData.getKeyMacroSaveFileTitle();
    }

    public String getMouseMacroSaveFilePath() {
        return rootSaveFolder.getAbsolutePath() + "\\" + appData.getMouseMacroSaveFileTitle();
    }


    public boolean fileExists(String filename) {
        File file;
        switch (appData.getActiveApp().name()) {
            case "KEYBOARD_MACRO_EDITOR" -> {
                file = createKeyboardRepeaterSaveFilePath(filename);
            }
            case "MOUSE_MACRO_EDITOR" -> {
                file = createMouseSaveFilePath(filename);
            }
            default -> {
                return false;
            }
        }
        return file.exists();
    }


    private File createRootSaveFilePath(String filename) {
        String filePath = rootSaveFolder.getAbsolutePath() + '\\' + filename + ".txt";
        return new File(filePath);
    }

    private File createMouseSaveFilePath(String filename) {
        String filePath = getMouseMacroSaveFilePath() + '\\' + filename + ".txt";
        return new File(filePath);
    }

    private File createKeyboardRepeaterSaveFilePath(String filename) {
        String filePath = getKeyMacroSaveFilePath() + '\\' + filename + ".txt";
        return new File(filePath);
    }


}
