package com.pocan.pticket.transcripts;


import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class DiscordHtmlTranscripts {
    private final List<String> imageFormats = Arrays.asList(new String[] { "png", "jpg", "jpeg", "gif" });

    private final List<String> videoFormats = Arrays.asList(new String[] { "mp4", "webm", "mkv", "avi", "mov", "flv", "wmv", "mpg", "mpeg" });

    private final List<String> audioFormats = Arrays.asList(new String[] { "mp3", "wav", "ogg", "flac" });

    private static final DiscordHtmlTranscripts instance = new DiscordHtmlTranscripts();

    public static DiscordHtmlTranscripts getInstance() {
        return instance;
    }

    public String createTranscript(TextChannel channel) throws IOException, URISyntaxException {
        return generateFromMessages((Collection<Message>)channel.getIterableHistory().stream().collect(Collectors.toList()));
    }

    public String generateFromMessages(Collection<Message> messages) throws IOException, URISyntaxException {
        String directory = (new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI())).getParentFile().getPath();
        File htmlTemplate = new File(directory + "//template.html");
        if (messages.isEmpty())
            throw new IllegalArgumentException("No messages to generate a transcript from");
        TextChannel channel = ((Message)messages.iterator().next()).getTextChannel();
        Document document = Jsoup.parse(htmlTemplate, "UTF-8");
        document.outputSettings().indentAmount(0).prettyPrint(true);
        document.getElementsByClass("preamble__guild-icon")
                .first().attr("src", channel.getGuild().getIconUrl());
        document.getElementById("transcriptTitle").text(channel.getName());
        document.getElementById("guildname").text(channel.getGuild().getName());
        document.getElementById("ticketname").text(channel.getName());
        Element chatLog = document.getElementById("chatlog");
        for (Message message : messages.stream().sorted(Comparator.comparing(ISnowflake::getTimeCreated)).collect(Collectors.toList())) {
            Element messageGroup = document.createElement("div");
            messageGroup.addClass("chatlog__message-group");
            if (message.getReferencedMessage() != null) {
                Element referenceSymbol = document.createElement("div");
                referenceSymbol.addClass("chatlog__reference-symbol");
                Element reference = document.createElement("div");
                reference.addClass("chatlog__reference");
                Message referenceMessage = message.getReferencedMessage();
                User user = referenceMessage.getAuthor();
                Member member = channel.getGuild().getMember(user);
                String color = Formatter.toHex(Objects.<Color>requireNonNull(member.getColor()));
                reference.html(

                        ("<img class=\"chatlog__reference-avatar\" src=\"" + user.getAvatarUrl() + "\" alt=\"Avatar\" loading=\"lazy\">" + "<span class=\"chatlog__reference-name\" title=\"" + user.getName() + "\" style=\"color: " + color + "\">" + user.getName() + "\"</span>" + "<div class=\"chatlog__reference-content\">" + " <span class=\"chatlog__reference-link\" onclick=\"scrollToMessage(event, '" + referenceMessage.getId() + "')\">" + "<em>" + referenceMessage.getContentDisplay() != null) ? (
                                (referenceMessage.getContentDisplay().length() > 42) ? (

                                        referenceMessage.getContentDisplay().substring(0, 42) + "...") :
                                        referenceMessage.getContentDisplay()) :
                                "Click to see attachment</em></span></div>");
                messageGroup.appendChild((Node)referenceSymbol);
                messageGroup.appendChild((Node)reference);
            }
            User author = message.getAuthor();
            Element authorElement = document.createElement("div");
            authorElement.addClass("chatlog__author-avatar-container");
            Element authorAvatar = document.createElement("img");
            authorAvatar.addClass("chatlog__author-avatar");
            authorAvatar.attr("src", author.getAvatarUrl());
            authorAvatar.attr("alt", "Avatar");
            authorAvatar.attr("loading", "lazy");
            authorElement.appendChild((Node)authorAvatar);
            messageGroup.appendChild((Node)authorElement);
            Element content = document.createElement("div");
            content.addClass("chatlog__messages");
            Element authorName = document.createElement("span");
            authorName.addClass("chatlog__author-name");
            authorName.attr("title", author.getAsTag());
            authorName.text(author.getName());
            authorName.attr("data-user-id", author.getId());
            content.appendChild((Node)authorName);
            if (author.isBot()) {
                Element botTag = document.createElement("span");
                botTag.addClass("chatlog__bot-tag").text("BOT");
                content.appendChild((Node)botTag);
            }
            Element timestamp = document.createElement("span");
            timestamp.addClass("chatlog__timestamp");
            timestamp
                    .text(message.getTimeCreated().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            content.appendChild((Node)timestamp);
            Element messageContent = document.createElement("div");
            messageContent.addClass("chatlog__message");
            messageContent.attr("data-message-id", message.getId());
            messageContent.attr("id", "message-" + message.getId());
            messageContent.attr("title", "Message sent: " + message
                    .getTimeCreated().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            if (message.getContentDisplay().length() > 0) {
                Element messageContentContent = document.createElement("div");
                messageContentContent.addClass("chatlog__content");
                Element messageContentContentMarkdown = document.createElement("div");
                messageContentContentMarkdown.addClass("markdown");
                Element messageContentContentMarkdownSpan = document.createElement("span");
                messageContentContentMarkdownSpan.addClass("preserve-whitespace");
                messageContentContentMarkdownSpan
                        .html(Formatter.format(message.getContentDisplay()));
                messageContentContentMarkdown.appendChild((Node)messageContentContentMarkdownSpan);
                messageContentContent.appendChild((Node)messageContentContentMarkdown);
                messageContent.appendChild((Node)messageContentContent);
            }
            if (!message.getAttachments().isEmpty())
                for (Message.Attachment attach : message.getAttachments()) {
                    Element attachmentsDiv = document.createElement("div");
                    attachmentsDiv.addClass("chatlog__attachment");
                    String attachmentType = attach.getFileExtension();
                    if (this.imageFormats.contains(attachmentType)) {
                        Element attachmentLink = document.createElement("a");
                        Element attachmentImage = document.createElement("img");
                        attachmentImage.addClass("chatlog__attachment-media");
                        attachmentImage.attr("src", attach.getUrl());
                        attachmentImage.attr("alt", "Image attachment");
                        attachmentImage.attr("loading", "lazy");
                        attachmentImage.attr("title", "Image: " + attach
                                .getFileName() + Formatter.formatBytes(attach.getSize()));
                        attachmentLink.appendChild((Node)attachmentImage);
                        attachmentsDiv.appendChild((Node)attachmentLink);
                    } else if (this.videoFormats.contains(attachmentType)) {
                        Element attachmentVideo = document.createElement("video");
                        attachmentVideo.addClass("chatlog__attachment-media");
                        attachmentVideo.attr("src", attach.getUrl());
                        attachmentVideo.attr("alt", "Video attachment");
                        attachmentVideo.attr("controls", true);
                        attachmentVideo.attr("title", "Video: " + attach
                                .getFileName() + Formatter.formatBytes(attach.getSize()));
                        attachmentsDiv.appendChild((Node)attachmentVideo);
                    } else if (this.audioFormats.contains(attachmentType)) {
                        Element attachmentAudio = document.createElement("audio");
                        attachmentAudio.addClass("chatlog__attachment-media");
                        attachmentAudio.attr("src", attach.getUrl());
                        attachmentAudio.attr("alt", "Audio attachment");
                        attachmentAudio.attr("controls", true);
                        attachmentAudio.attr("title", "Audio: " + attach
                                .getFileName() + Formatter.formatBytes(attach.getSize()));
                        attachmentsDiv.appendChild((Node)attachmentAudio);
                    } else {
                        Element attachmentGeneric = document.createElement("div");
                        attachmentGeneric.addClass("chatlog__attachment-generic");
                        Element attachmentGenericIcon = document.createElement("svg");
                        attachmentGenericIcon.addClass("chatlog__attachment-generic-icon");
                        Element attachmentGenericIconUse = document.createElement("use");
                        attachmentGenericIconUse.attr("xlink:href", "#icon-attachment");
                        attachmentGenericIcon.appendChild((Node)attachmentGenericIconUse);
                        attachmentGeneric.appendChild((Node)attachmentGenericIcon);
                        Element attachmentGenericName = document.createElement("div");
                        attachmentGenericName.addClass("chatlog__attachment-generic-name");
                        Element attachmentGenericNameLink = document.createElement("a");
                        attachmentGenericNameLink.attr("href", attach.getUrl());
                        attachmentGenericNameLink.text(attach.getFileName());
                        attachmentGenericName.appendChild((Node)attachmentGenericNameLink);
                        attachmentGeneric.appendChild((Node)attachmentGenericName);
                        Element attachmentGenericSize = document.createElement("div");
                        attachmentGenericSize.addClass("chatlog__attachment-generic-size");
                        attachmentGenericSize.text(Formatter.formatBytes(attach.getSize()));
                        attachmentGeneric.appendChild((Node)attachmentGenericSize);
                        attachmentsDiv.appendChild((Node)attachmentGeneric);
                    }
                    messageContent.appendChild((Node)attachmentsDiv);
                }
            content.appendChild((Node)messageContent);
            if (!message.getEmbeds().isEmpty())
                for (MessageEmbed embed : message.getEmbeds()) {
                    if (embed == null)
                        continue;
                    Element embedDiv = document.createElement("div");
                    embedDiv.addClass("chatlog__embed");
                    if (embed.getColor() != null) {
                        Element embedColorPill = document.createElement("div");
                        embedColorPill.addClass("chatlog__embed-color-pill");
                        embedColorPill.attr("style", "background-color: #" +
                                Formatter.toHex(embed.getColor()));
                        embedDiv.appendChild((Node)embedColorPill);
                    }
                    Element embedContentContainer = document.createElement("div");
                    embedContentContainer.addClass("chatlog__embed-content-container");
                    Element embedContent = document.createElement("div");
                    embedContent.addClass("chatlog__embed-content");
                    Element embedText = document.createElement("div");
                    embedText.addClass("chatlog__embed-text");
                    if (embed.getAuthor() != null && embed.getAuthor().getName() != null) {
                        Element embedAuthor = document.createElement("div");
                        embedAuthor.addClass("chatlog__embed-author");
                        if (embed.getAuthor().getIconUrl() != null) {
                            Element embedAuthorIcon = document.createElement("img");
                            embedAuthorIcon.addClass("chatlog__embed-author-icon");
                            embedAuthorIcon.attr("src", embed.getAuthor().getIconUrl());
                            embedAuthorIcon.attr("alt", "Author icon");
                            embedAuthorIcon.attr("loading", "lazy");
                            embedAuthor.appendChild((Node)embedAuthorIcon);
                        }
                        Element embedAuthorName = document.createElement("span");
                        embedAuthorName.addClass("chatlog__embed-author-name");
                        if (embed.getAuthor().getUrl() != null) {
                            Element embedAuthorNameLink = document.createElement("a");
                            embedAuthorNameLink.addClass("chatlog__embed-author-name-link");
                            embedAuthorNameLink.attr("href", embed.getAuthor().getUrl());
                            embedAuthorNameLink.text(embed.getAuthor().getName());
                            embedAuthorName.appendChild((Node)embedAuthorNameLink);
                        } else {
                            embedAuthorName.text(embed.getAuthor().getName());
                        }
                        embedAuthor.appendChild((Node)embedAuthorName);
                        embedText.appendChild((Node)embedAuthor);
                    }
                    if (embed.getTitle() != null) {
                        Element embedTitle = document.createElement("div");
                        embedTitle.addClass("chatlog__embed-title");
                        if (embed.getUrl() != null) {
                            Element embedTitleLink = document.createElement("a");
                            embedTitleLink.addClass("chatlog__embed-title-link");
                            embedTitleLink.attr("href", embed.getUrl());
                            Element embedTitleMarkdown = document.createElement("div");
                            embedTitleMarkdown.addClass("markdown preserve-whitespace")
                                    .html(Formatter.format(embed.getTitle()));
                            embedTitleLink.appendChild((Node)embedTitleMarkdown);
                            embedTitle.appendChild((Node)embedTitleLink);
                        } else {
                            Element embedTitleMarkdown = document.createElement("div");
                            embedTitleMarkdown.addClass("markdown preserve-whitespace")
                                    .html(Formatter.format(embed.getTitle()));
                            embedTitle.appendChild((Node)embedTitleMarkdown);
                        }
                        embedText.appendChild((Node)embedTitle);
                    }
                    if (embed.getDescription() != null) {
                        Element embedDescription = document.createElement("div");
                        embedDescription.addClass("chatlog__embed-description");
                        Element embedDescriptionMarkdown = document.createElement("div");
                        embedDescriptionMarkdown.addClass("markdown preserve-whitespace");
                        embedDescriptionMarkdown
                                .html(Formatter.format(embed.getDescription()));
                        embedDescription.appendChild((Node)embedDescriptionMarkdown);
                        embedText.appendChild((Node)embedDescription);
                    }
                    if (!embed.getFields().isEmpty()) {
                        Element embedFields = document.createElement("div");
                        embedFields.addClass("chatlog__embed-fields");
                        for (MessageEmbed.Field field : embed.getFields()) {
                            Element embedField = document.createElement("div");
                            embedField.addClass(field.isInline() ? "chatlog__embed-field-inline" :
                                    "chatlog__embed-field");
                            Element embedFieldName = document.createElement("div");
                            embedFieldName.addClass("chatlog__embed-field-name");
                            Element embedFieldNameMarkdown = document.createElement("div");
                            embedFieldNameMarkdown.addClass("markdown preserve-whitespace");
                            embedFieldNameMarkdown.html(field.getName());
                            embedFieldName.appendChild((Node)embedFieldNameMarkdown);
                            embedField.appendChild((Node)embedFieldName);
                            Element embedFieldValue = document.createElement("div");
                            embedFieldValue.addClass("chatlog__embed-field-value");
                            Element embedFieldValueMarkdown = document.createElement("div");
                            embedFieldValueMarkdown.addClass("markdown preserve-whitespace");
                            embedFieldValueMarkdown
                                    .html(Formatter.format(field.getValue()));
                            embedFieldValue.appendChild((Node)embedFieldValueMarkdown);
                            embedField.appendChild((Node)embedFieldValue);
                            embedFields.appendChild((Node)embedField);
                        }
                        embedText.appendChild((Node)embedFields);
                    }
                    embedContent.appendChild((Node)embedText);
                    if (embed.getThumbnail() != null) {
                        Element embedThumbnail = document.createElement("div");
                        embedThumbnail.addClass("chatlog__embed-thumbnail-container");
                        Element embedThumbnailLink = document.createElement("a");
                        embedThumbnailLink.addClass("chatlog__embed-thumbnail-link");
                        embedThumbnailLink.attr("href", embed.getThumbnail().getUrl());
                        Element embedThumbnailImage = document.createElement("img");
                        embedThumbnailImage.addClass("chatlog__embed-thumbnail");
                        embedThumbnailImage.attr("src", embed.getThumbnail().getUrl());
                        embedThumbnailImage.attr("alt", "Thumbnail");
                        embedThumbnailImage.attr("loading", "lazy");
                        embedThumbnailLink.appendChild((Node)embedThumbnailImage);
                        embedThumbnail.appendChild((Node)embedThumbnailLink);
                        embedContent.appendChild((Node)embedThumbnail);
                    }
                    embedContentContainer.appendChild((Node)embedContent);
                    if (embed.getImage() != null) {
                        Element embedImage = document.createElement("div");
                        embedImage.addClass("chatlog__embed-image-container");
                        Element embedImageLink = document.createElement("a");
                        embedImageLink.addClass("chatlog__embed-image-link");
                        embedImageLink.attr("href", embed.getImage().getUrl());
                        Element embedImageImage = document.createElement("img");
                        embedImageImage.addClass("chatlog__embed-image");
                        embedImageImage.attr("src", embed.getImage().getUrl());
                        embedImageImage.attr("alt", "Image");
                        embedImageImage.attr("loading", "lazy");
                        embedImageLink.appendChild((Node)embedImageImage);
                        embedImage.appendChild((Node)embedImageLink);
                        embedContentContainer.appendChild((Node)embedImage);
                    }
                    if (embed.getFooter() != null) {
                        Element embedFooter = document.createElement("div");
                        embedFooter.addClass("chatlog__embed-footer");
                        if (embed.getFooter().getIconUrl() != null) {
                            Element embedFooterIcon = document.createElement("img");
                            embedFooterIcon.addClass("chatlog__embed-footer-icon");
                            embedFooterIcon.attr("src", embed.getFooter().getIconUrl());
                            embedFooterIcon.attr("alt", "Footer icon");
                            embedFooterIcon.attr("loading", "lazy");
                            embedFooter.appendChild((Node)embedFooterIcon);
                        }
                        Element embedFooterText = document.createElement("span");
                        embedFooterText.addClass("chatlog__embed-footer-text");
                        embedFooterText.text((embed.getTimestamp() != null) ? (
                                embed.getFooter().getText() + " " + embed.getTimestamp()
                                        .format(DateTimeFormatter.ofPattern("HH:mm:ss"))) :
                                embed.getFooter().getText());
                        embedFooter.appendChild((Node)embedFooterText);
                        embedContentContainer.appendChild((Node)embedFooter);
                    }
                    embedDiv.appendChild((Node)embedContentContainer);
                    content.appendChild((Node)embedDiv);
                }
            messageGroup.appendChild((Node)content);
            chatLog.appendChild((Node)messageGroup);
        }
        return document.outerHtml();
    }

    private File findFile(String fileName) {
        URL url = getClass().getClassLoader().getResource(fileName);
        if (url == null)
            throw new IllegalArgumentException("file is not found: " + fileName);
        return new File(url.getFile());
    }
}
