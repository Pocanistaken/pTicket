package com.pocan.pticket.checkers;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import java.util.ArrayList;
import java.util.List;

public class SupportTeamChecker {

    public final static String generalAuthorityID = "578964534556098560";
    public final static String üstYetkiliID = "771320153676709906";
    public final static String yetkiliID = "578964711035502603";
    public final static String departmanGörevlisiID = "771320156202467339";
    public final static String destekEkibiID = "578965093769805862";
    public final static String denemeID = "578967785892478993";


    public static boolean isMemberContainsSupportTeam(Member member) {
        List<Role> memberRoles = member.getRoles();
        List<String> memberRolesID = new ArrayList<>();
        for (int i = 0; i < memberRoles.size(); i++) {
            memberRolesID.add(memberRoles.get(i).getId());
        }
        if (memberRolesID.contains(generalAuthorityID)) { // General Authority Role
            return true;
        }
        if (memberRolesID.contains(üstYetkiliID)) { // Üst Yetkili Role
            return true;
        }
        if (memberRolesID.contains(yetkiliID)) { // Yetkili Role
            return true;
        }
        if (memberRolesID.contains(departmanGörevlisiID)) { // Departman Görevlisi Role
            return true;
        }
        if (memberRolesID.contains(destekEkibiID)) { // Destek Ekibi Role
            return true;
        }
        if (memberRolesID.contains(denemeID)) { // Deneme Role
            return true;
        }
        if (memberRolesID.contains("1137822845347569675")) { // Owner Role
            return true;
        }
        return false;
    }
    public static boolean isMemberCanSeeTheTicketLogs(Member member) {
        List<Role> memberRoles = member.getRoles();
        List<String> memberRolesID = new ArrayList<>();
        for (int i = 0; i < memberRoles.size(); i++) {
            memberRolesID.add(memberRoles.get(i).getId());
        }
        if (memberRolesID.contains(generalAuthorityID)) { // General Authority Role
            return true;
        }
        if (memberRolesID.contains(üstYetkiliID)) { // Üst Yetkili Role
            return true;
        }
        if (memberRolesID.contains(yetkiliID)) { // Yetkili Role
            return true;
        }
        return false;
    }

}
