package com.pocan.pticket.checkers;

import java.util.ArrayList;

public class GameChecker {

    public static boolean isValidGame(String game) {
        ArrayList<String> list = new ArrayList<String>();
        list.add("minecraft");
        list.add("mcpe");
        list.add("unturned");
        list.add("gmod");
        list.add("mta");
        list.add("fivem");
        list.add("sclp");
        list.add("dcbot");
        list.add("scp");
        list.add("vrising");
        list.add("ark");
        if (list.contains(game)) {
            return true;
        }
        return false;

    }
}
