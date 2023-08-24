package com.pocan.pticket.builders;

import com.pocan.pticket.database.DatabaseOperation;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.TimeFormat;

import java.awt.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class EmbedBuilder {
    private String title,description,image,footer;
    private Color color;
    private LocalDateTime timestamp;



    /*
    Some overloadings xD
     */


    public EmbedBuilder(String description, Color color) {
        this.description = description;
        this.color = color;
    }

    public EmbedBuilder(String title, String description, Color color, String footer) {
        this.title = title;
        this.description = description;
        this.color = color;
        this.footer = footer;
    }

    public EmbedBuilder(String title, String description, Color color, String footer, LocalDateTime timestamp) {
        this.title = title;
        this.description = description;
        this.color = color;
        this.footer = footer;
        this.timestamp = timestamp;
    }

    public EmbedBuilder(String title, Color color, String footer) {
        this.title = title;
        this.color = color;
        this.footer = footer;
    }

    public EmbedBuilder(String title, String description, String image, Color color, String footer) {
        this.title = title;
        this.description = description;
        this.color = color;
        this.footer = footer;
        this.image = image;
    }


    public net.dv8tion.jda.api.EmbedBuilder getEmbed() {
        net.dv8tion.jda.api.EmbedBuilder embedBuilder = new net.dv8tion.jda.api.EmbedBuilder()
                .setDescription(description)
                .setColor(color);
        if (image != null) {
            embedBuilder.setImage(image);
        }
        if (title != null) {
            embedBuilder.setTitle(title);
        }
        if (footer != null) {
            embedBuilder.setFooter(footer);
        }
        if (timestamp != null) {
            embedBuilder.setTimestamp(timestamp);
        }

        return embedBuilder;
    }

    public static net.dv8tion.jda.api.EmbedBuilder getTicketClosedPrivateEmbedMessage(JDA jda, Channel channel, Member ticketClosedMember, String ticketCloseReason) {
        DatabaseOperation databaseOperation = new DatabaseOperation();
        Timestamp ticketCreateDate = databaseOperation.getTicketCreateDate(channel.getId());
        net.dv8tion.jda.api.EmbedBuilder privateMessageEmbed = new net.dv8tion.jda.api.EmbedBuilder()
                .setTitle("**SAGANETWORK TELECOM. INC. | DESTEK SİSTEMİ**")
                .setColor(new Color(0,255,255))
                .setFooter("Saganetwork Telecom. Inc. - Tüm hakları saklıdır.")
                .addField(":id: Talep Numarası", channel.getName().substring(channel.getName().lastIndexOf("-") + 1), true)
                .addField(":evergreen_tree: Oluşturan Kişi", jda.getUserById(databaseOperation.getTicketCreaterID(channel.getId())).getAsMention(), true)
                .addField(":red_envelope: Sonlandıran Kişi", ticketClosedMember.getAsMention(), true)
                .addField(":mailbox: Sebep", ticketCloseReason, true)
                .addField(":scroll: Bilet Geçmişi", "Gönderildi.", true)
                .addField(":calendar: Oluşturulma Zamanı", TimeFormat.DATE_TIME_SHORT.format(ticketCreateDate.toInstant()), true);
        return privateMessageEmbed;
    }

    public static net.dv8tion.jda.api.EmbedBuilder getTicketInformationEmbed(String ticketMemberMessageOutput, String ticketMemberEmail) {
        net.dv8tion.jda.api.EmbedBuilder ticketInformationEmbed = new net.dv8tion.jda.api.EmbedBuilder()
                .setTitle("**SAGANETWORK TELECOM. INC. | DESTEK SİSTEMİ**")
                .setColor(new Color(0,255,255))
                .setFooter("Saganetwork Telecom. Inc. - Tüm hakları saklıdır.")
                .addField(":red_envelope: Bilgilendirme", ticketMemberMessageOutput, true)
                .addField(":envelope: E-posta adresi", ticketMemberEmail, true);
        return ticketInformationEmbed;
    }

    public static net.dv8tion.jda.api.EmbedBuilder getProfileEmbed(Member member, int totalClaim) {
        String embedDescription = "**Saganetwork** yetkilileri için özel olarak hazırlandı.\nBu komut üzerinden destek taleplerindeki topladığınız **claim** adetini görüntüleyebilirsiniz ve destek taleplerini sonlandırırken sebep ile sonlandırıp sonlandırmama butonu üzerinden bunu değiştirebilirsiniz.";
        net.dv8tion.jda.api.EmbedBuilder ticketInformationEmbed = new net.dv8tion.jda.api.EmbedBuilder()
                .setTitle("**SAGANETWORK TELECOM. INC. | PROFİL SİSTEMİ**")
                .setColor(new Color(0,255,255))
                .setThumbnail(member.getEffectiveAvatarUrl())
                .setFooter("Saganetwork Telecom. Inc. - Tüm hakları saklıdır.")
                .addField(":busts_in_silhouette: Kullanıcı", member.getEffectiveName(), true)
                .addField(":red_envelope: Claim", String.valueOf(totalClaim), true);
        return ticketInformationEmbed;
    }

    public static void sendTicketClosedEmbedMessage(TextChannel channel, Member ticketClosedMember) {
        String embedDescription = "Destek talebi " + ticketClosedMember.getAsMention() + " tarafından sonlandırıldı.";
        EmbedBuilder closeConfirmEmbed = new com.pocan.pticket.builders.EmbedBuilder("**SAGANETWORK TELECOM. INC. | DESTEK SİSTEMİ**", embedDescription, new Color(0,255,255), "Saganetwork Telecom. Inc. - Tüm hakları saklıdır.");
        ButtonBuilder deleteTicketButton = new ButtonBuilder(ButtonStyle.DANGER, "ticket-close-delete", "Sil", "U+1F53B");
        ButtonBuilder reActiveTicketButton = new ButtonBuilder(ButtonStyle.SUCCESS, "ticket-close-reactive", "Aktifleştir", "U+1F332");
        ButtonBuilder claimTicketButton = new ButtonBuilder(ButtonStyle.SECONDARY, "ticket-close-claim", "Claim", "U+1F4DC");
        channel.sendMessageEmbeds(closeConfirmEmbed.getEmbed().build()).setActionRow(deleteTicketButton.getButton(), reActiveTicketButton.getButton(), claimTicketButton.getButton()).queue();
    }
}
