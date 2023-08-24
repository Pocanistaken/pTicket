package com.pocan.pticket.builders;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import java.util.HashMap;

public class SelectMenuBuilder {
    private String menuID,placeholder;
    private HashMap<String, String> list;

    public SelectMenuBuilder(String menuID, String placeholder, HashMap<String, String> list) {
        this.menuID = menuID;
        this.placeholder = placeholder;
        this.list = list;
    }
    public SelectMenu.Builder getSelectMenu() {
        SelectMenu.Builder selectMenu = SelectMenu.create(menuID);
        selectMenu.setPlaceholder(placeholder);
        for (String i : list.keySet()) {
            selectMenu.addOption(i, i, "Destek talebi oluşturmak için tıkla.", Emoji.fromUnicode(list.get(i)));
        }
        return selectMenu;
    }

}
