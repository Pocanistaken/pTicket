package com.pocan.pticket.transcripts;

import net.dv8tion.jda.api.entities.TextChannel;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/*
    Made it multi-threaded beacuse when generating a new transcript there is a like 0.5-.0.8 seconds thread froze
    - Pocan
 */

public class TranscriptManager {
    private String ticketID;
    private TextChannel discordChannel;

    public TranscriptManager(String ticketID, TextChannel discordChannel) {
        this.ticketID = ticketID;
        this.discordChannel = discordChannel;
    }

    public TranscriptManager(TextChannel discordChannel) {
        this.discordChannel = discordChannel;
    }


    public String getTranscriptHTML() {
        DiscordHtmlTranscripts transcript = DiscordHtmlTranscripts.getInstance();
        String html = null;
        try {
            html = transcript.createTranscript(discordChannel);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return html;
    }

    public void writeTranscript(String transcriptHTML) {
        try {
            Path path = Paths.get("./pTicket/ticket-logs/" + ticketID + "/");
            Files.createDirectories(path);
            File file = new File(String.valueOf(path) + "/transcript.html");
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(transcriptHTML);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static File getTranscriptFile(String transcriptHTML) {
        File tempFile = null;
        try {
            File directoryPath = new File("./pTicket/ticket-logs/");
            tempFile = File.createTempFile("talep-geçmişi", ".html", directoryPath);
            FileWriter fileWriter = new FileWriter(tempFile);
            fileWriter.write(transcriptHTML);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFile;
    }

}
