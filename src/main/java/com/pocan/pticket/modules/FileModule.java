package com.pocan.pticket.modules;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileModule {


    public static String getClassLocation() {
        String directory = null;
        try {
            directory = (new File(FileModule.class.getProtectionDomain().getCodeSource().getLocation().toURI())).getParentFile().getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return directory;
    }

    public static File getTicketTXTLogFile(String ticketID) {
        String directory = getClassLocation();
        File txtFile = new File(directory + "//pTicket//ticket-logs//" + ticketID + "//log.txt");
        return txtFile;
    }

    public static void createFolder(String ticketID) {
        try {
            String directory = (new File(FileModule.class.getProtectionDomain().getCodeSource().getLocation().toURI())).getParentFile().getPath();
            File txtFile = new File(directory + "//pTicket//ticket-logs//" + ticketID);
            if (txtFile.exists()) {
            } else if (txtFile.mkdirs()) {
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }


    public static void createFile(String ticketID) {
        try {
            String directory = (new File(FileModule.class.getProtectionDomain().getCodeSource().getLocation().toURI())).getParentFile().getPath();
            File txtFile = new File(directory + "//pTicket//ticket-logs//" + ticketID + "//log.txt");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(String ticketID, String message) {
        try {
            String directory = (new File(FileModule.class.getProtectionDomain().getCodeSource().getLocation().toURI())).getParentFile().getPath();
            File txtFile = new File(directory + "//pTicket//ticket-logs//" + ticketID + "//log.txt");
            FileWriter fileWriter = new FileWriter(txtFile, true);
            if (txtFile.length() != 0) {
                fileWriter.write("\n" + message);
            }
            else {
                fileWriter.write(message);
            }
            fileWriter.close();
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }

    }



}
