package com.pocan.pticket.commands;

import com.pocan.pticket.builders.ButtonBuilder;
import com.pocan.pticket.builders.EmbedBuilder;
import com.pocan.pticket.builders.SelectMenuBuilder;
import com.pocan.pticket.buttons.ButtonManager;
import com.pocan.pticket.checkers.SupportTeamChecker;
import com.pocan.pticket.database.DatabaseOperation;
import com.pocan.pticket.managers.ChannelManager;
import com.pocan.pticket.managers.PermissionManager;
import com.pocan.pticket.managers.TicketManager;
import com.pocan.pticket.pTicket;
import com.pocan.pticket.transcripts.TranscriptManager;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.utils.TimeFormat;
import org.jetbrains.annotations.NotNull;

import javax.swing.text.html.Option;
import javax.xml.crypto.Data;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.pocan.pticket.managers.ChannelManager.sendPrivateTranscriptLog;

/*
                HashMap<String, String> setupList = new HashMap<String, String>();
                setupList.put("Oyun Destek", "U+1F3AE\t");
                setupList.put("Teknik Destek", "U+1F527\t");
                String embedDescription = "Destek talebi oluşturmak için aşağıdaki **Destek türünü seç** menüsüne tıklayın.\n\nHizmet almak istediğiniz destek alanını kutucuğa tıklayarak seçin.\nSeçtikten sonra size yönlendirilen mesaj kutucuklarını doldurup **Gönder** butonuna tıklayın.\n\nLütfen açtığınız destek taleplerinin hata olmasına dikkat edin.\nSorularınız için sitemizde bulunan canlı destek sistemini kullanabilirsiniz.";
                EmbedBuilder setupEmbed = new EmbedBuilder("**SAGANETWORK TELECOM. INC. | DESTEK SİSTEMİ**",embedDescription, "https://media.discordapp.net/attachments/797566102259367936/998948700850241606/unknown.png", new Color(0,255,255),"Saganetwork Telecom. Inc. - Tüm hakları saklıdır.");
                SelectMenuBuilder selectMenu = new SelectMenuBuilder("menu:setup","Destek türünü seç.", setupList);
                event.getTextChannel().editMessageEmbedsById("875737188845879366", setupEmbed.getEmbed().build()).setActionRow(selectMenu.getSelectMenu().build()).queue();

 */

