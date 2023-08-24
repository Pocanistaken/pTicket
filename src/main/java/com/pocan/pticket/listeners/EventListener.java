package com.pocan.pticket.listeners;

import com.pocan.pticket.managers.ChannelManager;
import com.pocan.pticket.managers.TicketManager;
import com.pocan.pticket.modules.FileModule;
import com.pocan.pticket.pTicket;
import com.pocan.pticket.transcripts.TranscriptManager;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.StatusChangeEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EventListener extends ListenerAdapter {


    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getChannelType() == ChannelType.TEXT) { // Fix java.lang.IllegalStateException
            if (event.getTextChannel().getParentCategoryId().equals(pTicket.getInstance().getConfig().get("SupportCategoryID")) || event.getTextChannel().getParentCategoryId().equals(pTicket.getInstance().getConfig().get("BeklemeAlaniCategoryID")) || event.getTextChannel().getParentCategoryId().equals(pTicket.getInstance().getConfig().get("BeklemeAlani2CategoryID")) || event.getTextChannel().getParentCategoryId().equals(pTicket.getInstance().getConfig().get("McpeAlaniCategoryID"))) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String logFormat = "[" + pTicket.getSimpleDateFormat("dd.MM.yyyy HH:mm:ss") + " MESAJ]: " + event.getMessage();
                            String ticketID = event.getChannel().getName().substring(event.getChannel().getName().lastIndexOf("-") + 1);
                            FileModule.createFolder(ticketID);
                            FileModule.createFile(ticketID);
                            FileModule.writeFile(ticketID, logFormat);
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start(); // This is a text.
            }
        }
    }





    /*
    @Override
    public void onUserUpdateOnlineStatus(@NotNull UserUpdateOnlineStatusEvent event) {
        List <Member> members = event.getGuild().getMembers();
        int onlinemember = 0;
        for (Member member : members) {
            if (member.getOnlineStatus() == OnlineStatus.ONLINE) {
                onlinemember++;
            }
        }
        User user = event.getUser();
        String message = user.getAsTag() + " changed their status to " + event.getNewOnlineStatus().getKey() + " and there is online " + onlinemember;
        System.out.println(message);
        event.getGuild().getDefaultChannel().sendMessage(message).queue();
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        User user = event.getUser();
        String emoji = event.getReaction().getReactionEmote().getAsReactionCode();
        String channelMention = event.getChannel().getAsMention();
        String jumpLink = event.getJumpUrl();
        String message = user.getAsTag() + " reacted the message with " + emoji + " emoji in the channel " + channelMention;
        event.getGuild().getDefaultChannel().sendMessage(message).queue();
    }

     */
}
