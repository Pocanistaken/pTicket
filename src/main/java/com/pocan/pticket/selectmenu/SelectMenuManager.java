package com.pocan.pticket.selectmenu;

import com.pocan.pticket.builders.TextInputBuilder;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.jetbrains.annotations.NotNull;

public class SelectMenuManager extends ListenerAdapter {

    @Override

    public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {
        String selectMenuID = event.getSelectMenu().getId();

        /*

        Ticket Creating

         */

        if (selectMenuID.equals("menu:setup")) {

            switch (event.getInteraction().getValues().get(0)) {

                case "Oyun Destek":
                    TextInputBuilder gameGame = new TextInputBuilder("gamesupport-input-game", "Hangi oyun hakkında destek almak istiyorsun?", TextInputStyle.PARAGRAPH, "Oyunlar: minecraft, mcpe, unturned", true, 3, 50);
                    TextInputBuilder problemGame = new TextInputBuilder("gamesupport-input-problem", "Destek almak istediğiniz konuyu belirtin.", TextInputStyle.PARAGRAPH, "Sunucumda beyaz liste nasıl açabilirim?", true, 10, 200);
                    TextInputBuilder mailGame = new TextInputBuilder("gamesupport-input-email", "E-posta adresinizi yazın.", TextInputStyle.PARAGRAPH, "test@saganetwork.net", true, 2, 150);

                    Modal modalGame = Modal.create("Game Support", "Oyun Destek")
                            .addActionRow(gameGame.getTextInput())
                            .addActionRow(problemGame.getTextInput())
                            .addActionRow(mailGame.getTextInput())
                            .build();
                    event.replyModal(modalGame).queue();
                    break;
                case "Teknik Destek":
                    TextInputBuilder problemTechnical = new TextInputBuilder("technicalsupport-input-problem", "Destek almak istediğiniz konuyu belirtin.", TextInputStyle.PARAGRAPH, "Panelime bağlanamıyorum.", true, 10, 200);
                    TextInputBuilder mailTechnical = new TextInputBuilder("technicalsupport-input-email", "E-posta adresinizi yazın.", TextInputStyle.PARAGRAPH, "test@saganetwork.net", true, 2, 150);

                    Modal modalTechnical = Modal.create("Technical Support", "Teknik Destek")
                            .addActionRow(problemTechnical.getTextInput())
                            .addActionRow(mailTechnical.getTextInput())
                            .build();
                    event.replyModal(modalTechnical).queue();
                    break;


            }
        }

    }
}
