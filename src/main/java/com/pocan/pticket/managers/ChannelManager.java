package com.pocan.pticket.managers;

import com.pocan.pticket.builders.ButtonBuilder;
import com.pocan.pticket.builders.EmbedBuilder;
import com.pocan.pticket.database.DatabaseOperation;
import com.pocan.pticket.pTicket;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.awt.*;
import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;


public class ChannelManager {

    public static final String logChannelID = "927232405251710986";
    public static final String minecraftRoleID = "615140389808242710";
    public static final String unturnedRoleID = "615139981253410826";
    public static final String fivemRoleID = "681577470562009151";
    public static final String gmodRoleID = "646099891076071444";
    public static final String mcpeRoleID = "706876730635452527";
    public static final String mtaRoleID = "676818386742673418";
    public static final String rustRoleID = "676833049920667669";


    public static void createTicket(Member member, Guild guild, String categoryID, String channelName, String ticketMemberInformationOutput, String ticketMemberEmail) {
        PermissionManager permissionManager = new PermissionManager();
        String embedDescription = member.getAsMention() + " Destek talebiniz başarıyla oluşturuldu.\nEn kısa sürede destek ekibimiz sizin ile ilgilenecektir.\n";
        guild.getCategoryById(categoryID).createTextChannel(channelName)
                .setTopic(member.getAsMention() + " adlı kullanıcının destek talebi")
                .addMemberPermissionOverride(member.getIdLong(), permissionManager.getTicketMemberAllowedPermissions(), null)
                .addRolePermissionOverride(guild.getPublicRole().getIdLong(), null, permissionManager.getTicketEveryoneDenyPermissions())
                //.addPermissionOverride(guild.getPublicRole(), null, permissionManager.getTicketEveryoneDenyPermissions())
                .queue(textChannel -> {
                    String[] channelNameType = textChannel.getName().split("-");
                    EmbedBuilder setupEmbed = new EmbedBuilder("**SAGANETWORK TELECOM. INC. | DESTEK SİSTEMİ**", embedDescription, "https://media.discordapp.net/attachments/797566102259367936/998948700850241606/unknown.png", new Color(0,255,255),"Saganetwork Telecom. Inc. - Tüm hakları saklıdır.");
                    ButtonBuilder closeTicketButton = new ButtonBuilder(ButtonStyle.DANGER, "ticket-close", "Sonlandır", "U+1F512");
                    textChannel.sendMessageEmbeds(setupEmbed.getEmbed().build()).setActionRow(closeTicketButton.getButton()).queue();
                    textChannel.sendMessageEmbeds(EmbedBuilder.getTicketInformationEmbed(ticketMemberInformationOutput,ticketMemberEmail).build()).queue();
                    DatabaseOperation databaseOperation = new DatabaseOperation();
                    databaseOperation.addTicketToDatabase(textChannel.getName().substring(textChannel.getName().lastIndexOf("-") + 1), channelNameType[0], textChannel.getId(), member.getEffectiveName(), member.getId(), ticketMemberInformationOutput, ticketMemberEmail, Instant.now());
                    databaseOperation.addTicketMemberToDatabase(textChannel.getName().substring(textChannel.getName().lastIndexOf("-") + 1), channelNameType[0], textChannel.getId(), member.getEffectiveName(), member.getId());
                    String logger = "[" + pTicket.getSimpleDateFormat("HH:mm:ss") + " INFO]: " + member.getEffectiveName() + " adlı kullanıcı " + textChannel.getName() + " numaralı destek talebini oluşturdu.";
                    pTicket.sendLogToConsole(logger);
                    sendTicketCreatedLog(member, textChannel, guild);
                    if (channelNameType[0].equals("minecraft")) {
                        textChannel.sendMessage("<@&615140389808242710>").queue();
                        textChannel.sendMessage("Destek ekibimiz gelene kadar sorunlarınıza https://minecraftdocs.mcsunucun.com/ adresinden yanıt bulabilirsiniz.").queue();
                    }
                    if (channelNameType[0].equals("unturned")) {
                        textChannel.sendMessage("<@&615139981253410826>").queue();
                        textChannel.sendMessage("Destek ekibimiz gelene kadar sorunlarınıza https://untsnd.xyz/ adresinden yanıt bulabilirsiniz.").queue();
                    }
                    if (channelNameType[0].equals("mcpe")) {
                        textChannel.sendMessage("<@&706876730635452527>").queue();
                    }
                    if (channelNameType[0].equals("fivem")) {
                        textChannel.sendMessage("<@&681577470562009151>").queue();
                    }
                    if (channelNameType[0].equals("mta")) {
                        textChannel.sendMessage("<@&676818386742673418>").queue();
                    }
                    if (channelNameType[0].equals("rust")) {
                        textChannel.sendMessage("<@&676833049920667669>").queue();
                    }
                    if (channelNameType[0].equals("gmod")) {
                        textChannel.sendMessage("<@&646099891076071444>").queue();
                    }
                });
    }

