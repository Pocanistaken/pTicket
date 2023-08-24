package com.pocan.pticket.managers;

import com.pocan.pticket.database.DatabaseOperation;
import com.pocan.pticket.modules.FileModule;
import com.pocan.pticket.pTicket;
import com.pocan.pticket.services.mail.MailService;
import com.pocan.pticket.transcripts.DiscordHtmlTranscripts;
import com.pocan.pticket.transcripts.TranscriptManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.managers.channel.concrete.TextChannelManager;
import net.dv8tion.jda.api.utils.TimeUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class TicketManager {

    public static void closeTicket(JDA jda, TextChannel channel, Member member) {
        DatabaseOperation databaseOperation = new DatabaseOperation();
        PermissionManager permissionManager = new PermissionManager();
        ArrayList<String> list = databaseOperation.getTicketAddedMembers(channel.getId());
        TextChannelManager channelManager = channel.getManager();
        for (int i = 0; i < list.size(); i++) {
            channelManager.putMemberPermissionOverride(Long.parseLong(list.get(i)), null, permissionManager.getTicketEveryoneDenyPermissions());
        }
        channelManager.queue();
        TranscriptManager transcriptManager = new TranscriptManager(channel);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    databaseOperation.addTranscriptHTMLToDatabase(channel.getId(), transcriptManager.getTranscriptHTML());
                    ChannelManager.sendPrivateTranscriptLog(jda.getUserById(databaseOperation.getTicketCreaterID(channel.getId())), TranscriptManager.getTranscriptFile(transcriptManager.getTranscriptHTML()), channel.getName());
                    //MailService.sendMail(databaseOperation.getTicketOwnerEmail(channel.getId()), channel.getName().substring(channel.getName().lastIndexOf("-") + 1));
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String ticketID = channel.getName().substring(channel.getName().lastIndexOf("-") + 1);
                    ChannelManager.sendPrivateTXTLog(jda.getUserById(databaseOperation.getTicketCreaterID(channel.getId())), FileModule.getTicketTXTLogFile(ticketID), channel.getName());
                    FileReader reader = new FileReader(FileModule.getTicketTXTLogFile(ticketID));
                    databaseOperation.addTicketLogTXTToDatabase(channel.getId(), reader);

                    Thread.sleep(1000);
                } catch (InterruptedException | FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        /*
        Made here with multi-threaded beacuse of main thread get freeze like 1 seconds
        - Pocan
         */
        thread.start();
        thread2.start();
        String logger = "[" + pTicket.getSimpleDateFormat("HH:mm:ss") + " INFO]: " + channel.getName() + " numaralı destek talebi " + member.getEffectiveName() + " tarafından kapatıldı.";
        pTicket.sendLogToConsole(logger);
        ChannelManager.sendTicketClosedLog(member, channel, channel.getGuild());
    }
    public static void reActiveTicket(TextChannel channel, Member member) {
        DatabaseOperation databaseOperation = new DatabaseOperation();
        PermissionManager permissionManager = new PermissionManager();
        ArrayList<String> list = databaseOperation.getTicketAddedMembers(channel.getId());
        TextChannelManager channelManager = channel.getManager();
        for (int i = 0; i < list.size(); i++) {
            channelManager.putMemberPermissionOverride(Long.parseLong(list.get(i)), permissionManager.getTicketMemberAllowedPermissions(), null);
        }
        channelManager.queue();
        String logger = "[" + pTicket.getSimpleDateFormat("HH:mm:ss") + " INFO]: " + channel.getName() + " numaralı destek talebi " + member.getEffectiveName() + " tarafından yeniden aktifleştirildi.";
        pTicket.sendLogToConsole(logger);
        ChannelManager.sendTicketReactiveLog(member, channel, channel.getGuild());
    }

    public static void deleteTicket(TextChannel channel, Member member) {
        DatabaseOperation databaseOperation = new DatabaseOperation();
        PermissionManager permissionManager = new PermissionManager();
        ArrayList<String> list = databaseOperation.getTicketAddedMembers(channel.getId());
        for (int i = 0; i < list.size(); i++) {
            databaseOperation.deleteAddedTicketMembers(channel.getId(), list.get(i));
        }
        channel.delete().queueAfter(5, TimeUnit.SECONDS);
        String logger = "[" + pTicket.getSimpleDateFormat("HH:mm:ss") + " INFO]: " + channel.getName() + " numaralı destek talebi " + member.getEffectiveName() + " tarafından silindi.";
        pTicket.sendLogToConsole(logger);
        ChannelManager.sendTicketDeleteLog(member, channel, channel.getGuild());
    }



    public static void deleteChannelManuel(JDA jda, Member member, String ticketID, String channelID, String channelName) {
        DatabaseOperation databaseOperation = new DatabaseOperation();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //String ticketID = channel.getName().substring(channel.getName().lastIndexOf("-") + 1);
                    ChannelManager.sendPrivateTXTLog(jda.getUserById(databaseOperation.getTicketCreaterID(channelID)), FileModule.getTicketTXTLogFile(ticketID), channelName);
                    FileReader reader = new FileReader(FileModule.getTicketTXTLogFile(ticketID));
                    databaseOperation.addTicketLogTXTToDatabase(channelID, reader);
                    Thread.sleep(1000);
                } catch (InterruptedException | FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<String> list = databaseOperation.getTicketAddedMembers(channelID);
                    for (int i = 0; i < list.size(); i++) {
                        databaseOperation.deleteAddedTicketMembers(channelID, list.get(i));
                    }
                    databaseOperation.setTicketClosedMember(channelID, member.getId());
                    databaseOperation.setTicketCloseReason(channelID, "Sağ tık.");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }


}
