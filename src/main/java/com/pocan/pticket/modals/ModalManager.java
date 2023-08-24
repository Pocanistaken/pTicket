package com.pocan.pticket.modals;
// https://apps.timwhitlock.info/emoji/tables/unicode
import com.pocan.pticket.builders.EmbedBuilder;
import com.pocan.pticket.checkers.EmailChecker;
import com.pocan.pticket.checkers.GameChecker;
import com.pocan.pticket.database.DatabaseOperation;
import com.pocan.pticket.managers.ChannelManager;
import com.pocan.pticket.managers.TicketManager;
import com.pocan.pticket.pTicket;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.TimeFormat;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.nio.channels.Channel;
import java.sql.Timestamp;

public class ModalManager extends ListenerAdapter {


    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        String modalID = event.getModalId();
        if (modalID.equals("Game Support")) {
            String gameInput = (String) event.getValue("gamesupport-input-game").getAsString();
            String problemInput = (String) event.getValue("gamesupport-input-problem").getAsString();
            String emailInput = (String) event.getValue("gamesupport-input-email").getAsString();
            if (GameChecker.isValidGame(gameInput)) {
                if (EmailChecker.isValidEmail(emailInput)) {
                    DatabaseOperation databaseOperation = new DatabaseOperation();
                    ChannelManager.createTicket(event.getMember(), event.getGuild(), pTicket.getInstance().getConfig().get("SupportCategoryID"), gameInput + "-" + (databaseOperation.getCurrentTicketID() + 1), problemInput, emailInput);
                    databaseOperation.setCurrentTicketID(databaseOperation.getCurrentTicketID() + 1);
                    event.deferEdit().queue();

                }
                else {
                    event.reply("E-post adresi geçersiz.").setEphemeral(true).queue();
                }
            }
            else {
                event.reply("Belirtilen oyun sistem üzerinde bulunamadı.\n\n**Desteklenen oyunlar**\n\n- minecraft" +
                        "\n- mcpe\n- unturned\n- gmod\n- mta\n- fivem\n- sclp\n- scp\n- vrising" +
                        "\n- ark\n- dcbot").setEphemeral(true).queue();
            }
        }
        if (modalID.equals("Technical Support")) {
            String problemInput = (String) event.getValue("technicalsupport-input-problem").getAsString();
            String emailInput = (String) event.getValue("technicalsupport-input-email").getAsString();
            if (EmailChecker.isValidEmail(emailInput)) {
                DatabaseOperation databaseOperation = new DatabaseOperation();
                ChannelManager.createTicket(event.getMember(), event.getGuild(), pTicket.getInstance().getConfig().get("SupportCategoryID"), "technical" + "-" + (databaseOperation.getCurrentTicketID() + 1), problemInput, emailInput);
                databaseOperation.setCurrentTicketID(databaseOperation.getCurrentTicketID() + 1);
                event.deferEdit().queue();
            }
            else {
                event.reply("E-post adresi geçersiz.").setEphemeral(true).queue();
            }
        }
        if (modalID.equals("Close Ticket Reason")) {
            event.deferEdit().queue();
            TicketManager.closeTicket(event.getJDA(), event.getTextChannel(), event.getMember());
            DatabaseOperation databaseOperation = new DatabaseOperation();
            String ticketCloseReason = event.getValue("ticketclose-reason").getAsString();
            Timestamp ticketCreateDate = databaseOperation.getTicketCreateDate(event.getChannel().getId());
            databaseOperation.setTicketClosedMember(event.getTextChannel().getId(), event.getMember().getId());
            databaseOperation.setTicketCloseReason(event.getTextChannel().getId(), ticketCloseReason);
            ChannelManager.sendPrivateMessage(event.getTextChannel(), event.getJDA().getUserById(databaseOperation.getTicketCreaterID(event.getChannel().getId())), EmbedBuilder.getTicketClosedPrivateEmbedMessage(event.getJDA(), event.getChannel(), event.getMember(), ticketCloseReason).build());
            EmbedBuilder.sendTicketClosedEmbedMessage(event.getTextChannel(), event.getMember());
        }



    }
}