public class CommandManager extends ListenerAdapter {

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        OptionData selectMember = new OptionData(OptionType.USER, "üye", "Lütfen destek talebine eklenecek kişiyi seçin.", true);
        OptionData ticketID = new OptionData(OptionType.STRING, "talep-id", "Lütfen talep kayıtlarının getirileceği talep numarasını yazın.", true);
        commandData.add(Commands.slash("setup", "Setup the pTicket."));
        commandData.add(Commands.slash("add", "Destek talebine kişi ekle.").addOptions(selectMember));
        commandData.add(Commands.slash("log", "Talep geçmişi kayıtlarını görüntüle.").addOptions(ticketID));
        commandData.add(Commands.slash("close", "Destek talebini kapat."));
        commandData.add(Commands.slash("profil", "Toplam claim sayını görüntüle."));
        commandData.add(Commands.slash("sopa", "Hatıra komutu v1"));
        commandData.add(Commands.slash("test", "test command"));
        event.getGuild().updateCommands().addCommands(commandData).queue();

    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String command = event.getName();
        if (command.equals("setup")) {
            PermissionManager permissionManager = new PermissionManager(event.getMember());
            if (permissionManager.isUserCanExecuteSetupCommand()) {
                HashMap<String, String> setupList = new HashMap<String, String>();
                setupList.put("Oyun Destek", "U+1F3AE\t");
                setupList.put("Teknik Destek", "U+1F527\t");
                String embedDescription = "Destek talebi oluşturmak için aşağıdaki **Destek türünü seç** menüsüne tıklayın.\n\nHizmet almak istediğiniz destek alanını kutucuğa tıklayarak seçin.\nSeçtikten sonra size yönlendirilen mesaj kutucuklarını doldurup **Gönder** butonuna tıklayın.\n\nLütfen açtığınız destek taleplerinin hata olmasına dikkat edin.\nSorularınız için sitemizde bulunan canlı destek sistemini kullanabilirsiniz.";
                EmbedBuilder setupEmbed = new EmbedBuilder("**SAGANETWORK TELECOM. INC. | DESTEK SİSTEMİ**",embedDescription, "https://media.discordapp.net/attachments/797566102259367936/998948700850241606/unknown.png", new Color(0,255,255),"Saganetwork Telecom. Inc. - Tüm hakları saklıdır.");
                SelectMenuBuilder selectMenu = new SelectMenuBuilder("menu:setup","Destek türünü seç.", setupList);
                event.getChannel().sendMessageEmbeds(setupEmbed.getEmbed().build()).setActionRow(selectMenu.getSelectMenu().build()).queue();
                event.reply("Başarıyla uygulama kuruldu.").setEphemeral(true).queue();
            }
        }
        if (command.equals("add")) {
            if (event.getTextChannel().getParentCategoryId().equals(pTicket.getInstance().getConfig().get("SupportCategoryID")) || event.getTextChannel().getParentCategoryId().equals(pTicket.getInstance().getConfig().get("BeklemeAlaniCategoryID")) || event.getTextChannel().getParentCategoryId().equals(pTicket.getInstance().getConfig().get("BeklemeAlani2CategoryID")) || event.getTextChannel().getParentCategoryId().equals(pTicket.getInstance().getConfig().get("McpeAlaniCategoryID"))) {
                OptionMapping messageOption = event.getOption("üye");
                User addedUser = messageOption.getAsUser();
                DatabaseOperation databaseOperation = new DatabaseOperation();
                PermissionManager permissionManager = new PermissionManager();
                event.getTextChannel().getManager().putMemberPermissionOverride(addedUser.getIdLong(), permissionManager.getTicketMemberAllowedPermissions(), null).queue();
                String[] channelNameType = event.getTextChannel().getName().split("-");
                databaseOperation.addTicketMemberToDatabase(event.getTextChannel().getName().substring(event.getTextChannel().getName().lastIndexOf("-") + 1), channelNameType[0], event.getChannel().getId(), addedUser.getName(), addedUser.getId());
                event.reply("Başarıyla destek talebine " + addedUser.getAsMention() + " adlı kullanıcıyı ekledin.").queue();
            }
        }
        if (command.equals("log")) {
            if (SupportTeamChecker.isMemberCanSeeTheTicketLogs(event.getMember())) {
                OptionMapping messageOption = event.getOption("talep-id");
                String ticketID = messageOption.getAsString();
                DatabaseOperation databaseOperation = new DatabaseOperation();
                String transcriptHTML = databaseOperation.getTicketHTML(ticketID);
                File ticketLogTXT = databaseOperation.getTicketLogTXT(event.getTextChannel().getId());
                if (transcriptHTML != null) {
                    File transcriptFile = TranscriptManager.getTranscriptFile(transcriptHTML);
                    event.replyFile(transcriptFile, ticketID + "-talep.html").setEphemeral(true).queue();
                }
                else if (ticketLogTXT != null) {
                    event.replyFile(ticketLogTXT, ticketID + "-talep.txt").setEphemeral(true).queue();
                }
                else {
                    event.reply("Talep geçmişi veri tabanında bulunamadı.").setEphemeral(true).queue();
                }
            }
        }
        if (command.equals("close")) {
            if (event.getTextChannel().getParentCategoryId().equals(pTicket.getInstance().getConfig().get("SupportCategoryID")) || event.getTextChannel().getParentCategoryId().equals(pTicket.getInstance().getConfig().get("BeklemeAlaniCategoryID")) || event.getTextChannel().getParentCategoryId().equals(pTicket.getInstance().getConfig().get("BeklemeAlani2CategoryID")) || event.getTextChannel().getParentCategoryId().equals(pTicket.getInstance().getConfig().get("McpeAlaniCategoryID"))) {
                DatabaseOperation databaseOperation = new DatabaseOperation();
                TicketManager.closeTicket(event.getJDA(), event.getTextChannel(), event.getMember());
                databaseOperation.setTicketClosedMember(event.getTextChannel().getId(), event.getMember().getId());
                databaseOperation.setTicketCloseReason(event.getTextChannel().getId(), "Belirtilmedi.");
                EmbedBuilder.sendTicketClosedEmbedMessage(event.getTextChannel(), event.getMember());
                ChannelManager.sendPrivateMessage(event.getTextChannel(), event.getJDA().getUserById(databaseOperation.getTicketCreaterID(event.getChannel().getId())), EmbedBuilder.getTicketClosedPrivateEmbedMessage(event.getJDA(), event.getChannel(), event.getMember(), "Belirtilmedi.").build());
                event.reply("Başarıyla destek talebini sonlandırdın.").setEphemeral(true).queue();
            }
        }
        if (command.equals("profil")) {
            if (SupportTeamChecker.isMemberContainsSupportTeam(event.getMember())) {
                DatabaseOperation databaseOperation = new DatabaseOperation();
                if (databaseOperation.isDiscordAccountExistsInStaffDatabase(event.getMember().getId())) {
                    net.dv8tion.jda.api.EmbedBuilder profileEmbed = EmbedBuilder.getProfileEmbed(event.getMember(), databaseOperation.getStaffClaimAmount(event.getMember().getId()));
                    ButtonBuilder changeCloseTicketWithoutReason = null;
                    if (databaseOperation.getCloseTicketWithoutReasonStatus(event.getMember().getId())) {
                        changeCloseTicketWithoutReason = new ButtonBuilder(ButtonStyle.DANGER, "profile-" + event.getMember().getId(), "Sebep yazmadan sonlandır.");
                    } else {
                        changeCloseTicketWithoutReason = new ButtonBuilder(ButtonStyle.SUCCESS, "profile-" + event.getMember().getId(), "Sebep ile sonlandır.");
                    }
                    event.getTextChannel().sendMessageEmbeds(profileEmbed.build()).setActionRow(changeCloseTicketWithoutReason.getButton()).queue();
                    event.reply("Başarıyla profil sayfanız oluşturuldu.").setEphemeral(true).queue();
                }
                else {
                    databaseOperation.addStaffToDatabase(event.getMember().getId(), event.getMember().getEffectiveName(), ButtonManager.defaultCloseTicketWithoutReasonStatus);
                    net.dv8tion.jda.api.EmbedBuilder profileEmbed = EmbedBuilder.getProfileEmbed(event.getMember(), databaseOperation.getStaffClaimAmount(event.getMember().getId()));
                    ButtonBuilder changeCloseTicketWithoutReason = null;
                    if (databaseOperation.getCloseTicketWithoutReasonStatus(event.getMember().getId())) {
                        changeCloseTicketWithoutReason = new ButtonBuilder(ButtonStyle.DANGER, "profile-" + event.getMember().getId(), "Sebep yazmadan sonlandır.");
                    } else {
                        changeCloseTicketWithoutReason = new ButtonBuilder(ButtonStyle.SUCCESS, "profile-" + event.getMember().getId(), "Sebep ile sonlandır.");
                    }
                    event.getTextChannel().sendMessageEmbeds(profileEmbed.build()).setActionRow(changeCloseTicketWithoutReason.getButton()).queue();
                    event.reply("Başarıyla profil sayfanız oluşturuldu.").setEphemeral(true).queue();
                }
            }
        }
        if (command.equals("test")) {
            PermissionManager permissionManager = new PermissionManager(event.getMember());
            if (permissionManager.isUserCanExecuteSetupCommand()) {
                DatabaseOperation databaseOperation = new DatabaseOperation();
                File ticketLogTXT = databaseOperation.getTicketLogTXT(event.getTextChannel().getId());
                event.replyFile(ticketLogTXT).queue();
            }
        }
        if (command.equals("sopa")) {
            if (SupportTeamChecker.isMemberContainsSupportTeam(event.getMember())) {
                event.reply("Hey <@457532503759257613> sanırım özledin, al sana! Biraz daha ister misin? https://media.discordapp.net/attachments/928740539547938866/931279812583100446/447.png").queue();
            }
        }
    }
}
