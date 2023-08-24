package com.pocan.pticket.builders;

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

public class ButtonBuilder {
    private String id,label,emojiUnicode;
    private ButtonStyle buttonStyle;

    // Some overloadings again ._.

    public ButtonBuilder(ButtonStyle buttonStyle, String id, String label, String emojiUnicode) {
        this.id = id;
        this.buttonStyle = buttonStyle;
        this.label = label;
        this.emojiUnicode = emojiUnicode;
    }

    public ButtonBuilder(ButtonStyle buttonStyle, String id, String label) {
        this.id = id;
        this.buttonStyle = buttonStyle;
        this.label = label;
    }

    public Button getButton() {
        if (emojiUnicode != null) {
            return Button.of(buttonStyle, id, label, Emoji.fromUnicode(emojiUnicode));
        }
        return Button.of(buttonStyle, id, label);
    }
}
