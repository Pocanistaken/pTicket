package com.pocan.pticket.builders;


import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

public class TextInputBuilder {
    private String id,label,placeholder;
    private boolean requiredStatus;
    private Integer minLength,maxLength;
    private TextInputStyle textInputStyle;

    public TextInputBuilder(String id, String label, TextInputStyle textInputStyle, String placeholder, boolean requiredStatus) {
        this.id = id;
        this.label = label;
        this.textInputStyle = textInputStyle;
        this.placeholder = placeholder;
        this.requiredStatus = requiredStatus;
    }

    public TextInputBuilder(String id, String label, TextInputStyle textInputStyle, String placeholder, boolean requiredStatus, Integer minLength, Integer maxLength) {
        this.id = id;
        this.label = label;
        this.textInputStyle = textInputStyle;
        this.placeholder = placeholder;
        this.requiredStatus = requiredStatus;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }
        public TextInput getTextInput() {
            if (minLength != null && maxLength != null) {
                TextInput textInput = TextInput.create(id, label, textInputStyle)
                        .setRequired(requiredStatus)
                        .setPlaceholder(placeholder)
                        .setMinLength(minLength)
                        .setMaxLength(maxLength)
                        .build();
                return textInput;
            }
            else {
                TextInput textInput = TextInput.create(id, label, textInputStyle)
                        .setRequired(requiredStatus)
                        .setPlaceholder(placeholder)
                        .build();
                return textInput;
            }

        }
}
