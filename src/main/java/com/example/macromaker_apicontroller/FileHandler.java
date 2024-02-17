package com.example.macromaker_apicontroller;
/**
 * @author Anthony Diorio
 * @version 1.1
 * @since 12/19/2023
 * */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class FileHandler {
    private final File pathfinder;

    public FileHandler(String saveFileFolderName) {
        File file = new File("");
        pathfinder = new File(file.getAbsolutePath() + "\\" + saveFileFolderName);
        if (!pathfinder.isDirectory()) {
            System.out.println("*** Cannot locate save-file folder ***");
            try {
                Files.createDirectories(Paths.get(pathfinder.getAbsolutePath()));
                System.out.println("[Save-File Folder Created]-> " + pathfinder.getAbsolutePath());
            } catch (IOException e) {
                System.out.println("*An error occurred trying to create a save-file folder at location: " + pathfinder.getAbsolutePath());
            }
        }
        else
            System.out.println("*** Save-File Folder Located ***");
    }


    public String getRootPath() {
        return pathfinder.getAbsolutePath();
    }


    public boolean fileExists(String filename) {
        File file = createPath(filename);
        return file.exists();
    }


    private File createPath(String filename) {
        String filePath = pathfinder.getAbsolutePath() + '\\' + filename + ".txt";
        return new File(filePath);
    }


    public String getFileData(String filename) {
        Scanner fileScanner;
        File file = createPath(filename);
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
            return "*** File not found ***\n";
        }
        return fileData.toString();
    }


    public void createNewFile(String filename) {
        try {
            File newFile = createPath(filename);
            if (newFile.createNewFile())
                System.out.printf("*** [File created: %s] ***%n", newFile.getName());
            else
                System.out.println("*** File already exists ***\n");
        } catch (IOException e) {
            System.out.println("*** A file creation error occurred ***\n");
        }
    }


    public void writeToFile(String filename, String fileData) {
        // Create file pathway
        File file = createPath(filename);

        // if the file doesn't exist, create it
        if (!file.exists())
            createNewFile(filename);

        // Write the text to the file
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(String.valueOf(fileData));
            fileWriter.close();
            System.out.println("*** Successfully wrote to the file ***");
        } catch (IOException e) {
            System.out.println("*** A file write error occurred ***");
        }
    }


    public void deleteFile(String filename) {
        File file = createPath(filename);
        if (file.delete())
            System.out.println("*** File deleted successfully ***");
        else
            System.out.println("*** Failed to delete the file ***");
    }


    public String listOfAllStorageFiles() {
        String filePath = pathfinder.getAbsolutePath();
        File folder = new File(filePath);
        File[] listOfFiles = folder.listFiles();
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




}