    public static void sendPrivateMessage(TextChannel context, User user, MessageEmbed content) {
        user.openPrivateChannel()
                .flatMap(channel -> channel.sendMessageEmbeds(content))
                .queue(null, new ErrorHandler()
                        .ignore(ErrorResponse.UNKNOWN_MESSAGE) // if delete fails that's fine
                        .handle(
                                ErrorResponse.CANNOT_SEND_TO_USER,  // Fallback handling for blocked messages
                                (e) -> context.sendMessage("Talep geçmişi gönderilemedi çünkü kullanıcı sunucudan gelen mesajları engellemiş.").queue()));
    }

    public static void sendPrivateTranscriptLog(User user, File file, String ticketTID) {
        user.openPrivateChannel()
                .flatMap(channel -> channel.sendFile(file, ticketTID + "-talep-geçmişi.html"))
                .queue(null, new ErrorHandler()
                        .ignore(ErrorResponse.UNKNOWN_MESSAGE) // if delete fails that's fine
                        .ignore(ErrorResponse.CANNOT_SEND_TO_USER));
    }

    public static void sendPrivateTXTLog(User user, File file, String ticketTID) {
        user.openPrivateChannel()
                .flatMap(channel -> channel.sendFile(file, ticketTID + "-talep-geçmişi.txt"))
                .queue(null, new ErrorHandler()
                        .ignore(ErrorResponse.UNKNOWN_MESSAGE) // if delete fails that's fine
                        .ignore(ErrorResponse.CANNOT_SEND_TO_USER));
    }

    public static void sendTicketCreatedLog(Member ticketOwner, TextChannel ticketChannel, Guild guild) {
        String embedDescription = ticketOwner.getEffectiveName() + " adlı kullanıcı tarafından " + ticketChannel.getName() + " adlı destek talebi oluşturuldu.\n**Destek talebi oluşturan kullanıcının discord ID:** " + ticketOwner.getId() + "\n**Destek talebi oluşturulan yazı kanalının discord ID:** " + ticketChannel.getId();
        EmbedBuilder setupEmbed = new EmbedBuilder("**SAGANETWORK TELECOM. INC. | DESTEK SİSTEMİ**",embedDescription, new Color(0,204,0),"Saganetwork Telecom. Inc. - Tüm hakları saklıdır.", LocalDateTime.now());
        TextChannel logChannel = guild.getTextChannelById(logChannelID);
        logChannel.sendMessageEmbeds(setupEmbed.getEmbed().build()).queue();
    }

    public static void sendTicketClosedLog(Member ticketCloser, TextChannel ticketChannel, Guild guild) {
        String embedDescription = ticketCloser.getEffectiveName() + " adlı kullanıcı tarafından " + ticketChannel.getName() + " adlı destek talebi sonlandırıldı.\n**Destek talebini sonlandıran kullanıcının discord ID:** " + ticketCloser.getId() + "\n**Destek talebi sonlandırılan yazı kanalının discord ID:** " + ticketChannel.getId();
        EmbedBuilder setupEmbed = new EmbedBuilder("**SAGANETWORK TELECOM. INC. | DESTEK SİSTEMİ**",embedDescription, new Color(255,255,0),"Saganetwork Telecom. Inc. - Tüm hakları saklıdır.", LocalDateTime.now());
        TextChannel logChannel = guild.getTextChannelById(logChannelID);
        logChannel.sendMessageEmbeds(setupEmbed.getEmbed().build()).queue();
    }

    public static void sendTicketDeleteLog(Member ticketDeleter, TextChannel ticketChannel, Guild guild) {
        String embedDescription = ticketDeleter.getEffectiveName() + " adlı kullanıcı tarafından " + ticketChannel.getName() + " adlı destek talebi silindi.\n**Destek talebini silen kullanıcının discord ID:** " + ticketDeleter.getId() + "\n**Destek talebi silinen yazı kanalının discord ID:** " + ticketChannel.getId();
        EmbedBuilder setupEmbed = new EmbedBuilder("**SAGANETWORK TELECOM. INC. | DESTEK SİSTEMİ**",embedDescription, new Color(255,0,0),"Saganetwork Telecom. Inc. - Tüm hakları saklıdır.", LocalDateTime.now());
        TextChannel logChannel = guild.getTextChannelById(logChannelID);
        logChannel.sendMessageEmbeds(setupEmbed.getEmbed().build()).queue();
    }

    public static void sendTicketReactiveLog(Member ticketReactiver, TextChannel ticketChannel, Guild guild) {
        String embedDescription = ticketReactiver.getEffectiveName() + " adlı kullanıcı tarafından " + ticketChannel.getName() + " adlı destek talebi yeniden aktifleştirildi.\n**Destek talebini yeniden aktifleştiren kullanıcının discord ID:** " + ticketReactiver.getId() + "\n**Destek talebi yeniden aktifleştirilen yazı kanalının discord ID:** " + ticketChannel.getId();
        EmbedBuilder setupEmbed = new EmbedBuilder("**SAGANETWORK TELECOM. INC. | DESTEK SİSTEMİ**",embedDescription, new Color(255,102,0),"Saganetwork Telecom. Inc. - Tüm hakları saklıdır.", LocalDateTime.now());
        TextChannel logChannel = guild.getTextChannelById(logChannelID);
        logChannel.sendMessageEmbeds(setupEmbed.getEmbed().build()).queue();
    }


}
