package com.pocan.pticket.managers;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.EnumSet;
import java.util.List;

public class PermissionManager {
    private Member member;
    private List<String> admins;

    public PermissionManager(Member member) {
        this.member = member;
    }

    // Overloading
    public PermissionManager() {}

    public List<String> getAdmins() {
        admins.add("777208452165402674"); // Ali Öztürk#0000
        return admins;
    }

    public EnumSet<Permission> getTicketMemberAllowedPermissions() {
        return EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_EMBED_LINKS);
    }
    public EnumSet<Permission> getTicketEveryoneDenyPermissions() {
        return EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_MENTION_EVERYONE);
    }


    public boolean isUserCanExecuteSetupCommand(){
        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            return true;
        }
        else {
            Boolean foundStatus = null;
            for (String s : getAdmins()) {
                if (s.equals(member.getId().toString())) {
                    foundStatus = true;
                    break;
                }
            }
            if (foundStatus) {
                return true;
            }
            return false;
        }
    }


}
