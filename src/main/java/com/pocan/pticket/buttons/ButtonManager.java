package com.pocan.pticket.buttons;

import com.pocan.pticket.builders.ButtonBuilder;
import com.pocan.pticket.builders.EmbedBuilder;
import com.pocan.pticket.builders.TextInputBuilder;
import com.pocan.pticket.checkers.SupportTeamChecker;
import com.pocan.pticket.database.DatabaseOperation;
import com.pocan.pticket.managers.ChannelManager;
import com.pocan.pticket.managers.TicketManager;
import com.pocan.pticket.pTicket;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ButtonManager extends ListenerAdapter {

    /*
    İlk defa yetkili kayıt edildiğinde destek taleplerini kapatırken
    Modal oluşturup sebebini girip girmeme ile alakalı olan bir değişken defaultCloseTicketWithoutReasonStatus
    0 olursa girilmez
    1 olursa girilir.
     */
    public static final int defaultCloseTicketWithoutReasonStatus = 0;

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String buttonID = event.getButton().getId();

        if (buttonID.equals("ticket-close")) {
            event.deferEdit().queue();
            String channelID = event.getChannel().getId();
            String embedDescription = event.getMember().getAsMention() + " talebi sonlandırmak istediğinden emin misiniz?";
            EmbedBuilder closeConfirmEmbed = new com.pocan.pticket.builders.EmbedBuilder("**SAGANETWORK TELECOM. INC. | DESTEK SİSTEMİ**", embedDescription, new Color(0,255,255), "Saganetwork Telecom. Inc. - Tüm hakları saklıdır.");
            ButtonBuilder closeButton = new ButtonBuilder(ButtonStyle.DANGER, "ticket-close-confirm", "Onayla", "U+1F53A");
            ButtonBuilder cancelButton = new ButtonBuilder(ButtonStyle.SECONDARY, "ticket-close-cancel", "Vazgeç", "U+1F527");
            event.getChannel().sendMessageEmbeds(closeConfirmEmbed.getEmbed().build()).setActionRow(closeButton.getButton(), cancelButton.getButton()).queue(message -> {
                message.getId();
            });
        }
        if (buttonID.equals("ticket-close-cancel")) {
            event.deferEdit().queue();
            event.getChannel().deleteMessageById(event.getMessageId()).queue();
        }
        if (buttonID.equals("ticket-close-confirm")) {
            event.getChannel().deleteMessageById(event.getMessageId()).queue();
            DatabaseOperation databaseOperation = new DatabaseOperation();
            if (SupportTeamChecker.isMemberContainsSupportTeam(event.getMember())) {
                event.getChannel().deleteMessageById(event.getMessageId());
                if (databaseOperation.isDiscordAccountExistsInStaffDatabase(event.getMember().getId())) {
                    if (databaseOperation.getCloseTicketWithoutReasonStatus(event.getMember().getId())) {
                        TextInputBuilder closeReason = new TextInputBuilder("ticketclose-reason","Sonlandırma sebebini giriniz.", TextInputStyle.PARAGRAPH, "Örnek: Sorun çözüldü.", true, 1, 200);
                        Modal modal = Modal.create("Close Ticket Reason", "Talebi Sonlandır")
                                .addActionRow(closeReason.getTextInput())
                                .build();
                        event.replyModal(modal).queue();
                    }
                    else {
                        event.deferEdit().queue();
                        TicketManager.closeTicket(event.getJDA(), event.getTextChannel(), event.getMember());
                        databaseOperation.setTicketClosedMember(event.getTextChannel().getId(), event.getMember().getId());
                        databaseOperation.setTicketCloseReason(event.getTextChannel().getId(), "Belirtilmedi.");
                        EmbedBuilder.sendTicketClosedEmbedMessage(event.getTextChannel(), event.getMember());
                        ChannelManager.sendPrivateMessage(event.getTextChannel(), event.getJDA().getUserById(databaseOperation.getTicketCreaterID(event.getChannel().getId())), EmbedBuilder.getTicketClosedPrivateEmbedMessage(event.getJDA(), event.getChannel(), event.getMember(), "Belirtilmedi.").build());
                    }
                }
                else {
                    databaseOperation.addStaffToDatabase(event.getMember().getId(), event.getMember().getEffectiveName(), defaultCloseTicketWithoutReasonStatus);
                    if (databaseOperation.getCloseTicketWithoutReasonStatus(event.getMember().getId())) {
                        TextInputBuilder closeReason = new TextInputBuilder("ticketclose-reason","Sonlandırma sebebini giriniz.", TextInputStyle.PARAGRAPH, "Örnek: Sorun çözüldü.", true, 1, 200);
                        Modal modal = Modal.create("Close Ticket Reason", "Talebi Sonlandır")
                                .addActionRow(closeReason.getTextInput())
                                .build();
                        event.replyModal(modal).queue();
                    }
                    else {
                        event.deferEdit().queue();
                        TicketManager.closeTicket(event.getJDA(), event.getTextChannel(), event.getMember());
                        databaseOperation.setTicketClosedMember(event.getTextChannel().getId(), event.getMember().getId());
                        databaseOperation.setTicketCloseReason(event.getTextChannel().getId(), "Belirtilmedi.");
                        EmbedBuilder.sendTicketClosedEmbedMessage(event.getTextChannel(), event.getMember());
                        ChannelManager.sendPrivateMessage(event.getTextChannel(), event.getJDA().getUserById(databaseOperation.getTicketCreaterID(event.getChannel().getId())), EmbedBuilder.getTicketClosedPrivateEmbedMessage(event.getJDA(), event.getChannel(), event.getMember(), "Belirtilmedi.").build());
                    }
                }
            }
            else {
                event.deferEdit().queue();
                TicketManager.closeTicket(event.getJDA(), event.getTextChannel(), event.getMember());
                databaseOperation.setTicketClosedMember(event.getTextChannel().getId(), event.getMember().getId());
                databaseOperation.setTicketCloseReason(event.getTextChannel().getId(), "Belirtilmedi.");
                EmbedBuilder.sendTicketClosedEmbedMessage(event.getTextChannel(), event.getMember());
                ChannelManager.sendPrivateMessage(event.getTextChannel(), event.getJDA().getUserById(databaseOperation.getTicketCreaterID(event.getChannel().getId())), EmbedBuilder.getTicketClosedPrivateEmbedMessage(event.getJDA(), event.getChannel(), event.getMember(), "Belirtilmedi.").build());
            }
        }
        if (buttonID.equals("ticket-close-delete")) {
            if (event.getTextChannel().getParentCategoryId().equals(pTicket.getInstance().getConfig().get("SupportCategoryID")) || event.getTextChannel().getParentCategoryId().equals(pTicket.getInstance().getConfig().get("BeklemeAlaniCategoryID")) || event.getTextChannel().getParentCategoryId().equals(pTicket.getInstance().getConfig().get("BeklemeAlani2CategoryID")) || event.getTextChannel().getParentCategoryId().equals(pTicket.getInstance().getConfig().get("McpeAlaniCategoryID"))) {
                event.deferEdit().queue();
                String embedDescription = "Destek talebi " + event.getMember().getAsMention() + " tarafından 5 saniye içerisinde silinecektir.";
                EmbedBuilder ticketDeleteEmbed = new EmbedBuilder(embedDescription, new Color(0, 255, 255));
                event.getTextChannel().sendMessageEmbeds(ticketDeleteEmbed.getEmbed().build()).queue();
                TicketManager.deleteTicket(event.getTextChannel(), event.getMember());
            }
        }
        if (buttonID.equals("ticket-close-reactive")) {
            if (event.getTextChannel().getParentCategoryId().equals(pTicket.getInstance().getConfig().get("SupportCategoryID")) || event.getTextChannel().getParentCategoryId().equals(pTicket.getInstance().getConfig().get("BeklemeAlaniCategoryID")) || event.getTextChannel().getParentCategoryId().equals(pTicket.getInstance().getConfig().get("BeklemeAlani2CategoryID")) || event.getTextChannel().getParentCategoryId().equals(pTicket.getInstance().getConfig().get("McpeAlaniCategoryID"))) {
                event.deferEdit().queue();
                String embedDescription = "Destek talebi " + event.getMember().getAsMention() + " tarafından aktifleştirildi.";
                EmbedBuilder ticketReActiveEmbed = new EmbedBuilder(embedDescription, new Color(0, 255, 255));
                event.getTextChannel().sendMessageEmbeds(ticketReActiveEmbed.getEmbed().build()).queue();
                TicketManager.reActiveTicket(event.getTextChannel(), event.getMember());
            }
        }
        if (buttonID.equals("ticket-close-claim")) {
            if (SupportTeamChecker.isMemberContainsSupportTeam(event.getMember())) {
                DatabaseOperation databaseOperation = new DatabaseOperation();
                if (databaseOperation.isDiscordAccountExistsInStaffDatabase(event.getMember().getId())) {
                    if (!databaseOperation.isDiscordChannelIsClaimed(event.getChannel().getId())) {
                        databaseOperation.setTicketClaimStatus(event.getChannel().getId(), 1);
                        databaseOperation.setTicketClaimStaff(event.getChannel().getId(), event.getMember().getId());
                        int getStaffTicketClaimAmount = databaseOperation.getStaffClaimAmount(event.getMember().getId());
                        databaseOperation.setStaffClaim(event.getMember().getId(), getStaffTicketClaimAmount + 1);
                        String embedDescription = "Destek talebi " + event.getMember().getAsMention() + " tarafından claimlendi.";
                        EmbedBuilder ticketReActiveEmbed = new EmbedBuilder(embedDescription, new Color(0, 255, 255));
                        event.getTextChannel().sendMessageEmbeds(ticketReActiveEmbed.getEmbed().build()).queue();
                        event.deferEdit().queue();
                    }
                    else {
                        event.reply("Bu destek talebi başkası tarafından claimlenmiş.").setEphemeral(true).queue();
                    }
                }
                else {
                    databaseOperation.addStaffToDatabase(event.getMember().getId(), event.getMember().getEffectiveName(), defaultCloseTicketWithoutReasonStatus);
                    if (!databaseOperation.isDiscordChannelIsClaimed(event.getChannel().getId())) {
                        databaseOperation.setTicketClaimStatus(event.getChannel().getId(), 1);
                        databaseOperation.setTicketClaimStaff(event.getChannel().getId(), event.getMember().getId());
                        int getStaffTicketClaimAmount = databaseOperation.getStaffClaimAmount(event.getMember().getId());
                        databaseOperation.setStaffClaim(event.getMember().getId(), getStaffTicketClaimAmount + 1);
                        String embedDescription = "Destek talebi " + event.getMember().getAsMention() + " tarafından claimlendi.";
                        EmbedBuilder ticketReActiveEmbed = new EmbedBuilder(embedDescription, new Color(0, 255, 255));
                        event.getTextChannel().sendMessageEmbeds(ticketReActiveEmbed.getEmbed().build()).queue();
                        event.deferEdit().queue();
                    }
                    else {
                        event.reply("Bu destek talebi başkası tarafından claimlenmiş.").setEphemeral(true).queue();
                    }
                }
            }
        }
        if (buttonID.equals("profile-" + event.getMember().getId())) {
            DatabaseOperation databaseOperation = new DatabaseOperation();
            int status = 0;
            ButtonBuilder changeCloseTicketWithoutReason = null;
            String replyDescription = null;
            if (databaseOperation.getCloseTicketWithoutReasonStatus(event.getMember().getId())) {
                status = 0;
                changeCloseTicketWithoutReason = new ButtonBuilder(ButtonStyle.SUCCESS, "profile-" + event.getMember().getId(), "Sebep ile sonlandır.");
                databaseOperation.setCloseTicketWithoutReasonStatus(event.getMember().getId(), status);
                replyDescription = "Başarıyla destek taleplerini sebep ile sonlandırmayı devre dışı bıraktın.";
            }
            else {
                status = 1;
                changeCloseTicketWithoutReason = new ButtonBuilder(ButtonStyle.DANGER, "profile-" + event.getMember().getId(), "Sebep yazmadan sonlandır.");
                databaseOperation.setCloseTicketWithoutReasonStatus(event.getMember().getId(), status);
                replyDescription = "Başarıyla destek taleplerini sebep ile sonlandırmayı aktifleştirdin.";
            }
            net.dv8tion.jda.api.EmbedBuilder profileEmbed = EmbedBuilder.getProfileEmbed(event.getMember(), databaseOperation.getStaffClaimAmount(event.getMember().getId()));
            event.editMessageEmbeds(profileEmbed.build()).setActionRow(changeCloseTicketWithoutReason.getButton()).queue();


        }
    }
}
